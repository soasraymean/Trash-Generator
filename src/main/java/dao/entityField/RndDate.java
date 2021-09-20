package dao.entityField;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

//Class that represents a Date Field of Entity
public class RndDate {
    private final Date date;

    public RndDate() {
        date = generateDate();
    }
    public RndDate(java.sql.Date date){
        this.date=date;
    }

    //generate date in range
    private Date generateDate() {
        long startMillis = fiveYearsAgo().getTime();
        long endMillis = new Date().getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
    }

    private Date fiveYearsAgo() {
        String myDate = "14.09.2016";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date getDate() {
        return date;
    }

    //Method that returns the date that can be inserted into database
    public java.sql.Date getSQLDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new java.sql.Date(myDate.getTime());
    }

    public String getDateAsString() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }
}
