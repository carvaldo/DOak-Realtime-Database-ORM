package com.github.carvaldo.doak_realtime_database_orm

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME) // TODO("Mudar para SOURCE?")
annotation class Entity(val value: String)
