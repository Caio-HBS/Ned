package io.github.caiohbs.authentication.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class UncaughtExceptionLoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(UncaughtExceptionLoggingConfig.class);

    @PostConstruct
    public void registerDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) ->
                log.error("Uncaught exception in thread '{}'", thread.getName(), ex)
        );
    }
}