package learn.fpoly.fhotel.Model;

import com.google.gson.annotations.SerializedName;

public class UserVoucher {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;
    @SerializedName("voucherId")
    private String voucherId;

    private Voucher voucher;
    @SerializedName("createdAt")
    private String createdAt;
    public UserVoucher() {
    }

    public UserVoucher(String id, String userId, String voucherId) {
        this.id = id;
        this.userId = userId;
        this.voucherId = voucherId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
