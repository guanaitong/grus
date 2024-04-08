/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.http2;

import com.ciicgat.grus.boot.autoconfigure.web.WebProperties;
import com.ciicgat.sdk.gconf.PublicKeyOwner;
import com.ciicgat.sdk.util.system.Systems;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

/**
 * Created by August.Zhou on 2024/4/3 10:52
 */
public class Http2WebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final WebProperties.Http2 http2;

    public Http2WebServerFactoryCustomizer(WebProperties.Http2 http2) {
        this.http2 = http2;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(http2.getPort());
        connector.setThrowOnFailure(true);

        connector.setScheme("https");
        connector.setSecure(true);
        connector.setProperty("SSLEnabled", "true");

        //Add SSL configuration to your extra connector
        SSLHostConfig sslConfig = new SSLHostConfig();
        SSLHostConfigCertificate certConfig = new SSLHostConfigCertificate(sslConfig, SSLHostConfigCertificate.Type.RSA);
        String passwd = PublicKeyOwner.decrypt("NN1viYID5pMDmXd9++HumUfMkv7ELRNIMnPSUMCqYb6azMPfCHt0NrR7G3j7X3icUVo2pOYQbwuDn7Deg4Usk3EHp3qOUkPoR0FquTqYzMj/QLaP4v3tx3fBMQyAx50aRNZcgwWBwhby9wsqBUpXYai3BxxC8Qbe+9R/k2W1FdvCdM+p6rr9ogIFnp+2qIWxWamCTM35ZJLve6Ge7iH7lqojttlf+WZbU+p/pWrQXxYFEbkcFdUNqfAqCirQ/skBRzXsrHdhLnSs43D8KBtAoKikg+nwGCcBipD/dUVSzAfbepUwxXC1zUiaUGZ8jb9viDmY0SvOG2XF8LrpZVLApw==");

        String certificateKeystoreFile;

        // 如果在容器里，优先使用容器的里的最新证书
        // classpath中的证书只是为了开发调试使用
        if (Systems.IN_K8S) {
            certificateKeystoreFile = "/home/gat/.keystore/server.p12";
        } else {
            try {
                certificateKeystoreFile = ResourceUtils.getURL("classpath:server.p12").toString();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        String trustStoreFile = null;
        try {
            trustStoreFile = ResourceUtils.getURL("classpath:truststore.p12").toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        certConfig.setCertificateKeystoreFile(certificateKeystoreFile);
        certConfig.setCertificateKeystorePassword(passwd);
        sslConfig.addCertificate(certConfig);

        // client auth
        sslConfig.setTruststoreFile(trustStoreFile);
        sslConfig.setTruststorePassword(passwd);
        sslConfig.setTruststoreType("PKCS12");
        sslConfig.setCertificateVerification("required");
        connector.addSslHostConfig(sslConfig);

        connector.addUpgradeProtocol(new Http2Protocol());
        factory.addAdditionalTomcatConnectors(connector);
    }


}
