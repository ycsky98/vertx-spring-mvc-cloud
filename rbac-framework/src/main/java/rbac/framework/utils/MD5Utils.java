package rbac.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yangcong
 * 
 *      md5加密
 */
public class MD5Utils {

    public final static String md5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte b[] = md5.digest();

            StringBuffer sb = new StringBuffer("");
            for (int n = 0; n < b.length; n++) {
                int i = b[n];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
            return sb.toString(); // 32位加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
