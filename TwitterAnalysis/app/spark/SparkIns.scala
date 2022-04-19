package spark

import org.apache.spark.sql.{DataFrame, SparkSession}
import play.api.Configuration

import javax.inject._
import utils.implicits.MyConfigLoader._

@Singleton
case class SparkIns @Inject()(config: Configuration) {
	def spark = SparkSession.builder()
		.appName(config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject"))
		.master(config.getStringOption("SPARK_MASTER").getOrElse("local[*]"))
		.getOrCreate()
	def stopSpark = spark.stop()
	def closeSpark = spark.close()

	def readTable(tableName: String): DataFrame = spark.read
		.format("jdbc")
		.option("driver", config.getStringOption("SDB.driver").getOrElse(""))
		.option("url", config.getStringOption("SDB.url").getOrElse(""))
		.option("user", config.getStringOption("SDB.user").getOrElse(""))
		.option("password", config.getStringOption("SDB.password").getOrElse(""))
		.option("dbtable", s"public.$tableName")
		.load()
}

