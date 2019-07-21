package dag.podkast.testutil

import dag.podkast.model.Channel
import dag.podkast.model.Podcast
import dag.podkast.util.Logger
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

object TestUtil {
    private val r = Random()

    private val TESTRESOURCES = File("C:\\Users\\Dag\\AndroidStudioProjects\\AudioPlayer\\app\\src\\test\\testresources")
    val RAW = File("C:\\Users\\Dag\\AndroidStudioProjects\\Podkast\\app\\src\\main\\res\\raw")

    @Throws(IOException::class)
    fun copyTestResources(): File {
        val destDir = File("\\temp\\podkast\\" + System.currentTimeMillis())
        destDir.mkdirs()

        TESTRESOURCES.listFiles().forEach {
            val dest = File(destDir, it.name)
            Logger.info(it.absolutePath + " -> " + dest.absolutePath)
            Files.copy(it.toPath(), FileOutputStream(dest))
        }
        return destDir
    }

    fun createFromChannels(channels: List<Channel>): Podcast {
        val channel = channels[r.nextInt(channels.size)]
        return create(channel.id)
    }

    fun createFromChannelIds(channelIds: List<String>): Podcast {
        val channelId = channelIds[r.nextInt(channelIds.size)]
        return create(channelId)
    }

    fun createFromChannelIds(channelIds: List<String>, count: Int) =
            mutableListOf<Podcast>().apply { repeat(count) { add(createFromChannelIds(channelIds)) } }.toList()

    fun create(channeId: String) =
            Podcast(channeId, "Kanal $channeId",
                "Tittel" + r.nextInt(),
                "Beskrivelse" + r.nextInt(),
                LocalDateTime.now().minusDays(r.nextInt(500).toLong()),
                "guid" + r.nextInt() + r.nextInt(),
                "url" + r.nextInt(),
                r.nextInt(1000000),
                r.nextInt(1000000),
                null, LocalDateTime.now().minusHours(1000), r.nextBoolean(),
                r.nextBoolean())

    fun testEquals(podcast1: Podcast, podcast2: Podcast) {
        assertThat(podcast1.channelId, `is`(podcast2.channelId))
        assertThat(podcast1.description, `is`(podcast2.description))
        assertThat(podcast1.downloadDate, `is`(podcast2.downloadDate))
        assertThat(podcast1.durationInSecs, `is`(podcast2.durationInSecs))
        assertThat(podcast1.downloadUrl, `is`(podcast2.downloadUrl))
        assertThat(podcast1.guid, `is`(podcast2.guid))
        assertThat(podcast1.lengthInBytes, `is`(podcast2.lengthInBytes))
        assertThat(podcast1.localFile, `is`(podcast2.localFile))
        assertThat(podcast1.pubDate, `is`(podcast2.pubDate))
    }

    fun testEquals(channel1: Channel, channel2: Channel) {
        assertThat(channel1.id, `is`(channel2.id))
        assertThat(channel1.earliestPubDate, `is`(channel2.earliestPubDate))
        assertThat(channel1.name, `is`(channel2.name))
        assertThat(channel1.rssUrl, `is`(channel2.rssUrl))
    }
}
