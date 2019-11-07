package tk.lorddarthart.bankingtestapp.utils.network.requests

import retrofit2.Call
import retrofit2.http.GET
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrencyResponse
import tk.lorddarthart.bankingtestapp.utils.constant.Network.CurrencyInfo.GET_CURRENT_CURRENCIES

interface GetCurrentCurrency {
    @GET(GET_CURRENT_CURRENCIES)
    fun getCurrentCurrencies(): Call<CurrencyResponse>
}