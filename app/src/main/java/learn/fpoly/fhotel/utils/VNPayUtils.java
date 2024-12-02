package learn.fpoly.fhotel.utils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class VNPayUtils {
    private static final String VNP_TMNCODE = "BL1LAWHL"; // Thay bằng mã thật của bạn
    private static final String VNP_HASHSECRET = "P9RKBO42IHG6VL5VE3C7X09LNFCG63SH";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String RETURN_URL = "https://return.url";

    public static String generateVNPayUrl(String orderId, long amount) throws Exception {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpLocale = "vn";
        String vnpCurrCode = "VND";
        String vnpIpAddr = "127.0.0.1"; // Thay bằng IP thực khi chạy

        // Ngày giờ hiện tại
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(new Date());

        // Thời gian hết hạn (10 phút sau)
        Calendar expireCal = Calendar.getInstance();
        expireCal.add(Calendar.MINUTE, 10);
        String vnpExpireDate = formatter.format(expireCal.getTime());

        // Tạo danh sách tham số
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnpVersion);
        params.put("vnp_Command", vnpCommand);
        params.put("vnp_TmnCode", VNP_TMNCODE);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // Đơn vị VNĐ (nhân 100)
        params.put("vnp_CreateDate", vnpCreateDate);
        params.put("vnp_CurrCode", vnpCurrCode);
        params.put("vnp_IpAddr", vnpIpAddr);
        params.put("vnp_Locale", vnpLocale);
        params.put("vnp_OrderInfo", "Thanh toán hóa đơn " + orderId);
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_ReturnUrl", RETURN_URL);
        params.put("vnp_TxnRef", orderId);
        params.put("vnp_ExpireDate", vnpExpireDate);

        // Tạo chuỗi mã hóa
        StringBuilder hashData = new StringBuilder();
        StringBuilder queryString = new StringBuilder(VNP_URL).append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashData.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8")).append('&');
            queryString.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8")).append('&');
        }

        // Xóa ký tự '&' cuối cùng
        hashData.deleteCharAt(hashData.length() - 1);
        queryString.deleteCharAt(queryString.length() - 1);

        // Mã hóa bằng HmacSHA512
        String secureHash = hmacSHA512(VNP_HASHSECRET, hashData.toString());
        queryString.append("&vnp_SecureHash=").append(secureHash);

        return queryString.toString();
    }

    private static String hmacSHA512(String key, String data) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        StringBuilder hash = new StringBuilder();
        for (byte b : hmacBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

}
