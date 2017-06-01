package bd.edu.daffodilvarsity.classorganizer.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by musfiqus on 5/30/2017.
 */

public class RecoverDayData {
    @SerializedName("a")
    private String a;

    @SerializedName("b")
    private String b;

    @SerializedName("c")
    private String c;

    @SerializedName("d")
    private int d;

    @SerializedName("e")
    private int e;

    @SerializedName("f")
    private String f;

    @SerializedName("g")
    private String g;

    @SerializedName("h")
    private String h;

    @SerializedName("i")
    private double i;

    @SerializedName("j")
    private String j;

    public RecoverDayData(String a, String b, String c, int d, int e, String f, String g, String h, double i, String j) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getE() {
        return e;
    }

    public String getF() {
        return f;
    }

    public String getG() {
        return g;
    }

    public String getH() {
        return h;
    }

    public double getI() {
        return i;
    }

    public String getJ() {
        return j;
    }
}
