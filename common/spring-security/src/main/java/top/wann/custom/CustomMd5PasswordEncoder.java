package top.wann.custom;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.wann.common.utils.MD5;

/**
 * ClassName: CustomMd5PasswordEncoder
 * Package: com.jerry.security.custom
 * Description:
 *
 * @Author wann
 * @date 2023-03-03 14:31
 * @Version 1.0
 */
@Component
public class CustomMd5PasswordEncoder implements PasswordEncoder {
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
