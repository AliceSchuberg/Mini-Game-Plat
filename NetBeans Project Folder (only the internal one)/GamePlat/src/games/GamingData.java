package games;

import java.io.Serializable;

public class GamingData implements Serializable {
    private String dataString;
    private int dataInt;

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public int getDataInt() {
        return dataInt;
    }

    public void setDataInt(int dataInt) {
        this.dataInt = dataInt;
    }
}
