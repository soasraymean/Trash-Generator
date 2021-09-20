package dao;

import dao.entityField.*;

import java.sql.Date;

//Class that represents a record
public class Entity {
    private final RndDate date;
    private final RndStringEn stringEn;
    private final RndStringRu stringRu;
    private final RndInt rndInt;
    private final RndDouble rndDouble;

    public Entity() {
        this.date = new RndDate();
        this.stringEn = new RndStringEn();
        this.stringRu = new RndStringRu();
        this.rndInt = new RndInt();
        this.rndDouble = new RndDouble();
    }

    public Entity(RndDate date, RndStringEn stringEn, RndStringRu stringRu, RndInt rndInt, RndDouble rndDouble) {
        this.date = date;
        this.stringEn = stringEn;
        this.stringRu = stringRu;
        this.rndInt = rndInt;
        this.rndDouble = rndDouble;
    }

    @Override
    public String toString() {
        String delim = "||";
        return date.getDateAsString() + delim +
                stringEn.getString() + delim +
                stringRu.getString() + delim +
                rndInt.getRndInt() + delim +
                rndDouble.getRndDouble() + delim;
    }

    public Date getDate() {
        return date.getSQLDate();
    }

    public String getStringEn() {
        return stringEn.getString();
    }

    public String getStringRu() {
        return stringRu.getString();
    }

    public Long getInt() {
        return rndInt.getRndInt();
    }

    public double getDouble() {
        return rndDouble.getRndDouble();
    }
}
