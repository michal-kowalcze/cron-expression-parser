package eu.kowalcze.michal.kotlin.cron.api.cmdline

import eu.kowalcze.michal.kotlin.cron.config.ApplicationConfig
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import java.io.PrintStream

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Expected only one argument, got: ${args.toList()}")
        return
    }

    App(System.out).printSummary(args[0])
}

class App(
    private val out: PrintStream = System.out,
) {

    fun printSummary(line: String) {

        val cronExpressionLine = CronExpressionLine(line)

        val cronExpressionSummary =
            ApplicationConfig.createCronExpressionSummaryUseCase.createSummary(cronExpressionLine)

        printMatchedValues("minute", cronExpressionSummary.minute)
        printMatchedValues("hour", cronExpressionSummary.hour)
        printMatchedValues("day of month", cronExpressionSummary.dayOfMonth)
        printMatchedValues("month", cronExpressionSummary.month)
        printMatchedValues("day of week", cronExpressionSummary.dayOfWeek)
        printMatchedValues("command", cronExpressionSummary.command.value)
    }

    private fun printMatchedValues(fieldName: String, values: List<CalendarField>) {
        printMatchedValues(fieldName, values.map { it.value.toString() }.joinToString(separator = " "))
    }

    private fun printMatchedValues(fieldName: String, value: String) {
        out.format("%-14s%s\n", fieldName, value)
    }
}

