package com.tin.projectlist.app.library.base.frame.http.model;


import com.tin.projectlist.app.library.base.frame.http.watcher.ProgressResponseWatcher;

import java.io.File;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;

/**
 * @package : com.cliff.libs.base.http.model
 * @description :
 * Created by chenhx on 2018/4/4 15:27.
 */

public class ApiParams {

    private LinkedHashMap<String, Object> urlParamMap;

    private LinkedHashMap<String, List<FileWrapper>> fileParamMap;

    private int contentType;

    public ApiParams() {
        init();
    }

    public ApiParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParamMap = new LinkedHashMap<>();
        fileParamMap = new LinkedHashMap<>();
        contentType = ApiHeaders.CONTENT_TYPE_JSON_NO_TOKEN;
    }

    public ApiParams contentType(int type) {
        contentType = type;
        return this;
    }

    public void put(String key, String value) {
        urlParamMap.put(key, value);
    }

    public void put(String key, List list) {
        urlParamMap.put(key, list);
    }

    public void put(String key, Object object) {
        urlParamMap.put(key, object);
    }

    public void put(ApiParams params) {
        if (params != null) {
            if (params.urlParamMap != null && !params.urlParamMap.isEmpty()) {
                urlParamMap.putAll(params.urlParamMap);
            }
            if (params.fileParamMap != null && !params.fileParamMap.isEmpty()) {
                fileParamMap.putAll(params.fileParamMap);
            }
        }
    }


    public void put(Map<String, Object> map) {
        if (map == null || map.size() <= 0) {
            return;
        }
        urlParamMap.putAll(map);
    }


    public <T extends File> void put(String key, T file, ProgressResponseWatcher watcher) {
        put(key, file, file.getName(), watcher);
    }

    public <T extends File> void put(String key, T file, String fileName, ProgressResponseWatcher responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    public <T extends InputStream> void put(String key, T file, String fileName,
                                            ProgressResponseWatcher responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    public void put(String key, byte[] bytes, String fileName, ProgressResponseWatcher responseCallBack) {
        put(key, bytes, fileName, guessMimeType(fileName), responseCallBack);
    }

    public void put(String key, FileWrapper wrapper) {
        if (key != null && wrapper != null) {
            put(key, wrapper.file, wrapper.fileName, wrapper.contentType, wrapper.responseWatcher);
        }
    }

    public <T> void put(String key, T content, String fileName, MediaType type, ProgressResponseWatcher watcher) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParamMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper(content, fileName, type, watcher));
        }
    }

    public <T extends File> void putFileParams(String key, List<T> files, ProgressResponseWatcher responseCallBack) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                put(key, file, responseCallBack);
            }
        }
    }

    public void putFileWrapperParams(String key, List<FileWrapper> fileWrappers) {
        if (key != null && fileWrappers != null && !fileWrappers.isEmpty()) {
            for (FileWrapper fileWrapper : fileWrappers) {
                put(key, fileWrapper);
            }
        }
    }

    public void removeUrl(String key) {
        urlParamMap.remove(key);
    }

    public void removeFile(String key) {
        fileParamMap.remove(key);
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    public void clear() {
        urlParamMap.clear();
        fileParamMap.clear();
    }

    private MediaType guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        path = path.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return MediaType.parse(contentType);
    }

    public static class FileWrapper<T> {

        public T file;
        public String fileName;
        public MediaType contentType;
        public long fileSize;
        public ProgressResponseWatcher responseWatcher;

        public FileWrapper(T file, String fileName, MediaType contentType, ProgressResponseWatcher responseWatcher) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            if (file instanceof File) {
                this.fileSize = ((File) file).length();
            } else if (file instanceof byte[]) {
                this.fileSize = ((byte[]) file).length;
            }
            this.fileSize = fileSize;
            this.responseWatcher = responseWatcher;
        }

        @Override
        public String toString() {
            return "FileWrapper{" +
                    "file=" + file +
                    ", fileName='" + fileName + '\'' +
                    ", contentType=" + contentType +
                    ", fileSize=" + fileSize +
                    ", responseWatcher=" + responseWatcher +
                    '}';
        }
    }

    public LinkedHashMap<String, Object> getUrlParamMap() {
        return urlParamMap;
    }

    public void setUrlParamMap(LinkedHashMap<String, Object> urlParamMap) {
        this.urlParamMap = urlParamMap;
    }

    public LinkedHashMap<String, List<FileWrapper>> getFileParamMap() {
        return fileParamMap;
    }

    public void setFileParamMap(LinkedHashMap<String, List<FileWrapper>> fileParamMap) {
        this.fileParamMap = fileParamMap;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
}
