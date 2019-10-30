package com.jeanbarrossilva.power

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

open class MainActivity : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    lateinit var preferencesEditor: SharedPreferences.Editor

    private lateinit var appName: String
    lateinit var versionName: String

    private lateinit var deviceInfoName: String
    private lateinit var deviceInfo: String

    var shareUsageData: Boolean = false
        set(share) {
            preferencesEditor.putBoolean("shareUsageData", share)
                    .apply()

            field = share

            println("\"shareUsageData\": $shareUsageData")
        }

    private lateinit var calculatorFragment: CalculatorFragment
    private lateinit var layoutInfo: ConstraintLayout

    var isNightEnabled: Boolean = false

    lateinit var alertError: Dialog

    // private Button dialogBuyProButton;

    lateinit var dialogFeedback: Dialog
    lateinit var dialogFeedbackEmailButton: ConstraintLayout

    lateinit var dialogOK: Dialog
    lateinit var dialogOKTitle: TextView
    lateinit var dialogOKMessage: TextView

    lateinit var dialogYesNo: Dialog
    lateinit var dialogYesNoTitle: TextView
    lateinit var dialogYesNoMessage: TextView
    lateinit var dialogYesNoYesButton: Button
    lateinit var dialogYesNoNoButton: Button

    lateinit var dialogInput: Dialog
    lateinit var dialogInputField: EditText

    private lateinit var bounceInterpolator: BounceInterpolator

    private lateinit var format: DecimalFormat

    private lateinit var className: String

    private lateinit var timer: Timer
    private lateinit var unit: String

    private lateinit var number: Button
    val numbers = arrayOf(R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine)

    val bounceOut: Animation
        get() {
            val bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out)
            bounceOut.interpolator = bounceInterpolator

            return bounceOut
        }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        preferencesEditor = preferences.edit()

        val deviceName = Settings.Secure.getString(contentResolver, "bluetooth_name")

        appName = getString(R.string.app_name)
        versionName = BuildConfig.VERSION_NAME

        shareUsageData = preferences.getBoolean("shareUsageData", false)

        calculatorFragment = CalculatorFragment()

        layoutInfo = findViewById(R.id.layout_info)
        layoutInfo.visibility = View.GONE

        isNightEnabled = preferences.getBoolean("isNightEnabled", false)

        format = DecimalFormat("#.##")

        className = localClassName

        timer = Timer()

        deviceInfoName = "${getString(R.string.name)}: $deviceName"
        deviceInfo =
                "${getString(R.string.device_model)}: ${Build.MANUFACTURER} ${Build.MODEL}" + "\n" +
                "${getString(R.string.so_version)}: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}"

        setFragment(calculatorFragment)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        bottomNav()

        alerts()
        dialogs()
    }

    override fun onResume() {
        super.onResume()

        if (preferences.getBoolean("isFirstTime", true)) {
            // Defines that, for now on, it isn't the first time the user's opening the app.
            preferencesEditor.putBoolean("isFirstTime", false)
                    .apply()

            dialogWelcome()
        }
    }

    private fun setFragment(fragment: Fragment?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_frame, fragment!!)
        fragmentTransaction.commit()
    }

    private fun currentFragment(): String {
        val fragments = supportFragmentManager.fragments

        if (fragments.isNotEmpty()) {
            val fragment = fragments[fragments.size - 1].toString()
            val currentFragment = fragment.replace(fragment.substring(fragment.indexOf("{"), fragment.indexOf("}") + 1), "")

            if (shareUsageData)
                println("Current Fragment: $currentFragment")

            return currentFragment
        }

        return ""
    }

    private fun bottomNav() {
        val settingsFragment = SettingsFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val layoutTitle: TextView = findViewById(R.id.layout_title)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calculator -> {
                    setFragment(calculatorFragment)
                    layoutInfo.visibility = View.GONE
                }

                R.id.settings -> {
                    setFragment(settingsFragment)

                    layoutInfo.visibility = View.VISIBLE
                    layoutTitle.text = getString(R.string.settings)
                }
            }

            true
        }
    }

    private fun alerts() {
        // Success alert (alert with a positive message) declaration.
        val alertSuccess = Dialog(this)

        // Info alert (alert with a neutral message) declaration.
        val alertInfo = Dialog(this)

        // Warning alert (alert with a "neutral-negative" message) declaration.
        val alertWarning = Dialog(this)

        // Error alert (alert with a negative message) declaration.
        alertError = Dialog(this)

        val alerts = arrayOf(alertSuccess, alertInfo, alertWarning, alertError)

        for (alert in alerts) {
            Objects.requireNonNull<Window>(alert.window).setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)

            if (alert.isShowing) {
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        alert.dismiss()
                    }
                }, 3500)
            }
        }

        alertSuccess.setContentView(R.layout.alert_success)
        alertInfo.setContentView(R.layout.alert_info)
        alertWarning.setContentView(R.layout.alert_warning)
        alertError.setContentView(R.layout.alert_error)
    }

    private fun dialogs() {
        // Buy Pro Dialog (dialog with a description of the existing features in Power Pro and a "buy" button) declaration.
        val dialogBuyPro = Dialog(this)

        // Feedback Dialog (dialog with a "list" of feedback options) declaration.
        dialogFeedback = Dialog(this)

        // OK Dialog (dialog with an OK neutral button) declaration.
        dialogOK = Dialog(this)

        // Yes/No Dialog (dialog with a positive Yes and a negative No buttons) declaration.
        dialogYesNo = Dialog(this)

        // Input Dialog (dialog with a text field) declaration.
        dialogInput = Dialog(this)

        val dialogs = arrayOf(dialogBuyPro, dialogFeedback, dialogOK, dialogInput, dialogYesNo)

        for (dialog in dialogs) {
            Objects.requireNonNull<Window>(dialog.window).setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        // Dialog Buy Pro content.
        dialogBuyPro.setContentView(R.layout.dialog_buy_pro)

        val dialogBuyProTitle = dialogBuyPro.findViewById<TextView>(R.id.title)
        dialogBuyProTitle.text = String.format(getString(R.string.app_plus), appName)

        val dialogBuyProMessage = dialogBuyPro.findViewById<TextView>(R.id.message)
        dialogBuyProMessage.text = String.format(getString(R.string.buy_pro_features_description),
                getString(R.string.app_name),
                getString(R.string.conversions_all),
                getString(R.string.dark_mode),
                getString(R.string.ads_removal))

        // Dialog Feedback content.
        dialogFeedback.setContentView(R.layout.dialog_feedback)

        val dialogFeedbackReviewButton = dialogFeedback.findViewById<ConstraintLayout>(R.id.review)
        val dialogFeedbackChatButton = dialogFeedback.findViewById<ConstraintLayout>(R.id.chat)
        val dialogFeedbackTweetDirectMessageButton = dialogFeedback.findViewById<ConstraintLayout>(R.id.tweet_direct_message)
        dialogFeedbackEmailButton = dialogFeedback.findViewById(R.id.email)
        val dialogFeedbackCancelButton = dialogFeedback.findViewById<Button>(R.id.cancel)

        // Default Dialog Feedback Review button click listener, opens Google Play Store.
        dialogFeedbackReviewButton.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))) }

        dialogFeedbackChatButton.setOnClickListener {
            val freshchatConfig = FreshchatConfig("57b32ce0-60a5-4067-b601-288016f4be5e", "75534fd8-ed19-4107-9b54-6b5a9bf811a4")

            freshchatConfig.isGallerySelectionEnabled = true

            Freshchat.getInstance(this@MainActivity).init(freshchatConfig)
            Freshchat.showConversations(this@MainActivity)
        }

        // Default Dialog Feedback Tweet or Direct Message listener, opens the developer's Twitter profile.
        dialogFeedbackTweetDirectMessageButton.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/jeanbarrossilva"))) }

        // Default Dialog Feedback E-mail button click listener, composes a new e-mail message.
        dialogFeedbackEmailButton.setOnClickListener {
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jeanbarrossilva@outlook.com"))

            email.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.send_feedback_email_subject), appName, versionName))

            deviceInfo = if (shareUsageData) deviceInfoName + "\n" + deviceInfo else deviceInfo
            email.putExtra(Intent.EXTRA_TEXT, "\n\n" + StringUtils.Format.repeat("-", 50) + "\n\n" + deviceInfo)

            startActivity(Intent.createChooser(email, getString(R.string.send_feedback)))
        }

        dialogFeedbackCancelButton.setOnClickListener { dialogFeedback.dismiss() }

        // Dialog OK content.
        dialogOK.setContentView(R.layout.dialog_ok)

        dialogOKTitle = dialogOK.findViewById(R.id.title)
        dialogOKMessage = dialogOK.findViewById(R.id.message)
        val dialogOKButton = dialogOK.findViewById<Button>(R.id.ok)

        // Default OK Dialog button click listener, dismisses the dialog.
        dialogOKButton.setOnClickListener { dialogOK.dismiss() }

        // Dialog Yes/No content.
        dialogYesNo.setContentView(R.layout.dialog_yes_no)

        dialogYesNoTitle = dialogYesNo.findViewById(R.id.title)
        dialogYesNoMessage = dialogYesNo.findViewById(R.id.message)
        dialogYesNoNoButton = dialogYesNo.findViewById(R.id.no)
        dialogYesNoYesButton = dialogYesNo.findViewById(R.id.yes)

        // Default Yes/No dialog No button click listener, dismisses the dialog.
        dialogYesNoNoButton.setOnClickListener { dialogYesNo.dismiss() }

        // Dialog Input content.
        dialogInput.setContentView(R.layout.dialog_input)

        dialogInputField = dialogInput.findViewById(R.id.input)
        dialogInputField.inputType = InputType.TYPE_CLASS_TEXT

        val dialogInputButton = dialogInput.findViewById<Button>(R.id.ok)

        // Default Input Dialog button click listener, dismisses the dialog.
        dialogInputButton.setOnClickListener { dialogInput.dismiss() }
    }

    private fun dialogWelcome() {
        dialogOKTitle.text = String.format(getString(R.string.welcome_to_dialog_title), appName)
        dialogOKMessage.text = String.format(getString(R.string.welcome_to_dialog_message), appName)

        dialogOK.show()

        if (Build.VERSION.SDK_INT >= 29) {
            dialogOK.setOnDismissListener {
                dialogOKTitle.text = getString(R.string.dark_mode)
                dialogOKMessage.text = String.format(getString(R.string.dark_mode_compatible_message), appName)

                dialogYesNoYesButton.setOnClickListener {
                    night(true)
                    dialogYesNo.dismiss()
                }

                dialogYesNo.show()
            }
        }
    }

    private fun night(enableNight: Boolean) {
        AppCompatDelegate.setDefaultNightMode(if (enableNight) MODE_NIGHT_YES else MODE_NIGHT_NO)
        this.isNightEnabled = enableNight

        preferencesEditor.putBoolean("isNightEnabled", isNightEnabled)
                .apply()
    }

    fun bounceIn(view: View, amplitude: Double, frequency: Double) {
        bounceInterpolator = BounceInterpolator(amplitude, frequency)

        val bounceIn = AnimationUtils.loadAnimation(this@MainActivity, R.anim.bounce_in)
        bounceIn.interpolator = bounceInterpolator

        view.startAnimation(bounceIn)

        if (shareUsageData)
            println("The bounce in animation was successfully applied and executed in $view.")
    }

    @SuppressLint("ClickableViewAccessibility")
    fun calculatorMode(context: Context, calculatorMode: ImageButton) {
        calculatorMode.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> bounceIn(view, 0.35, 1.0)
                MotionEvent.ACTION_UP -> {
                    view.startAnimation(bounceOut)

                    val calculatorModes = PopupMenu(context, calculatorMode)
                    val inflater: MenuInflater

                    inflater = calculatorModes.menuInflater

                    inflater.inflate(R.menu.calculator_modes, calculatorModes.menu)
                    calculatorModes.show()

                    calculatorModes.setOnMenuItemClickListener { item ->
                        if (item.title == className.replace("Fragment", ""))
                            calculatorModes.dismiss()
                        else {
                            when (item.titleCondensed.toString()) {
                                "Calculator" -> setFragment(CalculatorFragment())
                                "Length" -> setFragment(LengthFragment())
                                "Time" -> setFragment(TimeFragment())
                                "Temperature" -> setFragment(TemperatureFragment())
                            }
                        }

                        true
                    }
                }
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    // The all Button[] must contain all the unit buttons available, including the selected one.
    fun selectButton(selected: Button, all: Array<Button>, background: Int) {
        val selectedId = selected.id

        selected.setTextColor(if (isNightEnabled) Color.WHITE else Color.BLACK)
        selected.setBackgroundResource(background)

        for (unit in all) {
            if (unit.id != selectedId)
                try {
                    unit.setTextColor(if (isNightEnabled) Color.BLACK else Color.WHITE)
                    unit.setBackgroundResource(R.drawable.option)
                } catch (nullPointerException: NullPointerException) {
                    alertError.show()
                    nullPointerException.printStackTrace()
                }

        }
    }

    fun Button.hover(background: Int, textColor: Int, animate: Boolean) {
        val defaultBackground = this.background
        val defaultTextColor = this.currentTextColor

        this.setOnHoverListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    if (animate)
                        view.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, android.R.anim.fade_in))

                    if (background.toString().contains("R.drawable"))
                        view.background = getDrawable(background)
                    else if (background.toString().contains("Color"))
                        view.setBackgroundColor(background)

                    this.setTextColor(textColor)

                }

                MotionEvent.ACTION_HOVER_EXIT -> {
                    if (animate)
                        view.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, android.R.anim.fade_out))

                    view.background = defaultBackground
                    this.setTextColor(defaultTextColor)
                }
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun inputNumber(view: View, input: EditText, conversionResult: TextView, conversionSymbolResult: TextView, calc: String) {
        val onTouchListener = View.OnTouchListener { v, event ->
            number = v as Button

            when (event.action) {
                MotionEvent.ACTION_DOWN -> bounceIn(v, 0.5, 5.0)
                MotionEvent.ACTION_UP -> {
                    number.startAnimation(bounceOut)

                    if (calc != getString(R.string.error)) {
                        input.append(number.text)
                    } else {
                        input.setText("")

                        number = v
                        input.append(number.text)
                    }

                    if (shareUsageData)
                        println("Number '" + number.text + "' added.")

                    calc(input, conversionResult, conversionSymbolResult)
                }
            }

            true
        }

        for (number in numbers)
            view.findViewById<View>(number).setOnTouchListener(onTouchListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun delete(input: EditText, delete: ImageButton, conversionResult: TextView, conversionResultSymbol: TextView) {
        delete.setOnTouchListener { view, event ->
            val calc = input.text.toString()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> bounceIn(view, 0.5, 1.0)
                MotionEvent.ACTION_UP -> {
                    delete.startAnimation(bounceOut)
                    delete.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                    if (calc.isNotEmpty()) {
                        // When calc ends with " " (" ), it means that it ends with an operator; the operator is shown as ""  + operator + " ". e. g., " × " and " + ". So, for example, if the input is "2 + 2", while deleting, the input would be equal to "2 + ", "2 +", "2 " and "2", respectively; with this method, the result should be "2 +" and "2", respectively.
                        input.setText(if (calc.endsWith(" ")) calc.substring(0, calc.length - 2) else calc.substring(0, calc.length - 1))

                        calc(input, conversionResult, conversionResultSymbol)
                    }

                    input.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                            if (s.toString().isEmpty())
                                conversionResult.text = getString(R.string.zero)
                        }

                        override fun afterTextChanged(s: Editable) {
                            if (s.toString().isEmpty())
                                conversionResult.text = getString(R.string.zero)
                        }
                    })
                }
            }

            true
        }
    }

    private fun convertFrom(): String? {
        if (preferences.getString("convertTo", null) != null) {
            when (currentFragment()) {
                "LengthFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertFrom", null))) {
                    "lightYear" -> unit = "lightYear"
                    "kilometer" -> unit = "kilometer"
                    "hectometer" -> unit = "hectometer"
                    "decameter" -> unit = "decameter"
                    "mile" -> unit = "mile"
                    "meter" -> unit = "meter"
                    "decimeter" -> unit = "decimeter"
                    "centimeter" -> unit = "centimeter"
                    "millimeter" -> unit = "millimeter"
                    "micrometer" -> unit = "micrometer"
                }
                "TimeFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertFrom", null))) {
                    "year" -> unit = "year"
                    "month" -> unit = "month"
                    "day" -> unit = "day"
                    "hour" -> unit = "hour"
                    "minute" -> unit = "minute"
                    "second" -> unit = "second"
                    "millisecond" -> unit = "millisecond"
                }
                "TemperatureFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertFrom", null))) {
                    "celsius" -> unit = "celsius"
                    "fahrenheit" -> unit = "fahrenheit"
                    "kelvin" -> unit = "kelvin"
                    "rankine" -> unit = "rankine"
                    "reaumur" -> unit = "reaumur"
                }
            }
        }

        return unit
    }

    private fun convertTo(): String? {
        if (preferences.getString("convertTo", null) != null) {
            when (currentFragment()) {
                "LengthFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertTo", null))) {
                    "lightYear" -> unit = "lightYear"
                    "kilometer" -> unit = "kilometer"
                    "hectometer" -> unit = "hectometer"
                    "decameter" -> unit = "decameter"
                    "mile" -> unit = "mile"
                    "meter" -> unit = "meter"
                    "decimeter" -> unit = "decimeter"
                    "centimeter" -> unit = "centimeter"
                    "millimeter" -> unit = "millimeter"
                    "micrometer" -> unit = "micrometer"
                }
                "TimeFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertTo", null))) {
                    "year" -> unit = "year"
                    "month" -> unit = "month"
                    "day" -> unit = "day"
                    "hour" -> unit = "hour"
                    "minute" -> unit = "minute"
                    "second" -> unit = "second"
                    "millisecond" -> unit = "millisecond"
                }
                "TemperatureFragment" -> when (Objects.requireNonNull<String>(preferences.getString("convertTo", null))) {
                    "celsius" -> unit = "celsius"
                    "fahrenheit" -> unit = "fahrenheit"
                    "kelvin" -> unit = "kelvin"
                    "rankine" -> unit = "rankine"
                    "reaumur" -> unit = "reaumur"
                }
            }
        }

        return unit
    }

    fun calc(input: EditText, conversionResult: TextView, conversionSymbolResult: TextView) {
        if (input.text.toString().isNotEmpty())
            try {
                when (currentFragment()) {
                    "LengthFragment" -> when (convertFrom()) {
                        "lightYear" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = input.text.toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9461000000000.0).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 94610000000000.0).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 946100000000000.0).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 5.789.pow(12.0)).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9461000000000000.0).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 94607304725809376.0).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 946073047258093824.0).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9460730472580937728.0).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9.461.pow(21.0)).toString()
                        }
                        "kilometer" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 9461000000000.0).toString()
                            "kilometer" -> conversionResult.text = input.text.toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1.609).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10000).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100000).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000000).toString()
                            "micrometer" -> {
                                conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(9.0)).toString()
                                println("Math.pow(10, 9): " + 10.0.pow(9.0))
                            }
                        }
                        "hectometer" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 94610000000000.0).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10).toString()
                            "hectometer" -> conversionResult.text = input.text.toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 16.093).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10000).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100000).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(8.0)).toString()
                        }
                        "decameter" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 946100000000000.0).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10).toString()
                            "decameter" -> conversionResult.text = input.text.toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 160.934).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10000).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(7.0)).toString()
                        }
                        "mile" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 5.879.pow(12.0)).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1.609).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 16.093).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 160.934).toString()
                            "mile" -> conversionResult.text = input.text.toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1609.344).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 16093.44).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 160934.4).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1.609.pow(6.0)).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1.609.pow(9.0)).toString()
                        }
                        "meter" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 9461000000000000.0).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1000).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1609.344).toString()
                            "meter" -> conversionResult.text = input.text.toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(6.0)).toString()
                        }
                        "decimeter" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 94607304725809376.0).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10000).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1000).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 16093.44).toString()
                            "meter" -> conversionResult.text = (1.8 / java.lang.Double.parseDouble(input.text.toString()) / 10.0).toString()
                            "decimeter" -> conversionResult.text = input.text.toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) * 10).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 100).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(5.0)).toString()
                        }
                        "centimeter" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 946073047258093824.0).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100000).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10000).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1000).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 160934.4).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) / 100).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) / 10).toString()
                            "centimeter" -> conversionResult.text = input.text.toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10).toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(4.0)).toString()
                        }
                        "millimeter" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-19.0))).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10.0.pow(6.0)).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100000).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10000).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1.609.pow(6.0)).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1000).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 100).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 10).toString()
                            "millimeter" -> conversionResult.text = input.text.toString()
                            "micrometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 10.0.pow(3.0)).toString()
                        }
                        "micrometer" -> when (convertTo()) {
                            "lightYear" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-22.0))).toString()
                            "kilometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-9.0))).toString()
                            "hectometer" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-8.0))).toString()
                            "decameter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-7.0))).toString()
                            "mile" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1.609.pow(9.0)).toString()
                            "meter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-6.0))).toString()
                            "decimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-5.0))).toString()
                            "centimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-4.0))).toString()
                            "millimeter" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / (1.057 * 10.0.pow(-3.0))).toString()
                            "micrometer" -> conversionResult.text = input.text.toString()
                        }
                    }
                    "TimeFragment" -> when (convertFrom()) {
                        "year" -> when (convertTo()) {
                            "year" -> conversionResult.text = input.text.toString()
                            "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 12).toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 365).toString()
                            "hour" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 8760).toString()
                            "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 525600).toString()
                            "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * (31536 * 10.0.pow(3.0))).toString()
                            "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * (31536 * 10.0.pow(6.0))).toString()
                        }
                        "month" -> when (convertTo()) {
                            "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 12).toString()
                            "month" -> conversionResult.text = input.text.toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 30.417).toString()
                            "hour" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 730.001).toString()
                            "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 43800.048).toString()
                            "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 2628002.88).toString()
                            "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 2628002880.0).toString()
                        }
                        "day" -> {
                            when (convertTo()) {
                                "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 365).toString()
                                "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 30.417).toString()
                                "day" -> conversionResult.text = input.text.toString()
                                "hour" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 24).toString()
                                "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1440).toString()
                                "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 86400).toString()
                                "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 86400000).toString()
                            }
                            when (convertTo()) {
                                "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 8760).toString()
                                "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 730.001).toString()
                                "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 24).toString()
                                "hour" -> conversionResult.text = input.text.toString()
                                "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 60).toString()
                                "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 3600).toString()
                                "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 3600000).toString()
                            }
                        }
                        "hour" -> when (convertTo()) {
                            "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 8760).toString()
                            "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 730.001).toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 24).toString()
                            "hour" -> conversionResult.text = input.text.toString()
                            "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 60).toString()
                            "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 3600).toString()
                            "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 3600000).toString()
                        }
                        "minute" -> when (convertTo()) {
                            "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 525600).toString()
                            "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 43800.048).toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1440).toString()
                            "hour" -> conversionResult.text = (1.8 / java.lang.Double.parseDouble(input.text.toString()) / 60.0).toString()
                            "minute" -> conversionResult.text = input.text.toString()
                            "second" -> conversionResult.text = ((java.lang.Double.parseDouble(input.text.toString()) - 32) * 60).toString()
                            "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 60000).toString()
                        }
                        "second" -> when (convertTo()) {
                            "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 31400000).toString()
                            "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 2628000).toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 86400).toString()
                            "hour" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) / 3600).toString()
                            "minute" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) / 60).toString()
                            "second" -> conversionResult.text = input.text.toString()
                            "millisecond" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1000).toString()
                        }
                        "millisecond" -> when (convertTo()) {
                            "year" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 31540000000.0).toString()
                            "month" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 2628000000.0).toString()
                            "day" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 86400000).toString()
                            "hour" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 3600000).toString()
                            "minute" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 60000).toString()
                            "second" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) / 1000).toString()
                            "millisecond" -> conversionResult.text = input.text.toString()
                        }
                    }
                    "TemperatureFragment" -> when (convertFrom()) {
                        "celsius" -> when (convertTo()) {
                            "celsius" -> conversionResult.text = input.text.toString()
                            "fahrenheit" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9 / 5 + 32).toString()
                            "kelvin" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) + 273.15).toString()
                            "rankine" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 9 / 5 + 491.67).toString()
                            "reaumur" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 5 / 4).toString()
                        }
                        "fahrenheit" -> when (convertTo()) {
                            "celsius" -> conversionResult.text = (1.8 / java.lang.Double.parseDouble(input.text.toString()) - 32).toString()
                            "fahrenheit" -> conversionResult.text = input.text.toString()
                            "kelvin" -> conversionResult.text = ((java.lang.Double.parseDouble(input.text.toString()) - 32) * 5 / 9 + 273.15).toString()
                            "rankine" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) + 459.67).toString()
                            "reaumur" -> conversionResult.text = ((java.lang.Double.valueOf(input.text.toString()) - 32) * 4 / 9).toString()
                        }
                        "kelvin" -> when (convertTo()) {
                            "celsius" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) - 273.15).toString()
                            "fahrenheit" -> conversionResult.text = ((java.lang.Double.parseDouble(input.text.toString()) - 273.15) * 9 / 5 + 32).toString()
                            "kelvin" -> conversionResult.text = input.text.toString()
                            "rankine" -> conversionResult.text = (java.lang.Double.valueOf(input.text.toString()) * 1.8).toString()
                            "reaumur" -> conversionResult.text = ((java.lang.Double.valueOf(input.text.toString()) - 273.15) * 4 / 5).toString()
                        }
                        "rankine" -> when (convertTo()) {
                            "celsius" -> conversionResult.text = ((java.lang.Double.parseDouble(input.text.toString()) - 491.67) * 5 / 9).toString()
                            "fahrenheit" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) - 459.67).toString()
                            "kelvin" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) * 5 / 9).toString()
                            "rankine" -> conversionResult.text = input.text.toString()
                            "reaumur" -> conversionResult.text = ((java.lang.Double.valueOf(input.text.toString()) - 491.67) * 4 / 9).toString()
                        }
                        "reaumur" -> when (convertTo()) {
                            "celsius" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) / 0.8).toString()
                            "fahrenheit" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) * 9 / 4 + 32).toString()
                            "kelvin" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) * 5 / 4 + 273.15).toString()
                            "rankine" -> conversionResult.text = (java.lang.Double.parseDouble(input.text.toString()) * 9 / 4 + 491.67).toString()
                            "reaumur" -> conversionResult.text = input.text.toString()
                        }
                    }
                }

                inputFormat(input, conversionResult)
            } catch (exception: Exception) {
                conversionResult.text = getString(R.string.error)
                conversionSymbolResult.visibility = View.GONE
            }

    }

    fun reformatCalc(calc: String?): String? {
        var calc = calc
        if (calc != null) {
            if (calc.contains(" "))
                calc = calc.replace(" ", "")

            if (calc.contains(StringUtils.Operator.Stylized.TIMES) || calc.contains(StringUtils.Operator.Stylized.DIVISION)) {
                if (calc.contains(StringUtils.Operator.Stylized.TIMES))
                    calc = calc.replace(StringUtils.Operator.Stylized.TIMES, "*")

                if (calc.contains(StringUtils.Operator.Stylized.DIVISION))
                    calc = calc.replace(StringUtils.Operator.Stylized.DIVISION, "/")
            }

            if (StringUtils.Format.hasSquareBracket(calc))
                StringUtils.Format.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL)

            if (shareUsageData)
                println("Transformed calc: ${StringUtils.Format.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL)}")

            return StringUtils.Format.removeSquareBracket(calc, REMOVE_SQUARE_BRACKET_ALL)
        }

        return null
    }

    /**
     * "Styles" the result that is shown to the user when pressing the equal button.
     *
     * @param input Represents the text field itself.
     * @param conversionResult Conversion result of the input keypad_button'.
     */
    private fun inputFormat(input: EditText, conversionResult: TextView) {
        if (conversionResult.text.toString().contains(".")) {
            conversionResult.text = format.format(java.lang.Double.parseDouble(conversionResult.text.toString()))
        }

        if (conversionResult.text.toString().endsWith(".0"))
            conversionResult.text = input.text.toString().replace(".0", "")

        if (conversionResult.text.toString().endsWith("E"))
            conversionResult.text = input.text.toString().replace("E", "e")
    }

    companion object {
        val context = this

        internal const val LAST_PARENTHESIS_LEFT = 0
        internal const val LAST_PARENTHESIS_RIGHT = 1

        internal const val REMOVE_SQUARE_BRACKET_LEFT = 0
        internal const val REMOVE_SQUARE_BRACKET_RIGHT = 1
        internal const val REMOVE_SQUARE_BRACKET_ALL = 2
    }
}