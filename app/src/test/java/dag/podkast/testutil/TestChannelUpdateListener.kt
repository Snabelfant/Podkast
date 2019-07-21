package dag.podkast.testutil

import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.model.Channel
import dag.podkast.util.Logger

class TestChannelUpdateListener : ChannelUpdateListener {
    override fun channelUpdated(channel: Channel, newPodcasts: Int, totalTime: Long) {
        Logger.info("Kanal " + channel.id + ": " + newPodcasts + " nye")
    }

    override fun channelUpdateFailed(channel: Channel, errorMessage: Exception) {
        Logger.error("Kanal " + channel.id + ": " + errorMessage.message)
    }
}
