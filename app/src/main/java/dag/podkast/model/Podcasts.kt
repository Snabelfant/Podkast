package dag.podkast.model

import dag.podkast.sort.Sort
import dag.podkast.util.Logger
import java.util.*
import java.util.regex.Pattern

class Podcasts(podcasts: List<Podcast>, private val channels: Channels, podcastFilterRegex: String?, private var podcastRegexFiltering: Boolean, var sort: Sort) {
    val all: MutableMap<String, Podcast>
    var podcastFilterRegexPattern: Pattern? = null
        private set

    val selected: List<Podcast>
        get() {
            val podcasts = all.values.filter { this.isSelectable(it) }
            Collections.sort(podcasts, sort.podcastComparator)
            return podcasts
        }

    var playing: Podcast
        @Synchronized get() = all.values.first { it.isPlaying }
        @Synchronized set(podcast) {
            setNotPlaying()
            podcast.isPlaying = true
        }

    var podcastFilterRegex: String?
        get() = podcastFilterRegexPattern?.pattern()
        set(podcastFilterRegex) {
            this.podcastFilterRegexPattern = if (podcastFilterRegex == null) null else Pattern.compile(podcastFilterRegex, Pattern.CASE_INSENSITIVE)
        }

    var isPodcastRegexFiltering: Boolean
        get() = podcastRegexFiltering
        set(podcastRegexFiltering) {
            Logger.info("Regex=$podcastRegexFiltering")
            this.podcastRegexFiltering = podcastRegexFiltering
        }

    init {
        all = HashMap(podcasts.size)
        podcasts.forEach { this.add(it) }
        all.values.forEach { podcast -> podcast.channelName = channels.getName(podcast.channelId) }
        this.podcastFilterRegex = podcastFilterRegex
    }

    private fun isSelectable(podcast: Podcast): Boolean {
        return podcast.isPlaying || !podcast.isDeleted && channels.isSelected(podcast.channelId) && matches(podcast)
    }

    private fun matches(podcast: Podcast): Boolean {
        return if (!podcastRegexFiltering || podcastFilterRegexPattern == null) {
            true
        } else podcast.matches(podcastFilterRegexPattern)
    }

    private fun add(podcast: Podcast): Int {
        if (!all.containsKey(podcast.guid)) {
            all[podcast.guid] = podcast
            return 1
        }

        return 0
    }

    @Synchronized
    fun copyAll(): List<Podcast> {
        return ArrayList(all.values)
    }

    @Synchronized
    fun addIfNew(podcasts: List<Podcast>): Int {
        var added = 0
        for (podcast in podcasts) {
            added += add(podcast)
        }
        return added
    }

    @Synchronized
    fun delete(podcast: Podcast) {
        podcast.setDeleted()
    }

    @Synchronized
    internal fun getRemainingCount(channel: Channel) = all.values.filter(getRemainingPredicate(channel)).count()

    private fun getRemainingPredicate(channel: Channel) = { podcast: Podcast -> podcast.channelId == channel.id && !podcast.isCompleted }

    private fun getCompletedPredicate(channel: Channel) = { podcast: Podcast -> podcast.channelId == channel.id && podcast.isCompleted }

    @Synchronized
    internal fun getCompletedCount(channel: Channel) = all.values.filter(getCompletedPredicate(channel)).count()

    @Synchronized
    internal fun getRemainingSeconds(channel: Channel) = all.values.filter(getRemainingPredicate(channel)).sumBy { podcast -> podcast.durationInSecs }

    @Synchronized
    private fun setNotPlaying() = all.forEach { podcast -> podcast.value.isPlaying = false }

    @Synchronized
    fun setPlayingCompleted() {
        val podcast = playing
        podcast.isCompleted = true
        podcast.isPlaying = false
    }

    fun deleteDownloadedFile(podcast: Podcast) {
        podcast.deleteDownloadedFile()
    }

    @Synchronized
    fun stopPlaying() {
        val podcast = playing
        podcast.isPlaying = false
    }

    @Synchronized
    fun deleteAll(channelId: String) {
        channels.delete(channelId)
        val guids = all.values.filter { podcast -> podcast.channelId == channelId }.map { it.guid }
        guids.forEach { guid -> all.remove(guid) }
    }
}
