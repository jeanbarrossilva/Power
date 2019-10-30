package com.jeanbarrossilva.power

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.jeanbarrossilva.power.MainActivity.Companion.LAST_PARENTHESIS_LEFT
import com.jeanbarrossilva.power.MainActivity.Companion.LAST_PARENTHESIS_RIGHT
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*

open class CalculatorFragment : Fragment() {
    lateinit var fragmentView: View
    internal lateinit var context: Context

    private lateinit var mainActivity: MainActivity

    private lateinit var keypadIn: Animation
    private lateinit var keypadOut: Animation

    private var isScientific: Boolean = false

    private val keypad: View by lazy {
        fragmentView.findViewById<View>(R.id.keypad_delete).findViewById<View>(R.id.keypad)
    }

    val keypadButtons: Array<Button> by lazy {
        arrayOf<Button>(
                keypad.findViewById(R.id.zero),
                keypad.findViewById(R.id.one),
                keypad.findViewById(R.id.two),
                keypad.findViewById(R.id.three),
                keypad.findViewById(R.id.four),
                keypad.findViewById(R.id.five),
                keypad.findViewById(R.id.six),
                keypad.findViewById(R.id.seven),
                keypad.findViewById(R.id.eight),
                keypad.findViewById(R.id.nine),
                keypad.findViewById(R.id.decimal_separator)
        )
    }

    internal lateinit var input: EditText

    private lateinit var othersHorizontalScrollView: HorizontalScrollView

    private lateinit var number: Button

    private lateinit var parenthesis: Array<Button>

    private lateinit var operator: Button
    private val operators = intArrayOf(R.id.plus, R.id.minus, R.id.times, R.id.division)

    lateinit var calculatorMode: ImageButton
    lateinit var delete: ImageButton

    private lateinit var equal: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context

