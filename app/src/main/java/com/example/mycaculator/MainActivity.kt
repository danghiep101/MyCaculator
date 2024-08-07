package com.example.mycaculator

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.service.autofill.FillEventHistory
import android.text.Editable
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mycaculator.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var workingText: EditText
    private lateinit var resultText: TextView
    private lateinit var textHistory: TextView
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonSubtract: Button
    private lateinit var buttonDevide: Button
    private lateinit var buttonPercent: Button
    private lateinit var buttonC: Button
    private lateinit var buttonAC: Button
    private lateinit var buttonMutiply: Button
    private lateinit var button00: Button
    private lateinit var button0: Button
    private lateinit var buttonEqual: Button
    private lateinit var buttonDot: Button
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchDarkmode :Switch
    private lateinit var layoutSwitch: LinearLayout
    private lateinit var layoutNumber: LinearLayout
    private var isDarkMode = true
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var adView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "MainActivity"

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      setContentView(R.layout.activity_main)
        initView();

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError?.toString()?.let { Log.d(TAG, it) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        val switch  = switchDarkmode
        switch.setOnClickListener{
            switchMode(this)
            //admob full screen activate
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
        val listButton = arrayOf(
            button0, button1, button2, button3, button4, button5, button6,
            button7, button8, button9, button00, buttonC, buttonAC, buttonDot
        )
        for (button in listButton) {
            button.setOnClickListener {
                numberButtonAction(it)
            }
        }
        val listButtonOperation =
            arrayOf(buttonPlus, buttonSubtract, buttonDevide, buttonPercent, buttonMutiply, buttonEqual)
        for (button in listButtonOperation) {
            button.setOnClickListener {
                operationButtonAction(it)
            }
        }



    }

    private fun switchMode(context: Context) {
        isDarkMode = !isDarkMode

        if (isDarkMode) {
            mainLayout.setBackgroundColor(context.getColor(R.color.black))
            layoutSwitch.setBackgroundColor(context.getColor(R.color.black))
            layoutNumber.setBackgroundColor(context.getColor(R.color.black))

            switchDarkmode.setText(R.string.darkmode)
            switchDarkmode.setBackgroundColor(context.getColor(R.color.black))
            switchDarkmode.setTextColor(context.getColor(R.color.white))

            workingText.setTextColor(context.getColor(R.color.white))
            workingText.setBackgroundColor(context.getColor(R.color.black))

            resultText.setBackgroundColor(context.getColor(R.color.black))
            resultText.setTextColor(context.getColor(R.color.white))

            textHistory.setBackgroundColor(context.getColor(R.color.black))
            textHistory.setTextColor(context.getColor(R.color.gray))

            updateColors(this)

        } else {
            mainLayout.setBackgroundColor(context.getColor(R.color.almost_white))
            layoutNumber.setBackgroundColor(context.getColor(R.color.almost_white))
            layoutSwitch.setBackgroundColor(context.getColor(R.color.almost_white))

            switchDarkmode.setBackgroundColor(context.getColor(R.color.almost_white))
            switchDarkmode.setTextColor(context.getColor(R.color.black))
            switchDarkmode.setText(R.string.text_light)

            workingText.setTextColor(context.getColor(R.color.black))
            workingText.setBackgroundColor(context.getColor(R.color.almost_white))

            resultText.setTextColor(context.getColor(R.color.black))
            resultText.setBackgroundColor(context.getColor(R.color.almost_white))

            textHistory.setTextColor(context.getColor(R.color.gray))
            textHistory.setBackgroundColor(context.getColor(R.color.almost_white))
            updateColors(this)

        }
    }
    private fun updateColors(context: Context) {

        val listButton = arrayOf(
            button0, button1, button2, button3, button4, button5, button6,
            button7, button8, button9, button00, buttonDot
        )
        val listButtonOperation = arrayOf(buttonPlus, buttonSubtract, buttonDevide, buttonMutiply,
            buttonEqual, buttonC, buttonPercent)
        if (isDarkMode) {
            for (button in listButton) {
                button.setTextColor(context.getColor(R.color.white))
                button.setBackgroundColor(context.getColor(R.color.black))
            }
            for (button in listButtonOperation) {
                button.setTextColor(context.getColor(R.color.cyan))
                button.setBackgroundColor(context.getColor(R.color.black))
            }
            buttonAC.setBackgroundColor(context.getColor(R.color.black))
            buttonAC.setTextColor(context.getColor(R.color.red))
        } else {
            for (button in listButton) {
                button.setTextColor(context.getColor(R.color.black))
                button.setBackgroundColor(context.getColor(R.color.almost_white))
            }
            for (button in listButtonOperation) {
                button.setTextColor(context.getColor(R.color.cyan))
                button.setBackgroundColor(context.getColor(R.color.almost_white))
            }
            buttonAC.setBackgroundColor(context.getColor(R.color.almost_white))
            buttonAC.setTextColor(context.getColor(R.color.red))
        }


    }

    @SuppressLint("SetTextI18n")
    private fun operationButtonAction(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()
        val currentText = workingText.text.toString()

        when (buttonText) {
            "+", "-", "*", "/", "%" -> {
                workingText.setText(currentText + buttonText)

            }
            "=" -> {
                equalAction()
            }
        }
    }

    private fun initView() {
        layoutNumber = findViewById(R.id.layout_number)
        layoutSwitch = findViewById(R.id.layout_switch)
        mainLayout = findViewById(R.id.main)
        workingText = findViewById(R.id.text_working)
        textHistory = findViewById(R.id.text_history)
        resultText = findViewById(R.id.text_view_result)
        buttonMutiply = findViewById(R.id.btn_mutiply)
        button0 = findViewById(R.id.btn_0)
        button1 = findViewById(R.id.btn_1)
        button2 = findViewById(R.id.btn_2)
        button3 = findViewById(R.id.btn_3)
        button4 = findViewById(R.id.btn_4)
        button5 = findViewById(R.id.btn_5)
        button6 = findViewById(R.id.btn_6)
        button7 = findViewById(R.id.btn_7)
        button8 = findViewById(R.id.btn_8)
        button9 = findViewById(R.id.btn_9)
        button00 = findViewById(R.id.btn_00)
        buttonPlus = findViewById(R.id.btn_plus)
        buttonDevide = findViewById(R.id.btn_devide)
        buttonSubtract = findViewById(R.id.btn_subtract)
        buttonEqual = findViewById(R.id.btn_equal)
        buttonAC = findViewById(R.id.btn_ac)
        buttonPercent = findViewById(R.id.btn_percent)
        buttonC = findViewById(R.id.btn_c)
        buttonDot = findViewById(R.id.btn_dot)
        switchDarkmode = findViewById(R.id.switch_darkmode)
    }

    @SuppressLint("SetTextI18n")
    fun numberButtonAction(view: View) {

        val button = view as Button
        val buttonText = button.text.toString()
        val currentText = workingText.text.toString()

        when (buttonText) {
            "AC" -> {
                workingText.setText("")
                resultText.text = ""
                textHistory.text = ""
            }

            "C" -> {
                workingText.setText(currentText.dropLast(1))
            }

            else -> {
                workingText.setText(currentText + buttonText)
                caculateAction()
            }
        }
    }


    private fun evaluateExpression(expression: String): Double {
        return ExpressionBuilder(expression).build().evaluate().toDouble()
    }

    @SuppressLint("SetTextI18n")
    fun equalAction() {
        try {

            val result = resultText.text.toString()
            val workingText = workingText.text.toString()
            textHistory.text = "$workingText\n=$result"

        }catch (e: Exception){
            Log.d("CACULATE", "Exception: ${e.message}")
        }

    }
    fun caculateAction(){
        val currentText = workingText.text.toString()
        try {
            if (currentText.isEmpty()) {
                throw IllegalArgumentException("Invalid Expression")
            }
            val validExpression = fixExpression(currentText)
            val result = evaluateExpression(validExpression )
            val formattedResult = DecimalFormat("0.#########").format(result)
            resultText.text = Editable.Factory.getInstance().newEditable(formattedResult)
        } catch (e: Exception) {
            Log.e("tag", "Exception: ${e.message}")
        }
    }
    private fun fixExpression(expression: String): String {
        // Regular expression to find operators at the beginning of the expression
        val regex = "^[*/%]+".toRegex()

        // Replace operators at the beginning with "0"
        var fixedExpression = expression
        if (regex.containsMatchIn(fixedExpression)) {
            fixedExpression = fixedExpression.replace(regex, "0$0")
        }

        return fixedExpression
    }


}