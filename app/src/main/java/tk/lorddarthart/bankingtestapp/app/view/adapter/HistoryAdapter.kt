package tk.lorddarthart.bankingtestapp.app.view.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tk.lorddarthart.bankingtestapp.R
import tk.lorddarthart.bankingtestapp.app.model.card_info.HistoryItem
import tk.lorddarthart.bankingtestapp.app.model.currency.CurrenciesEnum.CURRENCY_USD
import tk.lorddarthart.bankingtestapp.app.viewmodel.MainActivityViewModel
import tk.lorddarthart.bankingtestapp.utils.converter.CurrenciesConverter.convertCurrency

class HistoryAdapter(
    private val historyList: List<HistoryItem>,
    private val mainActivityViewModel: MainActivityViewModel
) : RecyclerView.Adapter<HistoryViewHolder>() {
    private lateinit var historyView: View
    var historyViewHolder: HistoryViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        historyView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_history,
            parent,
            false
        )

        historyViewHolder = HistoryViewHolder(historyView)
        return historyViewHolder!!
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        with(holder) {
            title.text = historyItem.title
            date.text = historyItem.date
            convertedValue.text = if (historyItem.amount.contains('-')) {
                "- "
            } else {
                ""
            } + mainActivityViewModel.getStringWithCurrencySymbol(
                mainActivityViewModel.getCurrentCurrency()!!,
                mainActivityViewModel.roundOffDecimal(historyItem.amount.toDouble()).toString().replace(
                    "-",
                    ""
                )
            )
            usdValue.text = mainActivityViewModel.getResultStringForDefaultBalance(historyItem.amount)
        }
        animatedImgLoad(historyItem.icon_url, holder)
    }

    private fun animatedImgLoad(urlString: String, holder: HistoryViewHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val animPlaceholderPiePlus = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.ic_preload
            ) as AnimatedImageDrawable
            animPlaceholderPiePlus.start()
            Glide.with(holder.itemView.context).load(urlString)
                .placeholder(animPlaceholderPiePlus)
                .error(R.drawable.ic_no_image_available)
                .into(holder.icon)
        } else {
            val animPlaceholderOreoMinus = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.ic_preload
            ) as AnimationDrawable
            animPlaceholderOreoMinus.start()
            Glide.with(holder.itemView.context).load(urlString)
                .placeholder(animPlaceholderOreoMinus)
                .error(R.drawable.ic_no_image_available)
                .into(holder.icon)
        }
    }
}