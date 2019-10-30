package com.jeanbarrossilva.power

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import android.widget.TextView

class LengthFragment : CalculatorFragment() {
    private lateinit var inputSymbol: TextView

    private lateinit var conversionResult: TextView
    private lateinit var conversionSymbolResult: TextView

    private lateinit var unit: Button
    private lateinit var options: Array<Button>

    private lateinit var lightYear: Button
    private lateinit var kilometer: Button
    private lateinit var hectometer: Button
    private lateinit var decameter: Button
    private lateinit var mile: Button
    private lateinit var meter: Button
    private lateinit var decimeter: Button
    private lateinit var centimeter: Button
    private lateinit var millimeter: Button
    private lateinit var micrometer: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_calculator_length, container, false)

        input = fragmentView.findViewById(R.id.input)
        inputSymbol = fragmentView.findViewById(R.id.input_symbol)

        input.isFocusable = false

        unit = fragmentView.findViewById(R.id.unit)

        conversionResult = fragmentView.findViewById(R.id.option_conversion_number_result)
        conversionSymbolResult = fragmentView.findViewById(R.id.option_conversion_symbol_result)

        val othersHorizontalView = fragmentView.findViewById<HorizontalScrollView>(R.id.others_horizontal_scroll_view)
        othersHorizontalView.isHorizontalScrollBarEnabled = false

        lightYear = fragmentView.findViewById(R.id.light_year)
        kilometer = fragmentView.findViewById(R.id.kilometer)
        hectometer = fragmentView.findViewById(R.id.hectometer)
        decameter = fragmentView.findViewById(R.id.decameter)
        mile = fragmentView.findViewById(R.id.mile)
        meter = fragmentView.findViewById(R.id.meter)
        decimeter = fragmentView.findViewById(R.id.decimeter)
        centimeter = fragmentView.findViewById(R.id.centimeter)
        millimeter = fragmentView.findViewById(R.id.millimeter)
        micrometer = fragmentView.findViewById(R.id.micrometer)

        options = arrayOf (
                lightYear,
                kilometer,
                hectometer,
                decameter,
                mile,
                meter,
                decimeter,
                centimeter,
                millimeter,
                micrometer
        )

        keypadButtons[10] = fragmentView.findViewById(R.id.decimal_separator)
        calculatorMode = fragmentView.findViewById(R.id.calculator_mode)
        delete = fragmentView.findViewById(R.id.delete)

        units()
        inputOption()

        // Default configuration (meter to kilometer).
        inputSymbol.text = getString(R.string.meter_symbol)
        (activity as MainActivity).selectButton(meter, options, R.drawable.option_clicked)
        (activity as MainActivity).preferencesEditor.putString("convertFrom", "meter")
                .apply()

        unit.text = getString(R.string.kilometer)
        conversionSymbolResult.text = getString(R.string.kilometer_symbol)
        (activity as MainActivity).preferencesEditor.putString("convertTo", "kilometer")
                .apply()

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

                    inflater.inflate(R.menu.units_length, units.menu)
                    units.show()

                    units.setOnMenuItemClickListener { item ->
                        when {
                            item.title == getString(R.string.light_year) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "lightYear")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.light_year_symbol)
                            }

                            item.title == getString(R.string.kilometer) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "kilometer")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.kilometer_symbol)
                                unit.text = getString(R.string.kilometer)
                            }

                            item.title == getString(R.string.hectometer) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "hectometer")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.hectometer_symbol)
                                unit.text = getString(R.string.hectometer)
                            }

                            item.title == getString(R.string.decameter) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "decameter")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.decameter_symbol)
                                unit.text = getString(R.string.decameter)
                            }

                            item.title == getString(R.string.mile) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "mile")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.mile_symbol)
                                unit.text = getString(R.string.mile)
                            }

                            item.title == getString(R.string.meter) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "meter")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.meter_symbol)
                                unit.text = getString(R.string.meter)
                            }

                            item.title == getString(R.string.mile) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "mile")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.mile_symbol)
                                unit.text = getString(R.string.mile)
                            }

                            item.title == getString(R.string.decimeter) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "decimeter")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.decimeter_symbol)
                                unit.text = getString(R.string.decimeter)
                            }

                            item.title == getString(R.string.centimeter) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "centimeter")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.centimeter_symbol)
                                unit.text = getString(R.string.centimeter)
                            }

                            item.title == getString(R.string.millimeter) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "millimeter")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.millimeter_symbol)
                                unit.text = getString(R.string.millimeter)
                            }

                            item.title == getString(R.string.micrometer) -> {
                                (activity as MainActivity).preferencesEditor.putString("convertTo", "micrometer")
                                        .apply()

                                conversionSymbolResult.text = getString(R.string.micrometer_symbol)
                                unit.text = getString(R.string.micrometer)
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
        lightYear.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(lightYear, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    lightYear.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(lightYear, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.light_year_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "lightYear")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        kilometer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(kilometer, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    kilometer.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(kilometer, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.kilometer_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "kilometer")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        hectometer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(hectometer, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    hectometer.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(hectometer, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.hectometer_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "hectometer")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        decameter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(decameter, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    decameter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(decameter, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.decameter_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "decameter")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        mile.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(mile, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    meter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(mile, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.mile_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "mile")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        meter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(meter, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    meter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(meter, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.meter_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "meter")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        decimeter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(decimeter, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    decimeter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(decimeter, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.decimeter_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "decimeter")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        centimeter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(centimeter, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    centimeter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(centimeter, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.centimeter_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "centimeter")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        millimeter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(millimeter, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    millimeter.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(millimeter, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.millimeter_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "millimeter")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }

        micrometer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> (activity as MainActivity).bounceIn(micrometer, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    micrometer.startAnimation((activity as MainActivity).bounceOut)

                    (activity as MainActivity).selectButton(micrometer, options, R.drawable.option_clicked)
                    inputSymbol.text = getString(R.string.micrometer_symbol)

                    (activity as MainActivity).preferencesEditor.putString("convertFrom", "micrometer")
                            .apply()
                }
            }

            (activity as MainActivity).calc(input, conversionResult, conversionSymbolResult)

            true
        }
    }
}