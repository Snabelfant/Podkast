package dag.podkast.androidtestutil

import dag.podkast.listener.PodcastFileSaverListener
import dag.podkast.util.Logger

class TestPodcastFileSaverListener : PodcastFileSaverListener {
    override fun saveResult(e: Exception?) {
        Logger.info("Kanal er lagret ${e?.toString()}")
    }
}