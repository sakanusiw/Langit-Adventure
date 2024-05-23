import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.example.langitadventure.ItemsViewModelBasket
import com.example.langitadventure.R

class BasketAdapter(private val mList: List<ItemsViewModelBasket>) : RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_basket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModelBasket = mList[position]

        holder.itemName.text = ItemsViewModelBasket.itemName
        holder.rentDate.text = ItemsViewModelBasket.rentDate
        holder.duration.text = ItemsViewModelBasket.duration
        holder.quantity.text = ItemsViewModelBasket.quantity.toString()
        holder.totalPrice.text = ItemsViewModelBasket.totalPrice
        holder.itemImage.setImageResource(ItemsViewModelBasket.imageResource)

//        holder.deleteButton.setOnClickListener {
//            // Implementasi untuk hapus item dari keranjang
//        }
//
//        holder.minusButton.setOnClickListener {
//            // Implementasi untuk mengurangi jumlah item
//        }
//
//        holder.plusButton.setOnClickListener {
//            // Implementasi untuk menambah jumlah item
//        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemName: TextView = ItemView.findViewById(R.id.textViewCardKeranjangNama)
        val rentDate: TextView = ItemView.findViewById(R.id.textViewCardKeranjangTanggal)
        val duration: TextView = ItemView.findViewById(R.id.textViewCardKeranjangDurasi)
        val quantity: TextView = ItemView.findViewById(R.id.textViewCardKeranjangJumlah)
        val totalPrice: TextView = ItemView.findViewById(R.id.textViewCardKeranjangTotal)
        val itemImage: ImageView = ItemView.findViewById(R.id.imageViewCardKeranjang)
        val deleteButton: ImageButton = ItemView.findViewById(R.id.imageButtonCardKeranjangDelete)
        val minusButton: ImageButton = ItemView.findViewById(R.id.imageButtonCardKeranjangMinus)
        val plusButton: ImageButton = ItemView.findViewById(R.id.imageButtonCardKeranjangPlus)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}