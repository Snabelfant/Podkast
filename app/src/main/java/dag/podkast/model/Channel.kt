package dag.podkast.model

import dag.podkast.repository.FileStorageChannel
import java.time.LocalDateTime

class Channel(val fileStorageChannel: FileStorageChannel) {

    val id: String
        get() = fileStorageChannel.id

    val earliestPubDate: LocalDateTime
        get() = fileStorageChannel.earliestPubDate

    val name: String
        get() = fileStorageChannel.name

    val rssUrl: String
        get() = fileStorageChannel.rssUrl

    var isSelected: Boolean
        get() = fileStorageChannel.isSelected
        set(isSelected) {
            fileStorageChannel.isSelected = isSelected

        }
}
