package lavahack.client.utils

import lavahack.client.features.module.Module
import java.util.*

/**
 * @author Ai_24
 * @version 0.9
 * @since 20/07/2023
 *
 * in the future i will rework it to make it decimal and better
 */

class ModuleRating {
    val modules = mutableListOf<Module>()

    /**
     * Calculates the module name rating based on the user input
     *
     * @return A List with the active Modules
     */
    fun modulesVisibility(
        input : String,
        precision : Boolean,
        aliases : Boolean
    ) : List<Module> {
        val endRes = mutableListOf<Module>()
        val userInput = input.lowercase(Locale.getDefault()).replace("\\s+".toRegex(), "")


        // module name rating
        var topRating = -1

        for (module in modules) {
            val names = moduleNames(module, aliases)

            for (s in names) {
                val rating = ratingCalc(s, userInput)

                if (rating > topRating) {
                    topRating = rating
                }
            }
        }

        // rating check
        for (module in modules) {
            val names = moduleNames(module, aliases)

            for (s in names) {
                val rating = ratingCalc(s.lowercase(), userInput)

                if (rating >= topRating - (if (precision) 0 else 1)) {
                    endRes.add(module)
                }
            }
        }

        return endRes
    }

    private fun moduleNames(
        module : Module,
        aliases : Boolean
    ) : List<String> {
        val names = mutableListOf<String>()

        if (module.info.aliases.isNotEmpty() && aliases) {
            for (s in module.info.aliases.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                names.add(s)
            }
        }

        names.add(module.name)

        return names
    }

    private fun ratingCalc(
        moduleName : String,
        userInput : String
    ) : Int {
        var rating = 0

        // check if the word is literally the same
        if (moduleName.equals(userInput, ignoreCase = true)) {
            rating = 100
        } else {
            if (moduleName.startsWith(userInput)) {
                rating++
            }

            if (userInput.length == 1 && moduleName.contains(userInput)) { // check for 1 char
                rating = 100
            } else { // else we continue to try combinations of 3+ words and count how many times does it match
                for (i in 1..userInput.length) {
                    val moduleNameLength = moduleName.length

                    if (moduleNameLength > i) {
                        var index = 0

                        while (index <= moduleNameLength - i) {
                            if (index + i <= userInput.length) {
                                val subSequence = userInput.substring(index, index + i)

                                if (moduleName.contains(subSequence)) {
                                    rating += subSequence.length
                                }
                            }

                            index++
                        }
                    }
                }
            }
        }

        return rating
    }
}