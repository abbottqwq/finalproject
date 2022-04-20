package service

import _root_.spark.SparkIns
import dao.{TweetDAO, TweetImplDAO}
import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SparkSession}

import javax.inject.{Inject, Singleton}

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns) extends AnalyzerBase {

  def run(sparkIns: SparkIns): Unit = {
    val spark: SparkSession = sparkIns.spark
    val path: String = getClass.getResource("/twcs.csv").getPath
    val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv(path)
    val result = super.preprocessing(df)
    // save base table to database
    val tweetDao: TweetDAO = new TweetImplDAO()
    tweetDao.writeCustomerSupport(result)
    // save second table with preprocessed_tweets to database
    tweetDao.writeTweets(result)
  }
}

// for quick test
object Analyzer extends AnalyzerBase with App {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("TwitterAnalysis")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
  val path: String = getClass.getResource("/sample.csv").getPath
  val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
  val result = super.preprocessing(df)

  val base_df = result.select("tweet_id", "author_id", "created_at", "new_text")
  base_df.show(10, truncate = false)
  val second_df = base_df.select(col("tweet_id").as("base_id"),
    explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
  second_df.show(20, false)

  // insert into database
//  val tweetDao: TweetDAO = new TweetImplDAO()
//  tweetDao.writeCustomerSupport(result)
//  tweetDao.writeTweets(result)
}
