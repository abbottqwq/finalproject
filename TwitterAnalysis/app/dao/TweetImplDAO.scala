package dao

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}
import spark.SparkIns
import org.apache.spark.sql.expressions.Window
import utils.implicits.TimeConvert.StringTimeConversions

import javax.inject.{Inject, Singleton}

@Singleton
class TweetImplDAO @Inject()(sparkIns: SparkIns) extends DAO {

	override implicit val tableName: TableName = TableName("t_tweets")
	override implicit val si = sparkIns

	sparkIns.spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
	lazy val df_tweet = sparkIns.spark.table("t_tweets")
	lazy val df_cs = sparkIns.spark.table("t_customer_support")

	val fmt = "E MMM dd HH:mm:ss Z yyyy"

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
		//		val df_tweet = sparkIns.spark.table("t_tweets")
		//		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.where(col("author_id") === name)
			.groupBy("tweets")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}

	/**
	 * get keywords from a time period sort by frequency
	 */
	def readByTime(start: String, end: String): DataFrame = {
		//		val df_tweet = sparkIns.spark.table("t_tweets")
		//		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.where(to_date(col("created_at"), fmt).between(start, end))
			.groupBy("tweets")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}

	/**
	 * get keywords from specific company sort and time period sort by frequency
	 */
	def readByCompanyAndTime(name: String, start: String, end: String): DataFrame = {
		//		val df_tweet = sparkIns.spark.table("t_tweets")
		//		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.where(df_cs("author_id") === name && to_date(col("created_at"), fmt).between(start, end))
			.groupBy("tweets")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}

	/**
	 * get Top 20 Company
	 */
	def getTop20Company: DataFrame = {
		//		val df_tweet = sparkIns.spark.table("t_tweets")
		//		val df_cs = sparkIns.spark.table("t_customer_support")
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.groupBy("author_id")
			.agg(count("*") as "freq")
			.orderBy(col("freq").desc)
			.toDF()
	}

	def getByCompWeek(name: String, limit: Int, offset: Int) = {
		df_tweet
			.join(df_cs, df_tweet.col("base_id") === df_cs.col("tweet_id"), "left_outer")
			.select(col("id"), col("tweets"), col("created_at"), col("author_id"))
			.select(col("id"), dayofweek(to_date(col("created_at"), fmt)) as "week", col("tweets"))
			.where(col("author_id") === name)
			.groupBy(col("week"), col("tweets"))
			.agg(count("*") as "freq")
			.select(col("*"), row_number() over Window.partitionBy("week").orderBy(col("freq").desc) as "rank")
			.where(col("rank") > offset and col("rank") <= offset + limit)
			.toDF()
	}

}
