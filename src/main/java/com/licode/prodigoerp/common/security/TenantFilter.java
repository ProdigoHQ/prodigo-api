package com.licode.prodigoerp.common.security;

import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.domain.model.User;
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
    private final LoadUserPort loadUserPort;

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tenantId = request.getHeader("X-Tenant-Id");

        if(tenantId == null || tenantId.isEmpty() || tenantId.equalsIgnoreCase("null")) {
           throw new NotFoundException("No Tenant Id found in the request");
        }

       // Then we need to find if the connected user exist in the tenant
        try{
            UUID tenantUuid = UUID.fromString(tenantId);
            UUID userId = SecurityUtils.getCurrentUser().userId();

            User existUser = loadUserPort.findUserByIdAndTenantId(userId, tenantUuid).orElseThrow(
                    () -> new NotFoundException("Access Denied:: User not found with id: " + userId + " in the Tenant with id: " + tenantId)
            );

            // Set the currentTenant to the Tenant Context
            TenantContext.setCurrentTenant(existUser.getTenant().getId());

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
