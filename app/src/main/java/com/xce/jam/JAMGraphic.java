package com.xce.jam;

import com.skt.m.Graphics2D;
import com.xce.io.XFile;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class JAMGraphic {
    public static final int CT_CARTOON = 9;
    public static final int CT_CHAT = 8;
    public static final int CT_DEFAULT = 1;
    public static final int CT_ECONOMY = 6;
    public static final int CT_EDUCATION = 5;
    public static final int CT_EMPTY = 0;
    public static final int CT_ENTERTM = 3;
    public static final int CT_GAME = 2;
    public static final int CT_LIFE = 7;
    public static final int CT_SING = 4;
    public static final int CT_SPORTS = 10;
    public static final int ICON_ANISIZE = 36;
    public static final int ICON_SIZE = 23;
    public static int button_height = 0;
    public static int button_width = 0;
    public static final int color_border_dark = 2306815;
    public static final int color_border_light = 9698812;
    public static final int color_scroll = 16585471;
    public static final int color_text = 0;
    public static int frame_bottom;
    public static int frame_left = 3;
    public static int frame_name_height;
    public static int frame_right;
    public static int frame_subtitle_height;
    public static int frame_title_height;
    public static int frame_top;
    public static int icon_back_size;
    public static int icon_select_size;
    public static int icon_space;
    private static Image img_arrow_left;
    private static Image img_arrow_right;
    private static Image img_button_cancel;
    private static Image img_button_delete;
    private static Image img_button_menu;
    private static Image img_button_ok;
    private static Image img_button_run;
    private static Image img_frame_back;
    private static Image img_frame_scroll;
    private static Image img_frame_subtitle;
    private static Image img_frame_title;
    private static Image img_icon_back;
    private static Image img_icon_select;
    private static Image img_list_icon;
    private static Image img_list_select;
    private static Image img_menu_title;
    private static Image img_text_control;
    private static Image img_text_delete;
    private static Image img_text_down;
    private static Image img_text_listicon;
    private static Image img_text_menu;
    private static Image img_text_number;
    private static Image img_text_sort;
    static JAMGraphic jamgraphic;
    public static int line_height;
    public static int line_space;
    public static int line_width;
    public static int list_select_height;
    public static int list_space;
    public static int scroll_width;

    public JAMGraphic() {
        jamgraphic = this;
    }

    public static void init() {
        if (jamgraphic == null) {
            new JAMGraphic();
        }
    }

    public static Image getImg_frame_back() {
        if (img_frame_back == null) {
            try {
                img_frame_back = Toolkit.createExImage("/iconsx", "/frame_back");
            } catch (Exception e) {
            }
        }
        return img_frame_back;
    }

    public static Image getImg_frame_scroll() {
        if (img_frame_scroll == null) {
            try {
                img_frame_scroll = Toolkit.createExImage("/iconsx", "/frame_scroll");
            } catch (Exception e) {
            }
        }
        return img_frame_scroll;
    }

    public static Image getImg_frame_title() {
        if (img_frame_title == null) {
            try {
                img_frame_title = Toolkit.createExImage("/iconsx", "/frame_title");
            } catch (Exception e) {
            }
        }
        return img_frame_title;
    }

    public static Image getImg_frame_subtitle() {
        if (img_frame_subtitle == null) {
            try {
                img_frame_subtitle = Toolkit.createExImage("/iconsx", "/frame_subtitle");
            } catch (Exception e) {
            }
        }
        return img_frame_subtitle;
    }

    public static Image getImg_arrow_left() {
        if (img_arrow_left == null) {
            try {
                img_arrow_left = Toolkit.createExImage("/iconsx", "/arrow_left");
            } catch (Exception e) {
            }
        }
        return img_arrow_left;
    }

    public static Image getImg_arrow_right() {
        if (img_arrow_right == null) {
            try {
                img_arrow_right = Toolkit.createExImage("/iconsx", "/arrow_right");
            } catch (Exception e) {
            }
        }
        return img_arrow_right;
    }

    public static Image getImg_button_cancel() {
        if (img_button_cancel == null) {
            try {
                img_button_cancel = Toolkit.createExImage("/iconsx", "/button_cancel");
            } catch (Exception e) {
            }
        }
        return img_button_cancel;
    }

    public static Image getImg_button_delete() {
        if (img_button_delete == null) {
            try {
                img_button_delete = Toolkit.createExImage("/iconsx", "/button_delete");
            } catch (Exception e) {
            }
        }
        return img_button_delete;
    }

    public static Image getImg_button_menu() {
        if (img_button_menu == null) {
            try {
                img_button_menu = Toolkit.createExImage("/iconsx", "/button_menu");
            } catch (Exception e) {
            }
        }
        return img_button_menu;
    }

    public static Image getImg_button_ok() {
        if (img_button_ok == null) {
            try {
                img_button_ok = Toolkit.createExImage("/iconsx", "/button_ok");
            } catch (Exception e) {
            }
        }
        return img_button_ok;
    }

    public static Image getImg_button_run() {
        if (img_button_run == null) {
            try {
                img_button_run = Toolkit.createExImage("/iconsx", "/button_run");
            } catch (Exception e) {
            }
        }
        return img_button_run;
    }

    public static Image getImg_icon_select() {
        if (img_icon_select == null) {
            try {
                img_icon_select = Toolkit.createExImage("/iconsx", "/icon_select");
            } catch (Exception e) {
            }
        }
        return img_icon_select;
    }

    public static Image getImg_icon_back() {
        if (img_icon_back == null) {
            try {
                img_icon_back = Toolkit.createExImage("/iconsx", "/icon_back");
            } catch (Exception e) {
            }
        }
        return img_icon_back;
    }

    public static Image getImg_list_select() {
        if (img_list_select == null) {
            try {
                img_list_select = Toolkit.createExImage("/iconsx", "/list_select");
            } catch (Exception e) {
            }
        }
        return img_list_select;
    }

    public static Image getImg_list_icon() {
        if (img_list_icon == null) {
            try {
                img_list_icon = Toolkit.createExImage("/iconsx", "/list_icon");
            } catch (Exception e) {
            }
        }
        return img_list_icon;
    }

    public static Image getImg_menu_title() {
        if (img_menu_title == null) {
            try {
                img_menu_title = Toolkit.createExImage("/iconsx", "/menu_title");
            } catch (Exception e) {
            }
        }
        return img_menu_title;
    }

    public static Image getImg_text_listicon() {
        if (img_text_listicon == null) {
            try {
                img_text_listicon = Toolkit.createExImage("/iconsx", "/text_listicon");
            } catch (Exception e) {
            }
        }
        return img_text_listicon;
    }

    public static Image getImg_text_number() {
        if (img_text_number == null) {
            try {
                img_text_number = Toolkit.createExImage("/iconsx", "/text_number");
            } catch (Exception e) {
            }
        }
        return img_text_number;
    }

    public static Image getImg_text_control() {
        if (img_text_control == null) {
            try {
                img_text_control = Toolkit.createExImage("/iconsx", "/text_control");
            } catch (Exception e) {
            }
        }
        return img_text_control;
    }

    public static Image getImg_text_menu() {
        if (img_text_menu == null) {
            try {
                img_text_menu = Toolkit.createExImage("/iconsx", "/text_menu");
            } catch (Exception e) {
            }
        }
        return img_text_menu;
    }

    public static Image getImg_text_sort() {
        if (img_text_sort == null) {
            try {
                img_text_sort = Toolkit.createExImage("/iconsx", "/text_sort");
            } catch (Exception e) {
            }
        }
        return img_text_sort;
    }

    public static Image getImg_text_delete() {
        if (img_text_delete == null) {
            try {
                img_text_delete = Toolkit.createExImage("/iconsx", "/text_delete");
            } catch (Exception e) {
            }
        }
        return img_text_delete;
    }

    public static Image getImg_text_down() {
        if (img_text_down == null) {
            try {
                img_text_down = Toolkit.createExImage("/iconsx", "/text_down");
            } catch (Exception e) {
            }
        }
        return img_text_down;
    }

    public static Image getDefaultIcon(int i, boolean flag) {
        String s;
        String s2;
        if (flag) {
            s = "/ctani_";
        } else {
            s = "/ct_";
        }
        switch (i) {
            case 2:
                s2 = String.valueOf(s) + "game";
                break;
            case 3:
                s2 = String.valueOf(s) + "entertm";
                break;
            case 4:
                s2 = String.valueOf(s) + "sing";
                break;
            case 5:
                s2 = String.valueOf(s) + "edu";
                break;
            case 6:
                s2 = String.valueOf(s) + "economy";
                break;
            case 7:
                s2 = String.valueOf(s) + "life";
                break;
            case 8:
                s2 = String.valueOf(s) + "chat";
                break;
            case 9:
                s2 = String.valueOf(s) + "cartoon";
                break;
            case 10:
                s2 = String.valueOf(s) + "sports";
                break;
            default:
                s2 = String.valueOf(s) + "default";
                break;
        }
        try {
            Image image = Toolkit.createExImage("/iconsx", s2);
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public static Image getGVMIcon(int i, boolean flag) {
        AppInfo appinfo = AppDB.getDB().elementAt(i);
        String s = Gvm.getDirName(appinfo.id);
        if (s == null) {
            Image image = getDefaultIcon(1, flag);
            return image;
        }
        if (s.indexOf("게임") != -1) {
            Image image2 = getDefaultIcon(2, flag);
            return image2;
        }
        if (s.indexOf("앨범") != -1) {
            Image image3 = getDefaultIcon(3, flag);
            return image3;
        }
        if (s.indexOf("노래방") != -1) {
            Image image4 = getDefaultIcon(4, flag);
            return image4;
        }
        if (s.indexOf("학습") != -1 || s.indexOf("E-book") != -1) {
            Image image5 = getDefaultIcon(5, flag);
            return image5;
        }
        if (s.indexOf("증권") != -1 || s.indexOf("금융") != -1) {
            Image image6 = getDefaultIcon(6, flag);
            return image6;
        }
        if (s.indexOf("생활정보") != -1 || s.indexOf("쿠폰") != -1) {
            Image image7 = getDefaultIcon(7, flag);
            return image7;
        }
        if (s.indexOf("채팅") != -1) {
            Image image8 = getDefaultIcon(8, flag);
            return image8;
        }
        if (s.indexOf("만화") != -1 || s.indexOf("유머") != -1 || s.indexOf("심리") != -1) {
            Image image9 = getDefaultIcon(9, flag);
            return image9;
        }
        if (s.indexOf("스포츠") != -1) {
            Image image10 = getDefaultIcon(10, flag);
            return image10;
        }
        Image image11 = getDefaultIcon(1, flag);
        return image11;
    }

    public static Image getContentsIcon(int i, boolean flag) {
        String s;
        Image image;
        AppInfo appinfo = AppDB.getDB().elementAt(i);
        XFile xfile = null;
        int i2 = appinfo.type;
        if (i2 != 1) {
            if (i2 == 2) {
                try {
                    return getGVMIcon(i, flag);
                } catch (Exception e) {
                    return getDefaultIcon(1, flag);
                }
            }
            return getDefaultIcon(1, flag);
        }
        int j = appinfo.id;
        try {
            try {
                if (!flag) {
                    s = InternalZipConstants.ZIP_FILE_SEPARATOR + j + InternalZipConstants.ZIP_FILE_SEPARATOR + j + ".lbm";
                } else {
                    s = InternalZipConstants.ZIP_FILE_SEPARATOR + j + "/ani" + j + ".lbm";
                }
                if (XFile.exists(s)) {
                    xfile = new XFile(s, 1);
                    int k = XFile.filesize(s);
                    byte[] abyte0 = new byte[k];
                    xfile.read(abyte0, 0, k);
                    image = Image.createImage(abyte0, 0, k);
                    if ((!flag && (image.getWidth() != 23 || image.getHeight() != 23)) || (flag && (image.getWidth() != 72 || image.getHeight() != 36))) {
                        image = getDefaultIcon(1, flag);
                    }
                } else {
                    image = getDefaultIcon(1, flag);
                }
                if (xfile != null) {
                    try {
                        xfile.close();
                        return image;
                    } catch (Exception e2) {
                        return image;
                    }
                }
                return image;
            } catch (Exception e3) {
                Image image2 = getDefaultIcon(1, flag);
                if (0 == 0) {
                    return image2;
                }
                try {
                    xfile.close();
                    return image2;
                } catch (Exception e4) {
                    return image2;
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    xfile.close();
                } catch (Exception e5) {
                }
            }
            throw th;
        }
    }

    public static void paintBack(Graphics g) {
        Graphics2D graphics2d = Graphics2D.getGraphics2D(g);
        g.drawImage(getImg_frame_title(), 0, 0, 20);
        graphics2d.drawImage(frame_left, frame_top, getImg_frame_back(), 0, 0, (frame_right - frame_left) + 1, (frame_bottom - frame_top) + 3, 0);
        g.setColor(color_border_dark);
        g.drawLine(0, frame_title_height, 0, XDisplay.height2 - 1);
        g.drawLine(0, XDisplay.height2 - 1, XDisplay.width - 1, XDisplay.height2 - 1);
        g.drawLine(XDisplay.width - 1, frame_title_height, XDisplay.width - 1, XDisplay.height2 - 1);
        g.drawLine(2, frame_title_height, 2, XDisplay.height2 - 3);
        g.drawLine(2, XDisplay.height2 - 3, XDisplay.width - 3, XDisplay.height2 - 3);
        g.drawLine(XDisplay.width - 3, frame_title_height, XDisplay.width - 3, XDisplay.height2 - 3);
        g.setColor(color_border_light);
        g.drawLine(1, frame_title_height, 1, XDisplay.height2 - 2);
        g.drawLine(1, XDisplay.height2 - 2, XDisplay.width - 2, XDisplay.height2 - 2);
        g.drawLine(XDisplay.width - 2, frame_title_height, XDisplay.width - 2, XDisplay.height2 - 2);
        graphics2d.drawImage(frame_left, frame_bottom + 4, getImg_frame_subtitle(), 0, 1, (frame_right - frame_left) + 1, button_height - 3, 0);
        g.setColor(color_border_dark);
        int i = frame_left;
        int i2 = frame_bottom;
        g.drawLine(i, i2 + 3, frame_right, i2 + 3);
    }

    public static void popupView(String s) {
        Graphics g = Toolkit.graphics;
        try {
            Image image = Toolkit.createExImage("/iconsx", "/msg_back");
            XDisplay.clear(g, null, Toolkit.dk_gray, 2);
            g.setClip(0, 0, XDisplay.width, XDisplay.height2);
            g.drawImage(image, XDisplay.width / 2, XDisplay.height2 / 2, 3);
            g.setColor(0);
            g.drawString(s, XDisplay.width / 2, (XDisplay.height2 / 2) - (Toolkit.FONT_HEIGHT / 2), 17);
            XDisplay.refresh(0, 0, XDisplay.width, XDisplay.height2);
        } catch (Exception e) {
        }
    }

    static {
        if (XDisplay.width >= 176) {
            frame_title_height = 18;
            frame_subtitle_height = 23;
            frame_name_height = 34;
            button_height = 15;
            button_width = 52;
            scroll_width = 9;
            line_space = 7;
            list_select_height = 37;
            icon_space = 5;
            icon_select_size = 48;
            icon_back_size = 29;
            list_space = 3;
        } else {
            frame_title_height = 14;
            frame_subtitle_height = 21;
            frame_name_height = 32;
            button_height = 13;
            button_width = 41;
            scroll_width = 8;
            line_space = 3;
            list_select_height = 33;
            icon_space = 3;
            icon_select_size = 44;
            icon_back_size = 27;
            list_space = 1;
        }
        frame_top = frame_title_height;
        frame_bottom = ((XDisplay.height2 - 1) - button_height) - 3;
        frame_right = (XDisplay.width - 1) - 3;
        line_width = (((XDisplay.width - 1) - 6) - scroll_width) - (line_space * 2);
        line_height = Toolkit.FONT_HEIGHT + 2;
    }
}
