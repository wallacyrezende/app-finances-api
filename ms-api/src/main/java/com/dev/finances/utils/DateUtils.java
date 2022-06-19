package com.dev.finances.utils;

import lombok.extern.log4j.Log4j2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
public class DateUtils {

    private static final String DATE_FORMAT_DEFAULT = "dd/MM/yyyy";

    public static String dateFormatDefault (Date date) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DEFAULT);
        return dateFormat.format(date);
    }

    public static Date dateFormatDefault (String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DEFAULT);
        Date dateFormatted = null;
        try {
            dateFormatted = dateFormat.parse(date);
        } catch (ParseException ex) {
            log.error("Error to format date: " + date);
        } catch (Exception ex) {
            log.error("Unexpected error");
        }
        return dateFormatted;
    }

}
