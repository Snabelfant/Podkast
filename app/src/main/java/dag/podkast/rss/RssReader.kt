package dag.podkast.rss

import dag.podkast.util.PodcastException
import dag.podkast.util.UrlStream
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

object RssReader {

    fun validateRssUrl(rssUrl: String?) {
        if (rssUrl.isNullOrBlank()) {
            throw PodcastException("Må ha rss-url")
        }

        try {
            URL(rssUrl)
        } catch (e: Exception) {
            throw PodcastException(e.toString())
        }
    }

    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun getRssItems(rssUrl: String, earliestPubDate: LocalDateTime) = getRssItems(UrlStream.getInputStream(rssUrl), earliestPubDate)

    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun getRssItems(inputStream: InputStream, earliestPubDate: LocalDateTime): List<RssItem> {
        val document = getDocument(inputStream)
        val rssItems = ArrayList<RssItem>()
        processChildren(document.documentElement, rssItems, earliestPubDate)
        return rssItems
    }

    private fun processChildren(startElement: Element, rssItems: MutableList<RssItem>, earliestPubDate: LocalDateTime) {
        val childNodes = startElement.childNodes

        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                if ("item" == element.tagName) {
                    val rssItem = toRssItem(element)
                    if (earliestPubDate.isBefore(rssItem.pubDate)) {
                        rssItems.add(rssItem)
                    }
                } else {
                    processChildren(element, rssItems, earliestPubDate)
                }
            }
        }
    }

    private fun toRssItem(topElement: Element): RssItem {
        val rssItem = RssItem()
        val children = topElement.childNodes

        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element

                val tag = element.tagName

                if ("itunes:duration" == tag) {
                    rssItem.setDurationInSecs(element.textContent)
                }
                if ("title" == tag) {
                    rssItem.title = element.textContent
                }

                if ("description" == tag) {
                    rssItem.description = element.textContent
                }

                if ("pubDate" == tag) {
                    rssItem.setPubDate(element.textContent)
                }

                if ("guid" == tag) {
                    rssItem.guid = element.textContent
                }


                if ("enclosure" == tag) {
                    val attributes = element.attributes

                    for (j in 0 until attributes.length) {
                        val attribute = attributes.item(j)
                        val attributeName = (attribute as Attr).name
                        if ("url" == attributeName) {
                            rssItem.url = attribute.getTextContent()
                        }
                        if ("length" == attributeName) {
                            rssItem.setLengthInBytes(attribute.getTextContent())
                        }
                        if ("type" == attributeName) {
                            rssItem.mimeType = attribute.getTextContent()
                        }
                    }
                }
            }
        }

        return rssItem
    }

    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    private fun getDocument(inputStream: InputStream): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val db = factory.newDocumentBuilder()
        val inputSource = InputSource(inputStream)
        return db.parse(inputSource)
    }
}
