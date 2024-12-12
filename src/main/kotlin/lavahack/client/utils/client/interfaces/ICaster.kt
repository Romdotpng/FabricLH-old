package lavahack.client.utils.client.interfaces

//TODO: rewrite it
interface ICaster<T : Any> {
    fun cast(
        setting : Any
    ) : T
}