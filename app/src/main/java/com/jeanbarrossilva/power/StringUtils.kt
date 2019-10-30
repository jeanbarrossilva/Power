package com.jeanbarrossilva.power

class StringUtils : MainActivity() {
    object Format {
        fun repeat(text: String, times: Int): String? {
            var result = StringBuilder("")

            for (quantity in 0 until times)
                result = result.append(text)

            return if (result.toString() == "") null else result.toString()
        }

        fun hasSquareBracket(text: String): Boolean {
            return text.contains("[") || text.contains("]")
        }

        fun removeSquareBracket(calc: String, squareBracket: Int): String {
            when (squareBracket) {
                REMOVE_SQUARE_BRACKET_LEFT -> calc.replace("[", "")
                REMOVE_SQUARE_BRACKET_RIGHT -> calc.replace("]", "")
                REMOVE_SQUARE_BRACKET_ALL -> {
                    calc.replace("[", "")
                    calc.replace("]", "")
                }
            }

            return calc
        }
    }

    object Punctuation {
        private const val DOT = "."
        private const val COMMA = ","

        @JvmStatic val decimalSeparators = arrayOf(DOT, COMMA)
    }

    object Operator {
        object Stylized {
            const val TIMES = "×"
            const val DIVISION = "÷"
        }
    }

    object Superscript {
        const val POWER_TWO = "²"
        @JvmStatic val powers = arrayOf(POWER_TWO)
    }
}