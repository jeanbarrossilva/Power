package com.jeanbarrossilva.power

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import android.widget.TextView

class TimeFragment : CalculatorFragment() {
    private lateinit var inputSymbol: TextView

    private lateinit var conversionResult: TextView
    private lateinit var conversionSymbolResult: TextView

    private lateinit var unit: Button
    private lateinit var options: Array<Button>

    private lateinit var year: Button
    private lateinit var month: Button
    private lateinit var day: Button
    private lateinit var hour: Button
    private lateinit var minute: Button
    private lateinit var second: Button
    private lateinit var millisecond: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_calculator_time, container, false)

        input = fragmentView.findViewById(R.id.input)
        inputSymbol = fragmentView.findViewById(R.id.input_symbol)

        input.isFocusable = false

        unit = fragmentView.findViewById(R.id.unit)

        conversionResult = fragmentView.findViewById(R.id.option_conversion_number_result)
        conversionSymbolResult = fragmentView.findViewById(R.id.option_conversion_symbol_result)

        val othersHorizontalView = fragmentView.findViewById<HorizontalScrollView>(R.id.others_horizontal_scroll_view)
        othersHorizontalView.isHorizontalScrollBarEnabled = false

        year = fragmentView.findViewById(R.id.year)
        month = fragmentView.findViewById(R.id.month)
        day = fragmentView.findViewById(R.id.day)
        hour = fragmentView.findViewById(R.id.hour)
        minute = fragmentView.findViewById(R.id.minute)
        second = fragmentView.findViewById(R.id.second)
        millisecond = fragmentView.findViewById(R.id.millisecond)

        options = arrayOf(
                year,
                month,
                day,
                hour,
                minute,
                second,
                millisecond
        )

        keypadButtons[10] = fragmentView.findViewById(R.id.decimal_separator)
        calculatorMode = fragmentView.findViewById(R.id.calculator_mode)
        delete = fragmentView.findViewById(R.id.delete)

        units()
        inputOption()

        try {
            // Default configuration (hour to minute).
            inputSymbol.text = getString(R.string.hour_symbol)
            (activity as MainActivity).selectButton(hour, options, R.drawable.option_clicked)
            (activity as MainActivity).preferences.edit().putString("convertFrom", "hour")
                    .apply()

            unit.text = getString(R.string.minute)
            conversionSymbolResult.text = getString(R.string.minute_symbol)
            (activity as MainActivity).preferences.edit().putString("convertTo", "minute")
                    .apply()
        } catch (exception: Exception) {
            (activity as MainActivity).alertError.show()
        }

        (activity as MainActivity).calculatorMode(context, calculatorMode)

        (activity as MainActivity).inputNumber(fragmentView, input, conversionResult, conversionSymbolResult, input.text.toString())
        inputDecimalSeparator(input)
        (activity as MainActivity).delete(input, delete, conversionResult, conversionSymbolResult)

        return fragmentView
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun units() {
        unit.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(unit, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    val units = PopupMenu(context, unit)
                    val inflater: MenuInflater

                    inflater = units.menuInflater

                    inflater.inflate(R.menu.units_time, units.menu)
                    units.show()

                    units.setOnMenuItemClickListener { item ->
                        when {
                            item.title == getString(R.string.year) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "year")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.year_symbol)
                                unit.text = getString(R.string.year)
                            }

                            item.title == getString(R.string.month) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "month")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.month_symbol)
                                unit.text = getString(R.string.month)
                            }

                            item.title == getString(R.string.day) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "day")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.day_symbol)
                                unit.text = getString(R.string.day)
                            }

                            item.title == getString(R.string.hour) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "hour")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.hour_symbol)
                                unit.text = getString(R.string.hour)
                            }

                            item.title == getString(R.string.minute) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "minute")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.minute_symbol)
                                unit.text = getString(R.string.minute)
                            }

                            item.title == getString(R.string.second) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "second")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.second_symbol)
                                unit.text = getString(R.string.second)
                            }

                            item.title == getString(R.string.millisecond) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "millisecond")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.millisecond_symbol)
                                unit.text = getString(R.string.millisecond)
                            }
                        }

                        (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

                        true
                    }
                }
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun inputOption() {
        year.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(year, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    year.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(year, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.year_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "year")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        month.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(month, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    month.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(month, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.month_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "month")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        day.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(day, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    day.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(day, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.day_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "day")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        hour.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(hour, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    hour.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(hour, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.hour_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "hour")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        minute.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(minute, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    minute.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(minute, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.minute_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "minute")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        second.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(second, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    second.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(second, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.second_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "second")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        millisecond.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(millisecond, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    millisecond.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(millisecond, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.millisecond_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "millisecond")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }
    }
}