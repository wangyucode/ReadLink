package cn.wycode.aidu

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val regex = "^(https|http)://.*".toRegex()
        val address = "http://www.runoob.com/regexp/regexp-tutorial.html"
        assert(address.matches(regex))
    }
}
