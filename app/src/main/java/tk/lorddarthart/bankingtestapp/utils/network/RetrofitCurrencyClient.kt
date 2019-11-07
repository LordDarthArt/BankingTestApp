package tk.lorddarthart.bankingtestapp.utils.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tk.lorddarthart.bankingtestapp.utils.constant.Network.CurrencyInfo.BASE_URL
import tk.lorddarthart.bankingtestapp.utils.network.requests.GetCurrentCurrency

object RetrofitCurrencyClient {
    private var ourInstance: GetCurrentCurrency? = null

    fun getInstance(): GetCurrentCurrency {
        if (ourInstance == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            ourInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(GetCurrentCurrency::class.java)
        }
        return ourInstance!!
    }
}