package tk.lorddarthart.bankingtestapp.app.model.currency

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CurrencyObject {
    @SerializedName("CharCode")
    @Expose
    lateinit var charCode: String
    @SerializedName("Value")
    @Expose
    var value: Double? = null

    constructor(charCode: String, value: Double) {
        this.charCode = charCode
        this.value = value
    }
}
