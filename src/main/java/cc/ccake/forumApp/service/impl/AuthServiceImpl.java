package cc.ccake.forumApp.service.impl;

import cc.ccake.forumApp.service.AuthService;
import cc.ccake.forumApp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            if (authenticate == null) {
                System.out.println(username + "认证失败");
                return null;
            }
            System.out.println(username + "认证成功");
        } catch (Exception e) {
            System.out.println("认证过程中出现异常: " + e.getMessage());
            e.printStackTrace();
        }
        return jwtTokenUtil.createToken(username);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null; // 或者可以选择抛出异常
        }
        return authentication.getName();
    }

}
