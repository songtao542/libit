import androidx.recyclerview.widget.RecyclerView

internal class V {
    var mAda: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
    fun setAda(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
        mAda = adapter
    }
}