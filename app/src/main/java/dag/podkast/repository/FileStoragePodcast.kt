package dag.podkast.repository

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDateTime

class FileStoragePodcast
@JsonCreator
constructor(@param:JsonProperty("channelId") val channelId: String,
            @param:JsonProperty("title") val title: String,
            @param:JsonProperty("description") var description: String?,
            @param:JsonProperty("pubDate") var pubDate: LocalDateTime,
            @param:JsonProperty("guid") val guid: String,
            @param:JsonProperty("downloadUrl") var downloadUrl: String,
            @param:JsonProperty("lengthInBytes") val lengthInBytes: Int?,
            @param:JsonProperty("durationInSecs") val durationInSecs: Int,
            @param:JsonProperty("localFilePath") var localFilePath: String?,
            @param:JsonProperty("downloadDate") var downloadDate: LocalDateTime?,
            @param:JsonProperty("completed") var isCompleted: Boolean,
            @param:JsonProperty("deleted") private var deleted: Boolean) {

    var isDeleted: Boolean
        get() = deleted
        set(deleted) {
            this.deleted = deleted
            isCompleted = true
        }
}

