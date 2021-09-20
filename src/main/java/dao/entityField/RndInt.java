package dao.entityField;

//Class represents a Random Integer Field of Entity
public class RndInt {
    private final Long rndInt;

    //Random Number generation
    public RndInt() {
        int min = 1, max = 100_000_000;
        rndInt = (long)((Math.random() * (max - min)) + min);
    }

    public RndInt(Long rndInt) {
        this.rndInt=rndInt;
    }

    public Long getRndInt() {
        return rndInt;
    }
}
