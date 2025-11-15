package org.dows.rade.util;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * BeanCopier工具类（高性能优化版）
 * 优化特性：预初始化缓存、对象构造器缓存、并行集合处理、弱引用缓存等
 */
public class BeanUtilBak {

    // 1. 使用ConcurrentHashMap作为主缓存，提供高效的并发读写性能
    private static final Map<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>(128);
    
    // 2. 缓存对象构造器，避免每次反射创建对象的开销
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>(128);
    
    // 3. 预初始化常用类型的缓存key前缀，减少字符串拼接
    private static final String CONVERTER_SUFFIX = "_CONVERTER";
    
    // 4. 用于并行处理的线程池
    private static final ExecutorService PARALLEL_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "bean-copier-worker-" + counter.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    
    // 5. 并行处理的阈值，集合大小超过此值才使用并行处理
    private static final int PARALLEL_THRESHOLD = 100;

    /**
     * 生成缓存key（优化版本，减少字符串拼接操作）
     */
    private static String generateKey(Class<?> sourceClass, Class<?> targetClass, boolean withConverter) {
        StringBuilder sb = new StringBuilder(64); // 预估容量避免扩容
        sb.append(sourceClass.getName());
        sb.append('_');
        sb.append(targetClass.getName());
        if (withConverter) {
            sb.append(CONVERTER_SUFFIX);
        }
        return sb.toString();
    }

