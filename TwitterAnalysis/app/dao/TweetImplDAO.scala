package dao

import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

class TweetImplDAO extends TweetDAO {

  // Todo replace below options of these methods with correct config
  override def writeCustomerSupport(df: DataFrame): Unit = {
    val base_df = df.select("tweet_id", "author_id", "created_at", "new_text")
    base_df.write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql:postgres")
      .option("dbtable", "public.t_customer_support")
      .option("user", "postgres")
      .option("password", "")
      .save()
  }

  override def writeTweets(df: DataFrame): Unit = {
    // split the array of tweets to new rows, create a new dataframe
    val second_df = df.select(col("tweet_id").as("base_id"),
      explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
    second_df.write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql:postgres")
      .option("dbtable", "public.t_tweets")
      .option("user", "postgres")
      .option("password", "")
      .save()
  }

  /**
   * get keywords from specific company sort by frequency
   * @param name
   * @param spark
   * @return
   */
  override def readByCompanyName(name: String, spark: SparkSession): DataFrame = {
    spark.read
      .format("jdbc")
      .option("url", "jdbc:postgresql:postgres")
      .option("query", s"SELECT tt.tweets, COUNT ( 1 ) AS freq FROM t_customer_support tcs " +
        s"LEFT JOIN t_tweets tt ON tcs.tweet_id = tt.base_id WHERE tcs.author_id = '$name' GROUP BY tt.tweets " +
        "ORDER BY COUNT ( 1 ) DESC LIMIT 10")
      .option("user", "postgres")
      .option("password", "")
      .load()
  }
}
