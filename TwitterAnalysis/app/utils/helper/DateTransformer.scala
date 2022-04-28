package utils.helper

case class DateTransformer(){

  def transform(date: String): String = {
    val data = date
    val split_data = date.split("-")
    split_data.size match {
      case 1 => split_data(0).concat("-01-01")
      case 2 => data.concat("-01")
      case _ => data
    }
  }
}
