package com.licode.prodigoerp.common.autoconfig;

import com.licode.prodigoerp.tenant.application.port.output.SaveTenantPort;
import com.licode.prodigoerp.tenant.application.service.TenantService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SaveTenantPort.class)
    public TenantService tenantService(SaveTenantPort saveTenantPort) {

        return new TenantService(saveTenantPort);
    }

}
