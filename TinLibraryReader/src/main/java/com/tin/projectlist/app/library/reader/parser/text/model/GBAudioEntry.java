package com.core.text.model;

import com.core.file.image.GBImage;
import com.core.text.model.style.GBTextStyleEntry;

import java.util.Map;

/**
 * html5音视频
 *
 * @author yangn
 *
 */
public class GBAudioEntry extends GBTextStyleEntry {

    protected Map<String, GBImage> myImageMap;
    protected static final char SRC = 1;
    protected static final char PRELOAD = 2;
    protected static final char LOOP = 3;
    protected static final char CONTROLS = 4;
    protected static final char AUTOPLAY = 5;

    public String Src;
    public String Preload;
    public String Loop;
    public String Controls;
    public String Autoplay;

    public GBAudioEntry(String src, String preload, String loop, String controls, String autoplay) {
        Src = src;
        Preload = preload;
        Loop = loop;
        Controls = controls;
        Autoplay = autoplay;

    }

    public GBAudioEntry(Map<String, GBImage> imageMap) {
        myImageMap = imageMap;
    }

    @Override
    public void loadData(char[] data, int offset, int len) {
        String tempVal;
        while (offset < len) {

            char kindType = data[offset++];
            int kindLen = data[offset++];
            try {
                tempVal = new String(data, offset, kindLen);
            } catch (StringIndexOutOfBoundsException ex) {
                return;
            }
            switch (kindType) {

                case SRC :
                    Src = tempVal;
                    break;
                case PRELOAD :
                    Preload = tempVal;
                    break;
                case LOOP :
                    Loop = tempVal;
                    break;
                case CONTROLS :
                    Controls = tempVal;
                    break;
                case AUTOPLAY :
                    Autoplay = tempVal;
                    break;
            }
            offset += kindLen;

        }

    }

    @Override
    public char[] toChars() {
        StringBuilder sb = new StringBuilder();
        if (null != Src) {
            sb.append(SRC);
            sb.append((char) Src.length());
            sb.append(Src);
        }

        if (null != Preload) {
            sb.append(PRELOAD);
            sb.append((char) Preload.length());
            sb.append(Preload);
        }
        if (null != Loop) {
            sb.append(LOOP);
            sb.append((char) Loop.length());
            sb.append(Loop);
        }
        if (null != Controls) {
            sb.append(CONTROLS);
            sb.append((char) Controls.length());
            sb.append(Controls);
        }
        if (null != Autoplay) {
            sb.append(AUTOPLAY);
            sb.append((char) Autoplay.length());
            sb.append(Autoplay);
        }

        return sb.toString().toCharArray();
    }

    public GBImage getImage() {
        return myImageMap.get(Src);
    }

}
