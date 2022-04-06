package utils

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object MyTryToOption {
	implicit def tryToOption[T](t: Try[T]): Option[T] = {
		t match {
			case Failure(_) => None
			case Success(s) => Some(s)
		}
	}
}
