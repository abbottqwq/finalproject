package dao

import org.apache.spark.sql.DataFrame
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class CustomerSupportDAO @Inject()(sparkIns: SparkIns) extends DAO {
	override implicit val tableName: TableName = TableName("t_customer_support")
	override implicit val si: SparkIns = sparkIns

	def selectTimePeriod(): DataFrame = {
		sparkIns.loadRead()
			.option("query", s"SELECT MIN( to_date( created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ) ) as start_time," +
				s"MAX ( to_date( created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ) ) as end_time FROM t_customer_support")
			.load()
	}

	def selectAll(): DataFrame = {
		sparkIns.loadRead()
			.option("query", s"SELECT tt.tweets, COUNT ( tt.tweets ) AS freq " +
				s"FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id GROUP BY tt.tweets ORDER BY freq DESC")
			.load()
	}
}
