package lavahack.client.utils.client.enums

enum class Anticheats(
    val rotate : Rotates
) {
    Vanilla(Rotates.None),
    //TODO: idk
    NCP(Rotates.Packet),
    Grim(Rotates.Normal)
}