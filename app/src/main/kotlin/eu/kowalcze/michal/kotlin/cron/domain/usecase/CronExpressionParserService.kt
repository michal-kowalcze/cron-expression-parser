package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.*

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

interface ListElementParser {
    fun tryParse(value: String): CalendarFieldPattern?
}

class SingleNumberParser : ListElementParser {

    override fun tryParse(value: String) =
        try {
            SingleNumberFieldPattern(value.toInt())
        } catch (exc: NumberFormatException) {
            null
        }
}

class AnyValueParser : ListElementParser {
    override fun tryParse(value: String) =
        if (value == ALL_VALUES_CHAR) {
            AnyValue()
        } else {
            null
        }

    companion object {
        private const val ALL_VALUES_CHAR = "*"
    }
}