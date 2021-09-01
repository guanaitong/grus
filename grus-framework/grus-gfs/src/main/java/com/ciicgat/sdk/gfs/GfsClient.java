/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gfs;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.service.naming.NamingService;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import com.ciicgat.sdk.lang.url.UrlCoder;
import com.ciicgat.sdk.util.http.HttpClientHelper;
import com.ciicgat.sdk.util.system.Systems;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albert.sun on Jul 15, 2019
 */
public class GfsClient {

    public static final String ACCESS_DOMAIN_IMG = "https://img1.guanaitong.com/";
    public static final String ACCESS_DOMAIN_FILE = "https://file.guanaitong.com/";
    public static final String ACCESS_DOMAIN_BIZ_FILE = "https://bizfile.guanaitong.com/";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int DEFAULT_OSS_OBJECT_CACHE_TIME = 3600 * 24 * 30 * 3; // 默认缓存3个月

    private static final Logger LOGGER = LoggerFactory.getLogger(GfsClient.class);

    private final String gfsServer;

    public GfsClient() {
        this.gfsServer = NamingService.DEFAULT.resolve("gfs");
    }

    /**
     * 直接上传图片(推荐头像和商品公开图片场景使用)
     *
     * @param data             图片名称
     * @param originalFileName 图片文件名
     * @return 图片访问链接
     */
    public String uploadImg(byte[] data, String originalFileName) {
        return uploadImgDirectly(data, originalFileName, null);
    }

    /**
     * 公开权限文件
     *
     * @param data             文件流
     * @param originalFileName 原始文件名（可作为下载附件的名称）
     * @return 公开文件访问链接
     */
    public String uploadPublicFile(byte[] data, String originalFileName) {

        String ossFileKey = defaultOssFileKey(data, originalFileName);

        String accessUrl = ACCESS_DOMAIN_FILE + ossFileKey;

        String signedUrl;
        try {
            signedUrl = getUploadUrl4PublicFile(ossFileKey, null, true);
        } catch (OSSObjectExistsException e) {
            return accessUrl;
        }

        ossPutObject(signedUrl, data, ossFileKey, originalFileName);

        return accessUrl;
    }


    /**
     * 支持自定义文件路径。
     *
     * @param data
     * @param originalFileName
     * @param ossFileKey
     * @param contentType
     * @return
     */
    public String uploadPublicFile(byte[] data,
                                   String originalFileName,
                                   String ossFileKey,
                                   String contentType) {

        String accessUrl = ACCESS_DOMAIN_FILE + ossFileKey;

        String signedUrl;
        try {
            signedUrl = getUploadUrl4PublicFile(ossFileKey, contentType, true);
        } catch (OSSObjectExistsException e) {
            return accessUrl;
        }

        ossPutObject(signedUrl,
                data,         // 待上传的文件输入流
                ossFileKey,          // 文件key
                null,  // originalFileName 为空时，上传文件中不设置附件头信息（针对img类文件特殊处理）
                contentType,         // 文件类型
                DEFAULT_OSS_OBJECT_CACHE_TIME);

        return accessUrl;
    }

    /**
     * 支持文件覆盖上传
     *
     * @param key
     * @param isSupportOverride
     * @return
     * @throws OSSObjectExistsException
     */
    public String getUploadUrl4PublicFile(String key, String contentType, boolean isSupportOverride)
            throws OSSObjectExistsException {

        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key can not be blank");
        }

        //1.不支持覆写时存在key时，直接抛出异常
        if (!isSupportOverride && checkExist4PublicFile(key)) {
            throw new OSSObjectExistsException(String.format("the key[%s] has existed in the oss.", key));
        }

        //2.支持覆写则重新签名由客户端重新上传文件
        String api = gfsServer + "/gfs/getUploadUrl4PublicFile.do";

