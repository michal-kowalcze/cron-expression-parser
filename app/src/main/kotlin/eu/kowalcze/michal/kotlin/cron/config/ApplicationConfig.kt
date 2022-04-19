package eu.kowalcze.michal.kotlin.cron.config

import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionParserService
import eu.kowalcze.michal.kotlin.cron.domain.usecase.CreateCronExpressionSummaryUseCase

object ApplicationConfig {

    private val cronExpressionParserService = CronExpressionParserService()
    val createCronExpressionSummaryUseCase = CreateCronExpressionSummaryUseCase(cronExpressionParserService)
}