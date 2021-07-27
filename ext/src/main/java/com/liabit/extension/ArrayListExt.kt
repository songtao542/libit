package com.liabit.extension

/**
 * @param elements
 * @param checkIsSelf true: 如果 elements == this 则不执行 addAll
 */
fun <T> ArrayList<T>.addAll(elements: Collection<T>, checkIsSelf: Boolean) {
    if (checkIsSelf) {
        if (elements !== this) {
            addAll(elements)
        }
    } else {
        addAll(elements)
    }
}

/**
 * 如果 elements == this 则不执行 addAll
 */
fun <T> ArrayList<T>.addAllOrNot(elements: Collection<T>) {
    addAll(elements, true)
}


/**
 * @param elements
 * @param checkIsSelf true: 如果 elements == this 则不执行 addAll
 */
fun <T> MutableList<T>.addAll(elements: Collection<T>, checkIsSelf: Boolean) {
    if (checkIsSelf) {
        if (elements !== this) {
            addAll(elements)
        }
    } else {
        addAll(elements)
    }
}

/**
 * 如果 elements == this 则不执行 addAll
 */
fun <T> MutableList<T>.addAllOrNot(elements: Collection<T>) {
    addAll(elements, true)
}

/**
 * @param select
 */
fun <T> List<T>.select(select: (one: T, other: T) -> T): T? {
    if (isEmpty()) return null
    if (size == 1) return get(0)
    var result: T = get(0)
    for (i in 1 until size) {
        result = select(result, get(i))
    }
    return result
}