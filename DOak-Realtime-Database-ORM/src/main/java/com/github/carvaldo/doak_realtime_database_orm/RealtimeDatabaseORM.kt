package com.github.carvaldo.doak_realtime_database_orm

import com.google.firebase.database.FirebaseDatabase

open class RealtimeDatabaseORM(database: () -> FirebaseDatabase) {
    companion object {
        internal lateinit var database: FirebaseDatabase
    }

    init {
        RealtimeDatabaseORM.database = database()
    }
}

fun requireDatabase() = RealtimeDatabaseORM.database