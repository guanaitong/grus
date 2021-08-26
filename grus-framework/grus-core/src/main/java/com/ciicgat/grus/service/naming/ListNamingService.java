/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by August.Zhou on 2018-11-22 10:37.
 */
public class ListNamingService implements NamingService {


    private LinkedList<NamingService> namingServices = new LinkedList<>();


    public ListNamingService(NamingService... namingServices) {
        this.namingServices.addAll(Arrays.asList(namingServices));
    }

    @Override
    public String resolve(String serviceName) {
        for (NamingService namingService : namingServices) {
            String target = namingService.resolve(serviceName);
            if (target != null && !target.isEmpty()) {
                return target;
            }
        }
        throw new IllegalArgumentException("serviceName[" + serviceName + "] cannot find the target");
    }

    public synchronized final void addFirst(NamingService namingService) {
        namingServices.addFirst(namingService);
    }

    public synchronized final void add(int index, NamingService namingService) {
        namingServices.add(index, namingService);
    }

    public synchronized final void addLast(NamingService namingService) {
        namingServices.addLast(namingService);
    }

}
