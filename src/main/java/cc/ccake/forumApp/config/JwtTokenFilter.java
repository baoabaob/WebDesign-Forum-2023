package cc.ccake.forumApp.config;

import cc.ccake.forumApp.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            // 在处理请求之前记录请求信息
            logRequest(request);

            String token = jwtTokenUtil.resolveToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(request, response);
        } finally {
            // 在请求处理完毕后记录响应信息
            long duration = System.currentTimeMillis() - startTime;
            logResponse(request, response, duration);
        }
    }

    private void logRequest(HttpServletRequest request) {
        String paramString = request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=[" + String.join(", ", entry.getValue()) + "]")
                .collect(Collectors.joining(", ", "{", "}"));
        logger.info("Incoming request {} {} with parameters: {}", request.getMethod(), request.getRequestURI(), paramString);
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        logger.info("Responded to request {} {} with status: {} in {}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
    }

    private Authentication getAuthentication(String token) {
        String username = jwtTokenUtil.getUsername(token);
        return new UsernamePasswordAuthenticationToken(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
