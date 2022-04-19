package service

import _root_.spark.SparkIns
import org.apache.spark.sql.{DataFrame, SparkSession}

import javax.inject.{Inject, Singleton}

@Singleton
case class Analyzer @Inject()(sparkIns: SparkIns) extends AnalyzerBase {

  def run(sparkIns: SparkIns): DataFrame = {
    val spark: SparkSession = sparkIns.spark
    val path: String = getClass.getResource("/twcs.csv").getPath
    val df: DataFrame = spark.read.option("delimiter", ",").option("header", "true").csv(path)
    super.preprocessing(df)
  }
}

object Analyzer extends AnalyzerBase with App {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("TwitterAnalysis")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
  val path: String = getClass.getResource("/twcs.csv").getPath
  val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
  val result = super.preprocessing(df)
  result.select("new_text").show(20, truncate = false)
}
