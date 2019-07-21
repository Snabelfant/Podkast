package dag.podkast.util

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.LocalDateTime

class DateUtilTest {

    @Test
    fun toDdMm() {
        assertThat(DateUtil.toDdMm(LocalDateTime.of(2019, 5, 7, 1, 2)), `is`("07.05"))
    }

    @Test
    fun toMmSs() {
        assertThat(DateUtil.toMmSs(0), `is`(" 0:00"))
        assertThat(DateUtil.toMmSs(60), `is`(" 1:00"))
        assertThat(DateUtil.toMmSs(3601), `is`("60:01"))
    }

    @Test
    fun toHhMmSs() {
        assertThat(DateUtil.toHhMmSs(0), `is`("   0:00:00"))
        assertThat(DateUtil.toHhMmSs(60), `is`("   0:01:00"))
        assertThat(DateUtil.toHhMmSs(3601), `is`("   1:00:01"))
        assertThat(DateUtil.toHhMmSs(3601876), `is`("1000:31:16"))
    }
}