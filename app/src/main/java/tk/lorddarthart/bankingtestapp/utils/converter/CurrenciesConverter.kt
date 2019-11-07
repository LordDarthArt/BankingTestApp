package tk.lorddarthart.bankingtestapp.utils.converter

object CurrenciesConverter {
    fun convertCurrency(value: Double?, oldCurrency: Double?, newCurrency: Double?): Double {
        return value!! * oldCurrency!! / newCurrency!!
    }
}