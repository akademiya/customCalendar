package adv.vadym.com.verticalrecyclercalendar

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class VerticalCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var lastSelectedDate: CalendarDay? = null
    private val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spaceBottom)

    private val selectedDatesList = ArrayList<CalendarDay>()

    private val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter(context) {
            val dateFrom = lastSelectedDate ?: it
            val dateTo = it.takeIf { lastSelectedDate != null }

            calendarAdapter.selectDate(dateFrom, dateTo ?: dateFrom)
            lastSelectedDate = dateFrom.takeIf { dateTo == null }
        }
    }

    private val recyclerView = RecyclerView(context).apply {
        layoutManager = GridLayoutManager(context, 7, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (calendarAdapter.getItemViewType(position)) {
                        CalendarAdapter.EMPTY_DAY_VIEW_TYPE,
                        CalendarAdapter.DAY_VIEW_TYPE -> 1
                        CalendarAdapter.MONTH_TITLE_VIEW_TYPE -> 7
                        else -> -1
                    }
                }
            }

        }
        adapter = calendarAdapter

        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = spacingInPixels
            }
        })
        scrollToPosition(adapter!!.itemCount - 1)
        setHasFixedSize(true)
    }

    private val headerView = LinearLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.header_month_height)
        )
        gravity = Gravity.CENTER_VERTICAL
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val cellWidth = display.width / 7
        addView(TextView(context).apply {
            text = resources.getString(R.string.sun)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.mon)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.tue)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.wed)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.thu)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.fri)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
        addView(TextView(context).apply {
            text = resources.getString(R.string.sat)
            minWidth = cellWidth
            gravity = Gravity.CENTER
        })
    }

    private val divider = LinearLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.divider_height))
        background = ColorDrawable(Color.LTGRAY)
    }


    init {
        orientation = VERTICAL
        addView(headerView)
        addView(divider)
        addView(recyclerView)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AttrCalendarView, 0, 0)
            calendarAdapter.monthTitleViewHeight = typedArray.getDimensionPixelSize(R.styleable.AttrCalendarView_monthTitleViewHeight, resources.getDimensionPixelSize(R.dimen.month_title_view_height))
            calendarAdapter.monthTitleTextSize = typedArray.getDimension(R.styleable.AttrCalendarView_textSizeMonth, resources.getDimension(R.dimen.text_size_month))
            calendarAdapter.monthTitleTextColor = typedArray.getColor(R.styleable.AttrCalendarView_colorMonthName, resources.getColor(R.color.colorAccent))
        }

    }

    fun getSelectedDates(): List<CalendarDay> {
        val result = ArrayList<CalendarDay>()
        return calendarAdapter.selectedDatesList
    }

}