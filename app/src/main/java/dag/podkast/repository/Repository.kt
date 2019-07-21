package dag.podkast.repository

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.listener.PodcastFileSaverListener
import dag.podkast.model.*
import dag.podkast.sort.Sort
import dag.podkast.util.Logger
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.Executors

object Repository {
    private var isOpened = false
    lateinit var channels: Channels
    private lateinit var podcasts: Podcasts
    private lateinit var podcastDir: File
    private lateinit var appDir: File
    lateinit var liveSelectedPodcasts: MutableLiveData<List<Podcast>>
        private set

    lateinit var liveChannelOverviews: MutableLiveData<List<ChannelOverview>>
        private set

    private val executorService = Executors.newFixedThreadPool(10)

    val channelOverviews: List<ChannelOverview>
        get() = channels.getChannelOverview(podcasts)

    var isPodcastRegexFiltering: Boolean
        get() = podcasts.isPodcastRegexFiltering
        set(enabled) {
            podcasts.isPodcastRegexFiltering = enabled
            signalPodcastUpdate()
        }

    var podcastFilterRegex: String?
        get() = podcasts.podcastFilterRegex
        set(filterRegex) {
            podcasts.podcastFilterRegex = filterRegex
            signalPodcastUpdate()
        }

    var sort: Sort
        get() = podcasts.sort
        set(sort) {
            podcasts.sort = sort
            signalPodcastUpdate()
        }

    @Throws(IOException::class)
    fun open(appDir: File) {
        if (isOpened) {
            Logger.info("Repo allerede åpnet")
            return
        }
        this.appDir = appDir
        podcastDir = File(appDir, "podkaster")
        podcastDir.mkdirs()
        val (channelList, podcastList, sort) = FileStorage.load(appDir)
        channels = Channels(channelList)
        podcasts = Podcasts(podcastList, channels, null, false, sort)
        liveSelectedPodcasts = MutableLiveData(podcasts.selected)
        liveChannelOverviews = MutableLiveData(channelOverviews)
        isOpened = true
    }

    private fun signalPodcastUpdate() {
        liveSelectedPodcasts.postValue(podcasts.selected)
    }

    @Synchronized
    private fun addIfNew(possiblyNewPodcasts: List<Podcast>): Int {
        val added = podcasts.addIfNew(possiblyNewPodcasts)
        signalPodcastUpdate()
        return added
    }

    @Synchronized
    fun recalculateOverviews() {
        liveChannelOverviews.postValue(channelOverviews)
    }

    @Synchronized
    fun deletePodcast(podcast: Podcast) {
        podcasts.delete(podcast)
        signalPodcastUpdate()
        recalculateOverviews()
    }

    fun updateAllChannels(channelUpdateListener: ChannelUpdateListener) {
        channels.all.forEach { channel -> this.updateChannel(channel, channelUpdateListener) }
    }

    fun updateChannel(channel: Channel, channelUpdateListener: ChannelUpdateListener) {
        val task = ChannelUpdaterTask(channel, { this.addIfNew(it) }, {
            signalPodcastUpdate()
            recalculateOverviews()
        }, channelUpdateListener)
        execute(task)
    }

    fun save(podcastFileSaverListener: PodcastFileSaverListener) {
        val podcastCopy = podcasts.copyAll()
        val podcastFileSaverTask = PodcastFileSaverTask(appDir, podcasts.sort, channels.all, podcastCopy, podcastFileSaverListener)
        execute(podcastFileSaverTask)
    }

    fun setPlaying(podcast: Podcast) {
        podcasts.playing = podcast
        signalPodcastUpdate()
    }

    fun setPlayingCompleted() {
        podcasts.setPlayingCompleted()
        signalPodcastUpdate()
    }

    fun deleteDownloadedFile(podcast: Podcast) {
        podcasts.deleteDownloadedFile(podcast)
        signalPodcastUpdate()
    }

    fun stopPlaying() {
        podcasts.stopPlaying()
        signalPodcastUpdate()
    }

    fun download(podcast: Podcast) {
        podcast.buildLocalFilePath(podcastDir)
        val task = PodcastDownloaderTask(podcast) { signalPodcastUpdate() }
        task.executeOnExecutor(executorService)
    }

    private fun execute(task: AsyncTask<*, *, *>) {
        task.executeOnExecutor(executorService)
    }

    fun setChannelSelected(channelId: String, isSelected: Boolean) {
        channels.setSelected(channelId, isSelected)
        recalculateOverviews()
        signalPodcastUpdate()
    }

    fun selectChannelOnly(channelId: String) {
        channels.selectOnly(channelId)
        recalculateOverviews()
        signalPodcastUpdate()
    }

    fun deleteChannel(channelId: String) {
        podcasts.deleteAll(channelId)
        recalculateOverviews()
        signalPodcastUpdate()
    }

    fun newChannel(channelId: String, channelName: String, rssUrl: String, earliestPubDate: LocalDateTime) {
        channels.add(channelId, channelName, rssUrl, earliestPubDate)
        recalculateOverviews()
    }
}
