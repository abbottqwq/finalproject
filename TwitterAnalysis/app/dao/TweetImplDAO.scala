package dao

import org.apache.spark.sql.catalog.Database
import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import spark.SparkIns



import javax.inject.{Inject, Singleton}

@Singleton
class TweetImplDAO @Inject()(sparkIns: SparkIns) extends DAO {

	override implicit val tableName: TableName = TableName("t_tweets")
	override implicit val si = sparkIns
	// Todo replace below options of these methods with correct config
	def writeCustomerSupport(df: DataFrame): Unit = {
		val base_df = df.select("tweet_id", "author_id", "created_at", "new_text")
		//		base_df.write
		//			.format("jdbc")
		//			.mode(SaveMode.Overwrite)
		//			.option("url", "jdbc:postgresql:postgres")
		//			.option("dbtable", "public.t_customer_support")
		//			.option("user", "postgres")
		//			.option("password", "")
		//			.save()
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
			.option("query", s"SELECT tt.tweets, COUNT ( 1 ) AS freq FROM t_customer_support tcs " +
				s"LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id WHERE tcs.author_id = '$name' GROUP BY tt.tweets " +
				"ORDER BY COUNT ( 1 ) DESC LIMIT 10")
			.load()
	}


}
