package adv.vadym.com.verticalrecyclercalendar

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * USE CASES

 * @param oldStart - дата начала периода до взаимодействия пользователя с календарем
 * @param oldEnd - дата конца периода до взаимодействия пользователя с календарем
 * @param newStart - дата начала периода, после клика на дату в календаре
 * @param newEnd - дата конца периода, после клика на дату в календаре
 * @param DateClicked - дата, на которую кликает пользователь
 *
 * 16-072-04-1 Если с момента открытия календаря следующий клик по календарю будет нечетным,
 * и кликнуть DateClicked,
 * то newStart = newEnd = DateClicked.

 * 16-072-04-2 Если с момента открытия календаря следующий клик по календарю будет четным,
 * и кликнуть DateClicked,
 * и DateClicked < oldStart,
 * то newStart = DateClicked и newEnd = oldEnd

 * 16-072-04-3 Если с момента открытия календаря следующий клик по календарю будет четным,
 * и кликнуть DateClicked
 * и DateClicked > oldStart,
 * то newStart = oldStart и newEnd = DateClicked

 * @author https://docs.google.com/document/d/1IgWlYvYVY_r9l98jcyJvLTBEeN-Nz-_g6wrIoEsFiYw/edit#
 */



class CalendarAdapter(val context: Context, val onDateClickListener: (Date) -> Unit) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var items = emptyList<Item>()
    private val listSelectedCells = ArrayList<Int>()

    init {
        //FIXME: Refactor
        val dateManager = DateModel()
        items = (0 until 24)
            .map {
                val days = dateManager.days()
                val newItems = mutableListOf<Item>()

                val format = SimpleDateFormat("MMMM yyyy", Locale.US)
                newItems.add(Item.MonthTitle(format.format(dateManager.calendar.time)))

                val monthDays = days.map {
                    val dateFormat = SimpleDateFormat("dd", Locale.US)
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    if(dateManager.isCurrentMonth(it)){
                        Item.MonthDay(
                            name = dateFormat.format(it),
                            isEnabled = !dateManager.isFutureDays(it),
                            selectionType = SelectionType.NONE,
                            date = formatter.parse(formatter.format(it))
                        )
                    } else Item.EmptyDay
                }
                newItems.addAll(monthDays)

                dateManager.prevMonth()
                newItems
            }
            .flatten()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when (viewType) {
        MONTH_TITLE_VIEW_TYPE -> ViewHolder.MonthTitleViewHolder(TextView(context).apply {
            //FIXME: Rewrite
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CELL_HEIGHT)
            gravity = Gravity.CENTER
            textSize = 25F
        })
        DAY_VIEW_TYPE -> ViewHolder.DayViewHolder(
            TextView(context).apply {
                //FIXME: Rewrite
                val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val cellWidth = (display.width + 4) / 7
                layoutParams = ViewGroup.LayoutParams(cellWidth, cellWidth)
                gravity = Gravity.CENTER
            },
            onDateClickListener
        )
        EMPTY_DAY_VIEW_TYPE -> ViewHolder.EmptyDayViewHolder(context)
        else -> throw IllegalStateException("wrong view holder type")
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when (holder) {
        is ViewHolder.MonthTitleViewHolder -> holder.bind(items[position] as Item.MonthTitle)
        is ViewHolder.DayViewHolder -> {
            val day = items[position] as Item.MonthDay
            holder.bind(day)

        }
        is ViewHolder.EmptyDayViewHolder -> { }
    }


    fun selectDate(startDate: Date, endDate: Date) {
        resetOldSelectedItems()
        val (positionFirst, positionLast) = initializedSelectedPositions(startDate, endDate)
        if (positionFirst == -1) {
            return
        }


        items = when {
            positionFirst == positionLast -> {
                items.toMutableList().apply {
                    set(positionFirst, (get(positionFirst) as Item.MonthDay).copy(selectionType = SelectionType.SINGLE))
                    listSelectedCells.add(positionFirst)
                }
            }
            positionFirst > positionLast -> items.selectRange(positionLast, positionFirst)
            else -> items.selectRange(positionFirst, positionLast)
        }
        notifyDataSetChanged()
    }

    private fun  List<Item>.selectRange(positionFrom: Int, positionTo: Int): List<Item> {
        val newList = items.toMutableList()


        (positionFrom until positionTo+1).forEach{ index ->
            val item = newList[index] as? Item.MonthDay
            if(item != null){
                newList[index] = item.copy(
                    selectionType = when (index) {
                        positionFrom -> SelectionType.FIRST
                        positionTo -> SelectionType.LAST
                        else -> SelectionType.MIDDLE
                    }
                )
                listSelectedCells.add(index) //FIXME: Side effect
            }
        }

        return newList
    }

    private fun initializedSelectedPositions(startDate: Date, endDate: Date): Pair<Int, Int> {
        val positionFirst = items.indexOfFirst {
            it is Item.MonthDay && it.date == startDate
        }

        val positionLast = if (startDate != endDate) {
            items.indexOfFirst { it is Item.MonthDay && it.date == endDate }
        } else positionFirst

        return Pair(positionFirst, positionLast)
    }

    private fun resetOldSelectedItems() {
        if(listSelectedCells.size == 0){
            return
        }

        items = items.toMutableList().apply {
            listSelectedCells.forEach {
                (this[it] as? Item.MonthDay)
                    ?.copy(selectionType = SelectionType.NONE)
                    ?.let { item -> this.set(it, item)   }
            }
        }


        (listSelectedCells.first() to listSelectedCells.last()).let { (first, last) ->
            listSelectedCells.clear() //FIXME: Side effect
            notifyItemRangeChanged(first, last-first+1)
        }
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Item.MonthTitle -> MONTH_TITLE_VIEW_TYPE
        is Item.MonthDay -> DAY_VIEW_TYPE
        Item.EmptyDay -> EMPTY_DAY_VIEW_TYPE
    }


    sealed class Item {
        data class MonthTitle(val name: String) : Item()
        data class MonthDay(
            val name: String,
            val isEnabled: Boolean,
            val selectionType: SelectionType,
            val date: Date
        ) : Item() {
            val isSelected: Boolean
                get() = selectionType != SelectionType.NONE
        }

        object EmptyDay : Item()
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class MonthTitleViewHolder(itemView: View) : ViewHolder(itemView) {
            fun bind(month: Item.MonthTitle) {
                (itemView as TextView).text = month.name
            }
        }

        class DayViewHolder(itemView: View, onDateClickListener: (Date) -> Unit) : ViewHolder(itemView) {
            private var date: Date? = null

            init {
                itemView.setOnClickListener{
                    date?.let(onDateClickListener)
                }
            }

            fun bind(day: Item.MonthDay) {
                date = day.date

                (itemView as TextView).apply {
                    isEnabled = day.isEnabled
                    text = day.name


                    if(day.isEnabled) {
                        when (day.selectionType) {
                            SelectionType.FIRST,
                            SelectionType.SINGLE,
                            SelectionType.LAST -> Color.WHITE
                            else -> Color.BLACK
                        }.let(::setTextColor)
                    }

                    background = when (day.selectionType) {
                        SelectionType.SINGLE -> {
                            context.resources.getDrawable(R.drawable.bg_circle_selected)
                        }
                        SelectionType.FIRST -> {
                            context.resources.getDrawable(R.drawable.bg_selected_start)
                        }
                        SelectionType.MIDDLE -> {
                            context.resources.getDrawable(R.drawable.bg_line_selected)
                        }
                        SelectionType.LAST -> {
                            context.resources.getDrawable(R.drawable.bg_selected_end)
                        }
                        SelectionType.NONE -> null

                    }

                }
            }

        }

        class EmptyDayViewHolder(context: Context) : ViewHolder(View(context))
    }


    companion object {
        const val MONTH_TITLE_VIEW_TYPE = 0
        const val DAY_VIEW_TYPE = 1
        const val EMPTY_DAY_VIEW_TYPE = 2

        const val CELL_HEIGHT = 170
    }

    enum class SelectionType { NONE, SINGLE, FIRST, MIDDLE, LAST }
}