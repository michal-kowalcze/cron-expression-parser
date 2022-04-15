package eu.kowalcze.michal.kotlin.cron.domain.model

data class CronExpression private constructor(
    val fields: List<String>,
) {


    companion object {

        fun createFrom(tokens: List<String>): CronExpression {
            assertTokensCount(tokens)
            return CronExpression(tokens)
        }


        private fun assertTokensCount(tokens: List<String>) {
            if (tokens.size != EXPECTED_TOKENS_COUNT) {
                throw InvalidTokensCountException(tokens.size)
            }
        }
    }
}

private const val EXPECTED_TOKENS_COUNT = 6

class InvalidTokensCountException(
    existingTokensCount: Int,
) : IllegalArgumentException("Found invalid no. of tokens. Expected: $EXPECTED_TOKENS_COUNT, got: $existingTokensCount")