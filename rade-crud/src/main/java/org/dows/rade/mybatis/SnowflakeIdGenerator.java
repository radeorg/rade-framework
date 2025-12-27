//package org.dows.rade.mybatis;
//
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * 高性能雪花ID生成器
// * 避免使用MyBatis-Flex的KeyGenerator，减少依赖
// */
//public class SnowflakeIdGenerator {
//
//    // 起始时间戳（2024-01-01 00:00:00）
//    private static final long START_TIMESTAMP = 1704067200000L;
//
//    // 机器ID所占的位数
//    private static final long WORKER_ID_BITS = 5L;
//
//    // 数据中心ID所占的位数
//    private static final long DATACENTER_ID_BITS = 5L;
//
//    // 序列号所占的位数
//    private static final long SEQUENCE_BITS = 12L;
//
//    // 机器ID向左移12位
//    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
//
//    // 数据中心ID向左移17位(12+5)
//    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
//
//    // 时间戳向左移22位(5+5+12)
//    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
//
//    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
//    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
//
//    // 工作机器ID(0~31)
//    private static final long WORKER_ID = 1L;
//
//    // 数据中心ID(0~31)
//    private static final long DATACENTER_ID = 1L;
//
//    // 毫秒内序列(0~4095)
//    private static final AtomicLong SEQUENCE = new AtomicLong(0L);
//
//    // 上次生成ID的时间截
//    private static volatile long LAST_TIMESTAMP = -1L;
//
//    /**
//     * 生成雪花ID（线程安全）
//     */
//    public static Long generateId() {
//        return nextId();
//    }
//
//    /**
//     * 生成雪花ID（字符串格式）
//     */
//    public static String generateIdString() {
//        return String.valueOf(nextId());
//    }
//
//    private static synchronized long nextId() {
//        long timestamp = timeGen();
//
//        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，应当抛出异常
//        if (timestamp < LAST_TIMESTAMP) {
//            throw new RuntimeException(
//                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
//                    LAST_TIMESTAMP - timestamp));
//        }
//
//        // 如果是同一时间生成的，则进行毫秒内序列
//        if (LAST_TIMESTAMP == timestamp) {
//            SEQUENCE.set((SEQUENCE.get() + 1) & SEQUENCE_MASK);
//            // 毫秒内序列溢出
//            if (SEQUENCE.get() == 0) {
//                // 阻塞到下一个毫秒，获得新的时间戳
//                timestamp = tilNextMillis(LAST_TIMESTAMP);
//            }
//        } else {
//            // 时间戳改变，毫秒内序列重置
//            SEQUENCE.set(0L);
//        }
//
//        // 上次生成ID的时间截
//        LAST_TIMESTAMP = timestamp;
//
//        // 移位并通过或运算拼到一起组成64位的ID
//        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
//                | (DATACENTER_ID << DATACENTER_ID_SHIFT)
//                | (WORKER_ID << WORKER_ID_SHIFT)
//                | SEQUENCE.get();
//    }
//
//    /**
//     * 阻塞到下一个毫秒，直到获得新的时间戳
//     */
//    private static long tilNextMillis(long lastTimestamp) {
//        long timestamp = timeGen();
//        while (timestamp <= lastTimestamp) {
//            timestamp = timeGen();
//        }
//        return timestamp;
//    }
//
//    /**
//     * 返回当前时间（毫秒）
//     */
//    private static long timeGen() {
//        return System.currentTimeMillis();
//    }
//}
