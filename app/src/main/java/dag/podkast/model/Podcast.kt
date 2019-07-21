package dag.podkast.model

import dag.podkast.repository.FileStoragePodcast
import dag.podkast.rss.RssItem
import org.jsoup.Jsoup
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern

class Podcast(val fileStoragePodcast: FileStoragePodcast, var channelName: String) {
    constructor(channelId: String, channelName: String, title: String, description: String?, pubDate: LocalDateTime, guid: String, downloadUrl: String, lengthInBytes: Int?, durationInSecs: Int, localFilePath: String?, downLoadDate: LocalDateTime?, isCompleted: Boolean, isDeleted: Boolean)
            : this(FileStoragePodcast(channelId, title, description, pubDate, guid, downloadUrl, lengthInBytes, durationInSecs, localFilePath, downLoadDate, isCompleted, isDeleted), channelName)

    var downloadPercentage: Int? = null
        private set

    var isPlaying = false
        internal set

    val description: String?
        get() = fileStoragePodcast.description

    val isDeleted: Boolean
        get() = fileStoragePodcast.isDeleted

    val state: State
        @Synchronized get() =
            when {
                isPlaying -> State.PLAYING
                fileStoragePodcast.isCompleted -> State.COMPLETED
                downloadPercentage != null -> State.DOWNLOADING
                downloadDate != null -> State.DOWNLOADED
                else -> State.NEW
            }

    val downloadDate: LocalDateTime?
        get() = fileStoragePodcast.downloadDate

    val durationInSecs: Int
        get() = fileStoragePodcast.durationInSecs

    val channelId: String
        get() = fileStoragePodcast.channelId

    val title: String
        get() = fileStoragePodcast.title

    val downloadUrl: String
        get() = fileStoragePodcast.downloadUrl

    val guid: String
        get() = fileStoragePodcast.guid

    val lengthInBytes: Int?
        get() = fileStoragePodcast.lengthInBytes

    val pubDate: LocalDateTime
        get() = fileStoragePodcast.pubDate

    var isCompleted: Boolean
        get() = fileStoragePodcast.isCompleted
        set(completed) {
            fileStoragePodcast.isCompleted = completed
        }

    val localFile: File?
        get() = fileStoragePodcast.localFilePath?.let { File(fileStoragePodcast.localFilePath) }

    private val localFileNameFromUrl: String
        get() {
            val urlPath =
                    try {
                        URL(fileStoragePodcast.downloadUrl).path
                    } catch (e: MalformedURLException) {
                        fileStoragePodcast.downloadUrl
                    }

            return "pk-${System.currentTimeMillis()}-${urlPath.replace("[^a-zA-Z0-9]".toRegex(), "").replace("[aeiouy]".toRegex(), "")}.mp3"
        }


    override fun equals(other: Any?) =
            when {
                this === other -> true
                javaClass != other?.javaClass -> false
                else -> fileStoragePodcast.guid == (other as Podcast?)?.fileStoragePodcast?.guid
            }

    override fun hashCode(): Int = Objects.hash(fileStoragePodcast.guid)

    fun matches(pattern: Pattern?): Boolean {
        if (pattern == null) {
            return false
        }

        if (pattern.matcher(title).find()) {
            return true
        }

        return description != null && pattern.matcher(description).find()
    }

    @Synchronized
    fun setDownloadCompleted() {
        fileStoragePodcast.downloadDate = LocalDateTime.now()
        downloadPercentage = null
    }

    @Synchronized
    fun setDownloadFailed(e: Exception) {
        fileStoragePodcast.description = e.toString() + "\n" + description
        deleteDownloadedFile()
        downloadPercentage = null
    }

    fun initDownload() {
        downloadPercentage = 0
    }

    @Synchronized
    fun setDownloadProgress(percentage: Int) {
        if (downloadPercentage != null) {
            downloadPercentage = percentage
        }
    }

    internal fun deleteDownloadedFile() {
        val localFile = localFile
        if (localFile != null) {
            localFile.delete()
            fileStoragePodcast.localFilePath = null
            fileStoragePodcast.downloadDate = null
        }
    }

    fun setDeleted() {
        fileStoragePodcast.isDeleted = true
    }

    fun buildLocalFilePath(podcastDir: File) {
        fileStoragePodcast.localFilePath = File(podcastDir, localFileNameFromUrl).absolutePath
    }

    enum class State {
        NEW,
        DOWNLOADING,
        DOWNLOADED,
        COMPLETED,
        PLAYING
    }

    companion object {

        fun fromRssItem(channelId: String, channelName: String, rssItem: RssItem): Podcast {
            return Podcast(channelId, channelName,
                    cleanUpTitle(rssItem.title),
                    cleanUpDescription(rssItem.description),
                    rssItem.pubDate,
                    rssItem.guid, rssItem.url,
                    rssItem.length,
                    rssItem.durationInSecs, null, null, false, false)
        }

        private fun cleanUpTitle(title: String): String {
            val ddMMyyyy = "\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d."
            val ddMMyy = "\\d\\d\\.\\d\\d\\.\\d\\d."
            val ddMMyy2 = "\\d\\d\\d\\d\\d\\d."

            return title.replace(ddMMyyyy.toRegex(), "").replace(ddMMyy.toRegex(), "").replace(ddMMyy2.toRegex(), "").trim { it <= ' ' }
        }

        private fun cleanUpDescription(description: String?) =
                description?.apply {
                    val partlyCleaned = description.replace("\n\n\n", "\n").replace("\n\n", "\n")
                    return Jsoup.parse(partlyCleaned).text()
                }
    }
}