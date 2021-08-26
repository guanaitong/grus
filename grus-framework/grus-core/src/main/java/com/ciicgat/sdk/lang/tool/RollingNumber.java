/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高效的qps计算类
 * 仿照：
 * https://github.com/Netflix/Hystrix/blob/master/hystrix-core/src/main/java/com/netflix/hystrix/util/HystrixRollingNumber.java
 * <p>
 * Created by August.Zhou on 2018/8/28 14:20.
 */
public class RollingNumber {
    /**
     * 槽位的数量
     */
    private int sizeOfBuckets;
    /**
     * 时间片，单位毫秒
     */
    private int unitOfTimeSlice;
    /**
     * 用于判断是否可跳过锁争抢
     */
    private int timeSliceUsedToCheckIfPossibleToBypass;
    /**
     * 槽位
     */
    private Bucket[] buckets;
    /**
     * 目标槽位的位置
     */
    private volatile Integer targetBucketPosition;
    /**
     * 接近目标槽位最新更新时间的时间
     */
    private volatile Long latestPassedTimeCloseToTargetBucket;
    /**
     * 进入下一个槽位时使用的锁
     */
    private ReentrantLock enterNextBucketLock;

    /**
     * 默认60个槽位，槽位的时间片为1000毫秒
     */
    public RollingNumber() {
        this(60, 1000);
    }

    /**
     * 初始化Bucket数量与每个Bucket的时间片等
     *
     * @param sizeOfBuckets
     * @param unitOfTimeSlice
     */
    public RollingNumber(int sizeOfBuckets, int unitOfTimeSlice) {
        this.latestPassedTimeCloseToTargetBucket = System.currentTimeMillis() - (2 * unitOfTimeSlice);
        this.targetBucketPosition = null;
        this.sizeOfBuckets = sizeOfBuckets;
        this.unitOfTimeSlice = unitOfTimeSlice;
        this.enterNextBucketLock = new ReentrantLock();
        this.buckets = new Bucket[sizeOfBuckets];
        this.timeSliceUsedToCheckIfPossibleToBypass = 3 * unitOfTimeSlice;
        for (int i = 0; i < sizeOfBuckets; i++) {
            this.buckets[i] = new Bucket();
        }
    }


    public int record() {
        long passTime = System.currentTimeMillis();
        if (targetBucketPosition == null) {
            targetBucketPosition = (int) (passTime / unitOfTimeSlice) % sizeOfBuckets;
        }
        Bucket currentBucket = buckets[targetBucketPosition];
        if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
            if (enterNextBucketLock.isLocked() && (passTime - latestPassedTimeCloseToTargetBucket) < timeSliceUsedToCheckIfPossibleToBypass) {
            } else {
                try {
                    enterNextBucketLock.lock();
                    if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
                        int nextTargetBucketPosition = (int) (passTime / unitOfTimeSlice) % sizeOfBuckets;
                        Bucket nextBucket = buckets[nextTargetBucketPosition];
                        if (nextBucket.equals(currentBucket)) {
                            if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
                                latestPassedTimeCloseToTargetBucket = passTime;
                            }
                        } else {
                            nextBucket.reset(passTime);
                            targetBucketPosition = nextTargetBucketPosition;
                            latestPassedTimeCloseToTargetBucket = passTime;
                        }

                        return nextBucket.pass();
                    } else {
                        currentBucket = buckets[targetBucketPosition];
                    }
                } finally {
                    enterNextBucketLock.unlock();
                }
            }
        }
        return currentBucket.pass();
    }

    public Bucket[] getBuckets() {
        return buckets;
    }

    private static class Bucket implements Serializable {

        private static final long serialVersionUID = -9085720164508215774L;

        private Long latestPassedTime;

        private AtomicInteger atomicInteger;

        Bucket() {
            this.latestPassedTime = System.currentTimeMillis();
            this.atomicInteger = new AtomicInteger();
        }


        public int pass() {
            return atomicInteger.incrementAndGet();
        }


        public long getLatestPassedTime() {
            return latestPassedTime;
        }

        public void reset(long latestPassedTime) {
            this.atomicInteger.set(0);
            this.latestPassedTime = latestPassedTime;
        }
    }


}
