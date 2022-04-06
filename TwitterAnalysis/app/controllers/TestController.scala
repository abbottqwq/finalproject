package controllers

import play.api._
import play.api.mvc._
import spark.SparkIns
import utils.MyToJson._
import utils.MyConfigLoader._

import javax.inject._
import scala.util._

import utils.MyConfigLoader._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TestController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns) extends AbstractController(cc) {

	def test(action: String) = Action {
		implicit request: Request[AnyContent] => {
			config.getStringOption("ENABLE_TEST_URI").getOrElse("0") match {
				case "0" => Forbidden("only for dev mode")
				case "1" =>
					action match {
						case "testconnect" => Ok(("Success" -> "1").toJson)
						case "testspark" => testSpark(sparkIns)
						case "closespark" => {
							Try(sparkIns.stopSpark) match {
								case Success(_) => Ok(("Success" -> "1").toJson)
								case Failure(_) => Ok(("Success" -> "0").toJson)
							}
						}
						case _ => NotFound("No Such Test")
					}
			}
		}
	}

	def testSpark(sparkIns: SparkIns): Result = {
		val stateMap = {
			Try(sparkIns.getSpark.sessionState) match {
				case Success(_) => {
					Try(sparkIns.getSpark.sparkContext.appName) match {
						case Success(name) if name == config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject") => Map("Success" -> "1", "AppName" -> name)
						case Success(name) => Map("Success" -> "1", "Error" -> "AppName error", "AppName" -> name)
						case Failure(_) => Map("Success" -> "0", "Error" -> "connection fail")
					}
				}
				case Failure(_) => Map("Success" -> "0", "Error" -> "connection fail")
			}
		}
		Ok(stateMap.toJson)
	}


}
