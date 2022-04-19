package eu.kowalcze.michal.kotlin.cron.api.cmdline

import eu.kowalcze.michal.kotlin.cron.config.ApplicationConfig
import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import eu.kowalcze.michal.kotlin.cron.utils.logger
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Expected only one argument, got: ${args.toList()}")
        exitProcess(ERROR_INVALID_ARGUMENTS)
    }

    val result = App(System.out).printSummary(args[0])
    exitProcess(result)
}

class App(
    private val out: PrintStream = System.out,
) {

    fun printSummary(line: String) = kotlin.runCatching {
        val cronExpressionLine = CronExpressionLine(line)

        val cronExpressionSummary =
            ApplicationConfig.createCronExpressionSummaryUseCase.createSummary(cronExpressionLine)

        printMatchedValues("minute", cronExpressionSummary.minute)
        printMatchedValues("hour", cronExpressionSummary.hour)
        printMatchedValues("day of month", cronExpressionSummary.dayOfMonth)
        printMatchedValues("month", cronExpressionSummary.month)
        printMatchedValues("day of week", cronExpressionSummary.dayOfWeek)
        printMatchedValues("command", cronExpressionSummary.command.value)
        return OK
    }.onFailure {
        logger.error("Unable to execute use case for: {}", line, it)
        out.print("ERROR: ${it.message}\n")
    }.getOrDefault(ERROR_RUNTIME_EXCEPTION)

    private fun printMatchedValues(fieldName: String, values: List<CalendarField>) {
        printMatchedValues(fieldName, values.map { it.value.toString() }.joinToString(separator = " "))
    }

    private fun printMatchedValues(fieldName: String, value: String) {
        out.format("%-14s%s\n", fieldName, value)
    }


    companion object {
        private val logger by logger()
    }
}

private const val OK = 0
private const val ERROR_INVALID_ARGUMENTS = 1
private const val ERROR_RUNTIME_EXCEPTION = 2

