import dao.{DAO, TweetImplDAO}
import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import service.AnalyzerBase
import spark.SparkIns

import scala.util.{Success, Try}

class TweetDAOSpec extends PlaySpec with BeforeAndAfter {

	implicit var spark: SparkSession = _
	implicit var config: Configuration = _
	implicit var sparkIns: SparkIns = _

	before {
		spark = SparkSession
			.builder()
			.appName("TwitterAnalysis")
			.master("local[*]")
			.getOrCreate()
		spark.sparkContext.setLogLevel("ERROR")

		config = Configuration("isLocalTest" -> true,
			"SDB.driver" -> "org.postgresql.Driver",
			"SDB.url" -> "jdbc:postgresql:postgres",
			"SDB.user" -> "postgres",
			"SDB.password" -> "postgres")

		sparkIns = SparkIns(config, Option(spark))

	}

	"tweetDao" must {
		"write_customer_support" in {
			val ab = new AnalyzerBase()
			val path: String = getClass.getResource("/sample.csv").getPath
			val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
			val result = ab.preprocessing(df)
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			Try(tweetImplDAO.writeCustomerSupport(result)) mustBe a[Success[_]]
		}

		"write_tweets" in {
			val ab = new AnalyzerBase()
			val path: String = getClass.getResource("/sample.csv").getPath
			val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
			val result = ab.preprocessing(df)

			val tweetImplDAO = new TweetImplDAO(sparkIns)
			tweetImplDAO.writeTweets(result)
			Try(tweetImplDAO.writeTweets(result)) mustBe a[Success[_]]
			Try(sparkIns.readTable(tweetImplDAO.tableName)) mustBe a[Success[_]]
		}

		"read_data_by_company" in {
			val tweetImplDAO = new TweetImplDAO(sparkIns)
			val result = tweetImplDAO.readByCompanyName("@AppleSupport")
			result.show(truncate = false)
			result.count() mustBe 10
		}
	}
}
