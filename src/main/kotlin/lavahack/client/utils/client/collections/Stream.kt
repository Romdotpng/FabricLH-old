package lavahack.client.utils.client.collections

/**
 * @author _kisman_
 * @since 13:54 of 17.07.2023
 */
@Suppress("UNCHECKED_CAST")
class Stream<T : Any>(
    private val original : Collection<*>
) {
    private val actions = mutableListOf<Any>()

    fun filter(
        filter : (T) -> Boolean
    ) = this.also {
        val action = object : Filter {
            override fun <K> check(
                element : K
            ) = filter(element as T)
        }

        actions.add(action)
    }

    fun <K : Any> map(
        mapper : (T) -> K
    ) = Stream<K>(original).also {
        val action = object : Mapper {
            override fun <F, S> map(
                element : F
            ) = mapper(element as T) as S
        }

        actions.add(action)

        it.actions.add(actions)
    }

    fun toList() : MutableList<T> {
        val collected = mutableListOf<T>()

        for(element in original) {
            run {
                var mapped = element

                for (action in actions) {
                    when (action) {
                        is Filter -> {
                            if(!action.check(mapped)) {
                                return@run
                            }
                        }
                        is Mapper -> {
                            mapped = action.map(mapped)
                        }
                    }
                }

                collected.add(mapped as T)
            }
        }

        return collected
    }

    interface Action

    interface Filter : Action {
        fun <T> check(
            element : T
        ) : Boolean
    }

    interface Mapper : Action {
        fun <F, S> map(
            element : F
        ) : S
    }
}