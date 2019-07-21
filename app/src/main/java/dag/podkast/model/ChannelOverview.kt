package dag.podkast.model

class ChannelOverview(channel: Channel, podcasts: Podcasts) {
    val channelId = channel.id
    val channelName = channel.name
    var isSelected = channel.isSelected
    val deletedPodcasts = podcasts.getCompletedCount(channel)
    val remainingPodcasts = podcasts.getRemainingCount(channel)
    val remainingSeconds = podcasts.getRemainingSeconds(channel)
}
