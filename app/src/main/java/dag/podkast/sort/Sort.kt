package dag.podkast.sort

import dag.podkast.model.Podcast
import java.util.*

private val C_CHANNEL = Comparator { p1: Podcast, p2: Podcast -> p1.channelName.compareTo(p2.channelName) }
private val C_PUBDATE = Comparator { p1: Podcast, p2: Podcast -> p1.pubDate.compareTo(p2.pubDate) }
private val C_DOWNLOADED = Comparator { p1: Podcast, p2: Podcast ->
    if (p1.state == Podcast.State.DOWNLOADED) {
        if (p2.state == Podcast.State.DOWNLOADED) 0 else -1
    } else {
        if (p2.state == Podcast.State.DOWNLOADED) 1 else 0
    }
}

private val C_ISPLAYING = Comparator { p1: Podcast, p2: Podcast -> if (p1.isPlaying) -1 else if (p2.isPlaying) 1 else 0 }


enum class Sort(val description: String, val podcastComparator: Comparator<Podcast>) {
    CHANNELP_PUBDATEP("Kanal+,dato+", C_ISPLAYING.thenComparing(C_CHANNEL).thenComparing(C_PUBDATE)),
    CHANNELP_PUBDATEM("Kanal+,dato-", C_ISPLAYING.thenComparing(C_CHANNEL).thenComparing(C_PUBDATE.reversed())),
    PUBDATEP_CHANNELP("Dato+, kanal+", C_ISPLAYING.thenComparing(C_PUBDATE).thenComparing(C_CHANNEL)),
    PUBDATEM_CHANNELP("Dato-, kanal+", C_ISPLAYING.thenComparing(C_PUBDATE.reversed()).thenComparing(C_CHANNEL)),
    DOWNLOADED_PUBDATEP_CHANNELP("Nedlastet, dato+, kanal+", C_ISPLAYING.thenComparing(C_DOWNLOADED).thenComparing(C_PUBDATE).thenComparing(C_CHANNEL));

    fun compare(p1: Podcast, p2: Podcast) = podcastComparator.compare(p1, p2)

    override fun toString() = description

    companion object {
        fun getSortIndex(sort: Sort): Int {
            return sorts.indexOf(sort)
        }

        val sorts: List<Sort>
            get() = listOf(*values())
    }
}
