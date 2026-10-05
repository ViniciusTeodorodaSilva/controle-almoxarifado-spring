package br.com.almoxarifado.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/** Validates external configuration before any datasource bean is initialized. */
@Configuration(proxyBeanMethods = false)
@Profile("!test")
public class ConfiguracaoBancoExterno {
    @Bean
    static BeanFactoryPostProcessor validarConfiguracaoBanco(Environment environment) {
        return beanFactory -> {
            for (String name : new String[]{"DB_URL", "DB_USERNAME", "DB_PASSWORD"}) {
                String value = environment.getProperty(name);
                if (value == null || value.isBlank() || value.contains("${")) {
                    throw new IllegalStateException("Configuracao externa obrigatoria ausente: " + name);
                }
            }
        };
    }
}
