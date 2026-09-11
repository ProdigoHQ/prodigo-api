package com.licode.prodigoerp.common.security;

import com.licode.prodigoerp.common.security.exception.JwtValidationException;
import com.licode.prodigoerp.common.security.dto.JwtPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Order(1)
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER);

        if(authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            Claims claims = jwtUtil.parseClaims(authHeader.substring(PREFIX.length()));

            if(!"access".equals(claims.get("type", String.class))){
                throw new JwtValidationException("Not an access token");
            }

            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(buildAuthentication(claims));
            }
        } catch (JwtValidationException ex){
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private Authentication buildAuthentication(Claims claims) {

        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);

        Collection<GrantedAuthority> authorities = Stream.concat(
                roles == null ? Stream.<String>empty() : roles.stream().map(r -> "ROLE_" + r),
                permissions == null ? Stream.<String>empty() : permissions.stream().map(p -> "PERM_" + p)
        ).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        JwtPrincipal principal = new JwtPrincipal(
                jwtUtil.parseUuid(claims.get("userId", String.class)),
                claims.get("username", String.class),
                claims.get("email", String.class),
                jwtUtil.parseUuid(claims.get("tenantId", String.class)),
                claims.get("tenantSlug", String.class)
        );

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return publicPaths.stream().anyMatch(publicPath ->
                pathMatcher.match(publicPath, path));
    }
}
