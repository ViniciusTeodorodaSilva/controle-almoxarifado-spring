package br.com.almoxarifado.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoBancoExternoTests {
    private AnnotationConfigApplicationContext context(Map<String, Object> values, String profile) {
        var context = new AnnotationConfigApplicationContext();
        // No datasource: these tests cannot open a database connection.
        context.getEnvironment().getPropertySources().remove("systemEnvironment");
        context.getEnvironment().getPropertySources().remove("systemProperties");
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("isolated", values));
        context.getEnvironment().setActiveProfiles(profile);
        context.register(ConfiguracaoBancoExterno.class);
        return context;
    }
    @Test void productionRequiresEveryVariableWithoutRevealingValues() {
        for (String missing : new String[]{"DB_URL", "DB_USERNAME", "DB_PASSWORD"}) {
            var values = new HashMap<String, Object>(Map.of("DB_URL", "placeholder-url", "DB_USERNAME", "placeholder-user", "DB_PASSWORD", "placeholder-only"));
            values.remove(missing);
            try (var context = context(values, "prod")) {
                var error = assertThrows(IllegalStateException.class, context::refresh);
                assertEquals("Configuracao externa obrigatoria ausente: " + missing, error.getMessage());
            }
        }
    }
    @Test void blankPasswordIsRejected() {
        try (var context = context(Map.of("DB_URL", "placeholder-url", "DB_USERNAME", "placeholder-user", "DB_PASSWORD", " "), "prod")) {
            assertThrows(IllegalStateException.class, context::refresh);
        }
    }
    @Test void externalConfigurationIsAcceptedWithoutConnecting() {
        try (var context = context(Map.of("DB_URL", "placeholder-url", "DB_USERNAME", "placeholder-user", "DB_PASSWORD", "placeholder-only"), "prod")) {
            assertDoesNotThrow(context::refresh);
            assertEquals("placeholder-only", context.getEnvironment().getProperty("DB_PASSWORD"));
        }
    }
    @Test void isolatedTestProfileDoesNotRequireExternalCredentials() {
        try (var context = context(Map.of(), "test")) {
            assertDoesNotThrow(context::refresh);
        }
    }
    @Test void packagedConfigurationContainsOnlyExternalDatabasePlaceholders() throws Exception {
        var properties = new java.util.Properties();
        try (var input = getClass().getResourceAsStream("/application.properties")) {
            assertNotNull(input);
            properties.load(input);
        }
        assertEquals("${DB_URL}", properties.getProperty("spring.datasource.url"));
        assertEquals("${DB_USERNAME}", properties.getProperty("spring.datasource.username"));
        assertEquals("${DB_PASSWORD}", properties.getProperty("spring.datasource.password"));
        assertEquals("none", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("false", properties.getProperty("spring.jpa.show-sql"));
        assertEquals("never", properties.getProperty("server.error.include-stacktrace"));
    }
}
