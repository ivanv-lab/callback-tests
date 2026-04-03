package ru.git.ivanv_lab.utils.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTimeUtils {
    /**
     * Возвращает время в формате "HH:mm".
     *
     * @param minutes Если 0 - текущее время.
     *                Если >0 - время + минуты.
     *                Если <0 - время - минуты
     */
    public static String getTime(int minutes) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (minutes != 0) {
            return currentTime.plusMinutes(minutes).format(formatter);
        }
        return currentTime.format(formatter);
    }

    /**
     * Возвращает дату в формате "dd.MM.yyyy"
     *
     * @param day Если 0 - текущая дата.
     *            Если >0 - дата + дней.
     *            Если <0 - дата - дней
     * @return
     */
    public static String getDate(int day) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, day);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date.getTime());
    }

    public static String getDateFormat(int day, String format){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, day);
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date.getTime());
    }
    /**
     * Возвращает дату в формате "yyyy-MM-dd HH:mm"
     *
     * @param day
     * @param minutes
     * @return
     */
    public static String getDateTime(int day, int minutes) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (day != 0)
            currentDateTime = currentDateTime.plusDays(day);

        if (minutes != 0)
            currentDateTime = currentDateTime.plusMinutes(minutes);

        String dateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                          + " " + currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return dateTime;
    }

    /**
     * Возвращает дату в формате "dd.MM.yyyy HH:mm"
     *
     * @param day
     * @param minutes
     * @return
     */
    public static String getDateTimeDotFormat(int day, int minutes) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (day != 0)
            currentDateTime = currentDateTime.plusDays(day);

        if (minutes != 0)
            currentDateTime = currentDateTime.plusMinutes(minutes);

        String dateTime = currentDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                          currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return dateTime;
    }

    /**
     * Возвращает дату в формате "yyyy-MM-ddTHH:mm:ss"
     *
     * @param day
     * @param minutes
     * @return
     */
    public static String getDateTTime(int day, int minutes) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (day != 0)
            currentDateTime = currentDateTime.plusDays(day);

        if (minutes != 0)
            currentDateTime = currentDateTime.plusMinutes(minutes);

        String dateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                          + "T" + currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return dateTime;
    }

    /**
     * Возвращает список дней в формате "MON, THU, ..."
     *
     * @param includeToday включая текущий день
     * @return
     */
    public static String getDays(boolean includeToday) {
        if (includeToday) return "MON, TUE, WED, THU, FRI, SAT, SUN";
        else {
            DayOfWeek currentDay = LocalDate.now().getDayOfWeek();

            StringBuilder daysBuilder = new StringBuilder();
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day != currentDay) {
                    daysBuilder.append(day.toString().substring(0, 3).toUpperCase()).append(",");
                }
            }
            return daysBuilder.delete(daysBuilder.length() - 1, daysBuilder.length()).toString();
        }
    }

    public static String[] getDaysArray(boolean includeToday) {
        if (includeToday) {
            return new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        } else {
            DayOfWeek currentDay = LocalDate.now().getDayOfWeek();

            String[] days = new String[6];
            int i = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day != currentDay) {
                    days[i] = day.toString().substring(0, 3).toUpperCase();
                    i++;
                }
            }
            return days;
        }
    }
}
