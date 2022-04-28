package dao

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class CustomerSupportDAO @Inject()(sparkIns: SparkIns) extends DAO {
	override implicit val tableName: TableName = TableName("t_customer_support")
	override implicit val si: SparkIns = sparkIns

	sparkIns.spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")

	def selectTimePeriod(): DataFrame = {
		val df_cs = sparkIns.spark.table("t_customer_support")
		df_cs
			.agg(min(to_date(col("created_at"), "E MMM dd HH:mm:ss Z yyyy")) as "start_time"
				, max(to_date(col("created_at"), "E MMM dd HH:mm:ss Z yyyy")) as "end_time")
			.toDF()
	}

	def selectAll(): DataFrame = {
		val df_tweet = sparkIns.spark.table("t_tweets")
		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.groupBy("tweets")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}
}
