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

	def selectByCompanyName() = Action(parse.json) {
		implicit request => {
			request.body match {
				case JsObject(fields) =>
					fields.get("name") match {
						case Some(named) =>
							Try({
								val company_name = named.as[String]
								analyzer.readByCompany(company_name, fields.get("limit").flatMap(x => x.asOpt[Int]),
									fields.get("offset").flatMap(x => x.asOpt[Int]))
							}) match {
								case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
								case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
							}
						case None => BadRequest("missing name")
					}
				case _ => BadRequest("data error")
			}
		}
	}

	def selectByTime() = Action(parse.json) {
		implicit request => {
			request.body match {
				case JsObject(fields) =>
					(fields.get("start"),fields.get("end")) match {
						case (Some(start0), Some(end0)) =>
							Try({
								val start_time = start0.as[String]
								val end_time = end0.as[String]
								analyzer.readByTime(start_time, end_time, fields.get("limit").flatMap(x => x.asOpt[Int]),
									fields.get("offset").flatMap(x => x.asOpt[Int]))
							}) match {
								case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
								case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
							}
						case (Some(_), None) => BadRequest("missing end")
						case (None, Some(_)) => BadRequest("missing start")
						case _ => BadRequest("missing start and end")
					}
				case _ => BadRequest("data error")
			}
		}
	}

	def selectByTimeAndCompany() = Action(parse.json) {
		implicit request => {
			request.body match {
				case JsObject(fields) =>
					(fields.get("start"),fields.get("end"), fields.get("name")) match {
						case (Some(start0), Some(end0), Some(name0)) =>
							Try({
								val start_time = start0.as[String]
								val end_time = end0.as[String]
								val named = name0.as[String]
								analyzer.readByTimeAndCompany(start_time, end_time,named, fields.get("limit").flatMap(x => x.asOpt[Int]),
									fields.get("offset").flatMap(x => x.asOpt[Int]))
							}) match {
								case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
								case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
							}
						case (_, _, None) => BadRequest("missing name")
						case (None, _, _) => BadRequest("missing start")
						case (_, None, _) => BadRequest("missing end")
						case _ => BadRequest("missing data")
					}
				case _ => BadRequest("data error")
			}
		}
	}

	def selectCompanyNames() = Action {
		implicit request: Request[AnyContent] => {
			val result = analyzer.getCompanyName()
			Try(result) match {
				case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
				case Failure(f) => BadRequest(Map("Success" -> "0", "Error" -> "get company name fail", "Reason" -> f.toString).toJson)
			}
		}
	}

	def selectTimePeriod() = Action {
		implicit request: Request[AnyContent] => {
			val result = analyzer.selectTimePeriod()
			Try(result) match {
				case Success(result) => Ok(Map("Success" -> "1", "Start_Date" -> result._1, "End_Date" -> result._2).toJson)
				case Failure(f) => BadRequest(Map("Success" -> "0", "Error" -> "get company name fail", "Reason" -> f.toString).toJson)
			}
		}
	}

	def selectAll() = Action {
		implicit request => {
			request.body.asJson match {
				case Some(x) =>
					Try({
						analyzer.selectAll((x \ "limit").asOpt[Int], (x \ "offset").asOpt[Int])
					}) match {
						case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
						case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
					}
				case None =>
					Try({
						analyzer.selectAll(None, None)
					}) match {
						case Success(result) => Ok(Json.obj("Success" -> "1", "Data" -> result))
						case Failure(f) => InternalServerError(ErrorReturn("get data fail", f).toJson)
					}
			}
		}
	}
}
