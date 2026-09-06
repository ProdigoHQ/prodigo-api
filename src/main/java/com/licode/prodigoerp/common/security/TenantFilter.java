package com.licode.prodigoerp.common.security;

import com.licode.prodigoerp.common.config.TenantContext;
import com.licode.prodigoerp.common.exception.NotFoundException;
import com.licode.prodigoerp.tenant.application.port.output.TenantQueryPort;
import com.licode.prodigoerp.tenant.domain.model.Tenant;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final TenantQueryPort tenantQueryPort;

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tenantId = request.getHeader("X-Tenant-Id");

        if(tenantId == null || tenantId.isEmpty() || tenantId.equalsIgnoreCase("null")) {
            filterChain.doFilter(request, response);
            return;
        }

        // then we need to find if there is a tenant with the id provided
        try{
            UUID tenantUuid = UUID.fromString(tenantId);

            Optional<Tenant> isExist = tenantQueryPort.findTenantById(tenantUuid);

            if(isExist.isEmpty()){
                throw new NotFoundException("Tenant not found with id " + tenantId + ", Access Denied.");
            }

            // TODO : need to get the current user and check if he/she belongs to the Tenant provided

            // Set the currentTenant to the Tenant Context
            TenantContext.setCurrentTenant(tenantUuid);

        }catch (NotFoundException ex){
            TenantContext.clearCurrentTenant();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
            return;
        } catch (Exception ex){
            TenantContext.clearCurrentTenant();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        List<String> paths = publicPaths;

        paths.addLast("/api/1.0/s/admin/**");

        return paths.stream().anyMatch(publicPath ->
                pathMatcher.match(publicPath, path));
    }
}
