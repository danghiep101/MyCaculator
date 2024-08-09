package com.example.mycaculator.view

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.mycaculator.R
import com.example.mycaculator.databinding.ActivityMainBinding
import com.example.mycaculator.utils.PreferencesManager
import com.example.mycaculator.viewmodel.CalculatorViewModel
import com.example.mycaculator.viewmodel.CalculatorViewModelFactory
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var calculatorViewModel: CalculatorViewModel

    private var mInterstitialAd: InterstitialAd? = null
    private val tag = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = CalculatorViewModelFactory(application)
        calculatorViewModel = ViewModelProvider(this, factory).get(CalculatorViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nightMode = PreferencesManager.isDarkMode(this)
        AppCompatDelegate.setDefaultNightMode(
            if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding.switchDarkmode.isChecked = nightMode
        binding.switchDarkmode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            PreferencesManager.setDarkMode(this, isChecked)
        }


        // Initialize Mobile Ads SDK
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        // Load interstitial ad
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
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

            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
        //start CalculationHistory
        binding.btnHistory.setOnClickListener{
            val intent = Intent(this@MainActivity, CalcutationHistoryActivity::class.java)
            startActivity(intent)
        }


        //list Button and observe by Viewmodel
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
    }
}