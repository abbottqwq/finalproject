package spark

import org.apache.spark.sql.SparkSession
import play.api.Configuration

import javax.inject._

import utils.MyConfigLoader._

@Singleton
case class SparkIns @Inject()(config: Configuration) {
	val spark = SparkSession.builder()
		.appName(config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject"))
		.master(config.getStringOption("SPARK_MASTER").getOrElse("local[*]"))
		.getOrCreate()
	def getSpark = spark
}

