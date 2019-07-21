package dag.podkast.repository

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDateTime

class FileStorageChannel
@JsonCreator
constructor(@param:JsonProperty("id") val id: String,
            @param:JsonProperty("name") val name: String,
            @param:JsonProperty("rssUrl") val rssUrl: String,
            @param:JsonProperty("earliestPubDate") var earliestPubDate: LocalDateTime,
            @param:JsonProperty("selected") var isSelected: Boolean) {

    override fun toString() =
            "Channel{id=$id, name='$name', rssUrl='$rssUrl', earliestPubDate=$earliestPubDate}"
}

