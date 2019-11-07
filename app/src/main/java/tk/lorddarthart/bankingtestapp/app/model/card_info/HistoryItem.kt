package tk.lorddarthart.bankingtestapp.app.model.card_info

import com.google.gson.annotations.Expose

class HistoryItem(
    @Expose val title: String,
    @Expose val icon_url: String,
    @Expose val date: String,
    @Expose var amount: String
)