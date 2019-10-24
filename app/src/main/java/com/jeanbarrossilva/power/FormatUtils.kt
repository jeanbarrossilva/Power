package com.jeanbarrossilva.power

import java.util.*

class FormatUtils : MainActivity() {
    companion object {
        fun generateId(text: String): Int {
            val insertable = charArrayOf('e', StringUtils.Punctuation.LEFT_PARENTHESIS.single(), StringUtils.Punctuation.RIGHT_PARENTHESIS.single(), StringUtils.Operator.Raw.PLUS.single(), StringUtils.Operator.Raw.MINUS.single(), StringUtils.Operator.Stylized.TIMES.single(), StringUtils.Operator.Stylized.DIVISION.single())

            for (character in text.toLowerCase(Locale.getDefault()).toCharArray())
                return insertable.indexOf(character)

            return ERROR
        }

        fun repeat(text: String, times: Int): String? {
            var result = StringBuilder(StringUtils.EMPTY)

            for (quantity in 0 until times)
                result = result.append(text)

            return if (result.toString() == StringUtils.EMPTY) null else result.toString()
        }

        fun hasSquareBracket(text: String): Boolean {
            return text.contains(StringUtils.Punctuation.LEFT_SQUARE_BRACKET) || text.contains(StringUtils.Punctuation.RIGHT_SQUARE_BRACKET)
        }

        fun removeSquareBracket(calc: String, squareBracket: Int): String {
            when (squareBracket) {
                REMOVE_SQUARE_BRACKET_LEFT -> calc.replace(StringUtils.Punctuation.LEFT_SQUARE_BRACKET, StringUtils.EMPTY)
                REMOVE_SQUARE_BRACKET_RIGHT -> calc.replace(StringUtils.Punctuation.RIGHT_SQUARE_BRACKET, StringUtils.EMPTY)
                REMOVE_SQUARE_BRACKET_ALL -> {
                    calc.replace(StringUtils.Punctuation.LEFT_SQUARE_BRACKET, StringUtils.EMPTY)
                    calc.replace(StringUtils.Punctuation.RIGHT_SQUARE_BRACKET, StringUtils.EMPTY)
                }
            }

            return calc
        }
    }
}