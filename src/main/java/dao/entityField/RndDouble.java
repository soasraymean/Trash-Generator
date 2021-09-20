package dao.entityField;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Random;

//Class represents a Random Double Field of Entity
public class RndDouble {
    private double rndDouble;

    public RndDouble(Double rndDouble) {
        this.rndDouble=rndDouble;
    }

    public double getRndDouble() {
        return rndDouble;
    }

    //generating a formatted double value from min to max
    public RndDouble() {
        int min = 1, max = 20;
        Random r = new Random();
        DecimalFormat df = new DecimalFormat("#.00000000");
        try {
            rndDouble = df.parse(df.format(min + (max - min) * r.nextDouble())).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
