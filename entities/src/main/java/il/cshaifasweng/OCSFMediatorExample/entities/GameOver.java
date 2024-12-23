package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameOver implements Serializable {
    private String result;
    public GameOver(String result) { this.result = result; }
    public String getResult() { return result; }
}
