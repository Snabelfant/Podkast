package dag.podkast.model

import dag.podkast.repository.FileStorage
import dag.podkast.repository.FileStorageChannel
import dag.podkast.sort.Sort
import dag.podkast.testutil.TestUtil
import dag.podkast.util.Logger
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

class PodcastsTest {
    @Rule
    @JvmField
    val appDir = TemporaryFolder()
    private lateinit var channels: Channels

    @Before
    fun init() {
        Logger.test()
        val earliestPubdate = LocalDateTime.of(2010, 1, 1, 0, 0)
        val channelA = FileStorageChannel("a", "A", "http://x.no", earliestPubdate, true)
        val channelB = FileStorageChannel("b", "B", "http://x.no", earliestPubdate, true)
        val channelC = FileStorageChannel("c", "C", "http://x.no", earliestPubdate, false)
        val channelD = FileStorageChannel("d", "D", "http://x.no", earliestPubdate, false)
        channels = Channels(listOf(Channel(channelA), Channel(channelB), Channel(channelC), Channel(channelD)))
    }

    @Test
    fun test1() {
        val podcasts = Podcasts(TestUtil.createFromChannelIds(listOf("a", "b", "c"), 200), channels, null, false, Sort.CHANNELP_PUBDATEM)
        assertThat(podcasts.all.size, `is`(200))
        val remaining = podcasts.selected.size

        var podcast: Podcast
        do {
            podcast = TestUtil.create("a")
        } while (podcast.isDeleted)

        podcast.isCompleted = false
        podcasts.addIfNew(listOf(podcast))
        assertThat(podcasts.all.size, `is`(201))
        assertThat(remaining + 1, `is`(podcasts.selected.size))
        podcasts.delete(podcast)
        assertThat(remaining, `is`(podcasts.selected.size))
        assertThat(podcasts.all.size, `is`(201))
    }

    @Test
    fun test2() {
        val newPodcasts = TestUtil.createFromChannelIds(listOf("a", "b", "c"), 200)
        val podcasts = Podcasts(newPodcasts, channels, null, false, Sort.CHANNELP_PUBDATEM)
        assertThat(podcasts.all.size, `is`(200))
        var added = podcasts.addIfNew(newPodcasts)
        assertThat(added, `is`(0))

        added = podcasts.addIfNew(TestUtil.createFromChannelIds(listOf("a", "b", "c"), 300))
        assertThat(added, `is`(300))
        assertThat(podcasts.all.size, `is`(500))
    }

    @Test
    fun test3() {
        val podcasts = Podcasts(TestUtil.createFromChannelIds(listOf("a", "b", "c"), 500), channels, null, false, Sort.CHANNELP_PUBDATEM)
        val remaining = ArrayList(podcasts.selected)

        for (podcast in remaining) {
            if (!podcast.isDeleted) {
                podcasts.delete(podcast)
            }
        }

        assertThat(podcasts.selected.size, `is`(0))
        assertThat(podcasts.all.size, `is`(500))
    }

    @Test
    @Throws(IOException::class)
    fun test4() {
        val podcasts = Podcasts(TestUtil.createFromChannelIds(listOf("a", "b", "c"), 500), channels, null, false, Sort.CHANNELP_PUBDATEM)
        FileStorage.save(appDir.root, Sort.CHANNELP_PUBDATEM, channels.all, podcasts.all.values)
        val podcastsRead = FileStorage.load(appDir.root).second
        val newPodcasts = Podcasts(podcastsRead, channels, null, false, Sort.CHANNELP_PUBDATEM)
        assertThat(podcasts.all.size, `is`(newPodcasts.all.size))
    }

    @Test
    fun test5() {
        Podcasts(TestUtil.createFromChannelIds(listOf("a", "b", "c"), 500), channels, null, false, Sort.CHANNELP_PUBDATEM)
    }
}