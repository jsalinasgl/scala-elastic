package elastic.resources

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import elastic.json.JsonFormat._
import elastic.views.CustomersView
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

object PlayerResources {
  def apply(view: CustomersView)(implicit ec: ExecutionContext): Route = {

    val parameters = parameter("name".?) &
      parameter("club".?) &
      parameter("overall".as[Int].?) &
      parameter("page".as[Int].?) &
      parameter("size".as[Int].?)

    get {
      pathPrefix("players") {
        (pathEndOrSingleSlash & parameters) { (maybeName, maybeClub, maybeOverall, maybePage, maybeSize) =>
          complete(view.findByFilters(maybeName, maybeClub, maybeOverall, maybePage, maybeSize))
        } ~
        pathPrefix(Segment) { id =>
          complete(view.find(id))
        }
      }
    }
  }
}
