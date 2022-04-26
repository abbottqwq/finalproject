package controllers

import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import service.Analyzer
import spark.SparkIns
import utils.implicits.MyToJson._

import javax.inject._
import scala.util._

class HomeController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns, analyzer: Analyzer) extends AbstractController(cc) {
  def index() = Action {
    implicit request: Request[AnyContent] => {
      // Ok(s"${config.getStringOption("PGURL").getOrElse("notfound")}")
      Ok("hello world!")
    }
  }

  def initData() = Action {
    implicit request: Request[AnyContent] => {
      Try(analyzer.testRun()) match {
        case Success(_) => Ok(Map("Success" -> "1").toJson)
        case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
      }
    }
  }

  def selectByCompanyName(name: String) = Action {
    implicit request: Request[AnyContent] => {
      val result = analyzer.readByCompany(name)
      Try(result) match {
        case Success(_) => Ok(Json.obj("Success" -> "0", "Data" -> result))
        case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
      }
    }
  }

  def selectByTime(start: String, end: String) = Action {
    implicit request: Request[AnyContent] => {
      val result = analyzer.readByTime(start, end)
      Try(result) match {
        case Success(_) => Ok(Json.obj("Success" -> "0", "Data" -> result))
        case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
      }
    }
  }

  def selectByTimeAndCompany(start: String, end: String, name: String) = Action {
    implicit request: Request[AnyContent] => {
      val result = analyzer.readByTimeAndCompany(start, end, name)
      Try(result) match {
        case Success(_) => Ok(Json.obj("Success" -> "0", "Data" -> result))
        case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
      }
    }
  }
}
