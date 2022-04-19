package eu.kowalcze.michal.kotlin.cron.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <TYPE : Any> TYPE.logger(): Lazy<Logger> =
    lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass.name)) }

private fun unwrapCompanionClass(name: String): String =
    if (name.endsWith(COMPANION_SUFFIX)) {
        name.subSequence(0, name.length - COMPANION_SUFFIX.length).toString()
    } else {
        name
    }

private const val COMPANION_SUFFIX = "\$Companion"
