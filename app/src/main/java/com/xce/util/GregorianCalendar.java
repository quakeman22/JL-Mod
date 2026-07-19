package com.xce.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class GregorianCalendar extends Calendar {
    private static final int EPOCH_WDAY = 4;
    private static final int EPOCH_YEAR = 1970;

    protected native long computeDateTime();

    protected native void computeFields(long j);

    public GregorianCalendar() {
        this(TimeZone.getDefault());
    }

    public GregorianCalendar(TimeZone timezone) {
        setTime(new Date());
        setTimeZone(timezone);
    }

    @Override // java.util.Calendar
    public boolean after(Object obj) {
        if (obj instanceof GregorianCalendar) {
            GregorianCalendar gregoriancalendar = (GregorianCalendar) obj;
            if (getTimeInMillis() > gregoriancalendar.getTimeInMillis()) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Calendar
    public boolean before(Object obj) {
        if (obj instanceof GregorianCalendar) {
            GregorianCalendar gregoriancalendar = (GregorianCalendar) obj;
            if (getTimeInMillis() < gregoriancalendar.getTimeInMillis()) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Calendar
    public boolean equals(Object obj) {
        if (obj instanceof GregorianCalendar) {
            GregorianCalendar gregoriancalendar = (GregorianCalendar) obj;
            if (getTimeZone() == gregoriancalendar.getTimeZone() && getTime() == gregoriancalendar.getTime()) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Calendar
    public synchronized int hashCode() {
        return (int) ((Calendar) this).time;
    }

    @Override // java.util.Calendar
    protected void computeTime() {
        long l = getTimeZone().getOffset(((Calendar) this).fields[1] >= 1 ? 1 : 0, ((Calendar) this).fields[1], ((Calendar) this).fields[2], ((Calendar) this).fields[5], ((Calendar) this).fields[7], ((Calendar) this).fields[14]);
        ((Calendar) this).time = computeDateTime() - l;
        ((Calendar) this).isTimeSet = true;
    }

    @Override // java.util.Calendar
    protected void computeFields() {
        long l = getTimeZone().getRawOffset();
        computeFields(((Calendar) this).time + l);
        long l1 = getTimeZone().getOffset(((Calendar) this).fields[1] >= 1 ? 1 : 0, ((Calendar) this).fields[1], ((Calendar) this).fields[2], ((Calendar) this).fields[5], ((Calendar) this).fields[7], ((Calendar) this).fields[14]);
        if (l1 != l) {
            computeFields(((Calendar) this).time + l1);
        }
        ((Calendar) this).areFieldsSet = true;
    }
}
