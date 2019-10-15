package adv.vadym.com.verticalrecyclercalendar

import java.util.*

data class LDate(val year: Int, val month: Int, val day: Int) {

    private val calendar: Calendar = Calendar.getInstance()

    fun getCalendar() : Calendar {
        return calendar
    }

    fun getDate() : Date {
        return calendar.time
    }


//    private val year: Int? = null
//    private val month: Int? = null
//    private val day: Int? = null


//    val year: Date? = null
//    val month: Date? = null
//    val day: Date? = null
//    val date: Date? = null
//
//    var monthColor = Color.CYAN
//    var monthTextSize = 14
//    var monthIteHeight = 48
//    var monthGravity = Gravity.CENTER_HORIZONTAL
//
//    var dayColorNormal = Color.BLACK
//    var firstSelectedDayColor = Color.WHITE
//    var lastSelectedDayColor = Color.WHITE
//    var selecteedDaysColor = Color.GRAY
//    var inactiveDaysColor = Color.LTGRAY
//    var firstSelectedDayBackground = Color.WHITE
//    var lastSelectedDayBackground = Color.WHITE
//    var selecteedDaysBackground = Color.BLUE
//    var daysItemHeight = 48
//
//    var headerDaysColor = Color.BLACK
//    var headerDaysSize = 14
//    var headerItemHeight = 48
//
//
//    interface ILoyCalendar {
//        fun getToday() : String
//    }

}