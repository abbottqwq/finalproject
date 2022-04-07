package controllers

import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.jdbc.JdbcProfile
import spark.SparkIns
import utils.MyToJson._
import utils.MyConfigLoader._

import javax.inject._
import scala.util._
import utils.MyConfigLoader._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TestController @Inject()(cc: ControllerComponents, config: Configuration, sparkIns: SparkIns, dbConfigProvider: DatabaseConfigProvider) extends AbstractController(cc) {

	def test(action: String) = Action {
		implicit request: Request[AnyContent] => {
			config.getStringOption("ENABLE_TEST_URI").getOrElse("0") match {
				case "0" => Forbidden("only for dev mode")
				case "1" =>
					action match {
						case "testconnect" => Ok(("Success" -> "1").toJson)
						case "testspark" => testSpark(sparkIns)
						case "testdatabase" => testDatabase(dbConfigProvider)
						case "closespark" =>
							Try(sparkIns.stopSpark) match {
								case Success(_) => Ok(("Success" -> "1").toJson)
								case Failure(_) => Ok(("Success" -> "0").toJson)
							}
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

	def testDatabase(dbConfigProvider: DatabaseConfigProvider): Result = {
		val dbConfig = dbConfigProvider.get[JdbcProfile]
		import dbConfig._
		import profile.api._
		Ok((Try(Await.result(db.run(sql"SELECT * FROM pg_catalog.pg_tables;".as[String]), Duration.Inf)) match {
			case Success(_) => Map("Success" -> "1")
			case Failure(f) => Map("Success" -> "0", "Error" -> "database connection fail", "Reason" -> f.toString)
		}).toJson)
	}
}
