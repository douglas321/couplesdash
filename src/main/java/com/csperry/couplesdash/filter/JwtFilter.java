package com.csperry.couplesdash.filter;

import com.csperry.couplesdash.service.CustomUserDetailsService;
import com.csperry.couplesdash.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("✅ JWT cookie found: {}", token);
                    break;
                }
            }
        } else {
            logger.debug("⚠️ No cookies present in request.");
        }

        String email = null;
        if (token != null) {
            email = jwtService.extractEmail(token);
            logger.debug("📨 Email extracted from JWT: {}", email);
        } else {
            logger.debug("⚠️ No JWT token found in cookies.");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("🔍 Loading user details for email: {}", email);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.validateToken(token, userDetails)) {
                logger.debug("✅ JWT is valid. Setting authentication.");
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("🔐 Authentication set for: {}", email);
            } else {
                logger.warn("❌ Invalid JWT token.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
