package eu.kowalcze.michal.kotlin.cron.utils

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec

class ArchitectureTest : FunSpec({

    val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
        .importPackages("eu.kowalcze.michal.kotlin.cron")

    test("should respect layer separation") {
        layeredArchitecture()
            .layer("api").definedBy("eu.kowalcze.michal.kotlin.cron.api..")
            .layer("domain.model").definedBy("eu.kowalcze.michal.kotlin.cron.domain.model..")
            .layer("domain.usecase").definedBy("eu.kowalcze.michal.kotlin.cron.domain.usecase..")
            .layer("config").definedBy("eu.kowalcze.michal.kotlin.cron.config..")
            .layer("platform").definedBy(
                "java..",
                "kotlin..",
                "org.jetbrains.annotations..",
                "org.slf4j..",
                "eu.kowalcze.michal.kotlin.cron.utils.."
            )

            .whereLayer("domain.model").mayOnlyAccessLayers("platform")
            .whereLayer("domain.usecase").mayOnlyAccessLayers("domain.model", "platform")
            .whereLayer("config").mayOnlyAccessLayers("domain.model", "domain.usecase", "platform")
            .whereLayer("api").mayNotBeAccessedByAnyLayer()

            .check(importedClasses)
    }
})
