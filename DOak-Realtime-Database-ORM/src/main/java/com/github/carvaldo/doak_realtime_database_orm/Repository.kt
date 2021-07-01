package com.github.carvaldo.doak_realtime_database_orm

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

abstract class Repository<T>(database: FirebaseDatabase? = null) {
    protected val database: FirebaseDatabase = database ?: requireDatabase()

    companion object {
        private val TAG = Repository::class.java.simpleName
    }

    open fun save(entity: T, id: String? = null): Task<Void> {
        val fRef = entity!!::class.java.getAnnotation(Entity::class.java)?.value?.let {
            return@let if (id == null) {
                database.getReference(it).push()
            } else {
                database.getReference("$it/$id")
            }
        } ?: throw Exception("${entity!!::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return fRef.setValue(entity).apply {
            this.addOnFailureListener {
                Log.e(TAG, "Error on save entity.", it)
            }
        }
    }

    open fun save(entities: List<T>, ids: List<String>? = null) { // TODO Criar retorno Task<Void>
        if (ids != null && ids.isNotEmpty()) {
            assert(entities.size == ids.size) // TODO Definir Exception e mensagem adequada.
        }
        for (i in entities.indices) {
            save(entities[i], ids?.get(i))
        }
    }
}