package com.tin.projectlist.app.library.reader.parser.text.model;


import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;

import java.util.Map;

/**
 * html5音视频
 *
 * @author yangn
 *
 */
public class GBVideoEntry extends GBAudioEntry {

    public GBVideoEntry(Map<String, GBImage> imageMap) {
        super(imageMap);
    }

    protected static final char HEIGHT = 6;
    protected static final char WIDTH = 7;

    public String Height;
    public String Width;

    public GBVideoEntry(String src, String preload, String loop, String controls, String autoplay, String height,
                        String width) {
        super(src, preload, loop, controls, autoplay);
        Height = height;
        Width = width;

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
                case HEIGHT :
                    Height = tempVal;
                    break;
                case WIDTH :
                    Width = tempVal;
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

        if (null != Height) {
            sb.append(HEIGHT);
            sb.append((char) Height.length());
            sb.append(Height);
        }

        if (null != Width) {
            sb.append(WIDTH);
            sb.append((char) Width.length());
            sb.append(Width);
        }

        return sb.toString().toCharArray();
    }

    public GBImage getImage() {
        return myImageMap.get(Src);
    }

}
