package dag.podkast.listener

import android.app.Activity

import dag.podkast.model.Channel
import dag.podkast.util.Logger
import dag.podkast.util.MessageBox

interface ChannelUpdateListener {

    fun channelUpdated(channel: Channel, newPodcasts: Int, totalTime: Long)

    fun channelUpdateFailed(channel: Channel, errorMessage: Exception)

    companion object {
        fun create(activity: Activity): ChannelUpdateListener {
            return object : ChannelUpdateListener {
                private var channelResults = StringBuilder()
                private var messageBox = MessageBox(activity, "Oppdateringer", "...")

                override fun channelUpdated(channel: Channel, newPodcasts: Int, totalTime: Long) {
                    val message = "${channel.name}: $newPodcasts, ${totalTime}ms"
                    handleMessage(message)
                    Logger.info(message)
                }

                override fun channelUpdateFailed(channel: Channel, errorMessage: Exception) {
                    val message = "${channel.name}: ${errorMessage.message}"
                    handleMessage(message)
                    Logger.error(message)
                }

                private fun handleMessage(message: String) {
                    channelResults.append(message).append('\n')
                    messageBox.setMessage(channelResults)
                    messageBox.show()
                }

            }
        }
    }

}
