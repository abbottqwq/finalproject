package utils.helper

import play.api.mvc._
import utils.implicits.MyToJson._

case class ErrorReturn(error: String, reason: Any = None) {
	def toJson = {
		reason match {
			case None => Map("Success" -> "0", "Error" -> error).toJson
			case r => Map("Success" -> "0", "Error" -> error, "Reason" -> r.toString).toJson
		}
	}
}
