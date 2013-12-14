package allen.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_DD_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }

        ;

    };
    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_DD_HH_MM_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }

        ;
    };
    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_DD_HH_MM_SS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        ;
    };

    private final static ThreadLocal<SimpleDateFormat> HH_MM_SS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }

        ;
    };

    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_CHINESE = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年MM月");
        }

        ;

    };
    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM");
        }

        ;
    };

    private final static ThreadLocal<SimpleDateFormat> M_D = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("M.d");
        }

        ;
    };

    /**
     * yyyy-MM-dd HH:mm
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parsemm(String dateStr) throws ParseException {
        return YYYY_MM_DD_HH_MM_FORMAT.get().parse(dateStr);
    }

    public static Date parseMM(String dateStr) throws ParseException {
        return YYYY_MM_FORMAT.get().parse(dateStr);
    }

    /**
     * yyyy-MM-dd HH:mm
     *
     * @param date
     * @return
     */
    public static String formatMM(Date date) {
        return YYYY_MM_DD_HH_MM_FORMAT.get().format(date);
    }

    public static String formatYYYY_MM_CHINESE(Date date) {
        return YYYY_MM_CHINESE.get().format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseSS(String dateStr) throws ParseException {
        return YYYY_MM_DD_HH_MM_SS_FORMAT.get().parse(dateStr);
    }

    public static Date parseSimpleSS(String dateStr) throws ParseException {
        return HH_MM_SS_FORMAT.get().parse(dateStr);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formatSS(Date date) {
        if (date == null) {
            return null;
        }
        return YYYY_MM_DD_HH_MM_SS_FORMAT.get().format(date);
    }

    public static String formatSimpleSS(Date date) {
        return HH_MM_SS_FORMAT.get().format(date);
    }

    /**
     * yyyy-MM-dd
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseDD(String dateStr) {
        try {
            return YYYY_MM_DD_FORMAT.get().parse(dateStr);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String formatDD(Date date) {
        return YYYY_MM_DD_FORMAT.get().format(date);
    }

    /**
     * @param offsetDays 当前时间的偏移：-2前天，-1昨天，0今天，1明天，2后天
     * @return yyyy-MM-dd
     * @author yangxuehua
     */
    public static String getYYYY_MM_DD(int offsetDays) {
        Calendar calendar = Calendar.getInstance();
        if (offsetDays != 0)
            calendar.add(Calendar.DATE, offsetDays);
        Date date = calendar.getTime();
        return formatDD(date);
    }

    /**
     * @param offsetDays 当前时间的偏移：-2前天，-1昨天，0今天，1明天，2后天
     * @return M.d or 明天(M.d)
     * @author yangxuehua
     */
    public static String getDisplayName(int offsetDays) {
        Calendar calendar = Calendar.getInstance();
        if (offsetDays != 0)
            calendar.add(Calendar.DATE, offsetDays);
        Date date = calendar.getTime();
        String m_d = M_D.get().format(date);// e.g：5.2
        switch (offsetDays) {
            case -2:
                return "前天(" + m_d + ")";
            case -1:
                return "昨天(" + m_d + ")";
            case 0:
                return "今天(" + m_d + ")";
            case 1:
                return "明天(" + m_d + ")";
            case 2:
                return "后天(" + m_d + ")";
            default:
                return m_d;
        }
    }

    public static String getDisplayForMovieShow(String yyyy_MM_dd) {
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = parse.parse(yyyy_MM_dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat monthAndDay = new SimpleDateFormat("M月dd日");
        String monthAndDayString = monthAndDay.format(date);

        String today = DateUtils.getYYYY_MM_DD(0);
        if (today.equals(yyyy_MM_dd)) {
            return "今天 " + monthAndDayString;
        }
        String tomorrow = DateUtils.getYYYY_MM_DD(1);
        if (tomorrow.equals(yyyy_MM_dd)) {
            return "明天 " + monthAndDayString;
        }
        String dayAfterTomorrow = DateUtils.getYYYY_MM_DD(2);
        if (dayAfterTomorrow.equals(yyyy_MM_dd)) {
            return "后天 " + monthAndDayString;
        }

        SimpleDateFormat format = new SimpleDateFormat("E M月dd日");
        String ret = format.format(date);
        ret = ret.replaceAll("星期", "周");
        return ret;
    }

    /**
     * 到第二天0点的时间间隔
     *
     * @param date
     * @return
     */
    public static long getMilliSecondToTomorrow(Date date) {
        String today = formatDD(date);
        Date today0 = parseDD(today);
        return (86400000 - (date.getTime() - today0.getTime()));
    }

}
