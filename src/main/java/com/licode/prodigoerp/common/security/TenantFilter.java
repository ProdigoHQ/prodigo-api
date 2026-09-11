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

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            UUID tenantUuid = SecurityUtils.getCurrentUser().tenantId();

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
