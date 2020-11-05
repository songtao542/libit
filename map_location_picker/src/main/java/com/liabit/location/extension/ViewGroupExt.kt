package com.liabit.location.extension

import android.view.View
import android.view.ViewGroup

fun ViewGroup.asSequence(): Sequence<View> = object : Sequence<View> {

    override fun iterator(): Iterator<View> = object : Iterator<View> {
        private var nextValue: View? = null
        private var done = false
        private var position: Int = 0

        override fun hasNext(): Boolean {
            if (nextValue == null && !done) {
                nextValue = getChildAt(position)
                position++
                if (nextValue == null)
                    done = true
            }
            return nextValue != null
        }

        override fun next(): View {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val answer = nextValue
            nextValue = null
            return answer!!
        }
    }
}

fun ViewGroup.forEach(action: (View) -> Unit) {
    asSequence().forEach(action)
}

fun ViewGroup.forEach(action: (Int, View) -> Unit) {
    for ((index, view) in asSequence().withIndex()) {
        action(index, view)
    }
}
