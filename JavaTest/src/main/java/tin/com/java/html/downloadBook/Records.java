package tin.com.java.html.downloadBook;

import java.util.List;

public class Records {
    List RECORDS;

    @Override
    public String toString() {
        return "Records{" +
                "RECORDS=" + RECORDS +
                '}';
    }

    public List getRECORDS() {
        return RECORDS;
    }

    public void setRECORDS(List RECORDS) {
        this.RECORDS = RECORDS;
    }
}
