package model

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Reads, Writes}

case class TweetResult(tweets: String, time_to_month: String, freq: String)

object TweetResult {
  implicit val reads: Reads[TweetResult] = (
    (JsPath \ "tweets").read[String] and
      (JsPath \ "time_to_month").read[String] and
      (JsPath \ "freq").read[String]
    ) (TweetResult.apply _)
  implicit val writes: Writes[TweetResult] = (
    (JsPath \ "tweets").write[String] and
      (JsPath \ "time_to_month").write[String] and
      (JsPath \ "freq").write[String]
    ) (unlift(TweetResult.unapply))
}
