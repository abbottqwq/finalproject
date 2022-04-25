package controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import service.Analyzer
import utils.helper.ErrorReturn
import utils.implicits.MyToJson._

import javax.inject._
import scala.language.postfixOps
import scala.util._

class HomeController @Inject()(cc: ControllerComponents, analyzer: Analyzer) extends AbstractController(cc) {
	def index() = Action {
		implicit request: Request[AnyContent] => {
			Ok("hello world!")
		}
	}

	def initData() = Action {
		implicit request: Request[AnyContent] => {
			Try(analyzer.testRun()) match {
				case Success(_) => Ok(Map("Success" -> "1").toJson)
				case Failure(f) => InternalServerError(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
			}
		}
	}

	def selectByCompanyName(name: String) = Action {
		implicit request: Request[AnyContent] => {
			Try(analyzer.readByCompany(name)) match {
				case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
				case Failure(f) => BadRequest(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
			}
		}
	}

	def selectByTime() = Action(parse.json) {
		implicit request => {
			request.body match {
				case JsObject(fields) =>
					(fields.get("start"), fields.get("end"), fields.get("name")) match {
						case (Some(startJS), Some(endJS), nameO) =>
							val name = nameO match {
								case Some(n) => Option(n.as[String])
								case None => None
							}
							Try({
								val start = startJS.as[String]
								val end = endJS.as[String]
								println(start, end)
								analyzer.readByTime(start, end, name)
							}) match {
								case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
								case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
							}
						case (None, Some(_), _) => BadRequest("missing start")
						case (Some(_), None, _) => BadRequest("missing end")
						case _ => BadRequest("missing data")
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
				case _ => BadRequest("data error")
			}
		}
	}

	def selectCompanyNames() = Action {
		implicit request: Request[AnyContent] => {
			Try(analyzer.getCompanyName()) match {
				case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
				case Failure(f) => BadRequest(Map("Success" -> "0", "Error" -> "get company name fail", "Reason" -> f.toString).toJson)
			}
		}
	}
}
