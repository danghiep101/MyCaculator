package com.example.mycaculator.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mycaculator.R
import com.example.mycaculator.databinding.ActivityMainBinding
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


class MainActivity : AppCompatActivity() {
    private val calculatorViewModel: CalculatorViewModel by viewModels()



    private var mInterstitialAd: InterstitialAd? = null
    private final val tag = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Mobile Ads SDK
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        // Load interstitial ad
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(tag, adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(tag, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(tag, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(tag, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(tag, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                Log.d(tag, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(tag, "Ad showed fullscreen content.")
            }
        }

        binding.switchDarkmode.setOnClickListener {
            calculatorViewModel.switchMode()
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
        binding.btn0
        val numberButtons = arrayOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btn00, binding.btnC,
            binding.btnAc, binding.btnDot
        )
        for (button in numberButtons) {
            button.setOnClickListener {
                calculatorViewModel.onNumberButtonClicked((it as Button).text.toString())
            }
        }

        val operationButtons = arrayOf(
            binding.btnEqual, binding.btnPlus, binding.btnSubtract, binding.btnDevide,
            binding.btnPercent, binding.btnMutiply

        )
        for (button in operationButtons) {
            button.setOnClickListener {
                calculatorViewModel.onOperationButtonClicked((it as Button).text.toString())
            }
        }


        calculatorViewModel.workingText.observe(this) {
            binding.textWorking.setText(it)
        }
        calculatorViewModel.resultText.observe(this) {
            binding.textViewResult.text = it
        }
        calculatorViewModel.textHistory.observe(this) {
            binding.textHistory.text = it
        }
        calculatorViewModel.isDarkMode.observe(this) {
            switchMode(it)
        }
    }


    private fun switchMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            binding.main.setBackgroundColor(getColor(R.color.black))
            binding.layoutSwitch.setBackgroundColor(getColor(R.color.black))
            binding.layoutNumber.setBackgroundColor(getColor(R.color.black))
            binding.switchDarkmode.setText(R.string.darkmode)
            binding.switchDarkmode.setBackgroundColor(getColor(R.color.black))
            binding.switchDarkmode.setTextColor(getColor(R.color.white))
            binding.textWorking.setTextColor(getColor(R.color.white))
            binding.textWorking.setBackgroundColor(getColor(R.color.black))
            binding.textViewResult.setBackgroundColor(getColor(R.color.black))
            binding.textViewResult.setTextColor(getColor(R.color.white))
            binding.textHistory.setBackgroundColor(getColor(R.color.black))
            binding.textHistory.setTextColor(getColor(R.color.gray))
            updateButtonColors(true)
        } else {
            binding.main.setBackgroundColor(getColor(R.color.almost_white))
            binding.layoutSwitch.setBackgroundColor(getColor(R.color.almost_white))
            binding.layoutNumber.setBackgroundColor(getColor(R.color.almost_white))
            binding.switchDarkmode.setText(R.string.text_light)
            binding.switchDarkmode.setBackgroundColor(getColor(R.color.almost_white))
            binding.switchDarkmode.setTextColor(getColor(R.color.black))
            binding.textWorking.setTextColor(getColor(R.color.black))
            binding.textWorking.setBackgroundColor(getColor(R.color.almost_white))
            binding.textViewResult.setTextColor(getColor(R.color.black))
            binding.textViewResult.setBackgroundColor(getColor(R.color.almost_white))
            binding.textHistory.setTextColor(getColor(R.color.gray))
            binding.textHistory.setBackgroundColor(getColor(R.color.almost_white))
            updateButtonColors(false)
        }
    }

    private fun updateButtonColors(isDarkMode: Boolean) {
        val listButton = arrayOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8, binding.btn9,binding.btn00,binding.btnDot
        )
        val listButtonOperation = arrayOf(
            binding.btnEqual, binding.btnPlus, binding.btnSubtract, binding.btnDevide,
            binding.btnPercent, binding.btnMutiply, binding.btnC

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
            binding.btnAc.setBackgroundColor(getColor(R.color.black))
            binding.btnAc.setTextColor(getColor(R.color.red))
        } else {
            for (button in listButton) {
                button.setTextColor(getColor(R.color.black))
                button.setBackgroundColor(getColor(R.color.almost_white))
            }
            for (button in listButtonOperation) {
                button.setTextColor(getColor(R.color.cyan))
                button.setBackgroundColor(getColor(R.color.almost_white))
            }
           binding.btnAc.setBackgroundColor(getColor(R.color.almost_white))
            binding.btnAc.setTextColor(getColor(R.color.red))
        }
    }
}