        if (context != this)
            isScientific = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_calculator, container, false)
        context = Objects.requireNonNull<Context>(getContext())

        mainActivity = activity as MainActivity

        keypadIn = AnimationUtils.loadAnimation(context, R.anim.keypad_in)
        keypadOut = AnimationUtils.loadAnimation(context, R.anim.keypad_out)

        input = fragmentView.findViewById(R.id.input)

        // Disables the keyboard, since the app already has predefined buttons.
        input.isFocusable = false

        parenthesis = arrayOf(fragmentView.findViewById(R.id.left_parenthesis), fragmentView.findViewById(R.id.right_parenthesis))

        othersHorizontalScrollView = fragmentView.findViewById(R.id.others_horizontal_scroll_view)
        othersHorizontalScrollView.isHorizontalScrollBarEnabled = false

        calculatorMode = fragmentView.findViewById(R.id.calculator_mode)
        delete = fragmentView.findViewById(R.id.delete)

        equal = fragmentView.findViewById(R.id.equal)

        mainActivity.calculatorMode(context, calculatorMode)

        inputNumber(input)
        inputDecimalSeparator(input)
        inputOperator(input)
        inputParenthesis(input)

        delete(input, delete)
        calc(false)

        return fragmentView
    }

    private fun isInputLastNumber(input: EditText): Boolean {
        return if (input.text.toString().isNotEmpty()) Character.isDigit(input.text.toString()[input.text.toString().length - 1]) else false

    }

    private fun isInputLastDecimalSeparator(input: EditText): Boolean {
        for (decimalSeparator in StringUtils.Punctuation.decimalSeparators) {
            return input.text.toString().endsWith(decimalSeparator)
        }

        return false
    }

    private fun isInputLastOperator(input: EditText): Boolean {
        val operators = arrayOf("+", "-", "*", "/")

        for (operator in operators) {
            if (input.text.toString().isNotEmpty()) {
                val buffer = StringBuilder(input.text.toString())
                return buffer[input.length() - 1] == operator[0]
            }
        }

        return false
    }

    private fun isInputLastParenthesis(input: EditText, parenthesis: Int): Boolean {
        when (parenthesis) {
            LAST_PARENTHESIS_LEFT -> return input.text.toString().endsWith("(")
            LAST_PARENTHESIS_RIGHT -> return input.text.toString().endsWith(")")
        }

        return false
    }

    private fun inputFormat(input: EditText, result: String) {
        var result = result
        if (!(isInputLastOperator(input) && isInputLastDecimalSeparator(input)))
            try {
                if (result.contains("E")) {
                    result = result.replace("E", "e")
                }

                if (result.endsWith(".0")) {
                    result = result.replace(".0", "")
                }

                if (result.contains("Infinity")) {
                    result = result.replace("Infinity", "∞")
                }

                if (result.length > 16) {
                    result = result.substring(17, result.length - 1)
                }

                input.setText(result)

                if (mainActivity.shareUsageData)
                    println("Calc result: $result")
            } catch (exception: Exception) {
                input.setText(getString(R.string.error))
            }
        else {
            input.setText("0")
        }
    }

    /**
     * Inputs a number in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun inputNumber(input: EditText) {
        val onTouchListener = View.OnTouchListener { view, event ->
            number = view as Button

            when (event.action) {
                MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    number.startAnimation(mainActivity.bounceOut)

                    if (input.text.toString() != getString(R.string.error)) {
                        input.append(number.text)
                    } else {
                        input.setText("")

                        number = view
                        input.append(number.text)
                    }
                }
            }

            true
        }

        for (number in mainActivity.numbers) {
            fragmentView.findViewById<View>(number).setOnTouchListener(onTouchListener)
        }
    }

    /**
     * Inputs a decimal separator in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun inputDecimalSeparator(input: EditText) {
        try {
            keypadButtons[10].setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 5.0)
                    MotionEvent.ACTION_UP -> {
                        view.startAnimation(mainActivity.bounceOut)

                        if (isInputLastNumber(input))
                            input.append(".")
                    }
                }

                true
            }
        } catch (nullPointerException: NullPointerException) {
            nullPointerException.printStackTrace()
        }
    }

    /**
     * Inputs an operator in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun inputOperator(input: EditText) {
        val onTouchListener = View.OnTouchListener { view, event ->
            operator = view as Button

            when (event.action) {
                MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    operator.startAnimation(mainActivity.bounceOut)

                    for (power in StringUtils.Superscript.powers) {
                        if (!isInputLastOperator(input)) {
                            if (operator.id == R.id.plus || operator.id == R.id.minus || input.text.toString().endsWith(power)) {
                                if (input.text.toString().isNotEmpty())
                                    input.append(" " + operator.text + " ")
                                else if ((isInputLastNumber(input) || isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT) || isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT)) and !isInputLastOperator(input))
                                    input.append(operator.text.toString() + " ")
                                else
                                    input.append(" " + operator.text + " ")
                            } else if (operator.id == R.id.times || operator.id == R.id.division) {
                                if (!(input.text.toString().isEmpty() && isInputLastDecimalSeparator(input) && isInputLastParenthesis(input, LAST_PARENTHESIS_LEFT)))
                                    input.append(" " + operator.text + " ")
                            }
                        }
                    }
                }
            }

            true
        }

        for (operator in operators) {
            fragmentView.findViewById<View>(operator).setOnTouchListener(onTouchListener)
        }
    }

    /**
     * Inputs a parenthesis in a text field.
     *
     * @param input Represents the text field itself.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun inputParenthesis(input: EditText) {
        val listener = View.OnTouchListener { view, event ->
            val parenthesis = view as Button

            when (event.action) {
                MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    parenthesis.startAnimation(mainActivity.bounceOut)

                    for (parenthesisButton in this@CalculatorFragment.parenthesis) {
                        when (parenthesisButton.id) {
                            R.id.left_parenthesis -> if (!isInputLastDecimalSeparator(input))
                                input.append("(")
                            else if (isInputLastNumber(input))
                                input.append(" " + StringUtils.Operator.Stylized.TIMES + " " + parenthesis)
                            R.id.right_parenthesis -> if (isInputLastNumber(input))
                                input.append(")")
                        }
                    }
                }
            }

            true
        }

        for (parenthesis in parenthesis) {
            fragmentView.findViewById<View>(parenthesis.id).setOnTouchListener(listener)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun delete(input: EditText, delete: ImageButton) {
        delete.setOnTouchListener { view, event ->
            val calc = input.text.toString()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 1.0)
                MotionEvent.ACTION_UP -> {
                    delete.startAnimation(mainActivity.bounceOut)
                    delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                    if (calc.isNotEmpty())
                        try {
                            input.setText(if (calc.endsWith(" ")) calc.substring(0, calc.length - 2) else calc.substring(0, calc.length - 1))
                        } catch (exception: Exception) {
                            input.setText("")
                        }

                }
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun calc(cancel: Boolean) {
        equal.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> equal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                MotionEvent.ACTION_UP -> {
                    if (!cancel) {
                        val expression: Expression

                        if (input.text.toString().isNotEmpty()) {
                            expression = ExpressionBuilder(mainActivity.reformatCalc(input.text.toString())!!).build()

                            try {
                                val result = expression.evaluate().toString()
                                input.setText(result)
                            } catch (exception: Exception) {
                                // If the calc is unfinished, "String.valueOf(expression.evaluate())" throws an IllegalArgumentException.
                                if (input.text.toString().isNotEmpty())
                                    input.append(if (input.text.toString().endsWith(" ")) getString(R.string.zero) else " " + getString(R.string.zero))
                                else
                                    input.append(getString(R.string.zero))

                                equal.performClick()
                            }

                            val previousCalc = input.text.toString()

                            inputFormat(input, input.text.toString())
                        }
                    }

                    if (mainActivity.shareUsageData) {
                        println("\"isScientific\": $isScientific")
                    }
                }
            }

            false
        }

        equal.setOnLongClickListener {
            isScientific = !isScientific
            scientific()

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun scientific() {
        calc(true)

        keypad.startAnimation(if (isScientific) keypadOut else keypadIn)

        for (keypadButton in keypadButtons) {
            for (index in keypadButtons.indices) {
                if (isScientific) {
                    if (keypadButtons[index].id == keypadButton.id) {
                        keypadButton.tag = "placeholder$index"
                        break
                    }
                }

                if (keypadButton.tag != "placeholder10")
                    keypadButton.text = index.toString()
                else
                    keypadButton.text = "."
            }

            keypadButton.setTypeface(keypadButton.typeface, if (isScientific) Typeface.NORMAL else Typeface.BOLD)
            if (isScientific) {
                when (keypadButton.id) {
                    R.id.seven -> {
                        keypadButton.tag = "powerTwo"

                        // Dynamic text preview.
                        input.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                if (isScientific) {
                                    keypadButton.text = if (s.toString().length == 1 && isInputLastNumber(input)) s.toString()[0] + StringUtils.Superscript.POWER_TWO else getString(R.string.x_power_two)
                                } else {
                                    keypadButton.text = getString(R.string.seven)
                                }
                            }

                            override fun afterTextChanged(s: Editable) {

                            }
                        })

                        keypadButton.setOnTouchListener { view, event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> mainActivity.bounceIn(view, 0.5, 5.0)
                                MotionEvent.ACTION_UP -> {
                                    view.startAnimation(mainActivity.bounceOut)

                                    if (isInputLastNumber(input) && isInputLastParenthesis(input, LAST_PARENTHESIS_RIGHT))
                                        input.append(StringUtils.Superscript.POWER_TWO)
                                }
                            }

                            true
                        }
                    }
                }
            } else {
                inputNumber(input)
            }
        }
    }
}