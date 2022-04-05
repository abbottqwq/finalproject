package utils

import play.api.libs.json._

import scala.language.implicitConversions

object MyToJson {
	implicit class MyToJson[A](a: A) {
		def toJson(implicit tjs: Writes[A]) = {
			Json.toJson(a)
		}
	}
	
}
