package service

import _root_.spark.SparkIns
import dao.TweetImplDAO
import org.apache.spark.sql.{DataFrame, SparkSession}

import javax.inject.{Inject, Singleton}

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns, tweetImplDAO: TweetImplDAO) extends AnalyzerBase {

  def testRun(): Unit = {
    val spark: SparkSession = sparkIns.spark
    val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv("resources/sample.csv")

    val start = System.currentTimeMillis()
    val result = super.preprocessing(df)
    val end = System.currentTimeMillis()
    val preprocess_time_duration = end - start
    println("preprocess_time_duration: " + preprocess_time_duration / 1000 + "s")

    // save base table to database
    tweetImplDAO.writeCustomerSupport(result)
    // save second table with preprocessed_tweets to database
    tweetImplDAO.writeTweets(result)

    val write_database_duration = System.currentTimeMillis() - end
    println("write_database_duration: " + write_database_duration / 1000 + "s")
  }

  def readByCompany(name: String) = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val read_result = tweetImplDAO.readByCompanyName(name)
    read_result.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
  }

  def readByTime(start: String, end: String) = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val read_result: DataFrame = tweetImplDAO.readByTime(start, end)
    //read_result.as[TweetTimeResult].collect()
    read_result.map(x => Map("tweets" -> x(0).toString, "time_to_month" -> x(1).toString, "freq" -> x(2).toString)).collect()
  }

  def readByTimeAndCompany(start: String, end: String, name: String) = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val read_result: DataFrame = tweetImplDAO.readByCompanyAndTime(name, start, end)
    //read_result.as[TweetTimeResult].collect()
    read_result.map(x => Map("tweets" -> x(0).toString, "time_to_month" -> x(1).toString, "author_id" -> x(2).toString, "freq" -> x(3).toString)).collect()
  }

	def getCompanyName() = {
		super.company_list
	}
}
