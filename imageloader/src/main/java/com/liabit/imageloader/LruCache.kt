package com.liabit.imageloader

import java.util.*
import kotlin.math.roundToInt

/**
 * A general purpose size limited cache that evicts items using an LRU algorithm. By default every
 * item is assumed to have a size of one. Subclasses can override [.getSize]} to
 * change the size on a per item basis.
 *
 * @param <T> The type of the keys.
 * @param <Y> The type of the values.
 * Constructor for LruCache.
 *
 * @param initialMaxSize The maximum size of the cache, the units must match the units used in [getSize].
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class LruCache<T, Y>(private val initialMaxSize: Long) {

    private val cache: MutableMap<T, Entry<Y>?> = LinkedHashMap(100, 0.75f, true)

    /**
     * Returns the current maximum size of the cache in bytes.
     */
    var maxSize: Long

    /**
     * Returns the sum of the sizes of all items in the cache.
     */
    var currentSize: Long = 0

    init {
        maxSize = initialMaxSize
    }

    /**
     * Sets a size multiplier that will be applied to the size provided in the constructor to put the
     * new size of the cache. If the new size is less than the current size, entries will be evicted
     * until the current size is less than or equal to the new size.
     *
     * @param multiplier The multiplier to apply.
     */
    @Synchronized
    fun setSizeMultiplier(multiplier: Float) {
        require(multiplier >= 0) { "Multiplier must be >= 0" }
        maxSize = (initialMaxSize * multiplier).roundToInt().toLong()
        evict()
    }

    /**
     * Returns the size of a given item, defaulting to one. The units must match those used in the
     * size passed in to the constructor. Subclasses can override this method to return sizes in
     * various units, usually bytes.
     *
     * @param item The item to get the size of.
     */
    protected fun getSize(item: Y?): Int {
        return 1
    }

    /**
     * Returns the number of entries stored in cache.
     */
    @Synchronized
    protected fun getCount(): Int {
        return cache.size
    }

    /**
     * A callback called whenever an item is evicted from the cache. Subclasses can override.
     *
     * @param key  The key of the evicted item.
     * @param item The evicted item.
     */
    protected fun onItemEvicted(key: T, item: Y?) {
        // optional override
    }

    /**
     * Returns true if there is a value for the given key in the cache.
     *
     * @param key The key to check.
     */
    @Synchronized
    operator fun contains(key: T): Boolean {
        return cache.containsKey(key)
    }

    /**
     * Returns the item in the cache for the given key or null if no such item exists.
     *
     * @param key The key to check.
     */
    @Synchronized
    operator fun get(key: T): Y? {
        val entry = cache[key]
        return entry?.value
    }

    /**
     * Adds the given item to the cache with the given key and returns any previous entry for the
     * given key that may have already been in the cache.
     *
     *
     * If the size of the item is larger than the total cache size, the item will not be added to
     * the cache and instead [.onItemEvicted] will be called synchronously with
     * the given key and item.
     *
     *
     * The size of the item is determined by the [.getSize] method. To avoid errors
     * where [.getSize] returns different values for the same object when called at
     * different times, the size value is acquired in `put` and retained until the item is
     * evicted, replaced or removed.
     *
     *
     * If `item` is null the behavior here is a little odd. For the most part it's similar to
     * simply calling [.remove] with the given key. The difference is that calling this
     * method with a null `item` will result in an entry remaining in the cache with a null
     * value and 0 size. The only real consequence is that at some point [.onItemEvicted] may be called with the given `key` and a null value. Ideally we'd make calling
     * this method with a null `item` identical to [.remove] but we're preserving
     * this odd behavior to match older versions :(.
     *
     * @param key  The key to add the item at.
     * @param item The item to add.
     */
    @Synchronized
    fun put(key: T, item: Y?): Y? {
        val itemSize = getSize(item)
        if (itemSize >= maxSize) {
            onItemEvicted(key, item)
            return null
        }
        if (item != null) {
            currentSize += itemSize.toLong()
        }
        val old = cache.put(key, if (item == null) null else Entry(item, itemSize))
        if (old != null) {
            currentSize -= old.size.toLong()
            if (old.value != item) {
                onItemEvicted(key, old.value)
            }
        }
        evict()
        return old?.value
    }

    /**
     * Removes the item at the given key and returns the removed item if present, and null otherwise.
     *
     * @param key The key to remove the item at.
     */
    @Synchronized
    fun remove(key: T): Y? {
        val entry = cache.remove(key) ?: return null
        currentSize -= entry.size.toLong()
        return entry.value
    }

    /**
     * Clears all items in the cache.
     */
    fun clearMemory() {
        trimToSize(0)
    }

    /**
     * Removes the least recently used items from the cache until the current size is less than the
     * given size.
     *
     * @param size The size the cache should be less than.
     */
    @Synchronized
    protected fun trimToSize(size: Long) {
        var last: Map.Entry<T, Entry<Y>?>
        var cacheIterator: MutableIterator<Map.Entry<T, Entry<Y>?>?>
        while (currentSize > size) {
            cacheIterator = cache.entries.iterator()
            last = cacheIterator.next()
            val toRemove = last.value
            currentSize -= toRemove?.size?.toLong() ?: 0
            val key = last.key
            cacheIterator.remove()
            onItemEvicted(key, toRemove?.value)
        }
    }

    private fun evict() {
        trimToSize(maxSize)
    }

    internal class Entry<Y>(val value: Y, val size: Int)

}