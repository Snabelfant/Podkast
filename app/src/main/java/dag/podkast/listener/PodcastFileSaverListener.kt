package dag.podkast.listener

import android.app.Activity
import android.widget.Toast

import dag.podkast.util.Logger
import dag.podkast.util.ToastOnUiThread

interface PodcastFileSaverListener {
    fun saveResult(e: Exception?)

    companion object {
        fun create(activity: Activity): PodcastFileSaverListener {
            return object : PodcastFileSaverListener {
                override fun saveResult(e: Exception?) {
                    val message: String
                    if (e == null) {
                        message = "Podkast lagret"
                        Logger.info(message)
                        ToastOnUiThread.show(activity, message)
                    } else {
                        message = "Podkastlagring feilet: $e"
                        Logger.error(message)
                        ToastOnUiThread.show(activity, message, Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }
}
