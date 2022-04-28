package dao

import org.apache.spark.sql.DataFrame
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class CustomerSupportDAO @Inject()(sparkIns: SparkIns) extends DAO {
	override implicit val tableName: TableName = TableName("t_customer_support")
	override implicit val si: SparkIns = sparkIns

	sparkIns.spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")

	def selectTimePeriod(): DataFrame = {
		sparkIns.spark.sql(s"SELECT MIN( to_date( created_at, 'E MMM dd HH:mm:ss Z yyyy' ) ) as start_time," +
				s"MAX ( to_date( created_at, 'E MMM dd HH:mm:ss Z yyyy' ) ) as end_time FROM t_customer_support")
	}

	def selectAll(): DataFrame = {
		sparkIns.spark.sql(s"SELECT tt.tweets, COUNT ( tt.tweets ) AS freq " +
				s"FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id GROUP BY tt.tweets ORDER BY freq DESC")
	}
}
