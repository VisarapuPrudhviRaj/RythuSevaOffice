package nk.bluefrog.rythusevaoffice.models;

/**
 *  Created by ipuser6 on 06-09-2017.
 */

public class CustomDate {

    private int date;
    private int month;
    private int year;


    public CustomDate(int date, int month, int year) {
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public int getDate() {
        return date;
    }

   public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}