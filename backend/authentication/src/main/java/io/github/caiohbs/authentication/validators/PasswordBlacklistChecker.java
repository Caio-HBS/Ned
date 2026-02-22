package io.github.caiohbs.authentication.validators;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class PasswordBlacklistChecker {

    private final Set<String> blacklistedPasswords = new HashSet<>();

    @PostConstruct
    public void init() {
        String fileName = "/common-password.txt";
        try (InputStream is = getClass().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Could not load resource: " + fileName + ".");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String password = line.trim();
                    if (!password.isEmpty()) {
                        blacklistedPasswords.add(password);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading resource: " + fileName + ".");
        }
    }

    public boolean isBlacklisted(String password) {
        if (password == null) return false;
        return blacklistedPasswords.contains(password.trim());
    }
}