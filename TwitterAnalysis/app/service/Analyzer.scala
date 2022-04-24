package service

import _root_.spark.SparkIns
import dao.TweetImplDAO
import model.TweetResult
import org.apache.spark.sql.{DataFrame, SparkSession}

import javax.inject.{Inject, Singleton}

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns, tweetImplDAO: TweetImplDAO) extends AnalyzerBase {

	def testRun(): Unit = {
		val spark: SparkSession = sparkIns.spark
		val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv("resources/sample.csv")
		val result = super.preprocessing(df)
		// save base table to database
		tweetImplDAO.writeCustomerSupport(result)
		// save second table with preprocessed_tweets to database
		tweetImplDAO.writeTweets(result)
	}

	def readByCompany(name: String)= {
		val spark: SparkSession = sparkIns.spark
		import spark.implicits._
		val read_result = tweetImplDAO.readByCompanyName("name")
		read_result.dtypes
		read_result.as[TweetResult].collect()
	}
}
