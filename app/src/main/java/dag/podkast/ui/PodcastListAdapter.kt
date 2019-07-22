package dag.podkast.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import dag.podkast.R
import dag.podkast.model.Podcast
import dag.podkast.util.DateUtil
import java.util.*

class PodcastListAdapter(context: Context) : ArrayAdapter<Podcast>(context, R.layout.podcast) {
    private val inflater: LayoutInflater
    private var selectedPodcasts: List<Podcast> = ArrayList()

    init {
        inflater = super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return selectedPodcasts.size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val podcast = getItem(position)
        val podcastView = inflater.inflate(R.layout.podcast, parent, false)
        val titleView = podcastView.findViewById<TextView>(R.id.podcast_title)
        titleView.text = "${podcast.channelName}/${podcast.title}"
        val durationView = podcastView.findViewById<TextView>(R.id.podcast_duration)
        durationView.text = DateUtil.toMmSs(podcast.durationInSecs)
        val pubDateView = podcastView.findViewById<TextView>(R.id.podcast_pubdate)
        pubDateView.text = DateUtil.toDdMm(podcast.pubDate)
        val descriptionView = podcastView.findViewById<TextView>(R.id.podcast_description)
        descriptionView.text = podcast.description

        val state = podcast.state

        if (state == Podcast.State.DOWNLOADING) {
            durationView.text = "${podcast.downloadPercentage!!}%"
        } else {
            durationView.text = DateUtil.toMmSs(podcast.durationInSecs)
        }

        if (podcast.isPlaying) {
            podcastView.setBackgroundColor(ContextCompat.getColor(context, R.color.podcaststate_playing))
        } else {
            when (state) {
                Podcast.State.NEW -> podcastView.setBackgroundColor(ContextCompat.getColor(context, R.color.podcaststate_new))
                Podcast.State.DOWNLOADING -> podcastView.setBackgroundColor(ContextCompat.getColor(context, R.color.podcaststate_downloading))
                Podcast.State.DOWNLOADED -> podcastView.setBackgroundColor(ContextCompat.getColor(context, R.color.podcaststate_downloaded))
                Podcast.State.COMPLETED -> podcastView.setBackgroundColor(ContextCompat.getColor(context, R.color.podcaststate_completed))
                else -> podcastView.setBackgroundColor(Color.CYAN)
            }
        }
        return podcastView
    }

    fun setSelectedPodcasts(selectedPodcasts: List<Podcast>) {
        this.selectedPodcasts = selectedPodcasts
    }

    override fun getItem(position: Int): Podcast {
        return selectedPodcasts[position]
    }
}