    /**
     * 获取缓存的BeanCopier（无Converter）
     */
    private static BeanCopier getBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
        String key = generateKey(sourceClass, targetClass, false);
        return BEAN_COPIER_CACHE.computeIfAbsent(key, k -> 
                BeanCopier.create(sourceClass, targetClass, false) // 不使用Converter
        );
    }

    /**
     * 获取缓存的BeanCopier（带Converter）
     */
    private static BeanCopier getBeanCopierWithConverter(Class<?> sourceClass, Class<?> targetClass) {
        String key = generateKey(sourceClass, targetClass, true);
        return BEAN_COPIER_CACHE.computeIfAbsent(key, k -> 
                BeanCopier.create(sourceClass, targetClass, true) // 使用Converter
        );
    }
    
    /**
     * 获取类的构造器（从缓存中或通过反射）
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getConstructor(Class<T> targetClass) {
        Assert.notNull(targetClass, "Target class must not be null");
        
        return (Constructor<T>) CONSTRUCTOR_CACHE.computeIfAbsent(targetClass, cls -> {
            try {
                Constructor<T> constructor = (Constructor<T>) cls.getDeclaredConstructor();
                constructor.setAccessible(true); // 允许访问私有构造器
                return constructor;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No default constructor found for class: " + cls.getName(), e);
            }
        });
    }
    
    /**
     * 创建目标对象实例（优化版：使用构造器缓存）
     */
    private static <T> T createTarget(Class<T> targetClass) {
        try {
            Constructor<T> constructor = getConstructor(targetClass);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + targetClass.getName(), e);
        }
    }

    // ------------------------------ 基础拷贝（无类型转换） ------------------------------

    /**
     * 简单拷贝（仅同名同类型属性，无Converter）
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        T target = createTarget(targetClass);
        BeanCopier copier = getBeanCopier(source.getClass(), targetClass);
        copier.copy(source, target, null); // 无Converter
        return target;
    }
    
    /**
     * 带目标对象的拷贝方法（避免重复创建对象）
     */
    public static void copy(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass());
        copier.copy(source, target, null);
    }
    
    /**
     * 使用对象工厂的拷贝方法（灵活控制对象创建）
     */
    public static <T> T copy(Object source, Supplier<T> targetSupplier, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        T target = targetSupplier.get();
        BeanCopier copier = getBeanCopier(source.getClass(), targetClass);
        copier.copy(source, target, null);
        return target;
    }

    // ------------------------------ 通用带Converter拷贝 ------------------------------

    /**
     * 带Converter的通用拷贝方法
     */
    public static <T> T copyWithConverter(Object source, Class<T> targetClass, org.springframework.cglib.core.Converter converter) {
        if (source == null) {
            return null;
        }
        
        T target = createTarget(targetClass);
        BeanCopier copier = getBeanCopierWithConverter(source.getClass(), targetClass);
        copier.copy(source, target, converter);
        return target;
    }

    // ------------------------------ 嵌套对象拷贝 ------------------------------

    /**
     * 拷贝嵌套对象（优化版：更健壮的类型处理）
     */
    public static <T> T copyWithNested(Object source, Class<T> targetClass, Map<String, NestedConverter<?, ?>> nestedConverters) {
        if (source == null) {
            return null;
        }

        // 创建目标对象
        T target = createTarget(targetClass);

        // 如果没有嵌套转换器，则直接使用普通拷贝
        if (nestedConverters == null || nestedConverters.isEmpty()) {
            BeanCopier copier = getBeanCopier(source.getClass(), targetClass);
            copier.copy(source, target, null);
            return target;
        }

        // 使用带Converter的拷贝处理基本属性和触发嵌套转换
        BeanCopier copier = getBeanCopierWithConverter(source.getClass(), targetClass);

        org.springframework.cglib.core.Converter converter = (value, targetType, context) -> {
            String propertyName = (String) context;

            // 查找是否有对应的嵌套转换器
            if (nestedConverters.containsKey(propertyName)) {
                NestedConverter<?, ?> nestedConverter = nestedConverters.get(propertyName);
                try {
                    // 安全类型转换
                    @SuppressWarnings("unchecked")
                    NestedConverter<Object, Object> typeSafeConverter = (NestedConverter<Object, Object>) nestedConverter;
                    return typeSafeConverter.convert(value);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Type conversion failed for property '" + propertyName + "': " +
                            (value != null ? value.getClass().getName() : "null") + " cannot be converted to expected type", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error during nested conversion for property '" + propertyName + "'", e);
                }
            }

            // 默认情况直接返回原值
            return value;
        };

        copier.copy(source, target, converter);
        return target;
    }

    /**
     * 嵌套对象转换接口
     */
    @FunctionalInterface
    public interface NestedConverter<S, T> {
        T convert(S source);
    }

    // ------------------------------ 集合拷贝 ------------------------------

    /**
     * 集合拷贝（List<sourceType> -> List<targetType>）
     * 优化：自动判断是否使用并行处理
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }

        // 根据集合大小决定是否并行处理
        if (sourceList.size() >= PARALLEL_THRESHOLD) {
            return copyListParallel(sourceList, targetClass);
        }

        // 串行处理
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copy(source, targetClass));
        }
        return targetList;
    }
    
    /**
     * 并行集合拷贝（针对大数据量优化）
     */
    private static <S, T> List<T> copyListParallel(List<S> sourceList, Class<T> targetClass) {
        int size = sourceList.size();
        List<T> targetList = new CopyOnWriteArrayList(new Object[size]);
        
        List<CompletableFuture<Void>> futures = new ArrayList<>(size);
        
        for (int i = 0; i < size; i++) {
            final int index = i;
            final S source = sourceList.get(i);
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if (source != null) {
                    targetList.set(index, copy(source, targetClass));
                }
            }, PARALLEL_EXECUTOR);
            
            futures.add(future);
        }
        
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // 移除null值（如果需要）
        return new ArrayList<>(targetList);
    }

    /**
     * 带Converter的集合拷贝
     */
    public static <S, T> List<T> copyListWithConverter(List<S> sourceList, Class<T> targetClass, 
                                                     org.springframework.cglib.core.Converter converter) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copyWithConverter(source, targetClass, converter));
        }
        return targetList;
    }

    /**
     * 带嵌套对象处理的集合拷贝
     */
    public static <S, T> List<T> copyListWithNested(List<S> sourceList, Class<T> targetClass, 
                                                  Map<String, NestedConverter<?, ?>> nestedConverters) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(copyWithNested(source, targetClass, nestedConverters));
        }
        return targetList;
    }
    
    // ------------------------------ 缓存管理 ------------------------------
    
    /**
     * 清除所有缓存（在需要时可以手动调用）
     */
    public static void clearCache() {
        BEAN_COPIER_CACHE.clear();
        CONSTRUCTOR_CACHE.clear();
    }
    
    /**
     * 获取当前缓存大小（用于监控）
     */
    public static int getCacheSize() {
        return BEAN_COPIER_CACHE.size();
    }
    
    /**
     * 获取构造器缓存大小
     */
    public static int getConstructorCacheSize() {
        return CONSTRUCTOR_CACHE.size();
    }
    
    /**
     * 预初始化指定类型对的BeanCopier（在应用启动时调用可提高首次性能）
     */
    public static void preInitialize(Class<?> sourceClass, Class<?> targetClass) {
        getBeanCopier(sourceClass, targetClass);
        getBeanCopierWithConverter(sourceClass, targetClass);
        getConstructor(targetClass);
    }
    
    // 私有构造器防止实例化
    private BeanUtilBak() {
        throw new AssertionError("Cannot instantiate utility class");
    }





    /**
     * 集合拷贝（Collection<sourceType> -> List<targetType>）
     * 优化：自动判断是否使用并行处理
     */
    public static <S, T> List<T> copyCollection(Collection<S> sourceCollection, Class<T> targetClass) {
        if (sourceCollection == null || sourceCollection.isEmpty()) {
            return new ArrayList<>(0);
        }

        // 根据集合大小决定是否并行处理
        if (sourceCollection.size() >= PARALLEL_THRESHOLD) {
            return copyCollectionParallel(sourceCollection, targetClass);
        }

        // 串行处理
        List<T> targetList = new ArrayList<>(sourceCollection.size());
        for (S source : sourceCollection) {
            targetList.add(copy(source, targetClass));
        }
        return targetList;
    }

    /**
     * 并行集合拷贝（针对大数据量优化）
     */
    private static <S, T> List<T> copyCollectionParallel(Collection<S> sourceCollection, Class<T> targetClass) {
        List<S> sourceList = new ArrayList<>(sourceCollection);
        int size = sourceList.size();
        List<T> targetList = new ArrayList<>(size);

        // Pre-populate with null values to allow indexed access
        for (int i = 0; i < size; i++) {
            targetList.add(null);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            final int index = i;
            final S source = sourceList.get(i);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if (source != null) {
                    targetList.set(index, copy(source, targetClass));
                }
            }, PARALLEL_EXECUTOR);

            futures.add(future);
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return targetList;
    }

    /**
     * 带Converter的集合拷贝
     */
    public static <S, T> List<T> copyCollectionWithConverter(Collection<S> sourceCollection, Class<T> targetClass,
                                                             org.springframework.cglib.core.Converter converter) {
        if (sourceCollection == null || sourceCollection.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> targetList = new ArrayList<>(sourceCollection.size());
        for (S source : sourceCollection) {
            targetList.add(copyWithConverter(source, targetClass, converter));
        }
        return targetList;
    }

    /**
     * 带嵌套对象处理的集合拷贝
     */
    public static <S, T> List<T> copyCollectionWithNested(Collection<S> sourceCollection, Class<T> targetClass,
                                                          Map<String, NestedConverter<?, ?>> nestedConverters) {
        if (sourceCollection == null || sourceCollection.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> targetList = new ArrayList<>(sourceCollection.size());
        for (S source : sourceCollection) {
            targetList.add(copyWithNested(source, targetClass, nestedConverters));
        }
        return targetList;
    }
}