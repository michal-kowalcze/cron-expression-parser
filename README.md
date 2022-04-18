# Cron Expressions Parser

This is a simple [Kotlin](https://kotlinlang.org/) application that parses a
single [cron expression](https://en.wikipedia.org/wiki/Cron#CRON_expression) and displays times when this particular
expression is applicable.

## Usage

In order to run the Cron Expression Parser execute following command:

```bash
./gradlew run --args='"cron-expression-to-parse"' 
```

**IMPORTANT**: remember to use both pair of quotes (`'` and `"`) in order to pass expression as a single argument to the
program.

### Prerequisites for running program

This project uses [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). Gradle Wrapper uses
JVM under the hood, so make sure you have a properly set up JDK. Version greater than or equal to 11 should be more than
fine.

## Development

### Named field vs. general convention

While it is technically possible to have a common denominator for any cron expression field and access field-related
data in an indexed approach (e.g. `cronExpression[MINUTE]`) the current approach uses separate fields in an explicit
way (e.g. `cronExpression.minute`). While the former approach provides greater flexibility while adding/removing fields
the latter is more explicit and simplifies creation of aggregates (`CronExpression`, `CronExpressionSummary`).

### Code style

This project contains `.editorconfig` file. Configure your IDE to use it.

### Dependencies

* [Logback](https://logback.qos.ch/) accessed via [SL4J API](https://www.slf4j.org/)
* [Kotest](https://kotest.io/)