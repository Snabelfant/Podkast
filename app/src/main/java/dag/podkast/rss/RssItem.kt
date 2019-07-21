package dag.podkast.rss

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class RssItem {
    lateinit var title: String
    var description: String? = null
    lateinit var pubDate: LocalDateTime
        private set
    lateinit var url: String
    lateinit var mimeType: String
    lateinit var guid: String
    var length: Int? = null
        private set
    var durationInSecs: Int = 0
        private set

    fun setDurationInSecs(durationInHhMmSs: String) {
        durationInSecs = 1
        val values = durationInHhMmSs.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val secs: Int
        val mins: Int
        val hours: Int

        if (values.size == 3) {
            secs = Integer.valueOf(values[2])
            mins = Integer.valueOf(values[1])
            hours = Integer.valueOf(values[0])
        } else {
            if (values.size == 2) {
                secs = Integer.valueOf(values[1])
                mins = Integer.valueOf(values[0])
                hours = 0
            } else {
                return
            }
        }

        durationInSecs = secs + mins * 60 + hours * 60 * 60
    }

    override fun toString(): String {
        return "RssItem{title='$title', description='$description', pubDate=${pubDate.toString()}, url='$url', guid='$guid', lengthInBytes=$length, durationInSecs=$durationInSecs\n, type=$mimeType}"
    }

    fun setPubDate(pubDateAsString: String?) {
        if (pubDateAsString == null) {
            pubDate = LocalDateTime.of(2000, 1, 1, 0, 0)
        } else {
            pubDate = try {
                val dtf = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                ZonedDateTime.parse(pubDateAsString, dtf).toLocalDateTime()
            } catch (e: DateTimeParseException) {
                try {
                    val dtf = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                    ZonedDateTime.parse(pubDateAsString, dtf).toLocalDateTime()
                } catch (e1: DateTimeParseException) {
                    try {
                        val dtf = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                        ZonedDateTime.parse(pubDateAsString, dtf).toLocalDateTime()
                    } catch (e2: DateTimeParseException) {
                        LocalDateTime.now()
                    }
                }
            }
        }
    }

    fun setLengthInBytes(lengthInBytes: String) =
            try {
                this.length = Integer.valueOf(lengthInBytes)
            } catch (e: NumberFormatException) {
                this.length = null
            }
}
