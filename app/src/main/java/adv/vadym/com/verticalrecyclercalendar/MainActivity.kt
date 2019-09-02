package adv.vadym.com.verticalrecyclercalendar

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_month_calendar.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var lastSelectedDate: Date? = null
    private lateinit var newCalendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_month_calendar)
        setSupportActionBar(toolbar)

        newCalendarAdapter = CalendarAdapter(this) {
            val dateFrom = lastSelectedDate ?: formatter.parse(formatter.format(it))
            val dateTo = formatter.parse(formatter.format(it)).takeIf { lastSelectedDate != null }

            newCalendarAdapter.selectDate(dateFrom, dateTo ?: dateFrom)
            lastSelectedDate = dateFrom?.takeIf { dateTo == null }
        }

        btn_back.setOnClickListener {
            Toast.makeText(this, "Click back arrow", Toast.LENGTH_SHORT).show()
        }

        btn_apply.setOnClickListener {
            Toast.makeText(this, "Click apply button", Toast.LENGTH_SHORT).show()
        }
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spaceBottom)

        calendar.apply {
            adapter = newCalendarAdapter

            layoutManager = GridLayoutManager(context, 7, RecyclerView.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (newCalendarAdapter.getItemViewType(position)) {
                            CalendarAdapter.EMPTY_DAY_VIEW_TYPE,
                            CalendarAdapter.DAY_VIEW_TYPE -> 1
                            CalendarAdapter.MONTH_TITLE_VIEW_TYPE -> 7
                            else -> -1
                        }
                    }
                }

            }
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = spacingInPixels
                }
            })
            this.scrollToPosition(adapter!!.itemCount-1)
//            addItemDecoration(SpacesItemDecoration(spacingInPixels))
            setHasFixedSize(true)
        }
    }


    inner class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = space
        }
    }

}