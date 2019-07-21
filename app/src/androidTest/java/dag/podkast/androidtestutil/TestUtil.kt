package dag.podkast.androidtestutil

import android.content.Context
import dag.podkast.R
import dag.podkast.model.Channel
import dag.podkast.model.Podcast
import dag.podkast.util.Logger
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

private const val TESTDIR = "test"
private val r = Random()

@Throws(IOException::class)
fun copyTestResources(context: Context): File {
    val testDir = context.getExternalFilesDir(TESTDIR)!!

    Logger.info("Filer i test: ${testDir.listFiles()}")
    val rawId = R.raw.podkast
    val src = context.resources.openRawResource(rawId)
    val dest = File(testDir, "${context.resources.getResourceEntryName(rawId)}.json")
    dest.delete()
    Logger.info("Testfil å kopiere: $rawId -> ${dest.absolutePath}")
    Files.copy(src, dest.toPath())
    return testDir
}

fun cleanUpAfterTest(context: Context) {
    val testDir = context.getExternalFilesDir(TESTDIR)!!
    val files = listOf(*testDir.listFiles())
    Logger.info("Sletter testfiler $files")
    files.forEach { file -> file.delete() }
    testDir.delete()
}

fun createFromChannels(channels: List<Channel>): Podcast {
    val channel = channels[r.nextInt(channels.size)]
    return create(channel.id)
}

fun createFromChannelIds(channelIds: List<String>): Podcast {
    val channelId = channelIds[r.nextInt(channelIds.size)]
    return create(channelId)
}

fun create1FromChannelIds(channelIds: List<String>, count: Int): List<Podcast> =
        mutableListOf<Podcast>().apply { repeat(count) { this.add(createFromChannelIds(channelIds)) } }

fun create(channeId: String) = Podcast(channeId, "Kanal X",
        "Tittel${r.nextInt()}",
        "Beskrivelse${r.nextInt()}",
        LocalDateTime.now().minusDays(r.nextInt(500).toLong()),
        "guid${r.nextInt()}${r.nextInt()}",
        "url${r.nextInt()}",
        r.nextInt(1000000),
        r.nextInt(1000000),
        null, LocalDateTime.now().minusHours(1000),
        r.nextBoolean(),
        r.nextBoolean())

fun testEquals(podcast1: Podcast, podcast2: Podcast) {
    assertThat(podcast1.channelId, `is`(podcast2.channelId))
    assertThat(podcast1.channelName, `is`(podcast2.channelName))
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
