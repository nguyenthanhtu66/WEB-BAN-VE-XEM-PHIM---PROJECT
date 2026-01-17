package vn.edu.hcmuaf.fit.demo1.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hash(String rawPassword){
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
    }
    public static boolean check(String rawPassword, String hashedPassword){
        return BCrypt.checkpw(rawPassword,hashedPassword);
    }
}
