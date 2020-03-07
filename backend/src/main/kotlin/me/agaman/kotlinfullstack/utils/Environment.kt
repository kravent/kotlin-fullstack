package me.agaman.kotlinfullstack.utils

import io.ktor.application.Application

val Application.isDevEnvironment get() = configEnvironment == "dev"
val Application.isProdEnvironment get() = configEnvironment != "dev"

private val Application.configEnvironment
    get() = environment.config.propertyOrNull("ktor.deployment.environment")?.getString() ?: "prod"
