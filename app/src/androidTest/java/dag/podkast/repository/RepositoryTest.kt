package dag.podkast.repository

import androidx.test.platform.app.InstrumentationRegistry
import dag.podkast.androidtestutil.TestChannelUpdateListener
import dag.podkast.androidtestutil.TestPodcastFileSaverListener
import dag.podkast.androidtestutil.cleanUpAfterTest
import dag.podkast.androidtestutil.copyTestResources
import dag.podkast.util.Logger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.*

class RepositoryTest {

    @Before
    @Throws(IOException::class)
    fun init() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val testResources = copyTestResources(context)
        Repository.open(testResources)
    }

    @After
    fun after() = cleanUpAfterTest(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    @Throws(InterruptedException::class)
    fun testUpdateChannel() {
        val channels = ArrayList(Repository.channels.all)
        Repository.updateChannel(channels[0], TestChannelUpdateListener())
        Thread.sleep(10000)
        for (channel in channels) {
            val channelOverview = Repository.channelOverviews
            Logger.info(channelOverview)
        }
    }

    @Test
    @Throws(InterruptedException::class)
    fun testUpdateAllChannels() {
        val channels = Repository.channels.all
        channels.forEach { channel -> Repository.updateChannel(channel, TestChannelUpdateListener()) }

        Thread.sleep(10000)

        val podcastCount = Repository.channelOverviews
                .map { channelOverview -> channelOverview.deletedPodcasts + channelOverview.remainingPodcasts }
                .sum()

        channels.forEach { channel -> Repository.updateChannel(channel, TestChannelUpdateListener()) }

        val newPodcastCount = Repository.channelOverviews
                .map { channelOverview -> channelOverview.deletedPodcasts + channelOverview.remainingPodcasts }
                .sum()

        assertThat(podcastCount, Is.`is`<Int>(newPodcastCount))

        Repository.save(TestPodcastFileSaverListener())

        Thread.sleep(10000)
    }
}