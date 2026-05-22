package com.banking.transaction.security;

import com.banking.common.constants.ServiceConstants;
import com.banking.transaction.util.JwtTokenProvider;
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
            authenticateBearer(request);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateGatewayHeader(request);
            }
        }
        chain.doFilter(request, response);
    }

    private void authenticateBearer(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return;
        }
        try {
            Claims claims = jwtTokenProvider.parseAndValidate(authorization.substring(7));
            setAuth(request, UUID.fromString(claims.getSubject()), jwtTokenProvider.extractRoles(claims));
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticateGatewayHeader(HttpServletRequest request) {
        String userId = request.getHeader(ServiceConstants.HEADER_USER_ID);
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            String roles = request.getHeader("X-User-Roles");
            List<String> roleList = roles != null && !roles.isBlank()
                    ? List.of(roles.split(",")) : List.of("ROLE_CUSTOMER");
            setAuth(request, UUID.fromString(userId.trim()), roleList);
        } catch (IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuth(HttpServletRequest request, UUID userId, List<String> roles) {
        UserPrincipal principal = new UserPrincipal(userId, roles);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
