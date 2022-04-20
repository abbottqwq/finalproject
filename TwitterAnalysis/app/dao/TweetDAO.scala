package dao

import org.apache.spark.sql.DataFrame

trait TweetDAO {

  def writeCustomerSupport(df: DataFrame): Unit

  def writeTweets(df: DataFrame): Unit

}
