package com.jeanbarrossilva.power

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import android.widget.TextView

class TemperatureFragment : CalculatorFragment() {
    private lateinit var inputSymbol: TextView

    private lateinit var conversionResult: TextView
    private lateinit var conversionSymbolResult: TextView

    private lateinit var unit: Button
    private lateinit var options: Array<Button>

    private lateinit var celsius: Button
    private lateinit var fahrenheit: Button
    private lateinit var kelvin: Button
    private lateinit var rankine: Button
    private lateinit var reaumur: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_calculator_temperature, container, false)

        input = fragmentView.findViewById(R.id.input)
        inputSymbol = fragmentView.findViewById(R.id.input_symbol)

        input.isFocusable = false

        unit = fragmentView.findViewById(R.id.unit)

        conversionResult = fragmentView.findViewById(R.id.option_conversion_number_result)
        conversionSymbolResult = fragmentView.findViewById(R.id.option_conversion_symbol_result)

        val othersHorizontalView = fragmentView.findViewById<HorizontalScrollView>(R.id.others_horizontal_scroll_view)
        othersHorizontalView.isHorizontalScrollBarEnabled = false

        celsius = fragmentView.findViewById(R.id.celsius)
        fahrenheit = fragmentView.findViewById(R.id.fahrenheit)
        kelvin = fragmentView.findViewById(R.id.kelvin)
        rankine = fragmentView.findViewById(R.id.rankine)
        reaumur = fragmentView.findViewById(R.id.reaumur)

        options = arrayOf(
                celsius,
                fahrenheit,
                kelvin,
                rankine,
                reaumur
        )

        keypadButtons[10] = fragmentView.findViewById(R.id.decimal_separator)
        calculatorMode = fragmentView.findViewById(R.id.calculator_mode)
        delete = fragmentView.findViewById(R.id.delete)

        units()
        inputOption()

        try {
            // Default configuration (Celsius to Fahrenheit).
            inputSymbol.text = getString(R.string.celsius_symbol)
            (activity as MainActivity).selectButton(celsius, options, R.drawable.option_clicked)
            (activity as MainActivity).preferencesEditor.putString("convertFrom", "celsius")
                    .apply()

            unit.text = getString(R.string.fahrenheit)
            conversionSymbolResult.text = getString(R.string.fahrenheit_symbol)
            (activity as MainActivity).preferences.edit().putString("convertTo", "fahrenheit")
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
                    val units = PopupMenu(getContext(), unit)
                    val inflater: MenuInflater

                    inflater = units.menuInflater

                    inflater.inflate(R.menu.units_temperature, units.menu)
                    units.show()

                    units.setOnMenuItemClickListener { item ->
                        when {
                            item.title == getString(R.string.celsius) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "celsius")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.celsius_symbol)
                                unit.text = getString(R.string.celsius)
                            }

                            item.title == getString(R.string.fahrenheit) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "fahrenheit")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.fahrenheit_symbol)
                                unit.text = getString(R.string.fahrenheit)
                            }

                            item.title == getString(R.string.kelvin) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "kelvin")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.kelvin_symbol)
                                unit.text = getString(R.string.kelvin)
                            }

                            item.title == getString(R.string.rankine) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "rankine")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.rankine_symbol)
                                unit.text = getString(R.string.rankine)
                            }

                            item.title == getString(R.string.reaumur) -> {
                                (activity as MainActivity).preferences.edit().putString("convertTo", "reaumur")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.reaumur_symbol)
                                unit.text = getString(R.string.reaumur)
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
        celsius.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(celsius, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    celsius.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(celsius, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.celsius_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "celsius")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        fahrenheit.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(fahrenheit, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    fahrenheit.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(fahrenheit, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.fahrenheit_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "fahrenheit")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        kelvin.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(kelvin, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    kelvin.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(kelvin, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.kelvin_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "kelvin")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        rankine.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(rankine, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    rankine.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(rankine, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.rankine_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "rankine")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        reaumur.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(reaumur, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    reaumur.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(reaumur, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.reaumur_symbol)

                    (activity as MainActivity).preferences.edit().putString("convertFrom", "reaumur")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }
    }
}