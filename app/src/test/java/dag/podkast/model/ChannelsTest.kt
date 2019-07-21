package dag.podkast.model

import dag.podkast.repository.FileStorageChannel
import dag.podkast.util.PodcastException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class ChannelsTest {

    @Test
    fun testAdd() {
        val channel = Channel(FileStorageChannel("id1", "navn", "rss", LocalDateTime.now(), false))
        val channels = Channels(listOf(channel))

        try {
            channels.add("id1", null, null, null)
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("finnes allerede"))
        }

        try {
            channels.add("id2", null, null, null)
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("navn"))
        }

        try {
            channels.add("id2", "blabla", null, null)
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("url"))
        }

        try {
            channels.add("id2", "blabla", "httpY://xyz.abc", LocalDateTime.now().minusDays(5))
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("MalformedURLException"))
        }

        try {
            channels.add("id2", "blabla", "https://acast.aftenposten.no/rss/teknologimagasinet", null)
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("Tidligste"))
        }

        try {
            channels.add("id2", "blabla", "https://acast.aftenposten.no/rss/teknologimagasinet", LocalDateTime.now().plusDays(1))
            fail()
        } catch (e: PodcastException) {
            assertThat(e.message, containsString("Tidligste"))
        }

        val earliestPubDate = LocalDateTime.now().minusYears(1)
        channels.add("id2", "blabla", "https://acast.aftenposten.no/rss/teknologimagasinet", earliestPubDate)

        val all = ArrayList(channels.all)
        assertThat(all.size, `is`(2))
        val newChannel = if (all[0].id == "id2") all[0] else all[1]
        assertThat(newChannel.earliestPubDate, `is`(earliestPubDate))
        assertThat(newChannel.rssUrl, `is`("https://acast.aftenposten.no/rss/teknologimagasinet"))
        assertThat(newChannel.name, `is`("blabla"))
        assertThat(newChannel.name, `is`("blabla"))
        assertThat(newChannel.isSelected, `is`(false))
    }

}