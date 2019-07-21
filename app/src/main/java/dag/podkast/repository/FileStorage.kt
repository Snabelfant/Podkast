package dag.podkast.repository

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import dag.podkast.model.Channel
import dag.podkast.model.Podcast
import dag.podkast.sort.Sort
import dag.podkast.util.JsonMapper
import dag.podkast.util.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileStorage {
    private val PODCASTS = "podkast.json"

    fun load(appDir: File): Triple<List<Channel>, List<Podcast>, Sort> {
        val file = File(appDir, PODCASTS)
        Logger.info("Podkast leses fra ${file.absolutePath}")
        val fileStorageData = JsonMapper.read(FileInputStream(file), object : TypeReference<FileStorageData>() {})
        val channels = fileStorageData.fileStorageChannels.map { Channel(it) }
        val podcasts = fileStorageData.fileStoragePodcasts.map { Podcast(it, "(Tittel mangler)") }
        val sort = fileStorageData.sort

        return Triple(channels, podcasts, sort)
    }


    fun save(appDir: File, sort: Sort, channels: Collection<Channel>, podcasts: Collection<Podcast>) {
        val fileStorageData = FileStorageData()
        fileStorageData.sort = sort
        fileStorageData.fileStorageChannels = channels.map { it.fileStorageChannel }
        fileStorageData.fileStoragePodcasts = podcasts.map { it.fileStoragePodcast }
        val file = File(appDir, PODCASTS)
        val outputStream = FileOutputStream(file)
        Logger.info("Podkast skrives til ${file.absolutePath}")
        JsonMapper.write(outputStream, fileStorageData, true)
    }

    private class FileStorageData {
        @JsonProperty("sort")
        lateinit var sort: Sort
        @JsonProperty("channels")
        lateinit var fileStorageChannels: List<FileStorageChannel>
        @JsonProperty("podcasts")
        lateinit var fileStoragePodcasts: List<FileStoragePodcast>
    }
}
