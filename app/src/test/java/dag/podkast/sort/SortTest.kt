package dag.podkast.sort

import dag.podkast.model.Podcast
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class SortTest {
    @Test
    fun test() {
        val p1 = createPodcast("K1", "T1", LocalDateTime.now().minusDays(2))
        val p2 = createPodcast("K2", "T2", LocalDateTime.now())
        val p3 = createPodcast("K2", "T2", LocalDateTime.now().plusDays(2))
        assertTrue(Sort.CHANNELP_PUBDATEP.compare(p1, p2) < 0)
        assertTrue(Sort.CHANNELP_PUBDATEP.compare(p2, p1) > 0)
        assertTrue(Sort.CHANNELP_PUBDATEP.compare(p2, p2) == 0)
        assertTrue(Sort.CHANNELP_PUBDATEP.compare(p2, p3) < 0)
        assertTrue(Sort.CHANNELP_PUBDATEP.compare(p3, p2) > 0)
        assertTrue(Sort.CHANNELP_PUBDATEM.compare(p1, p2) < 0)
        assertTrue(Sort.CHANNELP_PUBDATEM.compare(p2, p1) > 0)
        assertTrue(Sort.CHANNELP_PUBDATEM.compare(p2, p2) == 0)
        assertTrue(Sort.CHANNELP_PUBDATEM.compare(p2, p3) > 0)
        assertTrue(Sort.CHANNELP_PUBDATEM.compare(p3, p2) < 0)

        assertTrue(Sort.PUBDATEM_CHANNELP.compare(p1, p2) > 0)
        assertTrue(Sort.PUBDATEP_CHANNELP.compare(p1, p2) < 0)
    }

    @Test
    fun testDownload() {
        val p1 = createPodcast("K1", "T1", LocalDateTime.now().minusDays(2))
        p1.setDownloadCompleted()
        val p2 = createPodcast("K2", "T2", LocalDateTime.now())
        val p3 = createPodcast("K2", "T2", LocalDateTime.now().plusDays(2))
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p1, p2) < 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p2, p1) > 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p1, p1) == 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p2, p2) == 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p2, p3) < 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p3, p2) > 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p1, p3) < 0)
        assertTrue(Sort.DOWNLOADED_PUBDATEP_CHANNELP.compare(p3, p1) > 0)
    }


    private fun createPodcast(channelname: String, title: String, pubDate: LocalDateTime): Podcast {
        return Podcast("chid", channelname, title, null, pubDate, "abc", "x.y", 0, 0, null, null, false, false)
    }
}