package com.sky.utils;

import java.util.Collection;

/**
 * 集合工具类，判断List、Set等集合是否为空
 */
public class CollectionUtils {
    /**
     * 判断集合是否为空
     *
     * @param collection 集合对象
     * @return 为空返回true，否则false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合对象
     * @return 不为空返回true，否则false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
} 