package com.android.net.rx.parse.date;

import com.android.net.rx.parse.GsonTypeAdapter;
import com.android.utils.StringUtil;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：XieDu
 * 创建时间：2016/10/9 20:10
 * 描述：
 */
public class GsonDateFormatUtils {

    public static Map<Type, Object> processSerializable(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        if (!clazz.isAnnotationPresent(GsonDateFormat.class)) {
            return null;
        }
        Map<Type, Object> adapterMap = new HashMap<>();
        GsonDateFormat format = clazz.getAnnotation(GsonDateFormat.class);
        if (format != null && !StringUtil.isEmpty(format.dateTimeFormat())) {
            addDateTimeFormat(format.dateTimeFormat(), adapterMap);
        }
        if (format != null && !StringUtil.isEmpty(format.dateFormat())) {
            addDateFormat(format.dateFormat(), adapterMap);
        }
        if (format != null && format.instantFormat()) {
            addInstantFormat(adapterMap);
        }
        return adapterMap;
    }

    private static void addDateTimeFormat(String pattern, Map<Type, Object> adapterMap) {
        GsonTypeAdapter<LocalDateTime> adapter =
                new LocalDateTimeTypeAdapter(DateTimeFormatter.ofPattern(pattern));
        adapterMap.put(LocalDateTime.class, adapter);
    }

    private static void addDateFormat(String pattern, Map<Type, Object> adapterMap) {
        GsonTypeAdapter<LocalDate> adapter =
                new LocalDateTypeAdapter(DateTimeFormatter.ofPattern(pattern));
        adapterMap.put(LocalDate.class, adapter);
    }

    private static void addInstantFormat(Map<Type, Object> adapterMap) {
        adapterMap.put(Instant.class, new InstantTypeAdapter());
    }
}
