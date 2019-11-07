package tk.lorddarthart.bankingtestapp.app.model.currency

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CurrencyValute {
    @SerializedName("GBP")
    @Expose
    lateinit var gbpCurrency: CurrencyObject
    @SerializedName("EUR")
    @Expose
    lateinit var eurCurrency: CurrencyObject
    var rubCurrency: CurrencyObject? = CurrencyObject("RUB", 1.0)
    @SerializedName("USD")
    @Expose
    lateinit var usdCurrency: CurrencyObject
}
