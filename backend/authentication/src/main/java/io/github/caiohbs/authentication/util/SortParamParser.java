package io.github.caiohbs.authentication.util;

import io.github.caiohbs.authentication.exception.InvalidSortParameterException;
import io.github.caiohbs.authentication.model.User;
import io.github.caiohbs.authentication.model.Address;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SortParamParser {

    private static final Map<Class<?>, Set<String>> FIELD_CACHE = new HashMap<>();

    static {
        initializeFieldCache();
    }

    private static void initializeFieldCache() {
        FIELD_CACHE.put(User.class, getValidFields(User.class));
        FIELD_CACHE.put(Address.class, getValidFields(Address.class));
    }

    private static Set<String> getValidFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    public Sort parseSortParam(String sortParam) {
        return parseSortParam(sortParam, User.class);
    }

    public Sort parseSortParamForUser(String sortParam) {
        return parseSortParam(sortParam, User.class);
    }

    public Sort parseSortParamForAddress(String sortParam) {
        return parseSortParam(sortParam, Address.class);
    }

    public Sort parseSortParam(String sortParam, Class<?> entityClass) {
        if (sortParam == null || sortParam.isEmpty()) {
            return getDefaultSort(entityClass);
        }

        String[] parts = sortParam.split(",");
        if (parts.length == 2) {
            String field = parts[0].trim();
            String direction = parts[1].trim().toUpperCase();
            
            validateField(field, entityClass);
            
            if (direction.equals("ASC")) {
                return Sort.by(Sort.Order.asc(field));
            } else if (direction.equals("DESC")) {
                return Sort.by(Sort.Order.desc(field));
            } else {
                throw new InvalidSortParameterException(
                    String.format("Invalid sort direction: '%s'. Valid directions are: ASC, DESC", direction)
                );
            }
        }
        
        validateField(sortParam, entityClass);
        return Sort.by(Sort.Order.asc(sortParam));
    }

    private void validateField(String field, Class<?> entityClass) {
        Set<String> validFields = FIELD_CACHE.getOrDefault(entityClass, getValidFields(entityClass));
        if (!validFields.contains(field)) {
            throw new InvalidSortParameterException(
                String.format("Invalid sort field: '%s'. Valid fields are: %s", 
                    field, String.join(", ", validFields))
            );
        }
    }

    private Sort getDefaultSort(Class<?> entityClass) {
        if (entityClass == Address.class) {
            return Sort.by(Sort.Order.desc("addressId"));
        }
        return Sort.by(Sort.Order.desc("userId"));
    }
}
