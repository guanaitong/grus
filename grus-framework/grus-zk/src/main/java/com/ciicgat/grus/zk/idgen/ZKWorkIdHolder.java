/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.idgen;

import com.ciicgat.grus.idgen.WorkIdHolder;
import com.ciicgat.grus.zk.ZKConstants;
import com.ciicgat.grus.zk.ZKUtils;
import com.ciicgat.sdk.lang.exception.ZKRuntimeException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/26 15:56
 * @Description:
 */
public class ZKWorkIdHolder implements WorkIdHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKWorkIdHolder.class);
    private static final long HIS_NUM = 10;

    private long workValue;

    public ZKWorkIdHolder(String connectString, String appName)  {
        workValue = init(connectString, appName);
    }

    @Override
    public long getId() {
        return workValue;
    }

    @Override
    public long getId(long maxId) {
        return workValue % maxId;
    }

    private long init(String connectString, String appName)  {
        CuratorFramework curator = ZKUtils.init(connectString);

        try {
            String pathPrefix = ZKPaths.makePath(appName, ZKConstants.IDGEN_PATH);
            String node = curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).
                    forPath(pathPrefix + "/#", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            String[] nodeKey = node.split("#");
            int workValue = Integer.parseInt(nodeKey[1]);

            deleteHisNode(curator, pathPrefix, workValue);
            return workValue;
        } catch (Exception e) {
            LOGGER.error("ZK_INIT ERROR", e);
            throw new ZKRuntimeException("序号生成器获取机器ID失败，程序终止启动", e);
        } finally {
            if (curator != null) {
                curator.close();
            }
        }
    }

    private void deleteHisNode(CuratorFramework curator, String path, int current) {
        try {
            List<String> childrenPaths = curator.getChildren().forPath(path);
            for (String childPath : childrenPaths) {
                String[] nodeKey = childPath.split("#");
                int seq = Integer.parseInt(nodeKey[1]);
                if ((current - seq) > HIS_NUM) {
                    curator.delete().deletingChildrenIfNeeded().forPath(path + "/" + childPath);
                }
            }
        } catch (Exception e) {
            LOGGER.error("ZK_DELETE ERROR", e);
        }
    }
}
