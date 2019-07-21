package dag.podkast.repository

import android.os.AsyncTask
import dag.podkast.model.Podcast
import dag.podkast.util.Logger
import dag.podkast.util.UrlDownloader
import java.io.IOException

class PodcastDownloaderTask(private val podcast: Podcast, private val notifier: () -> Unit) : AsyncTask<Any, Int, Void>() {
    override fun doInBackground(vararg params: Any): Void? {
        try {
            podcast.initDownload()
            UrlDownloader.download(podcast.downloadUrl, podcast.localFile!!, object : UrlDownloader.ProgressListener {
                private var lastPercentage = -2

                override fun reportProgress(percentage: Int) {
                    if (percentage != lastPercentage) {
                        publishProgress(percentage)
                        lastPercentage = percentage
                    }
                }
            })

            podcast.setDownloadCompleted()
        } catch (e: IOException) {
            Logger.error("Nedlasting feilet for $podcast: $e")
            podcast.setDownloadFailed(e)
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        podcast.setDownloadProgress(values[0]!!)
        notifier()
    }

    override fun onPostExecute(v: Void?) {
        notifier()
    }
}
