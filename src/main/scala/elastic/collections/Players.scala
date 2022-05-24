package elastic.collections

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, Hit, HitReader}
import elastic.entities.Player

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait Players {
  def find(id: String)(implicit ec: ExecutionContext): Future[Option[Player]]
  def findByFilters(name: Option[String],
                    club: Option[String],
                    overall: Option[Int],
                    maybePage:      Option[Int],
                    maybePageSize:  Option[Int])
                   (implicit ec: ExecutionContext): Future[Iterable[Player]]
}

class DefaultPlayers(client: ElasticClient)
                    (implicit system: ActorSystem)
  extends Players {

  implicit object PlayerHitReader extends HitReader[Player] {
    override def read(hit: Hit): Try[Player] = Try {
      val source = hit.sourceAsMap
      Player(
        id = source("id").toString.toInt,
        name = source("name").toString,
        age = source("age").toString.toInt,
        nationality = source("nationality").toString,
        overall = source("overall").toString.toInt,
        club = source("club").toString,
        value = source("value").toString,
        foot = source("foot").toString,
        number = source("number").toString.toInt,
        position = source("position").toString
      )
    }
  }

  override def find(id: String)(implicit ec: ExecutionContext): Future[Option[Player]] = {
    client.execute {
      search("players").query(termQuery("id", id))
    } map { rs =>
      rs.result.to[Player].headOption
    } recoverWith {
    case e =>
      system.log.error("Failed to retrieve player info from elastic: {}", e.getMessage)
      Future.failed(e)
    }
  }

  override def findByFilters(name: Option[String],
                             club: Option[String],
                             overall: Option[Int],
                             maybePage: Option[Int],
                             maybePageSize: Option[Int])
                            (implicit ec: ExecutionContext): Future[Iterable[Player]] = {

    val pageSize = maybePageSize.getOrElse(100)
    val startRow = (maybePage.getOrElse(1) - 1) * pageSize

    val filters = Iterable(
      name.map(value => queryStringQuery(value).field("name")),
      club.map(value => matchPhrasePrefixQuery("club", value)),
      overall.map(value => rangeQuery("overall").gte(value))
    ).flatten

    client.execute {
      search("players")
        .query(boolQuery.must(filters))
        .start(startRow)
        .limit(pageSize)
    } map { rs =>
      rs.result.to[Player]
    }
  }
}

object Players {
  def apply(client: ElasticClient)(implicit system: ActorSystem): Players = {
    new DefaultPlayers(client)
  }
}
