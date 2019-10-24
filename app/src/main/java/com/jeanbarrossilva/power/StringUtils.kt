package com.jeanbarrossilva.power

class StringUtils : MainActivity() {
    companion object {
        const val EMPTY = ""
        const val SPACE = " "
        const val INFINITY = "∞"
    }

    object Punctuation {
        const val HYPHEN = "-"
        const val COLON = ":"
        const val LEFT_PARENTHESIS = "("
        const val RIGHT_PARENTHESIS = ")"
        const val LEFT_SQUARE_BRACKET = "["
        const val RIGHT_SQUARE_BRACKET = "]"
        const val DOT = "."
        const val COMMA = ","
        @JvmStatic val decimalSeparators = arrayOf(DOT, COMMA)
    }

    object Operator {
        object Raw {
            const val PLUS = "+"
            const val MINUS = "-"
            const val TIMES = "*"
            const val DIVISION = "/"
        }

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