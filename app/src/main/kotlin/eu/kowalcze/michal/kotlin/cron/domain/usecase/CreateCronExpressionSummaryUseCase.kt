package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.*
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionParserService
import eu.kowalcze.michal.kotlin.cron.logger

class CreateCronExpressionSummaryUseCase
    (
    private val cronExpressionParserService: CronExpressionParserService,
) {
    fun parse(input: CronExpressionLine): CronExpressionSummary {
        logger.debug("Creating summary for: {}", input)

        val cronExpression = cronExpressionParserService.parse(input)

        return CronExpressionSummary(
            minute = Minute.allValues().filter { cronExpression.minute.isMatched(it) },
            hour = Hour.allValues().filter { cronExpression.hour.isMatched(it) },
            dayOfMonth = DayOfMonth.allValues().filter { cronExpression.dayOfMonth.isMatched(it) },
            month = Month.allValues().filter { cronExpression.month.isMatched(it) },
            dayOfWeek = DayOfWeek.allValues().filter { cronExpression.dayOfWeek.isMatched(it) },
            command = cronExpression.command,
        )
    }

    companion object {
        private val logger by logger()
    }
}
