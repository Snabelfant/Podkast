package dag.podkast.rss

import dag.podkast.repository.FileStorage
import dag.podkast.testutil.TestUtil
import dag.podkast.util.Logger
import dag.podkast.util.PodcastException
import dag.podkast.util.UrlStream
import org.junit.Before
import org.junit.Test
import org.xml.sax.SAXException
import java.io.IOException
import java.time.LocalDateTime
import javax.xml.parsers.ParserConfigurationException

class RssReaderTest {
    @Before
    fun init() {
        Logger.test()
    }

    @Test
    fun testValidateRssUrl() {
        try {
            RssReader.validateRssUrl(null)
        } catch (e: PodcastException) {
            Logger.info("E=" + e.message)
        }
    }

    @Test
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun getRssItems() {
        val rssItems = RssReader.getRssItems(UrlStream.getInputStream("https://podkast.nrk.no/program/ekko_-_et_aktuelt_samfunnsprogram.rss"),
                LocalDateTime.now().minusMonths(3))
        println(rssItems)
    }

    @Test
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun getRssItems2() {
        val rssItems = RssReader.getRssItems(UrlStream.getInputStream("http://feeds.wnyc.org/radiolab"),
                LocalDateTime.now().minusMonths(3))
        println(rssItems)
    }

    @Test
    @Throws(IOException::class)
    fun testReadAllRss() {
        val channels = FileStorage.load(TestUtil.RAW).first
        for (channel in channels) {
            val rssItems: List<RssItem>
            try {
                rssItems = RssReader.getRssItems(UrlStream.getInputStream(channel.rssUrl), channel.earliestPubDate)
                println(channel.name + ": " + rssItems.size + "    " + channel.earliestPubDate)
            } catch (e: Exception) {
                println(channel.name + ": " + e.message)
            }

        }
    }
}