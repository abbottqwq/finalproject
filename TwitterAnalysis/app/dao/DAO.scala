package dao

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}
import spark.SparkIns

trait DAO {
	//  def writeCustomerSupport(df: DataFrame): Unit
	//  def writeTweets(df: DataFrame): Unit
	//  def readByCompanyName(name: String, spark: SparkSession): DataFrame
	implicit val tableName: TableName
	implicit val si: SparkIns
	def add(dd: DataFrame)(implicit sparkIns: SparkIns): Unit = sparkIns.writeTable(dd).save()
	def get(idName: String, id: Any)(implicit sparkIns: SparkIns): DataFrame = sparkIns.readTable
		.filter(col(idName) === id)

}

case class TableName(tableName: String)

