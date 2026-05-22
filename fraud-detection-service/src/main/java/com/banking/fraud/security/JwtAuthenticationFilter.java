package com.banking.fraud.security;

import com.banking.common.constants.ServiceConstants;
import com.banking.fraud.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.startsWith("Bearer ")) {
                try {
                    Claims claims = jwtTokenProvider.parseAndValidate(auth.substring(7));
                    setAuth(request, UUID.fromString(claims.getSubject()), jwtTokenProvider.extractRoles(claims));
                } catch (JwtException | IllegalArgumentException ignored) {
                    SecurityContextHolder.clearContext();
                }
            } else {
                String userId = request.getHeader(ServiceConstants.HEADER_USER_ID);
                if (userId != null && !userId.isBlank()) {
                    try {
                        setAuth(request, UUID.fromString(userId.trim()), List.of("ROLE_CUSTOMER"));
                    } catch (IllegalArgumentException ignored) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void setAuth(HttpServletRequest request, UUID userId, List<String> roles) {
        UserPrincipal principal = new UserPrincipal(userId, roles);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
