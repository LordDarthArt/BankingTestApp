package tk.lorddarthart.bankingtestapp.app.model.currency

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CurrencyResponse {
    @SerializedName("Valute")
    @Expose
    lateinit var currencyValute: CurrencyValute
}