/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gfs;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Albert on 2019/1/3.
 */
public abstract class ImageFormatUtil {

    private ImageFormatUtil() {
    }

    public static String determineContentType(byte[] data, String fileName) throws IOException {
        return determineContentType(new ByteArrayInputStream(data), fileName);
    }

    private static String determineContentType(InputStream in, String fileName) throws IOException {
        ImageFormat imageFormat = identifyFormat(in);
        if (imageFormat != null) {
            return "image/" + imageFormat.toString().toLowerCase();
        } else {
            return determineContentTypeByName(fileName);
        }
    }

    public static ImageFormat identifyFormat(InputStream in) throws IOException {
        if (isJPEG(in)) {
            return ImageFormat.JPEG;
        }
        if (isPNG(in)) {
            return ImageFormat.PNG;
        }
        if (isGIF(in)) {
            return ImageFormat.GIF;
        }
        if (isBMP(in)) {
            return ImageFormat.BMP;
        }
        if (isTIFF(in)) {
            return ImageFormat.TIFF;
        }
        return null;
    }

    public static boolean isJPEG(InputStream source) throws IOException {
        InputStream iis = source;
        if (!source.markSupported()) {
            throw new IllegalArgumentException("Input stream must support mark");
        }
        iis.mark(30);
        // If the first two bytes are a JPEG SOI marker, it's probably
        // a JPEG file. If they aren't, it definitely isn't a JPEG file.
        try {
            int byte1 = iis.read();
            int byte2 = iis.read();
            byte1 = byte1 & 0xFF;
            byte2 = byte2 & 0xFF;
            if ((byte1 == 0xFF) && (byte2 == 0xD8)) {
                return true;
            }
        } finally {
            iis.reset();
        }
        return false;
    }

    public static boolean isPNG(InputStream in) throws IOException {
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Input stream must support mark");
        }
        byte[] b = new byte[8];
        try {
            in.mark(30);
            in.read(b); //NOSONAR
        } finally {
            in.reset();
        }
        return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    public static boolean isBMP(InputStream in) throws IOException {
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Input stream must support mark");
        }
        byte[] b = new byte[2];
        try {
            in.mark(30);
            in.read(b); //NOSONAR
        } finally {
            in.reset();
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }

    public static boolean isGIF(InputStream in) throws IOException {
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Input stream must support mark");
        }
        byte[] b = new byte[6];
        try {
            in.mark(30);
            in.read(b); //NOSONAR
        } finally {
            in.reset();
        }
        return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    public static boolean isTIFF(InputStream in) throws IOException {
        if (!in.markSupported()) {
            throw new IllegalArgumentException("Input stream must support mark");
        }
        byte[] b = new byte[4];
        try {
            in.mark(30);
            in.read(b); //NOSONAR
        } finally {
            in.reset();
        }
        return ((b[0] == (byte) 0x49 && b[1] == (byte) 0x49 && b[2] == (byte) 0x2a && b[3] == (byte) 0x00)
                || (b[0] == (byte) 0x4d && b[1] == (byte) 0x4d && b[2] == (byte) 0x00 && b[3] == (byte) 0x2a));
    }

    /**
     * 根据图片文件名确定Http头的ContentType类型
     *
     * @param fileName
     * @return
     */
    private static String determineContentTypeByName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return null;
        }
        String suffix = fileName.substring(lastDotIndex + 1);
        if ("JPEG".equalsIgnoreCase(suffix))
            return "image/jpeg";
        if ("JPG".equalsIgnoreCase(suffix))
            return "image/jpeg";
        if ("BMP".equalsIgnoreCase(suffix))
            return "image/bmp";
        if ("GIF".equalsIgnoreCase(suffix))
            return "image/gif";
        if ("PNG".equalsIgnoreCase(suffix))
            return "image/png";
        if ("TIFF".equalsIgnoreCase(suffix))
            return "image/tiff";
        if ("TFF".equalsIgnoreCase(suffix)) {
            return "image/tiff";
        }
        return null;
    }


    public enum ImageFormat {
        JPEG, TIFF, PNG, BMP, GIF, ICO, RAW, PSD, UNKNOWN;

        public static ImageFormat getImageFormat(String suffix) {
            if (suffix == null || suffix.length() == 0) {
                return UNKNOWN;
            }
            if (suffix.charAt(0) == '.') {
                suffix = suffix.substring(1);
            }
            if ("JPEG".equalsIgnoreCase(suffix))
                return JPEG;
            if ("JPG".equalsIgnoreCase(suffix))
                return JPEG;
            if ("BMP".equalsIgnoreCase(suffix))
                return BMP;
            if ("GIF".equalsIgnoreCase(suffix))
                return GIF;
            if ("PNG".equalsIgnoreCase(suffix))
                return PNG;
            if ("TIFF".equalsIgnoreCase(suffix))
                return TIFF;
            if ("TFF".equalsIgnoreCase(suffix)) {
                return TIFF;
            }
            return UNKNOWN;
        }

        public static String getDesc(ImageFormat format) {
            if (JPEG == format)
                return "JPEG";
            if (BMP == format)
                return "BMP";
            if (GIF == format)
                return "GIF";
            if (PNG == format)
                return "PNG";
            if (TIFF == format) {
                return "TIFF";
            }
            return "UNKNOWN";
        }

        public static String getSuffix(ImageFormat format) {
            if (JPEG == format) {
                return ".jpg";
            } else if (TIFF == format) {
                return ".tiff";
            } else if (PNG == format) {
                return ".png";
            } else if (BMP == format) {
                return ".bmp";
            } else if (GIF == format) {
                return ".gif";
            } else if (ICO == format) {
                return ".ico";
            } else if (PSD == format) {
                return ".psd";
            } else {
                return "";
            }
        }

        public String getDesc() {
            return getDesc(this);
        }

        public String getSuffix() {
            return getSuffix(this);
        }
    }


}
