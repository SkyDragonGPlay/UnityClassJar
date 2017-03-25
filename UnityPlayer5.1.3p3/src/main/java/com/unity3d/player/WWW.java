package com.unity3d.player;

import android.util.Log;

import java.net.*;
import java.util.*;
import java.io.*;

class WWW extends Thread
{
    private int mRedirectCount;
    private int mRequestCode;
    private String mRequestURL;
    private byte[] mRequestData;
    private Map<String, String> mRequestProperties;
    
    WWW(final int requestCode, final String requestURL, final byte[] data, final Map requestProperties) {
        this.mRequestCode = requestCode;
        this.mRequestURL = requestURL;
        this.mRequestData = data;
        this.mRequestProperties = requestProperties;
        this.mRedirectCount = 0;
        this.start();
    }
    
    @Override
    public void run() {
        if (++this.mRedirectCount > 5) {
            errorCallback(this.mRequestCode, "Too many redirects");
            return;
        }
        URL url;
        URLConnection openConnection;
        try {
            openConnection = (url = new URL(this.mRequestURL)).openConnection();
        }
        catch (MalformedURLException ex) {
            errorCallback(this.mRequestCode, ex.toString());
            return;
        }
        catch (IOException ex2) {
            errorCallback(this.mRequestCode, ex2.toString());
            return;
        }
        if (url.getProtocol().equalsIgnoreCase("file") && url.getHost() != null && url.getHost().length() != 0) {
            errorCallback(this.mRequestCode, url.getHost() + url.getFile() + " is not an absolute path!");
            return;
        }
        if (this.mRequestProperties != null) {
            for (final Map.Entry<String, String> entry : this.mRequestProperties.entrySet()) {
                openConnection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        int bufferLength = 1428;
        if (this.mRequestData != null) {
            openConnection.setDoOutput(true);
            try {
                final OutputStream outputStream = openConnection.getOutputStream();
                int writeCount = 0;
                while (writeCount < this.mRequestData.length) {
                    final int min = Math.min(1428, this.mRequestData.length - writeCount);
                    outputStream.write(this.mRequestData, writeCount, min);
                    this.progressCallback(writeCount += min, this.mRequestData.length, 0, 0, 0L, 0L);
                }
            }
            catch (Exception ex3) {
                errorCallback(this.mRequestCode, ex3.toString());
                return;
            }
        }
        if (openConnection instanceof HttpURLConnection) {
            final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
            int responseCode;
            try {
                responseCode = httpURLConnection.getResponseCode();
            }
            catch (IOException ex4) {
                errorCallback(this.mRequestCode, ex4.toString());
                return;
            }
            final Map<String, List<String>> headerFields;
            final List<String> locationHeaderFields;
            if ((headerFields = httpURLConnection.getHeaderFields()) != null && (responseCode == 301 || responseCode == 302) && (locationHeaderFields = headerFields.get("Location")) != null && !locationHeaderFields.isEmpty()) {
                httpURLConnection.disconnect();
                this.mRequestURL = locationHeaderFields.get(0);
                this.run();
                return;
            }
        }
        final Map<String, List<String>> headerFields2 = openConnection.getHeaderFields();
        boolean headerCallback = this.headerCallback(headerFields2);
        if ((headerFields2 == null || !headerFields2.containsKey("content-length")) && openConnection.getContentLength() != -1) {
            headerCallback = (headerCallback || this.headerCallback("content-length", String.valueOf(openConnection.getContentLength())));
        }
        if ((headerFields2 == null || !headerFields2.containsKey("content-type")) && openConnection.getContentType() != null) {
            headerCallback = (headerCallback || this.headerCallback("content-type", openConnection.getContentType()));
        }
        if (headerCallback) {
            errorCallback(this.mRequestCode, this.mRequestURL + " aborted");
            return;
        }
        final int contentLength = (openConnection.getContentLength() > 0) ? openConnection.getContentLength() : 0;
        if (url.getProtocol().equalsIgnoreCase("file") || url.getProtocol().equalsIgnoreCase("jar")) {
            bufferLength = ((contentLength == 0) ? 32768 : Math.min(contentLength, 32768));
        }
        int readCount = 0;
        try {
            final long currentTimeMillis = System.currentTimeMillis();
            final byte[] array = new byte[bufferLength];
            final InputStream inputStream = openConnection.getInputStream();
            for (int j = 0; j != -1; j = inputStream.read(array)) {
                if (this.readCallback(array, j)) {
                    errorCallback(this.mRequestCode, this.mRequestURL + " aborted");
                    return;
                }
                readCount += j;
                this.progressCallback(0, 0, readCount, contentLength, System.currentTimeMillis(), currentTimeMillis);
            }
        }
        catch (Exception ex5) {
            errorCallback(this.mRequestCode, ex5.toString());
            return;
        }
        this.progressCallback(0, 0, readCount, readCount, 0L, 0L);
        doneCallback(this.mRequestCode);
    }
    
    private static native boolean headerCallback(final int p0, final String p1);
    
    protected boolean headerCallback(final Map map) {
        if (map == null || map.size() == 0) {
            return false;
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator<Map.Entry<String, List>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Object o;
            for (final String s : ((Map.Entry<String, List<String>>)(o = iterator.next())).getValue()) {
                sb.append(((Map.Entry<String, List<String>>)o).getKey());
                sb.append(": ");
                sb.append(s);
                sb.append("\r\n");
            }
            if (((Map.Entry<String, List>)o).getKey() == null) {
                for (final String s2 : ((Map.Entry<String, List<String>>)o).getValue()) {
                    sb.append("Status: ");
                    sb.append(s2);
                    sb.append("\r\n");
                }
            }
        }
        return headerCallback(this.mRequestCode, sb.toString());
    }
    
    protected boolean headerCallback(final String s, final String s2) {
        final StringBuilder sb;
        (sb = new StringBuilder()).append(s);
        sb.append(": ");
        sb.append(s2);
        sb.append("\n\r");
        return headerCallback(this.mRequestCode, sb.toString());
    }
    
    private static native boolean readCallback(final int p0, final byte[] p1, final int p2);
    
    protected boolean readCallback(final byte[] array, final int n) {
        return readCallback(this.mRequestCode, array, n);
    }
    
    private static native void progressCallback(final int p0, final float p1, final float p2, final double p3, final int p4);
    
    protected void progressCallback(final int writeCount, int totalToWrite, final int readCount, final int totalToRead, final long readEndTime, final long readStartTime) {
        float readPercent;
        float writePercent;
        double remainReadTime;
        if (totalToRead > 0) {
            readPercent = readCount / totalToRead;
            writePercent = 1.0f;
            int remainToRead = Math.max(totalToRead - readCount, 0);
            if (Double.isInfinite(remainReadTime = remainToRead / (1000.0 * readCount / Math.max(readEndTime - readStartTime, 0.1))) || Double.isNaN(remainReadTime)) {
                remainReadTime = 0.0;
            }
        }
        else {
            if (totalToWrite <= 0) {
                return;
            }
            readPercent = 0.0f;
            writePercent = writeCount / totalToWrite;
            remainReadTime = 0.0;
        }
        progressCallback(this.mRequestCode, writePercent, readPercent, remainReadTime, totalToRead);
    }
    
    private static native void errorCallback(final int p0, final String p1);
    
    private static native void doneCallback(final int p0);
}
