/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */


package com.ciicgat.grus.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author andy
 */
public class GrusVersion {

    private GrusVersion() {
        throw new IllegalStateException("Utility class");
    }

    public static String getVersion() {
        return getGrusVersion();
    }

    private static String getGrusVersion() {
        String implementationVersion = GrusVersion.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        } else {
            CodeSource codeSource = GrusVersion.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return "";
            }

            URL codeSourceLocation = codeSource.getLocation();
            try {
                URLConnection connection = codeSourceLocation.openConnection();
                if (connection instanceof JarURLConnection) {
                    return getImplementationVersion(((JarURLConnection) connection).getJarFile());
                }

                JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()));
                String version;
                try {
                    version = getImplementationVersion(jarFile);
                } finally {
                    jarFile.close();
                }
                return version;
            } catch (Exception ex) {
                return "";
            }
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
