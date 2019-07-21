package dag.podkast.repository

import dag.podkast.testutil.TestUtil
import dag.podkast.util.Logger
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FileStorageTest {

    @Before
    fun init() {
        Logger.test()
    }

    @Test
    @Throws(IOException::class)
    fun testReadWriteRead() {
        val (channelsRead1, podcastsRead1, sort1) = FileStorage.load(TestUtil.RAW)

        FileStorage.save(TestUtil.RAW, sort1, channelsRead1, podcastsRead1)

        val (channelsRead2, podcastsRead2, sort2) = FileStorage.load(TestUtil.RAW)

        for (i in channelsRead1.indices) {
            TestUtil.testEquals(channelsRead1[i], channelsRead2[i])
        }

        for (i in podcastsRead1.indices) {
            TestUtil.testEquals(podcastsRead1[i], podcastsRead2[i])
        }
    }
}