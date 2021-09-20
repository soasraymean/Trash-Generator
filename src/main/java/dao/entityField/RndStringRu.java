package dao.entityField;

//Class represents a Random String of Russian characters
public class RndStringRu {
    private final String string;
    private static final String ALPHABET = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя";

    public RndStringRu() {
        string = generateString();
    }

    public RndStringRu(String string) {
        this.string=string;
    }

    //generation of string with target length
    private String generateString() {
        int targetLength = 10;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetLength; i++) {
            int index = getRandomInt(0, 66);
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    private int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public String getString() {
        return string;
    }
}
