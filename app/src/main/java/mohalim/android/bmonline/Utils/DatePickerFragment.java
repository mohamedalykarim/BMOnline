package mohalim.android.bmonline.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mohalim.android.bmonline.MainActivity;

import static android.content.ContentValues.TAG;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    public final String DATE_FROM = "date_from";
    public final String DATE_TO = "date_to";
    String type = "";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        try {
            String dateString = "01/01/2017";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateString);
            long startDate = date.getTime();
            datePickerDialog.getDatePicker().setMinDate(startDate);
        } catch (ParseException e) {
        }

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (type == DATE_FROM){
            MainActivity.chooseDetails.dateFrom.setText("Date from: "+day+" / "+ (month+1) + " / "+ year);
            MainActivity.dateFromDay = day;
            MainActivity.dateFromMonth = month;
            MainActivity.dateFromYear = year;

        }else if (type == DATE_TO){
            MainActivity.chooseDetails.dateTo.setText("Date TO: "+day+" / "+ (month+1) + " / "+ year);
            MainActivity.dateToDay = day;
            MainActivity.dateToMonth = month;
            MainActivity.dateToYear = year;
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}

