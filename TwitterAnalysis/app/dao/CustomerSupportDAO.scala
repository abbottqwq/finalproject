package dao

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.col
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class CustomerSupportDAO @Inject()(sparkIns: SparkIns) extends DAO {
	override implicit val tableName: TableName = TableName("t_customer_support")
	override implicit val si: SparkIns = sparkIns
}
