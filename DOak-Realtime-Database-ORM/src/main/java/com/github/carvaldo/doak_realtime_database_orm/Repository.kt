package com.github.carvaldo.doak_realtime_database_orm

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

abstract class Repository<T>(database: FirebaseDatabase? = null) {
    protected val database: FirebaseDatabase = database ?: requireDatabase()

    companion object {
        private val TAG = Repository::class.java.simpleName
    }

    open fun save(entity: T): Task<Void> {
        val fRef = entity!!::class.java.getAnnotation(Entity::class.java)?.value?.let {
            database.getReference(it).push()
        } ?: throw Exception("${entity!!::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return fRef.setValue(entity).apply {
            this.addOnFailureListener {
                Log.e(TAG, "Error on save entity.", it)
            }
        }
    }

    open fun save(key: String, entity: T): Task<Void> {
        val fRef = entity!!::class.java.getAnnotation(Entity::class.java)?.value?.let {
            database.getReference("$it/$key")
        } ?: throw Exception("${entity!!::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return fRef.setValue(entity).apply {
            this.addOnFailureListener {
                Log.e(TAG, "Error on save entity.", it)
            }
        }
    }

    open fun save(entities: List<T>): List<Task<Void>> {
        val fRef = entities[0]!!::class.java.getAnnotation(Entity::class.java)?.value?.let {
            database.getReference(it)
        } ?: throw Exception("${entities[0]!!::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return entities.map {
            fRef.push().setValue(it).apply {
                this.addOnFailureListener { ex ->
                    Log.e(TAG, "Error on save entity.", ex)
                }
            }
        }
    }

    open fun save(key: String, entities: List<T>): List<Task<Void>> {
        val fRef = entities[0]!!::class.java.getAnnotation(Entity::class.java)?.value?.let {
            database.getReference("$it/$key")
        } ?: throw Exception("${entities[0]!!::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return entities.map {
            fRef.push().setValue(it).apply {
                this.addOnFailureListener { ex ->
                    Log.e(TAG, "Error on save entity.", ex)
                }
            }
        }
    }

    open fun save(key: String, subKeys: List<String>, entities: List<Pair<String, T>>): List<Task<Void>> {
        if (subKeys.size != entities.size) {
            throw Exception("A quantidade de identificadores difere da quantidade de entidades.") // TODO Qualificar Exception.
        }
        val fRef = entities[0]::class.java.getAnnotation(Entity::class.java)?.value?.let {
            database.getReference("$it/$key")
        } ?: throw Exception("${entities[0]::class.java.canonicalName} não foi identificado como uma ${Entity::class.qualifiedName}.")
        return entities.mapIndexed { position, item ->
            fRef.child(subKeys[position]).setValue(item).apply {
                this.addOnFailureListener { ex ->
                    Log.e(TAG, "Error on save entity.", ex)
                }
            }
        }
    }
}