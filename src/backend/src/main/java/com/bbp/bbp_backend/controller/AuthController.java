package com.bbp.bbp_backend.controller;

import com.bbp.bbp_backend.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserMapper userMapper;

    public AuthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

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

        // user exists -> check password (PLAIN TEXT)
        if (existing != null) {
            String dbPassword = (String) existing.get("passwordHash"); // 实际对应 users.password_hash
            if (dbPassword == null || !password.equals(dbPassword)) {
                throw new UnauthorizedException("INVALID_PASSWORD");
            }
            return resp(existing.get("id"), existing.get("username"), "LOGIN");
        }

        // user not exist -> create (PLAIN TEXT)
        userMapper.insert(username, password);

        Map<String, Object> created = userMapper.findByUsername(username);
        return resp(created.get("id"), created.get("username"), "REGISTER");
    }

    private static Map<String, Object> resp(Object id, Object username, String mode) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("username", username);
        m.put("mode", mode);
        return m;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
