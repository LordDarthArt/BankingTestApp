package tk.lorddarthart.bankingtestapp.utils.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tk.lorddarthart.bankingtestapp.utils.constant.Network.CardInfo.BASE_URL
import tk.lorddarthart.bankingtestapp.utils.network.requests.GetUsersList

object RetrofitBankClient {
    private var ourInstance: GetUsersList? = null

    fun getInstance(): GetUsersList {
        if (ourInstance == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            ourInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(GetUsersList::class.java)
        }
        return ourInstance!!
    }
}