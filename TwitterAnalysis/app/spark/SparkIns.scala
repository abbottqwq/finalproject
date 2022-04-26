package spark

import dao.TableName
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row, SaveMode, SparkSession}
import play.api.Configuration

import javax.inject._
import utils.implicits.MyConfigLoader._

@Singleton
case class SparkIns @Inject()(config: Configuration) {


	def spark =
		SparkSession.builder()
			.appName(config.getStringOption("SPARK_APP_NAME").getOrElse("finalproject"))
			.master(config.getStringOption("SPARK_MASTER").getOrElse("local[*]"))
			.getOrCreate()


	def stopSpark = spark.stop()
	def closeSpark = spark.close()

	def getTable(implicit tableName: TableName): DataFrameReader =
		loadRead()
			.option("dbtable", s"public.${tableName.tableName}")

	def readTable(implicit tableName: TableName): DataFrame = getTable(tableName).load()

	def writeTable(df: DataFrame)(implicit tableName: TableName) =
		loadWrite(df).option("dbtable", s"public.${tableName.tableName}")

	def loadRead() = spark.read
		.format("jdbc")
		.option("driver", config.getStringOption("SDB.driver").getOrElse(""))
		.option("url", config.getStringOption("SDB.url").getOrElse(""))
		.option("user", config.getStringOption("SDB.user").getOrElse(""))
		.option("password", config.getStringOption("SDB.password").getOrElse(""))

	def loadWrite(df: DataFrame) = df.write
		.format("jdbc")
		.option("driver", config.getStringOption("SDB.driver").getOrElse(""))
		.option("url", config.getStringOption("SDB.url").getOrElse(""))
		.option("user", config.getStringOption("SDB.user").getOrElse(""))
		.option("password", config.getStringOption("SDB.password").getOrElse(""))

	def loadTest() = {
		val test = spark.createDataFrame(Seq(("test", 1)))
		writeTable(test)(TableName("test")).mode(SaveMode.Overwrite)
	}

}
