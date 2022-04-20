package dao

import org.apache.spark.sql.{DataFrame, SparkSession}

trait TweetDAO {

  def writeCustomerSupport(df: DataFrame): Unit

  def writeTweets(df: DataFrame): Unit

  def readByCompanyName(name: String, spark: SparkSession): DataFrame

}
