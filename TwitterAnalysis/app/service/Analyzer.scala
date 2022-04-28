package service

import _root_.spark.SparkIns
import dao.{CustomerSupportDAO, TweetImplDAO}
import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SparkSession}

import javax.inject.{Inject, Singleton}

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns, tweetImplDAO: TweetImplDAO, customerDAO: CustomerSupportDAO) extends AnalyzerBase {

  def init_data(): Unit = {
    val spark: SparkSession = sparkIns.spark
    val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv("resources/sample.csv")

    val result = super.preprocessing(df)
    // save to spark
    val base_df = result.select("tweet_id", "author_id", "created_at", "new_text")
    base_df.createOrReplaceTempView("t_customer_support")
    val second_df = result.select(col("tweet_id").as("base_id"),
      explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
    second_df.createOrReplaceTempView("t_tweets")

    // save base table to database
//    tweetImplDAO.writeCustomerSupport(result)
    // save second table with preprocessed_tweets to database
//    tweetImplDAO.writeTweets(result)

  }

  def readByCompany(name: String, limit: Option[Int], offset: Option[Int]) = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val read_result: DataFrame = (limit, offset) match {
      case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByCompanyName(name))
      case _ => tweetImplDAO.readByCompanyName(name)
    }
    read_result.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
  }

	def readByTime(start: String, end: String, limit: Option[Int], offset: Option[Int]) = {
		val spark: SparkSession = sparkIns.spark
		import spark.implicits._
    val read_result: DataFrame = (limit, offset) match {
      case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByTime(start, end))
      case _ => tweetImplDAO.readByTime(start, end)
    }
    read_result.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

	def readByTimeAndCompany(start: String, end: String, name: String, limit: Option[Int], offset: Option[Int]) = {
		val spark: SparkSession = sparkIns.spark
		import spark.implicits._
    val read_result: DataFrame = (limit, offset) match {
      case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByCompanyAndTime(name, start, end))
      case _ => tweetImplDAO.readByCompanyAndTime(name, start, end)
    }
    read_result.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

  def getCompanyName() = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val ti = tweetImplDAO.getTop20Company
    ti.map(x => Map("author_id" -> x(0).toString, "freq" -> x(1).toString)).collect()
  }

  def selectTimePeriod() = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val df = customerDAO.selectTimePeriod()
    val start = df.select("start_time").as[String].collect()(0)
    val end = df.select("end_time").as[String].collect()(0)
    (start, end)
  }

  def selectAll(limit: Option[Int], offset: Option[Int]) = {
    val spark: SparkSession = sparkIns.spark
    import spark.implicits._
    val read_result: DataFrame = (limit, offset) match {
      case (Some(a), Some(b)) => limitNumber(a, b, customerDAO.selectAll())
      case _ => customerDAO.selectAll()
    }
    read_result.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
  }

}
