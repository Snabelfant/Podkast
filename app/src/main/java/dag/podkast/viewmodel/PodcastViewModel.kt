package dag.podkast.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.listener.PodcastFileSaverListener
import dag.podkast.model.Podcast
import dag.podkast.repository.Repository
import dag.podkast.sort.Sort
import java.io.IOException

class PodcastViewModel @Throws(IOException::class)
constructor(application: Application) : AndroidViewModel(application) {
    val liveSelectedPodcasts: LiveData<List<Podcast>>

    var isPodcastRegexFiltering: Boolean
        get() = Repository.isPodcastRegexFiltering
        set(enabled) {
            Repository.isPodcastRegexFiltering = enabled
        }

    var podcastFilterRegex: String?
        get() = Repository.podcastFilterRegex
        set(filterRegex) {
            Repository.podcastFilterRegex = filterRegex
        }


    var sort: Sort
        get() = Repository.sort
        set(sort) {
            Repository.sort = sort
        }

    init {
        Repository.open(application.getExternalFilesDir(null)!!)
        liveSelectedPodcasts = Repository.liveSelectedPodcasts
    }

    fun delete(podcast: Podcast) {
        Repository.deleteDownloadedFile(podcast)
        Repository.deletePodcast(podcast)
    }

    fun setPlaying(podcast: Podcast) {
        Repository.setPlaying(podcast)
    }

    fun stopPlaying() {
        Repository.stopPlaying()
    }

    fun setPlayingCompleted() {
        Repository.setPlayingCompleted()
    }

    fun download(podcast: Podcast) {
        Repository.download(podcast)
    }

    fun deleteDownloadedFile(podcast: Podcast) {
        Repository.deleteDownloadedFile(podcast)
    }

    fun save(activity: Activity) {
        Repository.save(PodcastFileSaverListener.create(activity))
    }

    fun updateAllChannels(channelUpdateListener: ChannelUpdateListener) {
        Repository.updateAllChannels(channelUpdateListener)
    }
}
