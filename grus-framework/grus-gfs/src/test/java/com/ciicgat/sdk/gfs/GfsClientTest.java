/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gfs;

import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by albert.sun on Jul 15, 2019
 */
public class GfsClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GfsClientTest.class);

    private GfsClient gfsClient;
    private byte[] sampleData;
    private byte[] sampleAudioData;
    private File imgFile;
    private File audioFile;

    public GfsClientTest() {

        {
            imgFile = new File(getClass().getClassLoader().getResource("ciicgat-signature.png").getFile());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                FileInputStream fis = new FileInputStream(imgFile);
                IOUtils.copy(fis, out);
                IOUtils.closeQuietly(fis);
            } catch (IOException e) {
                throw new BusinessRuntimeException(-1, "测试初始化失败");
            }
            sampleData = out.toByteArray();
        }

        {
            audioFile = new File(getClass().getClassLoader().getResource("test.amr").getFile());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                FileInputStream fis = new FileInputStream(audioFile);
                IOUtils.copy(fis, out);
                IOUtils.closeQuietly(fis);
            } catch (IOException e) {
                throw new BusinessRuntimeException(-1, "测试音频初始化失败");
            }
            sampleAudioData = out.toByteArray();
        }

        gfsClient = new GfsClient();
    }

    @Test
    public void uploadImg() {
        String imgUrl = gfsClient.uploadImg(sampleData, imgFile.getName());
        LOGGER.info(imgUrl);
    }


    @Test
    public void uploadPublicFile() {
        String publicFile = gfsClient.uploadPublicFile(sampleData, imgFile.getName());
        LOGGER.info(publicFile);
    }


    @Disabled("Disabled until is up!")
    @Test
    public void uploadPublicFile2() {

        String publicFile = gfsClient.uploadPublicFile(sampleAudioData,
                audioFile.getName(),
                "audio/origin/nj-greeting/xxxx.amr",
                null);

        LOGGER.info(publicFile);
    }

    @Test
    public void downloadPrivateFile() {

        String privateFileAccessUrl = gfsClient.uploadPrivateFile(sampleData, "TEST/ciicgat-signature.png");


        LOGGER.info("文件访问地址：{}", gfsClient.getDownloadUrl4PrivateFile(privateFileAccessUrl));

        try {
            String tmpFile = System.getProperty("java.io.tmpdir") + File.separator + "ciicgat-signature.png";
            LOGGER.info("文件下载路径：{}", tmpFile);
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            gfsClient.downloadPrivateFile(privateFileAccessUrl, fileOutputStream);
            IOUtils.closeQuietly(fileOutputStream);

            // 检查文件是否存在
            Assertions.assertTrue(new File(tmpFile).exists());

        } catch (FileNotFoundException e) {
            throw new BusinessRuntimeException(-1, "文件下载失败");
        }

        //私有文件删除
        gfsClient.delete4PrivateFile(privateFileAccessUrl);


        gfsClient.uploadPrivateFile(sampleData, "TMP/APP_NAME/ciicgat-signature.png", "ciicgat-signature.png");

    }
}
