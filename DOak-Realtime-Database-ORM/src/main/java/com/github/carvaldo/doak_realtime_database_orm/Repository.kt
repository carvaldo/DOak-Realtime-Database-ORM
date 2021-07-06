package com.github.carvaldo.doak_realtime_database_orm

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

/**
 * Repository
 *
 * @param T Class that will be persisted.
 * @constructor
 *
 * @param database FirebaseDatabase
 */
abstract class Repository<T>(database: FirebaseDatabase? = null) {
    protected val database: FirebaseDatabase = database ?: requireDatabase()

    companion object {
        private val TAG = Repository::class.java.simpleName
    }


    /**
     * Persists the entity with a random key.
     *
     * @param entity Entity to be persisted.
     * @return Task<Void>
     */
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

    /**
     * Persists the entity with a specific key.
     *
     * @param key Key for the entity.
     * @param entity Entity to be persisted.
     * @return Task<Void>
     */
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

    /**
     * Persist entities with random keys.
     *
     * @param entities List of entities that will be persisted.
     * @return List<Task<Void>>
     */
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

    /**
     * Save an entity grouping under a key.
     *
     * @param key Master key for an entity list.
     * @param entities List of entities that will be persisted.
     * @return List<Task<Void>>
     */
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

    /**
     * Updates an entity grouping under a master key
     *
     * @param key Master key for an entity list.
     * @param subKeys Key list of entities to be updated
     * @param entities List of entities that will be updated.
     * @return List<Task<Void>>
     */
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