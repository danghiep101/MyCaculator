package com.example.mycaculator.view

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.example.mycaculator.R
import com.example.mycaculator.viewmodel.CalculatorViewModel
import com.google.android.gms.ads.AdError

import com.google.android.gms.ads.AdRequest
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
    private val calculatorViewModel: CalculatorViewModel by viewModels()

    private lateinit var workingText: EditText
    private lateinit var resultText: TextView
    private lateinit var textHistory: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchDarkmode: Switch
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var layoutSwitch: LinearLayout
    private lateinit var layoutNumber: LinearLayout

    private lateinit var adView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "MainActivity"

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        // Initialize Mobile Ads SDK
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        // Load interstitial ad
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        switchDarkmode.setOnClickListener {
            calculatorViewModel.switchMode()
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }

        val numberButtons = arrayOf(
            findViewById<Button>(R.id.btn_0),
            findViewById<Button>(R.id.btn_1),
            findViewById<Button>(R.id.btn_2),
            findViewById<Button>(R.id.btn_3),
            findViewById<Button>(R.id.btn_4),
            findViewById<Button>(R.id.btn_5),
            findViewById<Button>(R.id.btn_6),
            findViewById<Button>(R.id.btn_7),
            findViewById<Button>(R.id.btn_8),
            findViewById<Button>(R.id.btn_9),
            findViewById<Button>(R.id.btn_00),
            findViewById<Button>(R.id.btn_c),
            findViewById<Button>(R.id.btn_ac),
            findViewById<Button>(R.id.btn_dot)
        )
        for (button in numberButtons) {
            button.setOnClickListener {
                calculatorViewModel.onNumberButtonClicked((it as Button).text.toString())
            }
        }

        val operationButtons = arrayOf(
            findViewById<Button>(R.id.btn_plus),
            findViewById<Button>(R.id.btn_subtract),
            findViewById<Button>(R.id.btn_devide),
            findViewById<Button>(R.id.btn_percent),
            findViewById<Button>(R.id.btn_mutiply),
            findViewById<Button>(R.id.btn_equal)
        )
        for (button in operationButtons) {
            button.setOnClickListener {
                calculatorViewModel.onOperationButtonClicked((it as Button).text.toString())
            }
        }


        calculatorViewModel.workingText.observe(this) {
            workingText.setText(it)
        }
        calculatorViewModel.resultText.observe(this) {
            resultText.text = it
        }
        calculatorViewModel.textHistory.observe(this) {
            textHistory.text = it
        }
        calculatorViewModel.isDarkMode.observe(this) {
            switchMode(it)
        }
    }

    private fun initView() {
        layoutNumber = findViewById(R.id.layout_number)
        layoutSwitch = findViewById(R.id.layout_switch)
        mainLayout = findViewById(R.id.main)
        workingText = findViewById(R.id.text_working)
        textHistory = findViewById(R.id.text_history)
        resultText = findViewById(R.id.text_view_result)
        switchDarkmode = findViewById(R.id.switch_darkmode)
    }

    private fun switchMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            mainLayout.setBackgroundColor(getColor(R.color.black))
            layoutSwitch.setBackgroundColor(getColor(R.color.black))
            layoutNumber.setBackgroundColor(getColor(R.color.black))
            switchDarkmode.setText(R.string.darkmode)
            switchDarkmode.setBackgroundColor(getColor(R.color.black))
            switchDarkmode.setTextColor(getColor(R.color.white))
            workingText.setTextColor(getColor(R.color.white))
            workingText.setBackgroundColor(getColor(R.color.black))
            resultText.setBackgroundColor(getColor(R.color.black))
            resultText.setTextColor(getColor(R.color.white))
            textHistory.setBackgroundColor(getColor(R.color.black))
            textHistory.setTextColor(getColor(R.color.gray))
            updateButtonColors(true)
        } else {
            mainLayout.setBackgroundColor(getColor(R.color.almost_white))
            layoutSwitch.setBackgroundColor(getColor(R.color.almost_white))
            layoutNumber.setBackgroundColor(getColor(R.color.almost_white))
            switchDarkmode.setText(R.string.text_light)
            switchDarkmode.setBackgroundColor(getColor(R.color.almost_white))
            switchDarkmode.setTextColor(getColor(R.color.black))
            workingText.setTextColor(getColor(R.color.black))
            workingText.setBackgroundColor(getColor(R.color.almost_white))
            resultText.setTextColor(getColor(R.color.black))
            resultText.setBackgroundColor(getColor(R.color.almost_white))
            textHistory.setTextColor(getColor(R.color.gray))
            textHistory.setBackgroundColor(getColor(R.color.almost_white))
            updateButtonColors(false)
        }
    }

    private fun updateButtonColors(isDarkMode: Boolean) {
        val listButton = arrayOf(
            findViewById<Button>(R.id.btn_0),
            findViewById<Button>(R.id.btn_1),
            findViewById<Button>(R.id.btn_2),
            findViewById<Button>(R.id.btn_3),
            findViewById<Button>(R.id.btn_4),
            findViewById<Button>(R.id.btn_5),
            findViewById<Button>(R.id.btn_6),
            findViewById<Button>(R.id.btn_7),
            findViewById<Button>(R.id.btn_8),
            findViewById<Button>(R.id.btn_9),
            findViewById<Button>(R.id.btn_00),
            findViewById<Button>(R.id.btn_dot)
        )
        val listButtonOperation = arrayOf(
            findViewById<Button>(R.id.btn_plus),
            findViewById<Button>(R.id.btn_subtract),
            findViewById<Button>(R.id.btn_devide),
            findViewById<Button>(R.id.btn_mutiply),
            findViewById<Button>(R.id.btn_equal),
            findViewById<Button>(R.id.btn_c),
            findViewById<Button>(R.id.btn_percent)
        )
        if (isDarkMode) {
            for (button in listButton) {
                button.setTextColor(getColor(R.color.white))
                button.setBackgroundColor(getColor(R.color.black))
            }
            for (button in listButtonOperation) {
                button.setTextColor(getColor(R.color.cyan))
                button.setBackgroundColor(getColor(R.color.black))
            }
            findViewById<Button>(R.id.btn_ac).setBackgroundColor(getColor(R.color.black))
            findViewById<Button>(R.id.btn_ac).setTextColor(getColor(R.color.red))
        } else {
            for (button in listButton) {
                button.setTextColor(getColor(R.color.black))
                button.setBackgroundColor(getColor(R.color.almost_white))
            }
            for (button in listButtonOperation) {
                button.setTextColor(getColor(R.color.cyan))
                button.setBackgroundColor(getColor(R.color.almost_white))
            }
            findViewById<Button>(R.id.btn_ac).setBackgroundColor(getColor(R.color.almost_white))
            findViewById<Button>(R.id.btn_ac).setTextColor(getColor(R.color.red))
        }
    }
}