package controllers

import play.api._
import play.api.mvc._
import spark.SparkIns
import utils.implicits.MyToJson._
import utils.implicits.MyConfigLoader._

import javax.inject._
import scala.util._

class HomeController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns) extends AbstractController(cc) {
	def index() = Action {
		implicit request: Request[AnyContent] => {
			// Ok(s"${config.getStringOption("PGURL").getOrElse("notfound")}")
			Ok("hello world!")
		}
	}
}
