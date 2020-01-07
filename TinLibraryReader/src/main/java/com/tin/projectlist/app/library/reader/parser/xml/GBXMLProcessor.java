package com.tin.projectlist.app.library.reader.parser.xml;


import com.tin.projectlist.app.library.reader.parser.common.CopyVersionInfo;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * XML处理者<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public abstract class GBXMLProcessor {
    public static Map<String, char[]> getEntityMap(List<String> dtdList) {
        try {
            return GBXMLParser.getDTDMap(dtdList);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    public static void read(GBXMLReader xmlReader, InputStream stream, int bufferSize) throws IOException {
        GBXMLParser parser = null;
        try {
            parser = new GBXMLParser(xmlReader, stream, bufferSize);
            xmlReader.startDocumentHandler();
            parser.doIt();
            xmlReader.endDocumentHandler();
        } finally {
            if (parser != null) {
                parser.finish();
            }
        }
    }

    public static void read(GBXMLReader xmlReader, Reader reader, int bufferSize) throws IOException {
        GBXMLParser parser = null;
        try {
            parser = new GBXMLParser(xmlReader, reader, bufferSize);
            xmlReader.startDocumentHandler();
            parser.doIt();
            xmlReader.endDocumentHandler();
        } finally {
            if (parser != null) {
                parser.finish();
            }
        }
    }

    public static void read(GBXMLReader xmlReader, GBFile file) throws IOException {
        read(xmlReader, file, 65536);
    }

    public static void read(GBXMLReader xmlReader, GBFile file, int bufferSize) throws IOException {

        if (file == null)
            throw new IOException();
        InputStream stream = file.getInputStream();
        try {
            read(xmlReader, stream, bufferSize);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void read(GBXMLReader xmlReader, GBFile file, CopyVersionInfo rightInfo) throws IOException {
        read(xmlReader, file, 65536, rightInfo);
    }

    /**
     * 版权信息替换方法
     *
     * @param xmlReader
     * @param file
     * @param bufferSize
     * @param rightInfo
     * @throws IOException
     */
    public static void read(GBXMLReader xmlReader, GBFile file, int bufferSize, CopyVersionInfo rightInfo)
            throws IOException {

        if (file == null)
            throw new IOException();

        InputStream stream = file.getInputStream();
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        ByteArrayInputStream bat;
        try {
            bat = new ByteArrayInputStream(rightInfo.replaceCopyRight(sb.toString()).getBytes());
        } catch (Exception e1) {
            e1.printStackTrace();
            bat = null;
        }
        try {
            read(xmlReader, bat == null ? stream : bat, bufferSize);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }
}
