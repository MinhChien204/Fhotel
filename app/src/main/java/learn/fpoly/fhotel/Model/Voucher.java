package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Voucher {
    @SerializedName("_id")
    private String id;

    private String code;
    private int discount;

    private String expirationDate;

    public Voucher() {
    }

    public Voucher(String id,String code, int discount, String expirationDate) {
        this.id = id;
        this.code = code;
        this.discount = discount;
        this.expirationDate = expirationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
