package com.majateam.bikespot.helper;

import java.util.Calendar;
import java.util.Date;

/**
 * Simons project - Human Equation - http://www.equationhumaine.co
 * Created by nmartino on 15-12-18.
 */
public class DateHelper {

    public static Date getDateMonthsAgo(int numOfMonthsAgo)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1 * numOfMonthsAgo);
        return c.getTime();
    }
}
