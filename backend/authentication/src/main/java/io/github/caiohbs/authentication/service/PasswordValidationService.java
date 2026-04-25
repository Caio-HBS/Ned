package io.github.caiohbs.authentication.service;

import io.github.caiohbs.authentication.exception.NonNumericException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    public String validatePassword(String email, String password, String fullName, LocalDate birthday) {
        List<String> dateFormats = new ArrayList<>();

        String date1 = birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String date2 = birthday.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        dateFormats.add(birthday.toString());
        dateFormats.add(date1);
        dateFormats.add(date2);
        dateFormats.add(makeNumeric(date1));
        dateFormats.add(makeNumeric(date2));

        for (String dateFormat : dateFormats) {
            if (password.contains(dateFormat)) {
                return "Password can't contain user's birthday";
            }
        }

        if (password.contains(email)) {
            return "Password can't contain user email";
        }

        String[] individualNames = fullName.split("\\s+");
        for (String name : individualNames) {
            if (password.contains(name)) {
                return "Password can't contain user's name";
            }
        }
        return null;
    }

    public String makeNumeric(String value) {
        value = value.replaceAll("[^0-9]", "");

        if (!value.matches("-?\\d+(\\.\\d+)?")) {
            throw new NonNumericException("This value is expected to be numeric: '" + value + "'");
        }

        return value;
    }

}
