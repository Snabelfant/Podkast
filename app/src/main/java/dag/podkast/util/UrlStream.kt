package dag.podkast.util

import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

object UrlStream {
    @Throws(IOException::class)
    fun getInputStream(urlString: String) = BufferedInputStream(URL(urlString).openStream())

    @Throws(IOException::class)
    fun getLength(urlString: String) = URL(urlString).openConnection().contentLength
}
