package seller.bmwallet.com.bangmodseller.Class;

import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Thanatkorn on 9/28/2014.
 */
public class Utility {
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String genSecureKey(String key){
        DateTime date = DateTime.now();
        System.out.println(key);
        int index = date.getDayOfMonth()+date.getMonthOfYear();
        String secKey = md5(key.substring(index,index+8));
        return secKey;
    }

    public static String getMonthName(int i){
        String month;
        switch (i){
            case 1: month = "มกราคม"; break;
            case 2: month = "กุมภาพันธ์"; break;
            case 3: month = "มีนาคม"; break;
            case 4: month = "เมษายน"; break;
            case 5: month = "พฤษภาคม"; break;
            case 6: month = "มิถุนายน"; break;
            case 7: month = "กรกฎาคม"; break;
            case 8: month = "สิงหาคม"; break;
            case 9: month = "กันยายน"; break;
            case 10: month = "ตุลาคม"; break;
            case 11: month = "พฤษจิกายน"; break;
            default: month = "ธันวาคม"; break;
        }
        return month;
    }
}
