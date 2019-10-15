package adv.vadym.com.verticalrecyclercalendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (calendarView.getSelectedDates().isEmpty())
            Toast.makeText(this, resources.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
        else {
            val dates = ArrayList<Date>()
            for (calendarDay in calendarView.getSelectedDates())
                dates.add(calendarDay.getDate())
            dates.sort()
        }

        calendarView.getSelectedDates()

        tv_selected_period.text = calendarView.getSelectedDates().toString()
    }
}