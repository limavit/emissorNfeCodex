package com.example.nfe.config;

import com.example.nfe.common.AccessKeyService;
import com.example.nfe.nfe.NFeCalculationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NFeBeansConfig {
    @Bean
    NFeCalculationService nFeCalculationService() {
        return new NFeCalculationService();
    }

    @Bean
    AccessKeyService accessKeyService() {
        return new AccessKeyService();
    }
}