        return requestUrl(key, contentType, api);
    }

    private String requestUrl(String key, String contentType, String api) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        if (null != contentType && !"".equals(contentType)) {
            params.put("contentType", contentType);
        }

        return extractSignedUrl(api, params);
    }

    /**
     * 上传私有文件(适用于导入导出临时文件场景，此文件会定期清理)
     *
     * @param data             私有文件字节流
     * @param originalFileName 原始文件名
     * @return 返回私有文件访问URL（需再次请求获取最终访问URL）
     */
    public String uploadPrivateFile(byte[] data, String originalFileName) {
        return uploadPrivateFile(data, defaultOssFileKey(data, originalFileName), originalFileName, true, null);
    }

    /**
     * 基于字节流, 文件对象Key，下载文件名上传私有文件。
     *
     * @param data             原文件字节数组
     * @param ossFileKey       文件key
     * @param originalFileName 原始文件名（用于下载）
     * @return 返回私有文件访问URL（需再次请求获取最终访问URL）
     */
    public String uploadPrivateFile(byte[] data, String ossFileKey, String originalFileName) {

        return uploadPrivateFile(data, ossFileKey, originalFileName, null);
    }

    /**
     * 基于字节流, 文件对象Key，下载文件名上传私有文件。
     *
     * @param data             原文件字节数组
     * @param ossFileKey       文件key
     * @param originalFileName 原始文件名（用于下载）
     * @param contentType      手动指定文件类型
     * @return 返回私有文件访问URL（需再次请求获取最终访问URL）
     */
    public String uploadPrivateFile(byte[] data, String ossFileKey, String originalFileName, String contentType) {

        return uploadPrivateFile(data, ossFileKey, originalFileName, false, contentType);
    }


    /**
     * 基于自定义文件key，完成私有文件上传。
     *
     * @param data             文件字节流
     * @param customOssFileKey 自定义文件key
     * @param originalFileName 原始文件名（用于下载）
     * @param contentType      文件类型
     * @return 返回私有文件访问URL（需再次请求获取最终访问URL）
     */
    private String uploadPrivateFile(byte[] data,
                                     String customOssFileKey,
                                     String originalFileName,
                                     boolean tmpFlag,
                                     String contentType) {

        String tmpOssFileKey = tmpFlag ? "tmp/" + Systems.APP_NAME + '/' + customOssFileKey : customOssFileKey;

        String signedUrl;

        try {
            signedUrl = getUploadUrl4PrivateFile(tmpOssFileKey, true, contentType);
        } catch (OSSObjectExistsException e) {
            throw new BusinessRuntimeException(-1, "上传文件请求签名失败（文件已存在）");
        }

        ossPutObject(signedUrl, data, tmpOssFileKey, originalFileName, contentType, 0);

        return ACCESS_DOMAIN_BIZ_FILE + tmpOssFileKey;
    }

    /**
     * 基于文件字节流和上传文件名获取上传URL
     *
     * @param data
     * @param originalFileName
     * @return
     * @throws OSSObjectExistsException
     */
    public String getUploadUrl4PrivateFile(byte[] data, String originalFileName) throws OSSObjectExistsException {

        return getUploadUrl4PrivateFile(defaultOssFileKey(data, originalFileName));
    }

    /**
     * 获取上传私有业务文件的授权签名地址(不支持覆盖写，不存在时直接返回原始文件地址)
     *
     * @param ossFileKey 业务文件的OSS存储key值
     * @return 授权的上传签名地址
     * @throws OSSObjectExistsException
     */
    public String getUploadUrl4PrivateFile(String ossFileKey) throws OSSObjectExistsException {
        return getUploadUrl4PrivateFile(ossFileKey, false);
    }

    /**
     * 获取上传私有业务文件的授权签名地址
     *
     * @param ossFileKey 业务文件的OSS存储key值
     * @return
     * @throws OSSObjectExistsException
     */
    public String getUploadUrl4PrivateFile(String ossFileKey, boolean isSupportOverride) throws OSSObjectExistsException {
        return getUploadUrl4PrivateFile(ossFileKey, isSupportOverride, null);
    }

    /**
     * 获取上传私有业务文件的授权签名地址
     *
     * @param ossFileKey
     * @param isSupportOverride
     * @param contentType
     * @return
     * @throws OSSObjectExistsException
     */
    public String getUploadUrl4PrivateFile(String ossFileKey, boolean isSupportOverride, String contentType) throws OSSObjectExistsException {

        if (StringUtils.isBlank(ossFileKey)) {
            throw new IllegalArgumentException("key can not be blank");
        }


        if (!isSupportOverride && checkExist4PrivateFile(ossFileKey)) {
            throw new OSSObjectExistsException(String.format("the key[%s] has existed in the oss.", ossFileKey));
        }

        String api = gfsServer + "/gfs/getUploadUrl4PrivateFile.do";
        return requestUrl(ossFileKey, contentType, api);
    }

    /**
     * 下载私有业务文件到指定的输出流中
     *
     * @param url    私有业务文件的地址
     * @param output 文件输出流
     */
    public void downloadPrivateFile(String url, final OutputStream output) {

        Request request = new Request.Builder().get().url(getDownloadUrl4PrivateFile(url)).build();

        HttpClientHelper.request(request, response -> {
            InputStream input = response.body().byteStream();
            try {
                IOUtils.copy(input, output);
            } catch (IOException e) {
                LOGGER.error("error", e);
            }
            IOUtils.closeQuietly(input);
            return null;
        });

    }

    /**
     * 获取私有业务文件的授权访问地址
     *
     * @param url 文件的未加授权的地址
     * @return 授权的签名访问地址
     */
    public String getDownloadUrl4PrivateFile(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url is invalid, url=" + url);
        }
        String api = gfsServer + "/gfs/getDownloadUrl4PrivateFile.do";
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        return extractSignedUrl(api, params);
    }

    /**
     * 删除私有文件
     *
     * @param key
     */
    @Deprecated
    public void delete4PrivateFile(String key) {

        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key can not be blank");
        }

        String api = gfsServer + "/gfs/delete4PrivateFile.do";
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);

        HttpClientHelper.postForm(api, params);
    }


    //~~~~~ 私有方法


    private String extractSignedUrl(String api, Map<String, Object> params) {

        String result = HttpClientHelper.get(api, params);
        ApiResponse<String> apiResponse = JSON.parse(result, new TypeReference<>() {
        });
        if (apiResponse.getCode() == 0) {
            String signedUrl = apiResponse.getData();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("signedUrl {}", signedUrl);
            }
            return signedUrl;
        } else {
            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }
    }

    /**
     * 检查图片文件是否存在
     *
     * @param key 要检查的OSS存储对象key值
     * @return true 文件存在 false 文件不存在
     */
    private boolean checkExist4Img(String key) {
        return checkExist(key, this.gfsServer + "/gfs/checkExist4Img.do");
    }

    /**
     * 检查私有业务文件是否已存在
     *
     * @param key 要检查的OSS存储对象key值
     * @return true 文件存在 false 文件不存在
     */
    private boolean checkExist4PrivateFile(String key) {
        return checkExist(key, this.gfsServer + "/gfs/checkExist4PrivateFile.do");
    }

    /**
     * 检查共有业务文件是否已存在
     *
     * @param key 要检查的OSS存储对象key值
     * @return true 文件存在 false 文件不存在
     */
    private boolean checkExist4PublicFile(String key) {
        return checkExist(key, this.gfsServer + "/gfs/checkExist4PublicFile.do");
    }

    private boolean checkExist(String key, String api) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        String result = HttpClientHelper.get(api, params);
        ApiResponse<Boolean> apiResponse = JSON.parse(result, new TypeReference<>() {
        });
        if (apiResponse.getCode() == 0) {
            return apiResponse.getData();
        } else {
            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }
    }

    /**
     * 确定OSS对象最终存储的key名，一般文件的key名规范是：md5+原文件后缀
     *
     * @param data             文件字节流
     * @param originalFileName 原始文件名
     * @return 生成后的文件名
     */
    private String defaultOssFileKey(byte[] data, String originalFileName) {

        StringBuilder gfsDefaultBuffer = new StringBuilder("grus-gfs");
        gfsDefaultBuffer.append('/').append(Systems.WORK_ENV);
        gfsDefaultBuffer.append('/').append(mergeAdBlock(Systems.APP_NAME));
        gfsDefaultBuffer.append('/').append("by-days"); // 默认存储逻辑为：按日期切分
        gfsDefaultBuffer.append('/').append(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        gfsDefaultBuffer.append('/').append(DigestUtils.md5Hex(data));
        gfsDefaultBuffer.append(getSuffix(originalFileName));
        return gfsDefaultBuffer.toString();

    }

    private String mergeAdBlock(String appName) {
        if (appName.contains("advertisement")) {
            return "gat"; //过滤AdBlock插件
        } else {
            return appName;
        }
    }

    /**
     * 基于文件名提取源文件后缀.
     *
     * @param originalFileName 原始文件名
     * @return 文件后缀名称
     */
    private String getSuffix(String originalFileName) {

        if (StringUtils.isBlank(originalFileName)) {
            return "";
        }

        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return originalFileName.substring(lastDotIndex);
        } else {
            return "";
        }
    }

    private String ossPutObject(String uri,
                                byte[] data,
                                String key,
                                String originalFileName) {
        return ossPutObject(uri, data, key, originalFileName, null, 0);
    }

    /**
     * 根据指定的上传地址上传文件，并设置相应的响应头
     *
     * @param signedUploadUrl
     * @param data
     * @param key
     * @param originalFileName
     * @param contentType
     * @param cacheSeconds
     * @return
     */
    private String ossPutObject(String signedUploadUrl,
                                byte[] data,
                                String key,
                                String originalFileName,
                                String contentType,
                                int cacheSeconds) {

        LOGGER.info("url {} key {} originalFileName {} contentType {}", signedUploadUrl, key, originalFileName, contentType);

        Request.Builder builder = new Request.Builder();
        builder.url(signedUploadUrl);
        RequestBody requestBody = RequestBody.create(null, data);
        builder.put(requestBody);

        if (contentType != null) {
            builder.addHeader("Content-Type", contentType);
        }

        if (originalFileName != null && !originalFileName.isBlank()) {
            builder.addHeader("Content-Disposition", "attachment;filename=" + UrlCoder.encode(originalFileName));
        }

        if (cacheSeconds > 0) {
            builder.addHeader("Cache-Control", "max-age=" + cacheSeconds);
        }

        return HttpClientHelper.request(builder.build());
    }


    /**
     * 直接上传图片文件到OSS http://img1.guanaitong.com/
     *
     * @param data             图片文件流
     * @param originalFileName 原始文件
     * @param contentType      文件类型
     * @return 图片访问链接
     */
    private String uploadImgDirectly(byte[] data, String originalFileName, String contentType) {


        String ossFileKey = defaultOssFileKey(data, originalFileName);

        String accessUrl = ACCESS_DOMAIN_IMG + ossFileKey;
        if (checkExist4Img(ossFileKey)) {
            return accessUrl;
        }

        // detach image content type
        if (null == contentType) {
            try {
                contentType = ImageFormatUtil.determineContentType(data, originalFileName);
            } catch (IOException e) {
                contentType = "image/jpeg";
            }
        }

        // GFS基于OSS中存在时则直接返回原始文件地址
        String signedUrl = getUploadUrl4Img(ossFileKey, contentType);

        ossPutObject(signedUrl,
                data,         // 待上传的文件输入流
                ossFileKey,          // 文件key
                null,  // originalFileName 为空时，上传文件中不设置附件头信息（针对img类文件特殊处理）
                contentType,         // 文件类型
                DEFAULT_OSS_OBJECT_CACHE_TIME);

        return accessUrl;
    }

    /**
     * 获取上传图片的授权签名地址
     *
     * @param ossFileKey 图片文件OSS存储key值
     * @return 授权的签名地址
     */
    private String getUploadUrl4Img(String ossFileKey, String contentType) {

        if (StringUtils.isBlank(ossFileKey)) {
            throw new IllegalArgumentException("ossFileKey can not be blank");
        }

        String api = gfsServer + "/gfs/getUploadUrl4Img.do";

        return requestUrl(ossFileKey, contentType, api);
    }


}
