package com.scaffold.cache

import com.scaffold.database.KeyValueDao
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import com.scaffold.database.put

abstract class SerialActionQueue(private val keyValueDao: KeyValueDao) {

    private val executor = SerialExecutor()

    fun enqueue(key: String, data: String? = null) {
        executor.execute(Task(key, data))
    }

    fun delete(key: String) {
        executor.execute(Task(key, null))
    }

    inner class Task(private val key: String, private val data: String?) : Runnable {
        override fun run() {
            if (data != null) {
                keyValueDao.put(key, data)
            } else {
                keyValueDao.remove(key)
            }
        }
    }

    class SerialExecutor : Executor {
        private val tasks: Queue<Runnable> = ArrayDeque()
        private val executor: Executor = Executors.newSingleThreadExecutor()
        private var active: Runnable? = null

        @Synchronized
        override fun execute(r: Runnable) {
            tasks.add(Runnable {
                try {
                    r.run()
                } finally {
                    scheduleNext()
                }
            })
            if (active == null) {
                scheduleNext()
            }
        }

        @Synchronized
        private fun scheduleNext() {
            if (tasks.poll().also { active = it } != null) {
                executor.execute(active)
            }
        }
    }
}