package kr.ac.jh.keycap.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
	private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	 // 비밀번호 암호화
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    // 비밀번호 체크
    public static boolean checkPassword(String plainPassword, String encodedPassword) {
        return encoder.matches(plainPassword, encodedPassword);
    }
}
