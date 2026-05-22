package com.banking.account.security;

import com.banking.account.util.JwtTokenProvider;
import com.banking.common.constants.ServiceConstants;
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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateFromBearer(request);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateFromGatewayHeader(request);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateFromBearer(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return;
        }
        try {
            Claims claims = jwtTokenProvider.parseAndValidate(authorization.substring(7));
            setAuthentication(request, UUID.fromString(claims.getSubject()),
                    jwtTokenProvider.extractRoles(claims));
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticateFromGatewayHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader(ServiceConstants.HEADER_USER_ID);
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return;
        }
        try {
            String rolesHeader = request.getHeader("X-User-Roles");
            List<String> roles = rolesHeader != null && !rolesHeader.isBlank()
                    ? List.of(rolesHeader.split(","))
                    : List.of("ROLE_CUSTOMER");
            setAuthentication(request, UUID.fromString(userIdHeader.trim()), roles);
        } catch (IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(HttpServletRequest request, UUID userId, List<String> roles) {
        UserPrincipal principal = new UserPrincipal(userId, roles);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
