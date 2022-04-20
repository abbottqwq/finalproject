package dao

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}

class TweetImplDAO extends TweetDAO {

  override def writeCustomerSupport(df: DataFrame): Unit = {
    // Todo replace below options with correct config
    val base_df = df.select("tweet_id", "author_id", "created_at", "new_text")
    base_df.write
      .format("jdbc")
      .option("url", "jdbc:postgresql:postgres")
      .option("dbtable", "public.t_customer_support")
      .option("user", "postgres")
      .option("password", "")
      .save()
  }

  override def writeTweets(df: DataFrame): Unit = {
    // split the array of tweets to new rows, create a new dataframe
    // Todo replace below options with correct config
    val second_df = df.select(col("tweet_id").as("base_id"),
      explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
    second_df.write
      .format("jdbc")
      .option("url", "jdbc:postgresql:postgres")
      .option("dbtable", "public.t_tweets")
      .option("user", "postgres")
      .option("password", "")
      .save()
  }
}
