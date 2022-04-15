package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.CronExpressionLine

class ParseCronExpressionUseCase {

    fun parse(input: CronExpressionLine): CronExpression {
        val match = CRON_EXPRESSION_FIELDS.matchEntire(input.value)
            ?: throw CronExpressionNotMatched(input)

        val tokens = match.groupValues.subList(1, match.groupValues.size)
        return CronExpression.createFrom(tokens)
    }

}

private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")

class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException("Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS")