package utils.implicits

import play.api.{ConfigLoader, Configuration}

import scala.util.Try

import MyTryToOption._

object MyConfigLoader {

	implicit class MyConfigLoader(config: Configuration) {
		def getStringOption(path: String): Option[String] = {
			Try(config.get(path)(ConfigLoader(_.getString)))
		}

		def getBoolOption(path: String): Option[Boolean] = {
			Try(config.get(path)(ConfigLoader(_.getBoolean)))
		}

		def getIntOption(path: String): Option[Int] = {
			Try(config.get(path)(ConfigLoader(_.getInt)))
		}

	}
}
