/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.security;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author August
 * @date 2020/2/26 10:59 AM
 */
public class PublicKeyOwnerTest {


    @Test
    public void decrypt() {
        String pwd = PublicKeyOwner.decrypt("E+lDullrAU/qV1MVqR7L0GrbBkFHWaftsKTVni3ooL90/PZyH/VpcKF/HqJqzAyzoHI8vR+tawW/kE5sgRcpVkYivugNhWhEtnQpbRNjvnkCd8OcyuhjEVnrzDg4iNtJ4+RWKq37vb4aXU1/skmXDLd1Jf2ZNYndzTgHM1EbP6Ac0KqWzpeS4o2QxtX4E1nzdrxCOtEYtTewtXiaxA4kHdVb6fIkLa/OvY2xDNOQZKhlw9IU6LC3Ypq8qqQPq1dCW+Y/TzktZcbKVmQ0aHchPLuWpiO2VNwojHu7hiD7ZiNsELiDvose8iNNSwwpfTKbIODqjtgBrRWD/VLjCbMcxg==");
        Assert.assertNotNull(pwd);
    }

}
