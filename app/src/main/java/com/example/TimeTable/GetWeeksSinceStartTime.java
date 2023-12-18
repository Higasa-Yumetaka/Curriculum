package com.example.TimeTable;

import java.util.Calendar;
import java.util.Date;

public class GetWeeksSinceStartTime {
    public static int getWeeksSinceStartTime(long time) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(time); // 设置开始时间
        Date startDate = startCalendar.getTime(); // 将Calendar对象转换为Date对象

        Calendar currentCalendar = Calendar.getInstance();// 设置当前时间
        Date endDate = currentCalendar.getTime(); // 将Calendar对象转换为Date对象

        long diffMillis = endDate.getTime() - startDate.getTime(); // 计算当前时间与开始时间之间相差的毫秒数
        long diffWeeks = diffMillis / (1000 * 60 * 60 * 24 * 7); // 将毫秒数转换为周数
        return (int) (diffWeeks + 1); // 将周数加1，因为开始的那一周算第1周
    }
    public static long getMondayTimestampForWeeksAgo(int weeksAgo) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY) { // 如果当前是周日
            calendar.add(Calendar.WEEK_OF_YEAR, -1); // 将时间往前推一周
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 设置为周一
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 设置为0时
        calendar.set(Calendar.MINUTE, 0); // 设置为0分
        calendar.set(Calendar.SECOND, 0); // 设置为0秒
        calendar.set(Calendar.MILLISECOND, 0); // 设置为0毫秒
        weeksAgo = weeksAgo - 1; // 因为开始的那一周算第1周，所以要减1
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksAgo); // 将时间设置为weeksAgo周前的周一

        return calendar.getTimeInMillis(); // 返回时间戳
    }
}
