package tk.lorddarthart.bankingtestapp.utils.network.requests

import retrofit2.Call
import retrofit2.http.GET
import tk.lorddarthart.bankingtestapp.app.model.card_info.CardInfo
import tk.lorddarthart.bankingtestapp.utils.constant.Network.CardInfo.GET_USERS_LIST

interface GetUsersList {
    @GET(GET_USERS_LIST)
    fun getCardInfo(): Call<CardInfo>
}