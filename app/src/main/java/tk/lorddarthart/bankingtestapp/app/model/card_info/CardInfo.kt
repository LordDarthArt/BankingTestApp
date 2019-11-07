package tk.lorddarthart.bankingtestapp.app.model.card_info

import com.google.gson.annotations.Expose

class CardInfo(
    @Expose val card_number: String,
    @Expose val cardholder_name: String,
    @Expose val valid: String,
    @Expose val balance: Double,
    @Expose val transaction_history: List<HistoryItem>
)