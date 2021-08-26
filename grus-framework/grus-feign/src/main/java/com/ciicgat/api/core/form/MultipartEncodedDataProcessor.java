/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.form;

import com.ciicgat.sdk.lang.tool.CloseUtils;
import feign.Request;
import feign.RequestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Map;

import static feign.Util.UTF_8;

/**
 * Multipart form data implementation of {@link feign.form.FormDataProcessor}.
 *
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 30.04.2016
 */
public class MultipartEncodedDataProcessor implements FormDataProcessor {

    public static final String CONTENT_TYPE;

    private static final String CRLF;

    static {
        CONTENT_TYPE = "multipart/form-data";
        CRLF = "\r\n";
    }

    @Override
    public void process(Map<String, Object> data, RequestTemplate template) {
        String boundary = createBoundary();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PrintWriter writer = new PrintWriter(outputStream);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                writer.append("--" + boundary).append(CRLF);
                if (isPayload(entry.getValue())) {
                    writeByteOrFile(outputStream, writer, entry.getKey(), entry.getValue());
                } else {
                    writeParameter(writer, entry.getKey(), entry.getValue().toString());
                }
                writer.append(CRLF).flush();
            }

            writer.append("--" + boundary + "--").append(CRLF).flush();
        } catch (Throwable throwable) {
            try {
                outputStream.close();
            } catch (IOException ex) {
            }
            throw throwable;
        }

        String contentType = new StringBuilder()
                .append(CONTENT_TYPE)
                .append("; boundary=")
                .append(boundary)
                .toString();

        template.header("Content-Type", contentType);
        template.body(Request.Body.encoded(outputStream.toByteArray(), UTF_8));
        CloseUtils.close(outputStream);
    }

    @Override
    public String getSupportetContentType() {
        return CONTENT_TYPE;
    }

    /**
     * Checks is passed object a supported file's type or not.
     *
     * @param value form file parameter.
     */
    protected boolean isPayload(Object value) {
        return value != null && (value instanceof File || value instanceof byte[]);
    }

    /**
     * Writes file's content to output stream.
     *
     * @param output output stream to remote destination.
     * @param writer wrapped output stream.
     * @param name   the name of the file.
     * @param value  file's content. Byte array or {@link File}.
     */
    protected void writeByteOrFile(OutputStream output, PrintWriter writer, String name, Object value) {
        if (value instanceof byte[]) {
            writeByteArray(output, writer, name, null, null, (byte[]) value);
        } else {
            writeFile(output, writer, name, null, (File) value);
        }
    }

    private String createBoundary() {
        return Long.toHexString(System.currentTimeMillis());
    }

    private void writeParameter(PrintWriter writer, String name, String value) {
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
        writer.append(CRLF).append(value);
    }

    /**
     * Writes file to output stream as a {@link File}.
     *
     * @param output      output stream to remote destination.
     * @param writer      wrapped output stream.
     * @param name        the name of the file.
     * @param contentType the content type (if known).
     * @param file        file object.
     */
    protected void writeFile(OutputStream output, PrintWriter writer, String name, String contentType, File file) {
        writeFileMeta(writer, name, file.getName(), contentType);

        try (InputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.flush();
    }

    /**
     * Writes file's content to output stream as a byte array.
     *
     * @param output           utput stream to remote destination.
     * @param writer           wrapped output stream.
     * @param name             the name of the file.
     * @param originalFilename the original filename (as on the client's machine).
     * @param contentType      the content type (if known).
     * @param bytes            file's content.
     */
    protected void writeByteArray(OutputStream output,
                                  PrintWriter writer,
                                  String name,
                                  String originalFilename,
                                  String contentType,
                                  byte[] bytes
    ) {
        writeFileMeta(writer, name, originalFilename, contentType);
        try {
            output.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.flush();
    }

    private void writeFileMeta(PrintWriter writer, String name, String fileName, String contentValue) {
        String contentDesposition = new StringBuilder()
                .append("Content-Disposition: form-data; name=\"").append(name).append("\"; ")
                .append("filename=\"").append(fileName).append("\"")
                .toString();

        if (contentValue == null) {
            contentValue = fileName != null
                    ? URLConnection.guessContentTypeFromName(fileName)
                    : "application/octet-stream";
        }
        String contentType = new StringBuilder()
                .append("Content-Type: ")
                .append(contentValue)
                .toString();

        writer.append(contentDesposition).append(CRLF);
        writer.append(contentType).append(CRLF);
        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        writer.append(CRLF).flush();
    }
}
