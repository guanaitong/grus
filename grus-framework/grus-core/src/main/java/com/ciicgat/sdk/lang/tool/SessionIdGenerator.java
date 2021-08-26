/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 这个类初始化的时候可能会很慢，https://stackoverflow.com/questions/137212/how-to-solve-slow-java-securerandom
 * copy from tomcat org.apache.catalina.util.StandardSessionIdGenerator
 * <p>
 * Created by August.Zhou on 2017/3/29 10:27.
 *
 * @see org.apache.catalina.util.StandardSessionIdGenerator
 */
public class SessionIdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionIdGenerator.class);
    private static final int DEFAULT_SESSIONIDLENGTH = 16;
    /**
     * Queue of random number generator objects to be used when creating session
     * identifiers. If the queue is empty when a random number generator is
     * required, a new random number generator object is created. This is
     * designed this way since random number generators use a sync to make them
     * thread-safe and the sync makes using a a single object slow(er).
     */
    private final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<>();
    private String secureRandomClass = null;
    private String secureRandomAlgorithm = "SHA1PRNG";
    private String secureRandomProvider = null;


    public String generateSessionId(int sessionIdLength) {
        byte[] random = new byte[16];

        StringBuilder buffer = new StringBuilder(2 * sessionIdLength + 20);

        int resultLenBytes = 0;

        while (resultLenBytes < sessionIdLength) {
            getRandomBytes(random);
            for (int j = 0;
                 j < random.length && resultLenBytes < sessionIdLength;
                 j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                if (b1 < 10)
                    buffer.append((char) ('0' + b1));
                else
                    buffer.append((char) ('A' + (b1 - 10)));
                if (b2 < 10)
                    buffer.append((char) ('0' + b2));
                else
                    buffer.append((char) ('A' + (b2 - 10)));
                resultLenBytes++;
            }
        }


        return buffer.toString();
    }


    public String generateSessionId() {
        return generateSessionId(DEFAULT_SESSIONIDLENGTH);
    }

    protected void getRandomBytes(byte[] bytes) {

        SecureRandom random = randoms.poll();
        if (random == null) {
            random = createSecureRandom();
        }
        random.nextBytes(bytes);
        randoms.add(random);
    }

    private SecureRandom createSecureRandom() {

        SecureRandom result = null;

        long t1 = System.currentTimeMillis();
        if (secureRandomClass != null) {
            try {
                // Construct and seed a new random number generator
                Class<?> clazz = Class.forName(secureRandomClass);
                result = (SecureRandom) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                LOGGER.error("sessionIdGeneratorBase.random:" + secureRandomClass, e);
            }
        }

        if (result == null) {
            // No secureRandomClass or creation failed. Use SecureRandom.
            try {
                if (secureRandomProvider != null &&
                        secureRandomProvider.length() > 0) {
                    result = SecureRandom.getInstance(secureRandomAlgorithm,
                            secureRandomProvider);
                } else if (secureRandomAlgorithm != null &&
                        secureRandomAlgorithm.length() > 0) {
                    result = SecureRandom.getInstance(secureRandomAlgorithm);
                }
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("sessionIdGeneratorBase.randomAlgorithm:" + secureRandomAlgorithm, e);
            } catch (NoSuchProviderException e) {
                LOGGER.error("sessionIdGeneratorBase.randomProvider:" +
                        secureRandomProvider, e);
            }
        }

        if (result == null) {
            // Invalid provider / algorithm
            try {
                result = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("sessionIdGeneratorBase.randomAlgorithm:" +
                        secureRandomAlgorithm, e);
            }
        }

        if (result == null) {
            // Nothing works - use platform default
            result = new SecureRandom();
        }

        // Force seeding to take place
        result.nextInt();

        long t2 = System.currentTimeMillis();
        if ((t2 - t1) > 100)
            LOGGER.info("sessionIdGeneratorBase.createRandom:" +
                    result.getAlgorithm(), Long.valueOf(t2 - t1));
        return result;
    }
}
