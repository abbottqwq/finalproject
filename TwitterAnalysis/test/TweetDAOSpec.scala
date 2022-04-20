import dao.{TweetDAO, TweetImplDAO}
import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import service.AnalyzerBase

class TweetDAOSpec extends PlaySpec with BeforeAndAfter {

  implicit var spark: SparkSession = _

  before {
    spark = SparkSession
      .builder()
      .appName("TwitterAnalysis")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
  }

  "tweetDao" must {
    "write_customer_support" in {
      val ab = new AnalyzerBase()
      val path: String = getClass.getResource("/sample.csv").getPath
      val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
      val result = ab.preprocessing(df)

      val dao: TweetDAO = new TweetImplDAO()
      dao.writeCustomerSupport(result)
    }

    "write_tweets" in {
      val ab = new AnalyzerBase()
      val path: String = getClass.getResource("/sample.csv").getPath
      val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
      val result = ab.preprocessing(df)

      val dao: TweetDAO = new TweetImplDAO()
      dao.writeTweets(result)
    }

    "read_data_by_company" in {
      val dao: TweetDAO = new TweetImplDAO()
      val result = dao.readByCompanyName("@AppleSupport", spark)
      result.show(truncate = false)
      result.count() mustBe 10
    }
  }
}
