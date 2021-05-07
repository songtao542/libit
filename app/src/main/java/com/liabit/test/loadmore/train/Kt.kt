package com.liabit.test.loadmore.train

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Calls the specified function [block] if predicate == true with `this` value as its argument and returns `this` value.
 */
@SinceKotlin("1.1")
@OptIn(ExperimentalContracts::class)
inline fun <T> T.runIf(predicate: Boolean, block: (T) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (predicate) {
        block(this)
    }
    return this
}
