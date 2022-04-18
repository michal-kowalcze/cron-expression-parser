package eu.kowalcze.michal.kotlin.cron.api.cmdline

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AppKtTest : StringSpec({

    "should provide proper summary"{
        listOf(
            "1 2 3 4 5 a command is  not as important" to """minute        1
hour          2
day of month  3
month         4
day of week   5
command       a command is  not as important
""",
        ).forAll { (line, expectedOutput) ->
            // given
            val out = ByteArrayOutputStream()
            val app = App(PrintStream(out))

            // when
            app.printSummary(line)

            // then
            String(out.toByteArray()) shouldBe expectedOutput
        }
    }
})