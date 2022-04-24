package dao

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col
import spark.SparkIns

trait DAO {

	implicit val tableName: TableName
	implicit val si: SparkIns
	def add(dd: DataFrame)(implicit sparkIns: SparkIns): Unit = sparkIns.writeTable(dd).save()
	def get(idName: String, id: Any)(implicit sparkIns: SparkIns): DataFrame = sparkIns.readTable
		.filter(col(idName) === id)

}

case class TableName(tableName: String)

