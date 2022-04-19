package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpressionSummary
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.DayOfMonth
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.DayOfWeek
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Hour
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Minute
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.Month
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionParserService
import eu.kowalcze.michal.kotlin.cron.logger

class CreateCronExpressionSummaryUseCase
    (
    private val cronExpressionParserService: CronExpressionParserService,
) {
    fun createSummary(line: CronExpressionLine): CronExpressionSummary {
        logger.debug("Creating summary for: {}", line)

        val cronExpression = cronExpressionParserService.parse(line)

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
