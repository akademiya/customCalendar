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
 * @author https://docs.google.com/document/d/1IgWlYvYVY_r9l98jcyJvLTBEeN-Nz-_g6wrIoEsFiYw/edit#
 */



class CalendarAdapter(val context: Context, val onDateClickListener: (LDate) -> Unit) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var items = emptyList<Item>()
    private val listSelectedCells = ArrayList<Int>()

    fun getSelectedDates() : ArrayList<Int> {
        return listSelectedCells
    }

    init {
        //FIXME: Refactor
        val dateManager = DateModel()
        val calendar = Calendar.getInstance()
        items = (0 until 24)
            .map {
                val days = dateManager.days()
                val newItems = mutableListOf<Item>()

                val format = SimpleDateFormat("MMMM yyyy", Locale.US)
                newItems.add(Item.MonthTitle(format.format(dateManager.calendar.time)))

                val monthDays = days.map {
                    val dateFormat = SimpleDateFormat("dd", Locale.US)
                    if(dateManager.isCurrentMonth(it)){
                        calendar.time = it
                        Item.MonthDay(
                            name = dateFormat.format(it),
                            isEnabled = !dateManager.isFutureDays(it),
                            selectionType = SelectionType.NONE,
                            date = LDate(
                                day = calendar.get(Calendar.DAY_OF_MONTH),
                                month = calendar.get(Calendar.MONTH),
                                year = calendar.get(Calendar.YEAR)
                            )
                        )
                    } else Item.EmptyDay
                }
                newItems.addAll(monthDays)

                dateManager.prevMonth()
                newItems
            }.run { reverse(this) }
            .flatten()
    }

    private fun <T> reverse(list: List<T>): List<T> {
        val result = ArrayList<T>(list.size)

        for (i in list.size - 1 downTo 0) {
            val element = list[i]
            result.add(element)
        }

        return result
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
                val cellWidth = (display.width) / 7
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cellWidth)
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
        is ViewHolder.DayViewHolder -> holder.bind(items[position] as Item.MonthDay)
        is ViewHolder.EmptyDayViewHolder -> { }
    }


    fun selectDate(startDate: LDate, endDate: LDate) {
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
                    selectionType = if (item.isEnabled) {
                        when (index) {
                            positionFrom -> SelectionType.FIRST
                            positionTo -> SelectionType.LAST
                            else -> SelectionType.MIDDLE
                        }
                    } else {
                        SelectionType.NONE
                    }

                )
                listSelectedCells.add(index) //FIXME: Side effect
            }
        }

        return newList
    }

    private fun initializedSelectedPositions(startDate: LDate, endDate: LDate): Pair<Int, Int> {
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
            val date: LDate
        ) : Item() {
            val isSelected: Boolean
                get() = selectionType != SelectionType.NONE
        }

        object EmptyDay : Item()
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class MonthTitleViewHolder(itemView: View) : ViewHolder(itemView) {
            fun bind(month: Item.MonthTitle) {
                (itemView as TextView).apply {
                    text = month.name
//                    setTextColor(month.nameColor)
                }
            }
        }

        class DayViewHolder(itemView: View, onDateClickListener: (LDate) -> Unit) : ViewHolder(itemView) {
            private var date: LDate? = null

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
                            SelectionType.SINGLE,
                            SelectionType.FIRST,
                            SelectionType.LAST -> Color.WHITE
                            else -> Color.BLACK
                        }.let(::setTextColor)
                    } else setTextColor(Color.LTGRAY)

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