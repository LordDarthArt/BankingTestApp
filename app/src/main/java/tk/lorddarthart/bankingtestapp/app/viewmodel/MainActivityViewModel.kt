package tk.lorddarthart.bankingtestapp.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response
import tk.lorddarthart.bankingtestapp.app.model.card_info.CardInfo
import tk.lorddarthart.bankingtestapp.app.model.card_info.HistoryItem
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrenciesEnum
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrenciesEnum.*
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrencyResponse
import tk.lorddarthart.bankingtestapp.utils.converter.CurrenciesConverter.convertCurrency
import tk.lorddarthart.bankingtestapp.utils.network.RetrofitBankClient
import tk.lorddarthart.bankingtestapp.utils.network.RetrofitCurrencyClient
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivityViewModel : ViewModel() {

    private val _currentCurrencyLiveData = MutableLiveData<CurrenciesEnum>()
    val currentCurrencyLiveData: LiveData<CurrenciesEnum>
        get() = _currentCurrencyLiveData

    private val _cardNumberLiveData = MutableLiveData<String>()
    val cardNumberLiveData: LiveData<String>
        get() = _cardNumberLiveData

    private val _userNameLiveData = MutableLiveData<String>()
    val userNameLiveData: LiveData<String>
        get() = _userNameLiveData

    private val _validThroughLiveData = MutableLiveData<String>()
    val validThroughLiveData: LiveData<String>
        get() = _validThroughLiveData

    private val _yourConvertedBalanceLiveData = MutableLiveData<String>()
    val yourConvertedBalanceLiveData: LiveData<String>
        get() = _yourConvertedBalanceLiveData

    private val _yourBalanceLiveData = MutableLiveData<String>()
    val yourBalanceLiveData: LiveData<String>
        get() = _yourBalanceLiveData

    private val _historyLiveData = MutableLiveData<List<HistoryItem>>()
    val historyLiveData: LiveData<List<HistoryItem>>
        get() = _historyLiveData

    private val _isLoadingOverLiveData = MutableLiveData<Boolean>()
    val isLoadingOverLiveData: LiveData<Boolean>
        get() = _isLoadingOverLiveData

    private var cardInfoObject: CardInfo? = null
    private var currencyResponse: CurrencyResponse? = null
    var previousCurrency: CurrenciesEnum? = null
    var actualBalanceWithSelectedCurrency: Double? = null

    init {
        previousCurrency = CURRENCY_USD
        _currentCurrencyLiveData.value = CURRENCY_GBP
        _historyLiveData.value = mutableListOf()
    }

    fun fetchData() {
        _isLoadingOverLiveData.value = false
        RetrofitBankClient.getInstance().getCardInfo()
            .enqueue(object : retrofit2.Callback<CardInfo> {
                override fun onResponse(call: Call<CardInfo>, response: Response<CardInfo>) {
                    cardInfoObject = response.body()
                    fetchCurrencies()
                }

                override fun onFailure(call: Call<CardInfo>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun fetchCurrencies() {
        RetrofitCurrencyClient.getInstance().getCurrentCurrencies()
            .enqueue(object : retrofit2.Callback<CurrencyResponse> {
                override fun onResponse(
                    call: Call<CurrencyResponse>,
                    response: Response<CurrencyResponse>
                ) {
                    currencyResponse = response.body()
                    setLiveDatas()
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {

                }
            })
    }

    private fun setLiveDatas() {
        _cardNumberLiveData.value = cardInfoObject?.card_number
        _userNameLiveData.value = cardInfoObject?.cardholder_name
        _validThroughLiveData.value = cardInfoObject?.valid
        _yourBalanceLiveData.value =
            getCurrentBalanceDefault(cardInfoObject?.balance.toString())
        actualBalanceWithSelectedCurrency = convertCurrency(
            actualBalanceWithSelectedCurrency ?: cardInfoObject?.balance,
            getCurrencyFromEnum(CURRENCY_USD),
            getCurrencyFromEnum(_currentCurrencyLiveData.value!!)
        )
        _yourConvertedBalanceLiveData.value = getResultString(
            roundOffDecimal(
                actualBalanceWithSelectedCurrency!!
            ).toString(), CURRENCY_USD
        )
        if (_historyLiveData.value?.isEmpty()!!) {
            (_historyLiveData.value as MutableList).addAll(cardInfoObject?.transaction_history!!)
        }

        // Notifying observer
        _historyLiveData.value = _historyLiveData.value

        setCurrentCurrency(_currentCurrencyLiveData.value!!)
        _isLoadingOverLiveData.value = true
    }

    fun setCurrentCurrency(currency: CurrenciesEnum) {
        actualBalanceWithSelectedCurrency = convertCurrency(
            actualBalanceWithSelectedCurrency ?: cardInfoObject?.balance,
            getCurrencyFromEnum(_currentCurrencyLiveData.value!!),
            getCurrencyFromEnum(currency)
        )
        _yourConvertedBalanceLiveData.value = getResultString(
            roundOffDecimal(
                actualBalanceWithSelectedCurrency!!
            ).toString(), currency
        )
        for (historyItem in _historyLiveData.value!!) {
            historyItem.amount = convertCurrency(
                historyItem.amount.toDouble(),
                getCurrencyFromEnum(_currentCurrencyLiveData.value!!),
                getCurrencyFromEnum(currency)
            ).toString()
        }
        _currentCurrencyLiveData.value = currency
    }

    private fun getCurrencyFromEnum(currency: CurrenciesEnum): Double? {
        with(currencyResponse?.currencyValute) {
            return when (currency) {
                CURRENCY_GBP -> this?.gbpCurrency?.value
                CURRENCY_EUR -> this?.eurCurrency?.value
                CURRENCY_RUB -> this?.rubCurrency?.value
                CURRENCY_USD -> this?.usdCurrency?.value
            }
        }
    }

    fun begin() {
        fetchData()
    }

    fun getHistoryList(): List<HistoryItem>? {
        return _historyLiveData.value
    }

    fun setPreviousCurrency() {
        previousCurrency = when (_currentCurrencyLiveData.value) {
            CURRENCY_GBP -> CURRENCY_GBP
            CURRENCY_EUR -> CURRENCY_EUR
            CURRENCY_USD -> CURRENCY_USD
            CURRENCY_RUB -> CURRENCY_RUB
            else -> CURRENCY_USD
        }
    }

    fun getCurrentCurrency(): CurrenciesEnum? {
        return _currentCurrencyLiveData.value
    }

    fun getStringWithCurrencySymbol(currency: CurrenciesEnum, string: String): String {
        return when (currency) {
            CURRENCY_GBP -> "\u00a3 $string"
            CURRENCY_EUR -> "\u20ac $string"
            CURRENCY_RUB -> "\u20BD $string"
            CURRENCY_USD -> "\$ $string"
        }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).replace(",", ".").toDouble()
    }

    fun getResultString(targetString: String, currency: CurrenciesEnum): String? {
        return if (targetString.contains('-')) {
            "- "
        } else {
            ""
        } + getStringWithCurrencySymbol(
            currency,
            roundOffDecimal(targetString.toDouble()).toString().replace(
                "-",
                ""
            )
        )
    }

    fun getResultStringForDefaultBalance(targetString: String): String? {
        return if (targetString.contains('-')) {
            "- "
        } else {
            ""
        } + getStringWithCurrencySymbol(
            CURRENCY_USD,
            roundOffDecimal(
                convertCurrency(
                    targetString.toDouble(),
                    getCurrencyFromEnum(_currentCurrencyLiveData.value!!),
                    getCurrencyFromEnum(CURRENCY_USD)
                )
            ).toString().replace("-", "")
        )
    }

    fun getCurrentBalanceDefault(targetString: String): String {
        return if (targetString.contains('-')) {
            "- "
        } else {
            ""
        } + getStringWithCurrencySymbol(
            CURRENCY_USD,
            roundOffDecimal(
                targetString.toDouble()
            ).toString().replace("-", "")
        )
    }

    fun setActualBalanceWithSelectedCurrencyToNull() {
        actualBalanceWithSelectedCurrency = null
    }
}