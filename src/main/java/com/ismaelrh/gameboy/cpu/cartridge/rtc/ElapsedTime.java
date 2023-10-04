package com.ismaelrh.gameboy.cpu.cartridge.rtc;

public class ElapsedTime {


    private int seconds;

    private int minutes;

    private int hours;

    private int days;

    private boolean daysOverflow;

    public ElapsedTime() {

    }


    public ElapsedTime(int days, int hours, int minutes, int seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        correctDays();
    }


    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
        correctDays();
    }

    public ElapsedTime getPlusSeconds(int seconds) {

        Pair<Integer, Integer> secondsInfo = secondsRollInfo(seconds);
        Pair<Integer, Integer> minutesInfo = minutesRollInfo(secondsInfo.b);
        Pair<Integer, Integer> hoursInfo = hoursRollInfo(minutesInfo.b);
        int totalDays = days + hoursInfo.b;
        if (daysOverflow) {
            totalDays += 511;
        }
        return new ElapsedTime(totalDays, hoursInfo.a, minutesInfo.a, secondsInfo.a);
    }

    private void correctDays() {
        if (this.days > 511) {
            this.days -= 511;
            this.daysOverflow = true;
        } else {
            this.daysOverflow = false;
        }
    }

    public boolean isDaysOverflow() {
        return daysOverflow;
    }

    public void setDaysOverflow(boolean daysOverflow) {
        this.daysOverflow = daysOverflow;
    }

    //New seconds value, times it has rolled
    private Pair<Integer, Integer> secondsRollInfo(int increment) {
        if (this.seconds <= 59) {
            return new Pair<>((this.seconds + increment) % 60, (this.seconds + increment) / 60);
        } else if (this.seconds + increment <= 63) {   //Was artificially set to 60-63
            return new Pair<>(this.seconds + increment, 0);
        } else {
            return new Pair<>((this.seconds + increment - 4) % 60, (this.seconds + increment - 4) / 60 - 1);
        }
    }


    //New seconds value, times it has rolled
    private Pair<Integer, Integer> minutesRollInfo(int increment) {
        if (this.minutes <= 59) {
            return new Pair<>((this.minutes + increment) % 60, (this.minutes + increment) / 60);
        } else if (this.minutes + increment <= 63) {
            return new Pair<>(this.minutes + increment, 0);
        } else {
            return new Pair<>((this.minutes + increment - 4) % 60, (this.minutes + increment - 4) / 60 - 1);    //-1 bc does not increase by one
        }
    }

    private Pair<Integer, Integer> hoursRollInfo(int increment) {
        if (this.hours <= 23) {
            return new Pair<>((this.hours + increment) % 24, (this.hours + increment) / 24);
        } else if (this.hours + increment <= 31) {
            return new Pair<>(this.hours + increment, 0);
        } else {   //Was artificially set to 60-63
            return new Pair<>((this.hours + increment - 8) % 24, (this.hours + increment - 8) / 24 - 1); //-1 bc does not increase by one
        }
    }


    public ElapsedTime copy() {
        return getPlusSeconds(0);
    }

    public class Pair<A, B> {
        public final A a;
        public final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }


    @Override
    public String toString() {
        return "ElapsedTime{" +
                "seconds=" + seconds +
                ", minutes=" + minutes +
                ", hours=" + hours +
                ", days=" + days +
                ", daysOverflow=" + daysOverflow +
                '}';
    }
}
