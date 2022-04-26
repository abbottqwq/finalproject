package controllers

import dao.TableName
import play.api._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.mvc._
import service.Analyzer
import spark.SparkIns
import utils.implicits.MyConfigLoader._
import utils.implicits.MyToJson._

import javax.inject._
import scala.util._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TestController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns, analyzer: Analyzer) extends AbstractController(cc) {

	def test(action: String) = Action {
		implicit request: Request[AnyContent] => {
			config.getStringOption("ENABLE_TEST_URI").getOrElse("0") match {
				case "0" => Forbidden("only for dev mode")
				case "1" =>
					action match {
						case "testconnect" => Ok(Map("Success" -> "1").toJson)
						case "testspark" => testSpark(sparkIns)
						case "testdatabase" => testDatabase()
						case "closespark" =>
							Try(sparkIns.stopSpark) match {
								case Success(_) => Ok(Map("Success" -> "1").toJson)
								case Failure(f) => Ok(Map("Success" -> "0", "error" -> "stop spark fail", "reason" -> f.toString).toJson)
							}
						case "testpreprocess" => testPreProcess()
						case _ => NotFound("No Such Test")
					}
			}
		}
	}

	def testSpark(sparkIns: SparkIns): Result = {
		Ok((Try(sparkIns.spark.sessionState) match {
			case Success(_) =>
				Try(sparkIns.spark.sparkContext.appName) match {
					case Success(name) if name == config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject") => Map("Success" -> "1", "AppName" -> name)
					case Success(name) => Map("Success" -> "1", "Error" -> "AppName error", "AppName" -> name)
					case Failure(f) => Map("Success" -> "0", "Error" -> "Spark connection fail", "Reason" -> f.toString)
				}
			case Failure(f) => Map("Success" -> "0", "Error" -> "Spark connection fail", "Reason" -> f.toString)
		}).toJson)

	}

	def testDatabase(): Result = {
		Try(sparkIns.readTable(TableName("test")).show()) match {
			case Success(_) => Ok(("Success" -> "1").toJson)
			case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "Database connection fail", "Reason" -> f.toString).toJson)
		}

	}

	def testPreProcess(): Result = {
		Try(analyzer.testRun()) match {
			case Success(_) => Ok(("Success" -> "1").toJson)
			case Failure(f) => Ok(Map("Success" -> "0", "Error" -> "preprocess test fail", "Reason" -> f.toString).toJson)
		}
	}
}
