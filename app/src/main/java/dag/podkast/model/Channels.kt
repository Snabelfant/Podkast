package dag.podkast.model

import dag.podkast.repository.FileStorageChannel
import dag.podkast.rss.RssReader
import dag.podkast.util.Logger
import dag.podkast.util.PodcastException
import java.time.LocalDateTime
import java.util.HashMap
import kotlin.Boolean
import kotlin.Comparator
import kotlin.String
import kotlin.also

class Channels(channels: List<Channel>) {
    private val channelMap = HashMap<String, Channel>()

    val all: Collection<Channel>
        get() = channelMap.values

    init {
        for (channel in channels) {
            channelMap[channel.id] = channel
        }
    }

    fun getChannelOverview(podcasts: Podcasts) =
            mutableListOf<ChannelOverview>()
                    .also { it.addAll(channelMap.values.map { channel -> ChannelOverview(channel, podcasts) }) }
                    .sortedWith(Comparator.comparing(ChannelOverview::channelName))
                    .toList()


    fun getName(id: String): String {
        return channelMap[id]!!.name
    }

    internal fun isSelected(id: String): Boolean {
        return channelMap[id]!!.isSelected
    }

    fun getChannelOverview(channelId: String, podcasts: Podcasts): ChannelOverview {
        val channel = channelMap[channelId]!!
        return ChannelOverview(channel, podcasts)
    }

    fun setSelected(id: String, isSelected: Boolean) {
        Logger.info("Kanal $id valgt: $isSelected")
        channelMap[id]!!.isSelected = isSelected
    }


    fun selectOnly(channelId: String) {
        channelMap.values.forEach { channel -> channel.isSelected = false }
        channelMap[channelId]!!.isSelected = true
    }

    internal fun delete(channelId: String) {
        channelMap.remove(channelId)
    }

    fun add(channelId: String, channelName: String?, rssUrl: String?, earliestPubDate: LocalDateTime?) {
        if (channelMap[channelId] != null) {
            throw PodcastException("Kanalid $channelId finnes allerede")
        }

        if (channelName == null || channelName.isEmpty()) {
            throw PodcastException("Kanal må ha navn")
        }

        RssReader.validateRssUrl(rssUrl)

        if (earliestPubDate == null || earliestPubDate.isAfter(LocalDateTime.now())) {
            throw PodcastException("Tidligste publiseringsdato må < nå")
        }

        val newChannel = Channel(FileStorageChannel(channelId, channelName, rssUrl!!, earliestPubDate, false))
        channelMap[channelId] = newChannel
    }
}
