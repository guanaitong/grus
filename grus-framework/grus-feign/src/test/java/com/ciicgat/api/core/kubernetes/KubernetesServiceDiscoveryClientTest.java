/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import com.ciicgat.grus.service.discovery.ServiceInstance;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EndpointsList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @Author: August
 * @Date: 2021/7/8 16:41
 */
public class KubernetesServiceDiscoveryClientTest {
    private static final Log LOG = LogFactory.getLog(KubernetesServiceDiscoveryClientTest.class);

    @Test
    public void testGetKubernetesTargetContext1() {
        testGetKubernetesTargetContext("https://10.101.11.201:6443", "default", "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJjcmFiLXRva2VuLXI5bjR6Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImNyYWIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5MzA4ZDI2OS01YTAxLTExZWEtYjc0Yi0wMDUwNTY5YWU1Y2IiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06Y3JhYiJ9.ZV4TmxJ3JUVrr-rcwHqeuBblNe8NVdEUMX-GO_P6fuoRqvrC4m-da1jCOmkq74pHDwexzomvgf2tffRLwfvOWvtpcbL4-EFXeXct6br4Mehmk9reyIgkG2WyAa9sqcVSsri_086SFxPXQgI8nCAFQRPZtUHKEaYP7Sa877udE2AMExBPiTWowphRH53tYXvUCISiIARhm8zR4ahadYVcTQtrxnCvK9FTH2yxtYtzKKKTh0KH0Ej6HW2plANsiGW2GH7gGCUB72Ye9v5LoreRZsVmP2a-ael9WEVFdWqpLHIUHQ6bpyNuInbyi5ya0tIX-YQbTVK3cH9qRXp6Ekt69w");
    }

//    @Test
    public void testGetKubernetesTargetContext2() {
        testGetKubernetesTargetContext("https://10.101.11.201:6443", "default", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkZWZhdWx0LXRva2VuLWI4cmY3Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImRlZmF1bHQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiIzYTY2YjY4Ny05MjJjLTExZTctODdhZS0wMDUwNTY5YWU1Y2IiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06ZGVmYXVsdCJ9.H2QphOhdq0Umew7YBR7BFHIrumm5rCVU8rNQrNgF9ZpAeg0etUFHEc8eOGV0zpsypXSQXcuPJUwNA0HxIXR6D9rcLDz4RvwUaacPjFpeKl79r1QQT_xPpf0x3OlAWDDSV9KP_en8oKOEYg-Wftf4fSCkiFUp4A_mM7u9VsWARn-8mEbjR9yCa0dN4cdh7MBa3N81cknJmyw5gYLgUZ9itb9ACTToBuu-QHCQH2UIN0AcvyIl2xwwz_sBuhLF9f0yvGq_5SOsBJ-EBFhdK95fLlhZyk07LvYxhvvj6k6AdW6pZtfpiP0_RHHZTXoDWu4W1LlE8cSJ8C4Q9_tz2uSeow");
    }

//    @Test
    public void testGetKubernetesTargetContext3() {
        testGetKubernetesTargetContext("https://10.100.32.181:6443", "default", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tcmx4Y3oiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjNhNWRiNjk2LTkyMmMtMTFlNy04N2FlLTAwNTA1NjlhZTVjYiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.iDRBBW2cr8-v2OvCzyc-H17BOr997ZYnTCyAdVkyD5bYQMmXaESLs74U-tsmjOh3YEglwGz-8PouWsqYTSZy-xeqb0F2945GdGZCerTmLiLBOdUrD9152WWo780d6JQOhdm8oHvLzrAkOTWOAQdZNkjE_MCmDWhKX0vdrIi5MKUWkAOE4cpDwgEi-Lg4SRjo5gHdKk4vfKr7hab-4JwQ7jK3HUBpCr_BiA55iMOSywH4_7KFxwKbwoTAwVmLgIZFh8D7SNlNPPJ_kEgROkhq1p1OJt7FyAgmzEbZQChoM5noE_oprZvCrxPH59UYRUMLOqcJHueWRRafB1rMuDQYfg");
    }


//    @Test
    public void testGetKubernetesTargetContext4() {
        testGetKubernetesTargetContext("https://10.100.32.181:6443", "default", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tcmx4Y3oiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjNhNWRiNjk2LTkyMmMtMTFlNy04N2FlLTAwNTA1NjlhZTVjYiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.iDRBBW2cr8-v2OvCzyc-H17BOr997ZYnTCyAdVkyD5bYQMmXaESLs74U-tsmjOh3YEglwGz-8PouWsqYTSZy-xeqb0F2945GdGZCerTmLiLBOdUrD9152WWo780d6JQOhdm8oHvLzrAkOTWOAQdZNkjE_MCmDWhKX0vdrIi5MKUWkAOE4cpDwgEi-Lg4SRjo5gHdKk4vfKr7hab-4JwQ7jK3HUBpCr_BiA55iMOSywH4_7KFxwKbwoTAwVmLgIZFh8D7SNlNPPJ_kEgROkhq1p1OJt7FyAgmzEbZQChoM5noE_oprZvCrxPH59UYRUMLOqcJHueWRRafB1rMuDQYfg");
    }


    public void testGetKubernetesTargetContext(String basePath, String namespace, String token) {
        try {
            ApiClient build = new ClientBuilder().setBasePath(basePath).setVerifyingSsl(false).setAuthentication(new AccessTokenAuthentication(token)).build();
            CoreV1Api coreV1Api = new CoreV1Api(build);
            V1EndpointsList v1EndpointsList = coreV1Api.listNamespacedEndpoints(
                    namespace,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            System.out.println(v1EndpointsList.getItems().size());
            System.out.println(v1EndpointsList);
            KubernetesServiceDiscoveryClient kubernetesServiceDiscoveryClient = new KubernetesServiceDiscoveryClient(build, namespace);
//        while (true) {
//            List<ServiceInstance> instances = kubernetesServiceDiscoveryClient.getInstances("newapp");
//            assertTrue(instances != null);
//            assertTrue(instances.size() >= 1);
//            System.out.println(instances);
//            Threads.sleepSeconds(5);
//        }
            List<ServiceInstance> instances = kubernetesServiceDiscoveryClient.getInstances("newapp");
            Assertions.assertTrue(instances != null);
            Assertions.assertTrue(instances.size() >= 1);
            System.out.println(instances);
        } catch (Exception e) {
            LOG.error("error", e);
            Assertions.assertTrue(false);
        }
    }
}
