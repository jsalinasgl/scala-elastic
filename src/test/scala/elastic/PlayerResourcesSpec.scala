package elastic

import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import elastic.collections.Players
import elastic.entities.Player
import elastic.resources.PlayerResources
import elastic.views.DefaultCustomersView
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import elastic.json.JsonFormat._
import elastic.views.CustomersView
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.{ExecutionContext, Future}

class PlayerResourcesSpec extends AsyncWordSpec
  with Matchers
  with ScalatestRouteTest {

  val basePath = "/players"
  val routes = PlayerResources(new DefaultCustomersView(new MockPlayers))

  val playerId = "123"
  val playerName = "player"

  basePath should {
    s"GET player by id" in {
      Get(s"$basePath/$playerId") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[Player].id.toString shouldBe playerId
      }
    }
    s"GET player by param name" in {
      Get(s"$basePath?name=$playerName") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[Iterable[Player]].head.name shouldBe playerName
      }
    }
  }
}

class MockPlayers extends Players {
  override def find(id: String)(implicit ec: ExecutionContext): Future[Option[Player]] =
    Future.successful(Option(getItem(id)))

  override def findByFilters(name: Option[String],
                             club: Option[String],
                             overall: Option[Int],
                             maybePage: Option[Int],
                             maybePageSize: Option[Int])
                            (implicit ec: ExecutionContext): Future[Iterable[Player]] =
    Future.successful(Iterable(getItem(name = name.getOrElse(""))))

  private[this] def getItem(id: String = "1", name: String = "name"): Player = {
    Player(
      id = id.toInt,
      name = name,
      age = 30,
      nationality = "nationality",
      overall = 10,
      club = "club",
      value = "value",
      foot = "left",
      number = 9,
      position = "Position"
    )
  }
}