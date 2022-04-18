package eu.kowalcze.michal.kotlin.cron.config

import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionParserService
import eu.kowalcze.michal.kotlin.cron.domain.usecase.CreateCronExpressionSummaryUseCase

object ApplicationConfig {

    val createCronExpressionSummaryUseCase = CreateCronExpressionSummaryUseCase(CronExpressionParserService())
}