import org.apache.spark.sql.SparkSession
import org.scalatestplus.play.PlaySpec
import service.AnalyzerBase

import scala.util.{Success, Try}

class AnalyzerSpec extends PlaySpec {

	"analyzer" must {
		"read_file" in {
			val spark: SparkSession = SparkSession
				.builder()
				.appName("TwitterAnalysis")
				.master("local[*]")
				.getOrCreate()

			spark.sparkContext.setLogLevel("ERROR")
			val path: String = getClass.getResource("sample.csv").getPath
			val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
			df.count() mustBe 96L
			//			spark.close()
		}

		"preprocessing" in {
			val ab = new AnalyzerBase()
			val spark: SparkSession = SparkSession
				.builder()
				.appName("TwitterAnalysis")
				.master("local[*]")
				.getOrCreate()

			spark.sparkContext.setLogLevel("ERROR")
			val path: String = getClass.getResource("sample.csv").getPath
			val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
			val start = System.nanoTime()
			val result = ab.preprocessing(df)
			val end = System.nanoTime()
			val duration = (end - start) / 1000000000
			println(s"preprocessing run time:${duration}s")
			Try(result.select("new_text").show(5)) mustBe a[Success[_]]
			//			spark.close()
		}
	}

}
