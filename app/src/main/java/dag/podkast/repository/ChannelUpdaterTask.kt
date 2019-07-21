package dag.podkast.repository

import android.os.AsyncTask
import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.model.Channel
import dag.podkast.model.Podcast
import dag.podkast.rss.RssReader
import dag.podkast.util.Logger

class ChannelUpdaterTask(private val channel: Channel, private val newPodcastsHandler: (List<Podcast>) -> Int, private val afterTask: () -> Unit, private val channelUpdateListener: ChannelUpdateListener) : AsyncTask<Any, Int, Void>() {

    override fun doInBackground(vararg params: Any): Void? {
        val startTime = System.currentTimeMillis()
        try {
            Logger.info("Start oppdatering ${channel.id}")
            val rssItems = RssReader.getRssItems(channel.rssUrl, channel.earliestPubDate)
            val newPodcasts = rssItems.map { rssItem -> Podcast.fromRssItem(channel.id, channel.name, rssItem) }
            val added = newPodcastsHandler(newPodcasts)
            val totalTime = System.currentTimeMillis() - startTime

            Logger.info("Oppdaterte ${channel.id} på ${totalTime}ms. $added nye av ${rssItems.size}")
            channelUpdateListener.channelUpdated(channel, added, totalTime)
        } catch (e: Exception) {
            Logger.error("Kanal ${channel.id}: $e")
            channelUpdateListener.channelUpdateFailed(channel, e)
        }

        return null
    }


    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(aVoid: Void?) {
        afterTask()
    }
}
