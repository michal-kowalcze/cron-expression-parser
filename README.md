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

### General approach

This project is structured according to the Hexagonal
/ [Clean](https://blog.allegro.tech/2021/12/clean-architecture-story.html) Architecture approach and
uses [Domain-Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design) concepts.

|  Package  | Content |
|-----------|---------|
| api       | Contain outside world connector |
| config    | Configures / connects domain objects |
| domain.model | Contains domain model |
| domain.usecase | list of use cases |

### Execution flow

1. `App` class is responsible for translating command line arguments to the domain format.
2. `CreateCronExpressionSummaryUseCase` orchestrates execution flow:
    1. `CronExpressionParserService` uses internal list of parsers to create `CronExpression` object
       with `CalendarFieldPattern` values.
    2. Use case uses helper methods to generate all possible values for fields and filter only relevant.

### Field patterns

Hierarchy under `CalendarFieldPattern` is responsible for parsing cron fields.

In case you would like to introduce a new pattern remember to make following changes:

* Add a new type of `ListElementParser` - to support extracting pattern values from the string input.
* Update `CronExpressionParserService.parsers` field to use created parser.
* Add a new type of `CalendarFieldPattern` to support matching for provided values in case existing fields are not
  sufficient.

### Cron fields

While it is technically possible to have a common denominator for any cron expression field and access field-related
data in an indexed approach (e.g. `cronExpression[MINUTE]`) the current approach uses separate fields in an explicit
way (e.g. `cronExpression.minute`). While the former approach provides greater flexibility while adding/removing fields
the latter simplifies creation of aggregates (`CronExpression`, `CronExpressionSummary`) and with the current constant
list of fields it is easier to maintain the code.

### Logic in CreateCronExpressionSummaryUseCase

As the `generate all values for all fields` logic is very simple there was no point in extracting this logic to a
separate service / service method.

### Dependency injection

As the code base is very simple the dependencies between beans are managed manually. The `ApplicationConfig` class holds
values for all domain object (and corresponds to
the [Spring](https://spring.io/projects/spring-framework) `ApplicationContext` instance).

### Code style

This project contains `.editorconfig` file. Configure your IDE to use it.

### Dependencies

* [Logback](https://logback.qos.ch/) accessed via [SL4J API](https://www.slf4j.org/)
* [Kotest](https://kotest.io/)