package utils

import utils.MyTryToOption._
import play.api._

import scala.language.implicitConversions
import scala.util.Try

object MyConfigLoader {

	implicit class MyConfigLoader(config: Configuration) {
		def getStringOption(path: String): Option[String] = {
			Try(config.get(path)(ConfigLoader(_.getString)))
		}
	}
}


