package com.cse110.team28.flashbackmusicplayer;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

import static com.cse110.team28.flashbackmusicplayer.MediaViewActivity.context;

/**
 * Created by abipalli on 3/16/18.
 */

public class LocalTimeInfo {
    private static LocalDate currDate;
    private static LocalTime currTime;
    protected static boolean needLiveTime = true;
    protected Context context;

    LocalTimeInfo(Context context) {
        this.context = context;
    }

    public void setCustomTime() {
        int year = currDate != null ? currDate.getYear() : LocalDate.now().getYear();
        int month = currDate != null ? currDate.getMonthValue() : LocalDate.now().getMonthValue();
        int date = currDate != null ? currDate.getDayOfMonth() : LocalDate.now().getDayOfMonth();
        int hour = currTime != null ? currTime.getHour() : LocalTime.now().getHour();
        int minute = currTime != null ? currTime.getMinute() : LocalTime.now().getMinute();

        final TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                MediaViewActivity.isLiveTime = false;

                currTime = java.time.LocalTime.of(hourOfDay, minute);
                Log.v("DatePickerDialog", "Date: " + currTime.toString());
                Toast.makeText(context, "Mocking: " + LocalDateTime.of(currDate, currTime).toString(), Toast.LENGTH_LONG).show();
            }
        }, hour, minute, DateFormat.is24HourFormat(context));
        timePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Save", timePicker);
        timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Use Live Time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Using Current Time", Toast.LENGTH_LONG).show();
                MediaViewActivity.isLiveTime = true;
            }
        });

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                needLiveTime = false;
                currDate = java.time.LocalDate.of(year, month+1, dayOfMonth);

                timePicker.show();

                Log.v("DatePickerDialog", "Date: " + currDate.toString());
            }
        }, year, month, date);
        datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Continue", datePicker);
        datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Use Live Date and Time", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    needLiveTime = true;
                    Log.d("MediaViewActivity", "Using Current Time");
                    Toast.makeText(context, "Using Current Time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datePicker.show();
    }

    public static LocalDateTime getCurrentTime() {
        if(needLiveTime) {
            return LocalDateTime.now();
        } else {
            return LocalDateTime.of(currDate, currTime);
        }
    }
}
