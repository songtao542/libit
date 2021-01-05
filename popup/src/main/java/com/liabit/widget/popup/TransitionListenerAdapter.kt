package com.liabit.widget.popup

import android.transition.Transition

abstract class TransitionListenerAdapter : Transition.TransitionListener {
    /**
     * {@inheritDoc}
     */
    override fun onTransitionStart(transition: Transition) {}

    /**
     * {@inheritDoc}
     */
    override fun onTransitionEnd(transition: Transition) {}

    /**
     * {@inheritDoc}
     */
    override fun onTransitionCancel(transition: Transition) {}

    /**
     * {@inheritDoc}
     */
    override fun onTransitionPause(transition: Transition) {}

    /**
     * {@inheritDoc}
     */
    override fun onTransitionResume(transition: Transition) {}
}
