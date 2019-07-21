package dag.podkast.model

import dag.podkast.rss.RssItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.jsoup.Jsoup
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.util.regex.Pattern

class PodcastTest {

    @Test
    fun testMatches() {
        val regex = "abC"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        var podcast: Podcast

        podcast = create("tittel", null)
        assertFalse(podcast.matches(pattern))

        podcast = create("passerikke", "ikkedenneheller")
        assertFalse(podcast.matches(pattern))

        podcast = create("XaBcY", null)
        assertTrue(podcast.matches(pattern))

        podcast = create("tittel", "XaBcY")
        assertTrue(podcast.matches(pattern))

        podcast = create("tittel", "XaBcY\nlinje2\nlinje3\n")
        assertTrue(podcast.matches(pattern))

        podcast = create("tittel", "linje1\nXaB\ncY\n\nlinje3\n")
        assertFalse(podcast.matches(pattern))
    }

    @Test
    fun testJsoup() {
        val html = "<p class=\"p1\"><span>Our lives again.</span></p>\n<p class=\"p1\"><span>In this episode he point.</span></p>\n<p class=\"p1\"><span>With <a href=\"https://twitter.com/kristenschaaled?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor\"><span>Kristen Schaal</span></a> and <a href=\"https://www.kurtbraunohler.com/\"><span>Kurt Braunohler</span></a>,  <a href=\"http://www.alexbellos.com/\"><span>Alex Bellos</span></a>, <a href=\"http://www.stevenstrogatz.com/\"><span>Steven Strogatz</span></a>, <a href=\"http://jannalevin.com/\"><span>Janna Levin</span></a>, and <a href=\"http://melaniethernstrom.com/\"><span>Melanie Thernstrom</span></a>. Plus <a href=\"https://www.laguardiahs.org/\"><span>Laguardia Arts High School</span></a> singers Nathaniel Sabat, Julian Soto, Eli Greenhoe, Kelly Efthimiu, Julia Egan, and Ruby Froom.</span></p>\n<p class=\"p1\"><span>You can find the video Christine Campbell made of her mom Mary Sue <a href=\"https://www.youtube.com/watch?v=N3fA5uzWDU8\"><span>here</span></a>.</span></p>\n<div class=\"story__details\">\n<div id=\"ember1273\" class=\"ember-view\">\n<div id=\"ember1282\" class=\"article-tabs ivy-tabs nypr-tabs ember-view\">\n<div aria-hidden=\"false\" id=\"ember1302\" role=\"tabpanel\" class=\"ivy-tabs-tabpanel active ember-view\" aria-labelledby=\"ember1296\" tabindex=\"0\">\n<div class=\"story__body\">\n<div id=\"ember1319\" class=\"ember-view\">\n<div class=\"django-content\">\n<div>\n<p class=\"p2\"><span><em><em><em><em>Support Radiolab today at <a href=\"https://pledge3.wnyc.org/donate/radiolab-it/onestep/?utm_source=podcast&amp;utm_medium=notes&amp;utm_campaign=membership&amp;utm_content=radiolab\" target=\"_blank\">Radiolab.org/donate</a>. </em></em></em><br></em></span></p>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n<div id=\"ember1328\" class=\"story-credits ember-view\">\n<div class=\"story-credits__appearance-credits\"></div>\n</div>\n"
        val text = Jsoup.parse(html).text()
        assertThat(text, `is`("Our lives again. In this episode he point. With Kristen Schaal and Kurt Braunohler, Alex Bellos, Steven Strogatz, Janna Levin, and Melanie Thernstrom. Plus Laguardia Arts High School singers Nathaniel Sabat, Julian Soto, Eli Greenhoe, Kelly Efthimiu, Julia Egan, and Ruby Froom. You can find the video Christine Campbell made of her mom Mary Sue here. Support Radiolab today at Radiolab.org/donate."))
    }

    @Test
    fun testHtmlCleaning() {
        val html = "<p class=\"p1\"><span>Our lives again.</span></p>\n<p class=\"p1\"><span>In this episode he point.</span></p>\n<p class=\"p1\"><span>With <a href=\"https://twitter.com/kristenschaaled?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor\"><span>Kristen Schaal</span></a> and <a href=\"https://www.kurtbraunohler.com/\"><span>Kurt Braunohler</span></a>,  <a href=\"http://www.alexbellos.com/\"><span>Alex Bellos</span></a>, <a href=\"http://www.stevenstrogatz.com/\"><span>Steven Strogatz</span></a>, <a href=\"http://jannalevin.com/\"><span>Janna Levin</span></a>, and <a href=\"http://melaniethernstrom.com/\"><span>Melanie Thernstrom</span></a>. Plus <a href=\"https://www.laguardiahs.org/\"><span>Laguardia Arts High School</span></a> singers Nathaniel Sabat, Julian Soto, Eli Greenhoe, Kelly Efthimiu, Julia Egan, and Ruby Froom.</span></p>\n<p class=\"p1\"><span>You can find the video Christine Campbell made of her mom Mary Sue <a href=\"https://www.youtube.com/watch?v=N3fA5uzWDU8\"><span>here</span></a>.</span></p>\n<div class=\"story__details\">\n<div id=\"ember1273\" class=\"ember-view\">\n<div id=\"ember1282\" class=\"article-tabs ivy-tabs nypr-tabs ember-view\">\n<div aria-hidden=\"false\" id=\"ember1302\" role=\"tabpanel\" class=\"ivy-tabs-tabpanel active ember-view\" aria-labelledby=\"ember1296\" tabindex=\"0\">\n<div class=\"story__body\">\n<div id=\"ember1319\" class=\"ember-view\">\n<div class=\"django-content\">\n<div>\n<p class=\"p2\"><span><em><em><em><em>Support Radiolab today at <a href=\"https://pledge3.wnyc.org/donate/radiolab-it/onestep/?utm_source=podcast&amp;utm_medium=notes&amp;utm_campaign=membership&amp;utm_content=radiolab\" target=\"_blank\">Radiolab.org/donate</a>. </em></em></em><br></em></span></p>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n<div id=\"ember1328\" class=\"story-credits ember-view\">\n<div class=\"story-credits__appearance-credits\"></div>\n</div>\n"
        val rssItem = RssItem()
        rssItem.description = html
        rssItem.title = "Tittel"
        rssItem.setPubDate("Wed, 17 Jul 2019 08:45:04 +0200")
        rssItem.setDurationInSecs("0:01:01")
        rssItem.guid = "guid-65"
        rssItem.url = "http://x.y"
        val podcast = Podcast.fromRssItem("id", "navn", rssItem)
        assertThat(podcast.description, `is`("Our lives again. In this episode he point. With Kristen Schaal and Kurt Braunohler, Alex Bellos, Steven Strogatz, Janna Levin, and Melanie Thernstrom. Plus Laguardia Arts High School singers Nathaniel Sabat, Julian Soto, Eli Greenhoe, Kelly Efthimiu, Julia Egan, and Ruby Froom. You can find the video Christine Campbell made of her mom Mary Sue here. Support Radiolab today at Radiolab.org/donate."))
    }

    private fun create(title: String, description: String?): Podcast {
        return Podcast("x", "X", title, description, LocalDateTime.now(), "abc", "http://x.no", 0, 0, null, null, false, false)
    }
}
