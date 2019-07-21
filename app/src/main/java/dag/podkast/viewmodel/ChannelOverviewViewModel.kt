package dag.podkast.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.listener.PodcastFileSaverListener
import dag.podkast.model.ChannelOverview
import dag.podkast.repository.Repository
import java.io.IOException
import java.time.LocalDateTime

class ChannelOverviewViewModel @Throws(IOException::class)
constructor(application: Application) : AndroidViewModel(application) {
    val liveChannelOverviews: LiveData<List<ChannelOverview>>


    init {
        Repository.open(application.getExternalFilesDir(null)!!)
        liveChannelOverviews = Repository.liveChannelOverviews
    }

    fun setSelected(channelId: String, isSelected: Boolean) {
        Repository.setChannelSelected(channelId, isSelected)
    }

    fun selectOnly(channelId: String) {
        Repository.selectChannelOnly(channelId)
    }

    fun deleteChannel(channelId: String) {
        Repository.deleteChannel(channelId)
    }

    fun save(activity: Activity) {
        Repository.save(PodcastFileSaverListener.create(activity))
    }

    fun updateAllChannels(activity: Activity) {
        Repository.updateAllChannels(ChannelUpdateListener.create(activity))
    }

    fun newChannel(channelId: String, channelName: String, rssUrl: String, earliestPubDate: LocalDateTime) {
        Repository.newChannel(channelId, channelName, rssUrl, earliestPubDate)
    }
}
