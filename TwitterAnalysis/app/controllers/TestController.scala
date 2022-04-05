package controllers

import play.api._
import play.api.mvc._
import spark.SparkIns
import utils.MyToJson._
import utils.MyConfigLoader._

import javax.inject._
import scala.util._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TestController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns) extends AbstractController(cc) {

	def index() = Action {
		implicit request: Request[AnyContent] => {
			Ok("hello world")
		}
	}

	def testConnection() = Action {
		implicit request: Request[AnyContent] => {
			Ok(("Success" -> 1).toJson)
		}
	}

	def testSpark() = Action {
		implicit request: Request[AnyContent] => {
			val res = Try(sparkIns.getSpark.sparkContext.appName) match {
				case Success(name) if name == config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject") => Map("Success" -> "1", "AppName" -> name)
				case Success(name) => Map("Success" -> "1", "Error" -> "AppName error", "AppName" -> name)
				case Failure(_) => Map("Success" -> "0", "Error" -> "connection fail")
			}
			Ok(res.toJson)
		}
	}

}
