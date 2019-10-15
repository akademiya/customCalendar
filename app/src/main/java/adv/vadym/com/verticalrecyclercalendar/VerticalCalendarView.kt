package adv.vadym.com.verticalrecyclercalendar

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VerticalCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val DEFAULT_MONTH_NAME_COLOR = Color.RED
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var lastSelectedDate: LDate? = null
    private val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spaceBottom)

    private val listCalendar = ArrayList<LDate>()

    private val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter(context) {
            val dateFrom = lastSelectedDate ?: it //formatter.parse(formatter.format(it))
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
        background = ColorDrawable(Color.RED)
        addView(TextView(context).apply {
            text = "Su"
            gravity = Gravity.CENTER_VERTICAL
        })
        addView(TextView(context).apply {
            text = "Mo"
            gravity = Gravity.CENTER_VERTICAL
        })
        addView(TextView(context).apply {
            text = "Tu"
            gravity = Gravity.CENTER_VERTICAL
        })
    }


    init {
        addView(headerView)
        addView(recyclerView)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AttrCalendarView, 0, 0)
//            calendarAdapter.setMonthColor(typedArray.getColor(R.styleable.AttrCalendarView_colorMonthName, DEFAULT_MONTH_NAME_COLOR))
//            loyCalendar.monthColor = typedArray.getColor(
//                R.styleable.AttrCalendarView_colorMonthName,
//                DEFAULT_MONTH_NAME_COLOR
//            )

        }

    }

    fun getSelectedDates(): List<LDate> {
        return emptyList()
    }

}