import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.micode.listviewcatogory.R

class MyCustomAdapter(private val context: Context, private val dataList: List<Any>) : BaseAdapter() {

    private val SECTION_TYPE = 0
    private val ITEM_TYPE = 1

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val item = dataList[position]

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = if (getItemViewType(position) == SECTION_TYPE) {
                inflater.inflate(R.layout.section_header_layout, parent, false)
            } else {
                inflater.inflate(R.layout.item_layout, parent, false)
            }
        } else {
            view = convertView
        }

        if (getItemViewType(position) == SECTION_TYPE) {
            val section = item as Section
            val textViewSection = view.findViewById<TextView>(R.id.section_header_month)
            textViewSection.text = section.sectionName

            val textViewAmount = view.findViewById<TextView>(R.id.section_header_amount)
            textViewAmount.text = section.amount
        } else {
            //val textView = view.findViewById<TextView>(R.id.textView)
            //textView.text = item.toString()
        }

        return view
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] is Section) {
            SECTION_TYPE
        } else {
            ITEM_TYPE
        }
    }

    override fun getViewTypeCount(): Int {
        return 2 // SECTION_TYPE and ITEM_TYPE
    }
}

data class Section(val sectionName: String, val amount: String)
