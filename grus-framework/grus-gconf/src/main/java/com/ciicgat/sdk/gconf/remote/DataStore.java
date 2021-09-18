/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2017/1/20 15:42.
 */
class DataStore implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStore.class);

    private ConcurrentMap<String, Map<String, ValueReference>> dataCache = new ConcurrentHashMap<>();

    private ConcurrentMap<String, CopyOnWriteArrayList<ConfigChangeListener>> globalListeners = new ConcurrentHashMap<>();


    private final GConfHttpClient gConfHttpClient;

    DataStore(GConfHttpClient gConfHttpClient) {
        this.gConfHttpClient = gConfHttpClient;
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "gconf-refresher");
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleAtFixedRate(this, 0, 10, TimeUnit.MILLISECONDS);
    }

    public Map<String, ValueReference> getValueReferences(String configCollectionId) {
        Map<String, ValueReference> value = dataCache.get(configCollectionId);
        if (value != null) {
            return value;
        }
        synchronized (this) {
            value = dataCache.get(configCollectionId);
            if (value != null) {
                return value;
            }
            globalListeners.put(configCollectionId, new CopyOnWriteArrayList<>());
            Map<String, ValueReference> newValue = getCollections(configCollectionId);
            value = dataCache.putIfAbsent(configCollectionId, newValue);
            if (value == null) {
                value = newValue;
            }
            return value;
        }
    }


    private Map<String, ValueReference> getCollections(String configCollectionId) {
        List<String> keys = gConfHttpClient.listConfigKeys(configCollectionId);
        ConcurrentMap<String, ValueReference> res = new ConcurrentHashMap<>();
        for (String key : keys) {
            String keyContent = gConfHttpClient.getConfig(configCollectionId, key);
            if (!keyContent.isEmpty()) {
                res.put(key, new ValueReference(key, keyContent));
            }
        }
        return res;
    }

    public void addGlobalConfigChangeListener(String configCollectionId, ConfigChangeListener configChangeListener) {
        if (globalListeners.containsKey(configCollectionId)) {
            globalListeners.get(configCollectionId).add(configChangeListener);
        }
    }


    @Override
    public void run() {
        try {
            if (dataCache.size() == 0) {
                return;
            }
            List<String> needUpdateConfigApps = gConfHttpClient.watch(dataCache.keySet());
            if (needUpdateConfigApps.isEmpty()) {
                return;
            }
            LOGGER.info("change configApps {}", needUpdateConfigApps);
            refresh(needUpdateConfigApps);
        } catch (Throwable e) {
            LOGGER.error("unexpect error", e);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
            }
        }

    }

    private void refresh(List<String> needUpdateConfigApps) {
        for (Map.Entry<String, Map<String, ValueReference>> entry : dataCache.entrySet()) {
            if (!needUpdateConfigApps.contains(entry.getKey())) {
                continue;
            }
            try {
                String configCollectionId = entry.getKey();
                Map<String, ValueReference> oldData = entry.getValue();
                List<String> newKeys = gConfHttpClient.listConfigKeys(configCollectionId);

                for (Map.Entry<String, ValueReference> oldEntry : oldData.entrySet()) {
                    String key = oldEntry.getKey();
                    String oldRaw = oldEntry.getValue().getRaw();
                    if (newKeys.contains(key)) {
                        String newRaw = gConfHttpClient.getConfig(configCollectionId, key);
                        if (Objects.nonNull(newRaw) && !newRaw.isBlank() && !Objects.equals(oldRaw, newRaw)) {
                            oldEntry.getValue().setRaw(newRaw);
                            fireValueChanged(configCollectionId, key, oldRaw, newRaw);
                        }
                    } else {
                        //老的有，但新的没有，说明这条数据被删除了，先不从缓存里删除，避免程序出错。
                        fireValueChanged(configCollectionId, key, oldRaw, null);
                    }
                }
                for (String key : newKeys) {
                    if (!oldData.containsKey(key)) {
                        //新的有，但是老的没有，说明这条数据是新加的
                        String newRaw = gConfHttpClient.getConfig(configCollectionId, key);
                        oldData.put(key, new ValueReference(key, newRaw));
                        fireValueChanged(configCollectionId, key, null, newRaw);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("unexpect error", e);
            }
        }
    }

    private void fireValueChanged(String configCollectionId, String key, String oldValue, String newValue) {
        LOGGER.info("valueChanged,configAppId {},key {},\n<------------------oldValue------------------>\n{}\n<------------------newValue------------------>:\n{}",
                configCollectionId, key, oldValue, newValue);
        if (globalListeners.containsKey(configCollectionId)) {
            for (ConfigChangeListener globalListener : globalListeners.get(configCollectionId)) {
                try {
                    globalListener.valueChanged(key, oldValue, newValue);
                } catch (Exception e) {
                    LOGGER.error("fireValueChanged error", e);
                }
            }
        }

        Map<String, ValueReference> map = dataCache.get(configCollectionId);
        if (map == null) {
            return;
        }
        ValueReference valueReference = map.get(key);
        if (valueReference == null) {
            return;
        }
        try {
            valueReference.fireValueChanged(oldValue, newValue);
            LOGGER.info("firedValueChanged,configAppId {},key {}", configCollectionId, key);
        } catch (Exception e) {
            LOGGER.error(
                    String.format("fireValueChanged happen error,configAppId %s,key %s", configCollectionId, key), e);
        }
    }

}
