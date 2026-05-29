package br.com.nfesefassp.config;

import br.com.nfesefassp.service.*;
import br.com.nfesefassp.util.*;

import br.com.nfesefassp.util.AccessKeyService;
import br.com.nfesefassp.service.NFeCalculationService;
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
