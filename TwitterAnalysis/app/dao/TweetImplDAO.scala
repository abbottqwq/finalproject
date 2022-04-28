package dao

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class TweetImplDAO @Inject()(sparkIns: SparkIns) extends DAO {

	override implicit val tableName: TableName = TableName("t_tweets")
	override implicit val si = sparkIns

	sparkIns.spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
//	val df_tweet = sparkIns.spark.table("t_tweets")
//	val df_cs = sparkIns.spark.table("t_customer_support")

	def writeCustomerSupport(df: DataFrame): Unit = {
		val base_df = df.select("tweet_id", "author_id", "created_at", "new_text")
		sparkIns.writeTable(base_df)(TableName("t_customer_support")).mode(SaveMode.Overwrite).save()
		base_df.createOrReplaceTempView("t_customer_support")
	}

	def writeTweets(df: DataFrame): Unit = {
		// split the array of tweets to new rows, create a new dataframe
		val second_df = df.select(col("tweet_id").as("base_id"),
			explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
		sparkIns.writeTable(second_df).mode(SaveMode.Overwrite).save()
		second_df.createOrReplaceTempView("t_tweets")
	}

	/**
	 * get keywords from specific company sort by frequency
	 */
	def readByCompanyName(name: String): DataFrame = {
		sparkIns.spark.sql(s"SELECT tt.tweets, COUNT ( tt.tweets ) AS freq " +
				s"FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
				s"WHERE tcs.author_id = '${name}' GROUP BY tt.tweets ORDER BY freq DESC")
	}

	/**
	 * get keywords from a time period sort by frequency
	 */
	def readByTime(start: String, end: String): DataFrame = {
//		sparkIns.spark.sql(s"SELECT tt.tweets, " +
//									s"COUNT ( tt.tweets ) AS freq FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
//									s"WHERE to_date( tcs.created_at, 'E MMM dd HH:mm:ss Z yyyy' ) >= '${start}' " +
//									s"and to_date( tcs.created_at, 'E MMM dd HH:mm:ss Z yyyy' ) < '${end}' GROUP BY " +
//									s"tt.tweets " +
//									s"ORDER BY freq desc")

		val df_tweet = sparkIns.spark.table("t_tweets")
		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.where(to_date(col("created_at"), "E MMM dd HH:mm:ss Z yyyy").between(start, end))
			.groupBy("tweets")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}

	/**
	 * get keywords from specific company sort and time period sort by frequency
	 */
	def readByCompanyAndTime(name: String, start: String, end: String): DataFrame = {
		sparkIns.spark.sql(s"SELECT tt.tweets, " +
							s"COUNT ( tt.tweets ) AS freq " +
							s"FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
							s"WHERE tcs.author_id = '${name}' AND " +
							s"to_date( tcs.created_at, 'E MMM dd HH:mm:ss Z yyyy' )  >= '${start}' " +
							s"and to_date( tcs.created_at, 'E MMM dd HH:mm:ss Z yyyy' ) < '${end}' GROUP BY " +
							s"tt.tweets " +
							s"ORDER BY freq desc")
	}


}
