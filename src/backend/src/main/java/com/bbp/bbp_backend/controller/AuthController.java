package com.bbp.bbp_backend.controller;

import com.bbp.bbp_backend.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // request body
    public static class LoginReq {
        public String username;
        public String password;
    }

    @PostMapping("/login-or-register")
    public Map<String, Object> loginOrRegister(@RequestBody LoginReq req) {
        String username = req.username == null ? "" : req.username.trim();
        String password = req.password == null ? "" : req.password;

        if (username.isEmpty() || password.isEmpty()) {
            throw new BadRequestException("username/password required");
        }

        Map<String, Object> existing = userMapper.findByUsername(username);

        // user exists -> check password
        if (existing != null) {
            String hash = (String) existing.get("passwordHash");
            if (hash == null || !encoder.matches(password, hash)) {
                throw new UnauthorizedException("INVALID_PASSWORD");
            }
            return resp(existing.get("id"), existing.get("username"), "LOGIN");
        }

        // user not exist -> create
        String hash = encoder.encode(password);
        userMapper.insert(username, hash);

        Map<String, Object> created = userMapper.findByUsername(username);
        return resp(created.get("id"), created.get("username"), "REGISTER");
    }

    // ---- helpers (Java 8 compatible) ----
    private static Map<String, Object> resp(Object id, Object username, String mode) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("username", username);
        m.put("mode", mode);
        return m;
    }

    // ---- error handling ----
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
