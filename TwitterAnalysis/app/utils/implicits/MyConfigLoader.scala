package utils.implicits

import play.api.{ConfigLoader, Configuration}

import scala.util.Try

import MyTryToOption._

object MyConfigLoader {

	implicit class MyConfigLoader(config: Configuration) {
		def getStringOption(path: String): Option[String] = {
			Try(config.get(path)(ConfigLoader(_.getString)))
		}
	}
}
