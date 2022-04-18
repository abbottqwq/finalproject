package service

import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotators.Tokenizer
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, lower, regexp_replace, split, trim}

object Analyzer extends App {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("TweetAnalyzer")
    .master("local[*]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  val path: String = getClass.getResource("/sample.csv").getPath
  val df = spark.read.option("delimiter", ",").option("header", "true").csv(path)
  print(df.select("text").head().toString())



  // clean data, remove first word
  val df_without_company = df.withColumn("text", split(col("text")," ", 2)(1))

  // clean data, trim + toLowercase + remove anything except Alphanumeric and Punctuations
  val clean_df = df_without_company.withColumn("text",
    regexp_replace(trim(lower(col("text"))),"[^ 'a-zA-Z0-9,.?!]",""))
  print(clean_df.select("text").head().toString())

  // preprocessing
  // split each document into tokens
  val documentAssembler = new DocumentAssembler().setInputCol("text").setOutputCol("document").setCleanupMode("shrink")

  val tokenizer = new Tokenizer().setInputCols("document").setOutputCol("token")

}
