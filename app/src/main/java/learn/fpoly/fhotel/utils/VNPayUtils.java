package learn.fpoly.fhotel.utils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayUtils {
    public static String generateVNPayUrl(String roomId, long amount) {
        // URL mẫu của VNPay
        String vnp_TmnCode = "BL1LAWHL";
        String vnp_HashSecret = "GKZKQIPJXRVZ4IG1FR2I2593PWSTL0Q4";
        String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "https://yourdomain.com/vnpay_return";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "token_create");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu số tiền tính bằng VND * 100
        vnp_Params.put("vnp_TxnRef", roomId); // Mã đơn hàng
        vnp_Params.put("vnp_OrderInfo", "Thanh toan phong");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Sắp xếp và ký dữ liệu
        String queryUrl = VNPayUtils.hashAllFields(vnp_Params);
        String vnp_SecureHash = VNPayUtils.generateHash(vnp_Params, vnp_HashSecret);
        return vnp_Url + "?" + queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    private static String hashAllFields(Map<String, String> fields) {
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (hashData.length() > 0) hashData.append("&");
            hashData.append(field.getKey()).append("=").append(field.getValue());
        }
        return hashData.toString();
    }

    private static String generateHash(Map<String, String> fields, String secret) {
        String hashData = hashAllFields(fields);
        return VNPayUtils.hmacSHA512(secret, hashData);
    }

    private static String hmacSHA512(String key, String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : hmacBytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate HMAC SHA512 hash", e);
        }
    }
}

