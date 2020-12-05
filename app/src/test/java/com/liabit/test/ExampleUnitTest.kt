package com.liabit.test

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val hump = "FragmentTestPickerBinding"

        println("underline:" + humpToUnderline(hump))

    }

    private val humpPattern: Pattern = Pattern.compile("[A-Z]")

    private fun humpToUnderline(str: String): String {
        val matcher: Matcher = humpPattern.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            val match = matcher.group(0)
            if (match != null) {
                matcher.appendReplacement(sb, "_" + match.toLowerCase())
            }
        }
        matcher.appendTail(sb)
        val index = sb.lastIndexOf("_")
        return sb.substring(1).substring(0, index - 1)
    }

}
