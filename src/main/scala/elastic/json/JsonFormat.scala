package elastic.json

import elastic.entities.Player
import spray.json.DefaultJsonProtocol

object JsonFormat {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat10(Player)
}