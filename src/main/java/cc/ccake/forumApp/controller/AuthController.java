package cc.ccake.forumApp.controller;

import cc.ccake.forumApp.model.User;
import cc.ccake.forumApp.service.AuthService;
import cc.ccake.forumApp.service.UserService;
import cc.ccake.forumApp.utils.JwtTokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Processing POST /register request {}", user);
        var wp = new QueryWrapper<User>().eq("username", user.getUsername());
        Map<String, Object> response = new HashMap<>();
        if (userService.exists(wp)) {
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userService.save(user);

        response.put("message", "注册成功");
        return ResponseEntity.ok(response);
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Processing POST /login request");
        String username = user.getUsername();
        String password = user.getPassword();
        String token = authService.login(username, password);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "登录成功");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    //用户注销
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("Processing POST /logout request");
        jwtTokenUtil.blacklistToken(jwtTokenUtil.resolveToken(request));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "注销成功");
        return ResponseEntity.ok(response);
    }
    // ... 其他方法
}
