package dag.podkast.util

import java.io.*

object UrlDownloader {
    private const val BUFFER_SIZE = 8192

    @Throws(IOException::class)
    fun download(url: String, file: File, progressListener: ProgressListener) {
        val length = UrlStream.getLength(url)
        val inputStream = UrlStream.getInputStream(url)
        copy(inputStream, BufferedOutputStream(FileOutputStream(file)), progressListener, length)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, sink: OutputStream, progressListener: ProgressListener, length: Int): Int {
        var nread = 0
        val buf = ByteArray(BUFFER_SIZE)
        while (true) {
            val n = source.read(buf)
            if (n == -1) {
                break
            }

            sink.write(buf, 0, n)
            nread += n
            reportProgress(progressListener, nread, length)
        }

        reportProgress(progressListener, length, length)
        sink.close()
        return nread
    }

    private fun reportProgress(progressListener: ProgressListener?, bytesRead: Int, length: Int) {
        progressListener?.let {
            val progressPercentage = if (length == -1) -1 else ((bytesRead * 100L) / length).toInt()
            it.reportProgress(progressPercentage)
        }
    }

    interface ProgressListener {
        fun reportProgress(percentage: Int)
    }
}
