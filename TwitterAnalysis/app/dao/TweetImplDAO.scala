package dao

import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SaveMode}
import spark.SparkIns

import javax.inject.{Inject, Singleton}

@Singleton
class TweetImplDAO @Inject()(sparkIns: SparkIns) extends DAO {

	override implicit val tableName: TableName = TableName("t_tweets")
	override implicit val si = sparkIns
	def writeCustomerSupport(df: DataFrame): Unit = {
		val base_df = df.select("tweet_id", "author_id", "created_at", "new_text")
		sparkIns.writeTable(base_df)(TableName("t_customer_support")).mode(SaveMode.Overwrite).save()
	}

	def writeTweets(df: DataFrame): Unit = {
		// split the array of tweets to new rows, create a new dataframe
		val second_df = df.select(col("tweet_id").as("base_id"),
			explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
		sparkIns.writeTable(second_df).mode(SaveMode.Overwrite).save()
	}

	/**
	 * get keywords from specific company sort by frequency
	 */
	def readByCompanyName(name: String): DataFrame = {
		sparkIns.loadRead()
			.option("query", s"SELECT tt.tweets,to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) AS time_to_month," +
				s"COUNT ( tt.tweets ) AS freq FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
				s"WHERE tcs.author_id = '${name}' GROUP BY " +
				s"to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) ,tt.tweets " +
				s"ORDER BY to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ), freq desc")
			.load()
	}

	/**
	 * get keywords from a time period sort by frequency
	 */
	def readByTime(start: String, end: String): DataFrame = {
		sparkIns.loadRead()
			.option("query", s"SELECT tt.tweets,to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) AS time_to_month," +
							s"tcs.author_id," +
							s"COUNT ( tt.tweets ) AS freq FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
							s"WHERE to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ) BETWEEN  '${start}' and '${end}' GROUP BY " +
							s"to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) , tcs.author_id, tt.tweets " +
							s"ORDER BY to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ), tcs.author_id, freq desc")
			.load()
	}

	/**
	 * get keywords from specific company sort and time period sort by frequency
	 */
	def readByCompanyAndTime(name: String, start: String, end: String): DataFrame = {
		sparkIns.loadRead()
			.option("query", s"SELECT tt.tweets, to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) AS time_to_month, " +
				s"tcs.author_id, COUNT ( tt.tweets ) AS freq " +
				s"FROM t_customer_support tcs LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id " +
				s"WHERE tcs.author_id = '${name}' AND " +
				s"to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ) BETWEEN  '${start}' and '${end}' GROUP BY " +
				s"to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ) , tcs.author_id, tt.tweets " +
				s"ORDER BY to_char(to_date( tcs.created_at, 'Dy Mon dd HH24:MI:SS +ZZZZ yyyy' ),'YYYY-MM' ), tcs.author_id, freq desc")
			.load()
	}


}
