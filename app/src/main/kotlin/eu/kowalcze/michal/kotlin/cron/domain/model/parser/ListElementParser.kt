package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.AnyValueFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.SingleNumberFieldPattern

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
            AnyValueFieldPattern
        } else {
            null
        }

    companion object {
        private const val ALL_VALUES_CHAR = "*"
    }
}