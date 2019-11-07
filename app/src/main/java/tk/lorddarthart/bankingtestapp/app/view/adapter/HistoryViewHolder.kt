package tk.lorddarthart.bankingtestapp.app.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tk.lorddarthart.bankingtestapp.R

class HistoryViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    val title = item.findViewById<TextView>(R.id.history_transaction_title)
    val date = item.findViewById<TextView>(R.id.history_transaction_date)
    val convertedValue = item.findViewById<TextView>(R.id.history_transaction_converted)
    val usdValue = item.findViewById<TextView>(R.id.history_transaction_usd)
    val icon = item.findViewById<ImageView>(R.id.history_transaction_icon)
}