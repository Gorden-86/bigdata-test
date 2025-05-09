package com.lacus.utils;

import com.google.common.base.Strings;
import com.lacus.common.config.IPropertyDelegate;
import com.lacus.common.config.ImmutablePriorityPropertyDelegate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import static com.lacus.common.constant.Constants.COMMON_PROPERTIES_PATH;

@Slf4j
@UtilityClass
public class CommonPropertyUtils {

    private static final IPropertyDelegate propertyDelegate = new ImmutablePriorityPropertyDelegate(COMMON_PROPERTIES_PATH);

    public static String getString(String key) {
        return propertyDelegate.get(key.trim());
    }

    public static String getString(String key, String defaultVal) {
        String val = getString(key);
        return Strings.isNullOrEmpty(val) ? defaultVal : val;
    }

    public static String getUpperCaseString(String key) {
        String val = getString(key);
        return Strings.isNullOrEmpty(val) ? val : val.toUpperCase();
    }

    public static Properties loadPropertiesByStr(String str) {
        Properties props = new Properties();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(str.getBytes());
            props.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return props;
    }

    public static Integer getInt(String key) {
        return propertyDelegate.getInt(key.trim());
    }

    public static Integer getInt(String key, int defaultValue) {
        return propertyDelegate.getInt(key, defaultValue);
    }

    public static Boolean getBoolean(String key) {
        return propertyDelegate.getBoolean(key);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return propertyDelegate.getBoolean(key, defaultValue);
    }

    public static Long getLong(String key) {
        return propertyDelegate.getLong(key);
    }

    public static Long getLong(String key, Long defaultValue) {
        return propertyDelegate.getLong(key, defaultValue);
    }

    public static Double getDouble(String key) {
        return propertyDelegate.getDouble(key);
    }

    public static Double getDouble(String key, Double defaultValue) {
        return propertyDelegate.getDouble(key, defaultValue);
    }

    /**
     * get all properties with specified prefix, like: fs.
     *
     * @param prefix prefix to search
     * @return all properties with specified prefix
     */
    public static Map<String, String> getByPrefix(String prefix) {
        Map<String, String> matchedProperties = new HashMap<>();
        for (String propName : propertyDelegate.getPropertyKeys()) {
            if (propName.startsWith(prefix)) {
                matchedProperties.put(propName, propertyDelegate.get(propName));
            }
        }
        return matchedProperties;
    }

    public static <T> Set<T> getSet(String key, Function<String, Set<T>> transformFunction, Set<T> defaultValue) {
        return propertyDelegate.get(key, transformFunction, defaultValue);
    }
}
