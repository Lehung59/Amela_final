package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtils {
    public static Date getCurrentDay() {
        Date currentDate = new Date();
        return new Date(currentDate.getTime());
    }
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // or handle the error as per your requirement
        }
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static LocalDate getFirstDayOfMonth(Integer year, Integer month) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        if (month == null) {
            month = DateUtils.getCurrentMonth();
        }

        return YearMonth.of(year, month).atDay(1);
    }

    public static LocalDate getLastDayOfMonth(Integer year, Integer month) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        if (month == null) {
            month = DateUtils.getCurrentMonth();
        }

        return YearMonth.of(year, month).atEndOfMonth();
    }



    public String formatTime(LocalTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }


}