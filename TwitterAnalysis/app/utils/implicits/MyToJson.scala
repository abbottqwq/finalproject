package utils.implicits

import play.api.libs.json.{Json, Writes}

object MyToJson {
	implicit class MyToJson[A](a: A) {
		def toJson(implicit tjs: Writes[A]) = {
			Json.toJson(a)
		}
	}
}
