package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.Command
import eu.kowalcze.michal.kotlin.cron.domain.model.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.PossibleValues
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.ParserInput

class CronExpressionParserService {

    private val parsers = listOf(
        SingleNumberParser(),
        AnyValueParser(),
    )

    //TODO add range limits
    fun parse(parserInput: ParserInput) = CronExpression(
        minute = parseFieldPattern(parserInput.minute),
        hour = parseFieldPattern(parserInput.hour),
        dayOfMonth = parseFieldPattern(parserInput.dayOfMonth),
        month = parseFieldPattern(parserInput.month),
        dayOfWeek = parseFieldPattern(parserInput.dayOfWeek),
        command = Command(parserInput.command),
    )

    private fun parseFieldPattern(pattern: String): CalendarFieldPattern {
        val parsedFields = pattern.split(",")
            .map { parseExceptComma(it) }

        return if (parsedFields.size == 1) {
            parsedFields[0]
        } else {
            PossibleValues(parsedFields)
        }
    }

    private fun parseExceptComma(pattern: String): CalendarFieldPattern =
        parsers.mapNotNull { it.tryParse(pattern) }
            .firstOrNull() ?: throw FieldPatternNotMatched(pattern)
}

class FieldPatternNotMatched(pattern: String) :
    IllegalArgumentException("Provided value: '${pattern}' is not recognizable by any of known parsers")

