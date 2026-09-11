package com.licode.prodigoerp.common.security;


import com.licode.prodigoerp.common.config.TenantContext;
import com.licode.prodigoerp.common.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";



    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER);

        if(authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tenantId = request.getHeader("X-Tenant-Id");

        if(tenantId == null || tenantId.isEmpty() || tenantId.equalsIgnoreCase("null")) {
           throw new NotFoundException("No Tenant Id found in the request");
        }

       // Then we need to find if the connected user exist in the tenant
        try{
            // TODO: BIG SECURITY ISSUE
            // TODO: Action: Need to verify if the current user belongs to the Tenant (tenantId provided from the header)
            // NOTE: when using JwtUtils or SecurityUtils, we have a Forbidden Error
            // Why? It says we need a complete authentification request

            UUID tenantUuid = UUID.fromString(tenantId); // Temp solution with security issue

            // This is the secure solution but we have an error.
//            UUID tenantUId = SecurityUtils.getCurrentUser().tenantId();
//            UUID tenantUUId = jwtUtil.getTenantIdFromToken(authHeader);

            // Set the current TenantId to the Tenant Context
            TenantContext.setCurrentTenant(tenantUuid);

            filterChain.doFilter(request, response);

        }catch (NotFoundException ex){
            TenantContext.clearCurrentTenant();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
            return;
        } catch (Exception ex){
            TenantContext.clearCurrentTenant();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }finally {
            TenantContext.clearCurrentTenant();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        List<String> paths = new ArrayList<>(publicPaths);

        paths.add("/api/1.0/s/admin/**");

        return paths.stream().anyMatch(publicPath ->
                pathMatcher.match(publicPath, path));
    }
}
