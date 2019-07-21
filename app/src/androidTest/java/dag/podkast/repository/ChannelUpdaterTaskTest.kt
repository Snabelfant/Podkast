package dag.podkast.repository

import androidx.test.platform.app.InstrumentationRegistry
import dag.podkast.androidtestutil.TestChannelUpdateListener
import dag.podkast.androidtestutil.cleanUpAfterTest
import dag.podkast.androidtestutil.copyTestResources
import dag.podkast.model.Channel
import dag.podkast.util.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.Executors

class ChannelUpdaterTaskTest {
    private lateinit var channels: List<Channel>

    @Before
    @Throws(IOException::class)
    fun init() {
        val testResources = copyTestResources(InstrumentationRegistry.getInstrumentation().targetContext)
        channels = FileStorage.load(testResources).first;
    }

    @After
    fun after() = cleanUpAfterTest(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    @Throws(InterruptedException::class)
    fun testUpdateChannel() {
        val channelUpdaterTask = ChannelUpdaterTask(channels[0], { podcasts ->
            Logger.info("Podkaster=${podcasts.size}")
            podcasts.size
        }, { }, TestChannelUpdateListener())

        val executorService = Executors.newFixedThreadPool(1)
        channelUpdaterTask.executeOnExecutor(executorService)
        Thread.sleep(10000)
    }
}