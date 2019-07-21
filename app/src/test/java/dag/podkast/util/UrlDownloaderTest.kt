package dag.podkast.util

import org.junit.Test

import java.io.File

class UrlDownloaderTest {

    @Test
    fun testDownload() {
        UrlDownloader.download(
                "https://podkast.nrk.no/fil/ekko_-_et_aktuelt_samfunnsprogram/ekko_-_et_aktuelt_samfunnsprogram_2017-04-25_1250_2775.MP3?stat=1",
                File("C:\\temp\\xx.yy"),
                object : UrlDownloader.ProgressListener {
                    override fun reportProgress(percentage: Int) {
                        println("$percentage%")
                    }
                })
    }
}