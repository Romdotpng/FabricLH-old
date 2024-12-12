package lavahack.client.utils.client.interfaces.impl

import lavahack.client.utils.client.enums.BindTypes
import lavahack.client.utils.client.interfaces.IBindable

open class Binder(
    override val buttonName : String,
    override var type : BindTypes = BindTypes.Keyboard,
    override var keyboardKey : Int = -1,
    override var mouseButton : Int = -1,
    override var hold : Boolean = false
) : IBindable