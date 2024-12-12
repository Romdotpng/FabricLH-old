package lavahack.client.utils.client.interfaces.impl

import lavahack.client.features.subsystem.subsystems.DefaultAnimatorController
import lavahack.client.utils.Animator
import lavahack.client.utils.client.interfaces.IAnimatorContext

/**
 * @author _kisman_
 * @since 16:13 of 04.07.2023
 */
@Suppress("PropertyName")
class StateAnimator(
    animator : IAnimatorContext = DefaultAnimatorController.DEFAULT_ANIMATOR_CONTEXT
) {
    val ENABLE_ANIMATOR = Animator(animator, false)
    val ENABLE_ANIMATOR2 = Animator(animator, false)
    val DISABLE_ANIMATOR = Animator(animator, true)

    private var prevState : Boolean? = null

    fun update(
        state : Boolean
    ) {
        if(state) {
            if(prevState == null || !prevState!!) {
                ENABLE_ANIMATOR.reset()
            }

            ENABLE_ANIMATOR.update()
            ENABLE_ANIMATOR2.reset()
            DISABLE_ANIMATOR.reset()
        } else {
            DISABLE_ANIMATOR.update()
            ENABLE_ANIMATOR2.update()
            ENABLE_ANIMATOR.update()
        }

        prevState = state
    }
}