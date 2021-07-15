import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.liabit.test.TestAddSubViewActivity
import com.liabit.recyclerview.loadmore.LoadMoreAdapter
import java.util.regex.Pattern

internal class V {
    var mAda: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
    fun setAda(context: Context, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {


        val adapterq = TestAddSubViewActivity.TestAdapter()

        val recyclerView = RecyclerView(context)

        recyclerView.adapter = LoadMoreAdapter.wrap(adapterq)
        recyclerView.adapter = LoadMoreAdapter.wrap(adapter)


    }


}


fun main() {
    val PING_PATTERN = Pattern.compile("min/avg/max/mdev = (.*) ms")

    val str = "rtt min/avg/max/mdev = 254.781/269.314/297.368/15.109 ms"

    val m = PING_PATTERN.matcher(str)
    if (m.find()) {
        val delay = m.group(1)
        if (delay != null) {
            val avg = delay.split("/")[1].toFloatOrNull()?.toLong()
            System.out.println("avg: $avg")
        }
    } else {
        System.out.println("not find")
    }
}