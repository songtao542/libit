import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.liabit.autoclear.autoClearedValue
import com.liabit.test.TestAddSubViewActivity
import com.liabit.recyclerview.loadmore.LoadMoreAdapter

internal class V {
    var mAda: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
    fun setAda(context: Context, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {


        val adapterq = TestAddSubViewActivity.TestAdapter()

        val recyclerView = RecyclerView(context)

        recyclerView.adapter = LoadMoreAdapter.wrap(adapterq)
        recyclerView.adapter = LoadMoreAdapter.wrap(adapter)



    }
}