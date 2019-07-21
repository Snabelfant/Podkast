package dag.podkast.repository

import android.os.AsyncTask
import dag.podkast.listener.PodcastFileSaverListener
import dag.podkast.model.Channel
import dag.podkast.model.Podcast
import dag.podkast.sort.Sort
import dag.podkast.util.Logger
import java.io.File
import java.io.IOException

class PodcastFileSaverTask internal constructor(private val appDir: File, private val sort: Sort, private val channels: Collection<Channel>, private val podcasts: Collection<Podcast>, private val podcastFileSaverListener: PodcastFileSaverListener) : AsyncTask<Any, Int, Void>() {
    private var startTime: Long = 0

    override fun doInBackground(vararg params: Any): Void? {
        startTime = System.currentTimeMillis()
        try {
            FileStorage.save(appDir, sort, channels, podcasts)
            podcastFileSaverListener.saveResult(null)
        } catch (e: IOException) {
            Logger.error("PodcastFileSaverTask $e.toString()")
            podcastFileSaverListener.saveResult(e)
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(v: Void?) {
        val totalTime = System.currentTimeMillis() - startTime
        Logger.info("Lagret på $totalTime")
    }
}

