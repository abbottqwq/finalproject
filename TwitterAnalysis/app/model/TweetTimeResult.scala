/*package model

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

case class TweetTimeResult(tweets: String, time_to_month: String, freq: String)
case class TweetTimeAndCompanyResult(tweets: String, time_to_month: String, author_id: String, freq: String)

object TweetTimeResult {
  implicit val reads: Reads[TweetTimeResult] = (
    (JsPath \ "tweets").read[String] and
      (JsPath \ "time_to_month").read[String] and
      (JsPath \ "freq").read[String]
    ) (TweetTimeResult.apply _)
  implicit val writes: Writes[TweetTimeResult] = (
    (JsPath \ "tweets").write[String] and
      (JsPath \ "time_to_month").write[String] and
      (JsPath \ "freq").write[String]
    ) (unlift(TweetTimeResult.unapply))
}

object TweetTimeAndCompanyResult {
  implicit val reads: Reads[TweetTimeAndCompanyResult] = (
    (JsPath \ "tweets").read[String] and
      (JsPath \ "time_to_month").read[String] and
      (JsPath \ "author_id").read[String] and
      (JsPath \ "freq").read[String]
    ) (TweetTimeAndCompanyResult.apply _)
  implicit val writes: Writes[TweetTimeAndCompanyResult] = (
    (JsPath \ "tweets").write[String] and
      (JsPath \ "time_to_month").write[String] and
      (JsPath \ "author_id").write[String] and
      (JsPath \ "freq").write[String]
    ) (unlift(TweetTimeAndCompanyResult.unapply))
}
*/

