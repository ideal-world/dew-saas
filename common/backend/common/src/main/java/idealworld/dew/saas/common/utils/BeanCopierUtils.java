package idealworld.dew.saas.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>对象拷贝工具类</p>
 *
 * @author LB
 */
public class BeanCopierUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanCopierUtils.class);

    private BeanCopierUtils() {
    }

    private static Map<String, BeanCopier> beanCopierMap = new HashMap<>();

    public static void copyProperties(Object source, Object target) {
        String beanKey = generateKey(source.getClass(), target.getClass());
        BeanCopier copier;
        if (!beanCopierMap.containsKey(beanKey)) {
            copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.put(beanKey, copier);
        } else {
            copier = beanCopierMap.get(beanKey);
        }
        copier.copy(source, target, null);
    }

    public static <T> T copyForClass(Object source, Class<T> clz) {
        T result;
        try {
            result = clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("初始化失败e={}", e);
            throw new IllegalAccessError("无法初始化没有默认构函数的类");
        }

        copyProperties(source, result);

        return result;
    }

    private static String generateKey(Class<?> source, Class<?> target) {
        return source.toString() + target.toString();
    }
}
