package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.AnyValue
import eu.kowalcze.michal.kotlin.cron.domain.model.CalendarFieldPattern
import eu.kowalcze.michal.kotlin.cron.domain.model.SingleNumberFieldPattern

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