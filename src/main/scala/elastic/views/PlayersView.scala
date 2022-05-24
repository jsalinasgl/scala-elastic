package elastic.views

import elastic.collections.Players
import elastic.entities.Player

import scala.concurrent.{ExecutionContext, Future}

trait CustomersView {
  def find(id: String)(implicit ec: ExecutionContext): Future[Option[Player]]
  def findByFilters(name: Option[String],
                    club: Option[String],
                    overall: Option[Int],
                    maybePage:      Option[Int],
                    maybePageSize:  Option[Int])
                   (implicit ec: ExecutionContext): Future[Iterable[Player]]
}

class DefaultCustomersView(collection: Players) extends CustomersView {
  override def find(id: String)(implicit ec: ExecutionContext): Future[Option[Player]] =
    collection.find(id)

  override def findByFilters(name: Option[String],
                             club: Option[String],
                             overall: Option[Int],
                             maybePage: Option[Int],
                             maybePageSize: Option[Int])
                            (implicit ec: ExecutionContext): Future[Iterable[Player]] =
    collection.findByFilters(name, club, overall, maybePage, maybePageSize)
}
