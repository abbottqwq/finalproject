import dao.TweetImplDAO
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import service.AnalyzerBase
import spark.SparkIns

import scala.util.{Success, Try}

class TweetDAOSpec extends PlaySpec with BeforeAndAfter {

	implicit var config: Configuration = _
	implicit var sparkIns: SparkIns = _

	before {

		config = Configuration("isLocalTest" -> true,
			"SDB.driver" -> "org.postgresql.Driver",
			"SDB.url" -> "jdbc:postgresql:postgres",
			"SDB.user" -> "postgres",
			"SDB.password" -> "postgres",
			"SPARK_MASTER" -> "local[*]",
			"SPARK_APP_NAME" -> "PostgreSQL 14"
		)

		sparkIns = SparkIns(config)

	}

	"tweetDao" must {
		"write_customer_support" in {
			val ab = new AnalyzerBase()
			val path: String = getClass.getResource("/sample.csv").getPath
			val df = sparkIns.spark.read.option("delimiter", ",").option("header", "true").csv(path)
			val result = ab.preprocessing(df)
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			tweetImplDAO.writeCustomerSupport(result)
			Try(tweetImplDAO.writeCustomerSupport(result)) mustBe a[Success[_]]
		}

		"write_tweets" in {
			val ab = new AnalyzerBase()
			val path: String = getClass.getResource("/sample.csv").getPath
			val df = sparkIns.spark.read.option("delimiter", ",").option("header", "true").csv(path)
			val result = ab.preprocessing(df)

			val tweetImplDAO = new TweetImplDAO(sparkIns)
			tweetImplDAO.writeTweets(result)
			Try(tweetImplDAO.writeTweets(result)) mustBe a[Success[_]]
			Try(sparkIns.readTable(tweetImplDAO.tableName)) mustBe a[Success[_]]
		}

		"read_data_by_company" in {
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			val result = tweetImplDAO.readByCompanyName("AppleSupport")
			result.show(truncate = false)
			Try(result.count()) mustBe a[Success[_]]
		}

		"read_data_by_time" in {
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			val result = tweetImplDAO.readByTime("2017-10-10", "2017-10-12")
			result.show(truncate = false)
			result.count() mustBe 205
		}

		"read_data_by_company_and_time" in {
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			val result = tweetImplDAO.readByCompanyAndTime("AppleSupport","2017-10-10", "2017-10-12")
			result.show(truncate = false)
			result.count() mustBe 71
		}

		"read_top_company" in {
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			val result = tweetImplDAO.getTop20Company
			result.show(truncate = false)
			result.count() mustBe 7
		}
	}
}
