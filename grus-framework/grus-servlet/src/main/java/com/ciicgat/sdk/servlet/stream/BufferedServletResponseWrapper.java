/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet.stream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BufferedServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;
    private ServletOutputStream out;
    private PrintWriter writer;

    public BufferedServletResponseWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
        buffer = new ByteArrayOutputStream(); // 真正存储数据的流
        out = new WrapperOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
    }

    /**
     * 重载父类获取outputstream的方法
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    /**
     * 重载父类获取writer的方法
     */
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        return writer;
    }

    /**
     * 重载父类获取flushBuffer的方法
     */
    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    /**
     * 将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /**
     * 内部类，对ServletOutputStream进行包装
     */
    private class WrapperOutputStream extends ServletOutputStream {

        WrapperOutputStream() throws IOException {
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}
