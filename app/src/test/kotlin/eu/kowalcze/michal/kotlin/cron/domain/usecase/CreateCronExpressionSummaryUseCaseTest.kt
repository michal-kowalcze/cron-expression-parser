package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.config.ApplicationConfig
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpressionSummary
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

internal class CreateCronExpressionSummaryUseCaseTest : FunSpec({
    val tested = ApplicationConfig.createCronExpressionSummaryUseCase

    test("should provide proper summary for match-all") {
        // given
        val line = CronExpressionLine("* * * * * a command with some space-separated arguments")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes IntRange(0, 59).distinct()
        cronExpressionSummary shouldHaveAllHours IntRange(0, 23).distinct()
        cronExpressionSummary shouldHaveAllDaysOfMonth IntRange(1, 31).distinct()
        cronExpressionSummary shouldHaveAllMonths IntRange(1, 12).distinct()
        cronExpressionSummary shouldHaveAllDaysOfWeek IntRange(1, 7).distinct()
        cronExpressionSummary.command.value shouldBe "a command with some space-separated arguments"
    }

    test("should provide proper summary for value-specific") {
        // given
        val line = CronExpressionLine("1 1,2,5 1,10 6 7 simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes listOf(1)
        cronExpressionSummary shouldHaveAllHours listOf(1, 2, 5)
        cronExpressionSummary shouldHaveAllDaysOfMonth listOf(1, 10)
        cronExpressionSummary shouldHaveAllMonths listOf(6)
        cronExpressionSummary shouldHaveAllDaysOfWeek listOf(7)
        cronExpressionSummary.command.value shouldBe "simple-command"
    }


    test("should provide proper summary for range") {
        // given
        val line = CronExpressionLine("1-4 1-5 1-10 6-8 5-7 simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes listOf(1, 2, 3, 4)
        cronExpressionSummary shouldHaveAllHours listOf(1, 2, 3, 4, 5)
        cronExpressionSummary shouldHaveAllDaysOfMonth listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        cronExpressionSummary shouldHaveAllMonths listOf(6, 7, 8)
        cronExpressionSummary shouldHaveAllDaysOfWeek listOf(5, 6, 7)
        cronExpressionSummary.command.value shouldBe "simple-command"
    }

    test("should provide proper summary for range for named months") {
        // given
        val line = CronExpressionLine("1-4 1-5 1-10 JAN-dec 5-7 simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes listOf(1, 2, 3, 4)
        cronExpressionSummary shouldHaveAllHours listOf(1, 2, 3, 4, 5)
        cronExpressionSummary shouldHaveAllDaysOfMonth listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        cronExpressionSummary shouldHaveAllMonths listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        cronExpressionSummary shouldHaveAllDaysOfWeek listOf(5, 6, 7)
        cronExpressionSummary.command.value shouldBe "simple-command"
    }
    test("should provide proper summary for range with step") {
        // given
        val line = CronExpressionLine("1-4/2 1-5/3 1-10/20 6-8/1 5-7/2 simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes listOf(1, 3)
        cronExpressionSummary shouldHaveAllHours listOf(1, 4)
        cronExpressionSummary shouldHaveAllDaysOfMonth listOf(1)
        cronExpressionSummary shouldHaveAllMonths listOf(6, 7, 8)
        cronExpressionSummary shouldHaveAllDaysOfWeek listOf(5, 7)
        cronExpressionSummary.command.value shouldBe "simple-command"
    }

    test("should provide proper summary for range with step and a year ") {
        // given
        val line = CronExpressionLine("1-4/2 1-5/3 1-10/20 6-8/1 5-7/2 2022 simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllYears listOf(2022)
    }

    test("should provide proper summary for any with step") {
        // given
        val line = CronExpressionLine("*/20 */8 */10 */4 */1     simple-command")

        // when
        val cronExpressionSummary = tested.createSummary(line)

        // then
        cronExpressionSummary shouldHaveAllMinutes listOf(0, 20, 40)
        cronExpressionSummary shouldHaveAllHours listOf(0, 8, 16)
        cronExpressionSummary shouldHaveAllDaysOfMonth listOf(1, 11, 21, 31)
        cronExpressionSummary shouldHaveAllMonths listOf(1, 5, 9)
        cronExpressionSummary shouldHaveAllDaysOfWeek listOf(1, 2, 3, 4, 5, 6, 7)
        cronExpressionSummary.command.value shouldBe "simple-command"
    }
})


infix fun CronExpressionSummary.shouldHaveAllMinutes(values: Collection<Int>) {
    minute.map { it.value } shouldContainAll values
}

infix fun CronExpressionSummary.shouldHaveAllHours(values: Collection<Int>) {
    hour.map { it.value } shouldContainAll values
}

infix fun CronExpressionSummary.shouldHaveAllDaysOfMonth(values: Collection<Int>) {
    dayOfMonth.map { it.value } shouldContainAll values
}

infix fun CronExpressionSummary.shouldHaveAllMonths(values: Collection<Int>) {
    month.map { it.value } shouldContainAll values
}

infix fun CronExpressionSummary.shouldHaveAllDaysOfWeek(values: Collection<Int>) {
    dayOfWeek.map { it.value } shouldContainAll values
}
infix fun CronExpressionSummary.shouldHaveAllYears(values: Collection<Int>) {
    year!!.map { it.value } shouldContainAll values
}