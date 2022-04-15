package eu.kowalcze.michal.kotlin.cron.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CronExpressionLineTest : FunSpec({

    test("should trim input") {
        CronExpressionLine.from(" to be trimmed ").value shouldBe "to be trimmed"
    }
})
