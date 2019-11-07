package tk.lorddarthart.bankingtestapp.app.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tk.lorddarthart.bankingtestapp.R
import tk.lorddarthart.bankingtestapp.app.model.card_info.HistoryItem
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrenciesEnum
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrenciesEnum.*
import tk.lorddarthart.bankingtestapp.app.view.adapter.HistoryAdapter
import tk.lorddarthart.bankingtestapp.app.viewmodel.MainActivityViewModel
import tk.lorddarthart.bankingtestapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            this
        )[MainActivityViewModel::class.java]
    }

    private lateinit var loadingDialog: ProgressDialog

    private val currentCurrencyObserver = Observer<CurrenciesEnum> { currency ->
        with(mainActivityBinding) {
            when (currency) {
                CURRENCY_GBP -> {
                    currencyGbp.setCurrencyEnabled(currencyGbpIcon, currencyGbpTitle, true)
                    currencyEur.setCurrencyEnabled(currencyEurIcon, currencyEurTitle, false)
                    currencyRub.setCurrencyEnabled(currencyRubIcon, currencyRubTitle, false)
                }
                CURRENCY_EUR -> {
                    currencyGbp.setCurrencyEnabled(currencyGbpIcon, currencyGbpTitle, false)
                    currencyEur.setCurrencyEnabled(currencyEurIcon, currencyEurTitle, true)
                    currencyRub.setCurrencyEnabled(currencyRubIcon, currencyRubTitle, false)
                }
                CURRENCY_RUB -> {
                    currencyGbp.setCurrencyEnabled(currencyGbpIcon, currencyGbpTitle, false)
                    currencyEur.setCurrencyEnabled(currencyEurIcon, currencyEurTitle, false)
                    currencyRub.setCurrencyEnabled(currencyRubIcon, currencyRubTitle, true)
                }
                else -> {
                    currencyGbp.setCurrencyEnabled(currencyGbpIcon, currencyGbpTitle, false)
                    currencyEur.setCurrencyEnabled(currencyEurIcon, currencyEurTitle, false)
                    currencyRub.setCurrencyEnabled(currencyRubIcon, currencyRubTitle, false)
                }
            }
        }
        mainActivityBinding.mainActivityHistoryList.adapter?.notifyDataSetChanged()
    }

    private val cardNumberObserver = Observer<String> { cardNumber ->
        mainActivityBinding.mainActivityCardSerial.text = cardNumber
    }

    private val userNameObserver = Observer<String> { userName ->
        mainActivityBinding.mainActivityUserName.text = userName
    }

    private val validThroughObserver = Observer<String> { validThrough ->
        mainActivityBinding.mainActivityValidThruValue.text = validThrough
    }

    private val yourConvertedBalanceObserver = Observer<String> { myConvertedBalance ->
        mainActivityBinding.mainActivityCurrentBalance.text = myConvertedBalance
    }

    private val yourBalanceObserver = Observer<String> { myBalance ->
        mainActivityBinding.mainActivityCurrentBalanceDollar.text = myBalance
    }

    private val historyListObserver = Observer<List<HistoryItem>> {
        mainActivityBinding.mainActivityHistoryList.adapter?.notifyDataSetChanged()
    }

    private val isLoadingOverObserver = Observer<Boolean> {  isLoadingOver ->
        showLoadingDialog(!isLoadingOver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initialization()
    }

    private fun initialization() {
        hangObservers()
        start()
        initListeners()
    }

    private fun hangObservers() {
        with(mainActivityViewModel) {
            currentCurrencyLiveData.observe(this@MainActivity, currentCurrencyObserver)
            cardNumberLiveData.observe(this@MainActivity, cardNumberObserver)
            userNameLiveData.observe(this@MainActivity, userNameObserver)
            validThroughLiveData.observe(this@MainActivity, validThroughObserver)
            yourConvertedBalanceLiveData.observe(this@MainActivity, yourConvertedBalanceObserver)
            yourBalanceLiveData.observe(this@MainActivity, yourBalanceObserver)
            historyLiveData.observe(this@MainActivity, historyListObserver)
            isLoadingOverLiveData.observe(this@MainActivity, isLoadingOverObserver)
        }
    }

    private fun start() {
        if (mainActivityViewModel.previousCurrency == CURRENCY_USD) {
            mainActivityViewModel.begin()
            mainActivityViewModel.previousCurrency = CURRENCY_GBP
        }

        with(mainActivityBinding.mainActivityHistoryList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mainActivityBinding.mainActivityHistoryList.adapter =
                HistoryAdapter(mainActivityViewModel.getHistoryList()!!, mainActivityViewModel)
        }
    }

    private fun initListeners() {
        with(mainActivityBinding) {
            currencyGbp.setOnClickListener(this@MainActivity)
            currencyEur.setOnClickListener(this@MainActivity)
            currencyRub.setOnClickListener(this@MainActivity)
            mainActivityUfoDots.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        if (mainActivityViewModel.previousCurrency != null) {
            mainActivityViewModel.setPreviousCurrency()
        } else {
            mainActivityViewModel.previousCurrency = CURRENCY_GBP
        }
        when (v?.id) {
            R.id.currency_gbp -> {
                if (mainActivityViewModel.currentCurrencyLiveData.value != CURRENCY_GBP) {
                    mainActivityViewModel.setCurrentCurrency(CURRENCY_GBP)
                }
            }
            R.id.currency_eur -> {
                if (mainActivityViewModel.currentCurrencyLiveData.value != CURRENCY_EUR) {
                    mainActivityViewModel.setCurrentCurrency(CURRENCY_EUR)
                }
            }
            R.id.currency_rub -> {
                if (mainActivityViewModel.currentCurrencyLiveData.value != CURRENCY_RUB) {
                    mainActivityViewModel.setCurrentCurrency(CURRENCY_RUB)
                }
            }
            R.id.main_activity_ufo_dots -> {
                with (mainActivityViewModel) {
                    setActualBalanceWithSelectedCurrencyToNull()
                    fetchData()
                }
            }
        }
    }

    private fun showLoadingDialog(show: Boolean) {
        if (show) {
            loadingDialog = ProgressDialog(this)
            with(loadingDialog) {
                setTitle("")
                setMessage(getString(R.string.updating))
                setCancelable(false)
                show()
            }
        } else {
            if (::loadingDialog.isInitialized) {
                loadingDialog.cancel()
            }
        }
    }

    private fun View.setCurrencyEnabled(icon: ImageView, title: TextView, enabled: Boolean) {
        if (enabled) {
            (this as CardView).setCardBackgroundColor(
                resources.getColor(
                    android.R.color.white
                )
            )
            icon.setColorFilter(resources.getColor(R.color.primaryBackground))
            title.setTextColor(resources.getColor(R.color.primaryBackground))
        } else {
            (this as CardView).setCardBackgroundColor(resources.getColor(R.color.currency_selector_background))
            icon.setColorFilter(resources.getColor(android.R.color.white))
            title.setTextColor(resources.getColor(android.R.color.white))
        }
    }
}
