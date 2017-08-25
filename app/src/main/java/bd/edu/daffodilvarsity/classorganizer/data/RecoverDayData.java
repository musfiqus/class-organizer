package bd.edu.daffodilvarsity.classorganizer.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mushfiqus Salehin on 5/30/2017.
 * musfiqus@gmail.com
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

    public class GenerationOne {
        @SerializedName("a")
        private String a;

        @SerializedName("b")
        private String b;

        @SerializedName("c")
        private String c;

        @SerializedName("d")
        private String d;

        @SerializedName("e")
        private String e;

        @SerializedName("f")
        private double f;

        public GenerationOne(String a, String b, String c, String d, String e, double f) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
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

        public String getD() {
            return d;
        }

        public String getE() {
            return e;
        }

        public double getF() {
            return f;
        }
    }
}
