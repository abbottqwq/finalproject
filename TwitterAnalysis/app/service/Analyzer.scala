package service

import _root_.spark.SparkIns
import dao.{CustomerSupportDAO, TableName, TweetImplDAO}
import org.apache.spark.sql.functions.{col, explode, monotonically_increasing_id}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns, tweetImplDAO: TweetImplDAO, customerDAO: CustomerSupportDAO) extends AnalyzerBase {

	def init_data(): Unit = {
		val spark: SparkSession = sparkIns.spark
		val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv("resources/twcs.csv")

		val result = super.preprocessing(df)
		Future {
			// save to spark, make it non-blocking, use data when seeing output
			val t_customer_support = result.select("tweet_id", "author_id", "created_at", "new_text")
			t_customer_support.write.mode(SaveMode.Overwrite).saveAsTable("t_customer_support")
			val t_tweets = result.select(col("tweet_id").as("base_id"),
				explode(col("new_text")).as("tweets")).withColumn("id", monotonically_increasing_id())
			t_tweets.write.mode(SaveMode.Overwrite).saveAsTable("t_tweets")
			println("save to local spark-warehouse finished!")
			// save to postgres, the search efficiency is worse than local spark-warehouse
			//		sparkIns.writeTable(t_customer_support)(TableName("t_customer_support")).mode(SaveMode.Overwrite).save()
			//		sparkIns.writeTable(t_tweets)(TableName("t_tweet")).mode(SaveMode.Overwrite).save()
		}
	}

	def selectByWeekAndComp(name: String, limit: Int, offset: Int) = {
		tweetImplDAO.getByCompWeek(name, limit, offset).rdd.map(x => Map("week" -> x(0), "tweets" -> x(1), "freq" -> x(2))).collect()
	}

	def readByCompany(name: String, limit: Option[Int], offset: Option[Int]) = {
		val read_result: DataFrame = (limit, offset) match {
			case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByCompanyName(name))
			case _ => tweetImplDAO.readByCompanyName(name)
		}
		read_result.rdd.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

	def readByTime(start: String, end: String, limit: Option[Int], offset: Option[Int]) = {
		val read_result: DataFrame = (limit, offset) match {
			case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByTime(start, end))
			case _ => tweetImplDAO.readByTime(start, end)
		}
		read_result.rdd.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

	def readByTimeAndCompany(start: String, end: String, name: String, limit: Option[Int], offset: Option[Int]) = {
		val read_result: DataFrame = (limit, offset) match {
			case (Some(a), Some(b)) => limitNumber(a, b, tweetImplDAO.readByCompanyAndTime(name, start, end))
			case _ => tweetImplDAO.readByCompanyAndTime(name, start, end)
		}
		read_result.rdd.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

	def getCompanyName() = {
		val ti = tweetImplDAO.getTop20Company
		ti.rdd.map(x => Map("author_id" -> x(0).toString, "freq" -> x(1).toString)).collect()
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
		val read_result: DataFrame = (limit, offset) match {
			case (Some(a), Some(b)) => limitNumber(a, b, customerDAO.selectAll())
			case _ => customerDAO.selectAll()
		}
		//		read_result
		read_result.rdd.map(x => Map("tweets" -> x(0).toString, "freq" -> x(1).toString)).collect()
	}

}
