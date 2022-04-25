package service

import com.johnsnowlabs.nlp.annotators._
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, length, regexp_replace, split, substring, substring_index, trim}

class AnalyzerBase {

  def preprocessing(df: DataFrame): DataFrame = {
    // clean data, keep users' responses, remove companies' responses and irregular responses
    val df_true = df.filter("inbound=TRUE")
      .filter(col("text").startsWith("@"))

    // clean data, remove first word, replace author_id with company_name
    val split_array = split(col("text"), " ", 2)
    val df_transformer = df_true.withColumn("author_id", split_array(0))
      .withColumn("text", split_array(1))

    val df_with_company = df_transformer
      .withColumn("author_id", regexp_replace(col("author_id"), "@", ""))

    // clean data, trim + remove urls
    val cleaned_df = df_with_company.withColumn("text",
      regexp_replace(trim(col("text")),
        "http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+", ""))

    cleaned_df.select("text").show(10, truncate = false)

    // preprocessing
    val documentAssembler = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val sentenceDetector = new SentenceDetector()
      .setInputCols(Array("document"))
      .setOutputCol("sentence")

    val tokenizer = new Tokenizer()
      .setInputCols(Array("sentence"))
      .setOutputCol("token")

    val normalizer = new Normalizer()
      .setInputCols("token")
      .setOutputCol("normalized")
      .setLowercase(true)
      .setCleanupPatterns(Array("""[^A-Za-z ]"""))

    val stopWords = new StopWordsCleaner()
      .setInputCols("normalized")
      .setOutputCol("cleanTokens")
      .setCaseSensitive(false)

    val lemma = new Lemmatizer()
      .setInputCols("cleanTokens")
      .setOutputCol("lemma")
      .setDictionary("dependencies/lemma.txt", "->", "\t")

    val finisher = new Finisher()
      .setInputCols("lemma")
      .setOutputCols("new_text")
      .setCleanAnnotations(false)

    val pipeline = new Pipeline().setStages(Array(
      documentAssembler,
      sentenceDetector,
      tokenizer,
      normalizer,
      stopWords,
      lemma,
      finisher
    ))

    pipeline.fit(cleaned_df).transform(cleaned_df)
  }

}
