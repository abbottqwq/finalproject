package utils.implicits

import java.sql.Timestamp
import java.time.Instant

object TimeConvert {
	implicit class StringTimeConversions(sc: StringContext) {
		def t(args: Any*): Timestamp =
			Timestamp.from(Instant.parse(sc.s(args: _*)))
	}
}
