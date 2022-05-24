package elastic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.http.JavaClient
import elastic.collections.Players
import elastic.config.AppConfig
import elastic.resources.PlayerResources
import elastic.views.DefaultCustomersView

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("scala-elastic-server")
    import system.dispatcher

    val config = AppConfig()
    val elasticClient = ElasticClient(JavaClient(ElasticProperties(config.elasticUrl)))
    val resources = PlayerResources(new DefaultCustomersView(Players(elasticClient)))

    Http(system).bindAndHandle(resources, config.interface, config.port)

    system.log.info(s"Server online at at http://{}:{}/", config.interface, config.port)

    Await.result(system.whenTerminated, Duration.Inf)

  }
}