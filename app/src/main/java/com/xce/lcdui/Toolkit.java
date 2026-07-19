// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.xce.lcdui;

import com.xce.util.Debug;
import java.io.IOException;
import javax.microedition.lcdui.*;

// Referenced classes of package com.xce.lcdui:
//            MIDPRes, XDisplay

public class Toolkit
{

    public Toolkit()
    {
    }

    public static Image createImage(String s)
        throws IOException
    {
        return Image.createImage("/" + iconsDir + "/iconsm" + s + ext);
    }

    public static Image createExImage(String s, String s1)
        throws IOException
    {
        return Image.createImage("/" + iconsDir + s + s1 + ext);
    }

    public static int splitString(String s, int i)
    {
        int j = s.length();
        int k = 0;
        int l;
        for(l = 0; l < j; l++)
        {
            k += DEFAULT_FONT.charWidth(s.charAt(l));
            if(k > i)
                break;
        }

        return l;
    }

    public static Image titleImg()
    {
        if(_titleImg == null)
            try
            {
                _titleImg = createImage("/title");
            }
            catch(Exception exception) { }
        return _titleImg;
    }

    public static void setTitleImg(Image image)
    {
        _titleImg = image;
    }

    public static Image buttonBackLeftImg()
    {
        if(_buttonBackLeftImg == null)
            try
            {
                _buttonBackLeftImg = createImage("/button_back_l");
            }
            catch(Exception exception) { }
        return _buttonBackLeftImg;
    }

    public static void setButtonBackLeftImg(Image image)
    {
        _buttonBackLeftImg = image;
    }

    public static Image buttonBackRightImg()
    {
        if(_buttonBackRightImg == null)
            try
            {
                _buttonBackRightImg = createImage("/button_back_r");
            }
            catch(Exception exception) { }
        return _buttonBackRightImg;
    }

    public static void setButtonBackRightImg(Image image)
    {
        _buttonBackRightImg = image;
    }

    public static Image screenIconImg()
    {
        if(_screenIconImg == null)
            try
            {
                _screenIconImg = createImage("/icon_screen");
            }
            catch(Exception exception) { }
        return _screenIconImg;
    }

    public static void setScreenIconImg(Image image)
    {
        _screenIconImg = image;
    }

    public static Image backIconImg()
    {
        if(_backIconImg == null)
            try
            {
                _backIconImg = createImage("/icon_back");
            }
            catch(Exception exception) { }
        return _backIconImg;
    }

    public static void setBackIconImg(Image image)
    {
        _backIconImg = image;
    }

    public static Image cancelIconImg()
    {
        if(_cancelIconImg == null)
            try
            {
                _cancelIconImg = createImage("/icon_cancel");
            }
            catch(Exception exception) { }
        return _cancelIconImg;
    }

    public static void setCancelIconImg(Image image)
    {
        _cancelIconImg = image;
    }

    public static Image helpIconImg()
    {
        if(_helpIconImg == null)
            try
            {
                _helpIconImg = createImage("/icon_help");
            }
            catch(Exception exception) { }
        return _helpIconImg;
    }

    public static void setHelpIconImg(Image image)
    {
        _helpIconImg = image;
    }

    public static Image okIconImg()
    {
        if(_okIconImg == null)
            try
            {
                _okIconImg = createImage("/icon_ok");
            }
            catch(Exception exception) { }
        return _okIconImg;
    }

    public static void setOkIconImg(Image image)
    {
        _okIconImg = image;
    }

    public static Image stopIconImg()
    {
        if(_stopIconImg == null)
            try
            {
                _stopIconImg = createImage("/icon_stop");
            }
            catch(Exception exception) { }
        return _stopIconImg;
    }

    public static void setStopIconImg(Image image)
    {
        _stopIconImg = image;
    }

    public static Image exitIconImg()
    {
        if(_exitIconImg == null)
            try
            {
                _exitIconImg = createImage("/icon_exit");
            }
            catch(Exception exception) { }
        return _exitIconImg;
    }

    public static void setExitIconImg(Image image)
    {
        _exitIconImg = image;
    }

    public static Image itemIconImg()
    {
        if(_itemIconImg == null)
            try
            {
                _itemIconImg = createImage("/icon_item");
            }
            catch(Exception exception) { }
        return _itemIconImg;
    }

    public static void setItemIconImg(Image image)
    {
        _itemIconImg = image;
    }

    public static Image menuIconImg()
    {
        if(_menuIconImg == null)
            try
            {
                _menuIconImg = createImage("/icon_menu");
            }
            catch(Exception exception) { }
        return _menuIconImg;
    }

    public static void setMenuIconImg(Image image)
    {
        _menuIconImg = image;
    }

    public static Image ueimImg()
    {
        if(_ueimImg == null)
            try
            {
                _ueimImg = createImage("/im_ue");
            }
            catch(Exception exception) { }
        return _ueimImg;
    }

    public static void setUEimImg(Image image)
    {
        _ueimImg = image;
    }

    public static Image leimImg()
    {
        if(_leimImg == null)
            try
            {
                _leimImg = createImage("/im_le");
            }
            catch(Exception exception) { }
        return _leimImg;
    }

    public static void setLEimImg(Image image)
    {
        _leimImg = image;
    }

    public static Image kimImg()
    {
        if(_kimImg == null)
            try
            {
                _kimImg = createImage("/im_k");
            }
            catch(Exception exception) { }
        return _kimImg;
    }

    public static void setKimImg(Image image)
    {
        _kimImg = image;
    }

    public static Image simImg()
    {
        if(_simImg == null)
            try
            {
                _simImg = createImage("/im_s");
            }
            catch(Exception exception) { }
        return _simImg;
    }

    public static void setSimImg(Image image)
    {
        _simImg = image;
    }

    public static Image nimImg()
    {
        if(_nimImg == null)
            try
            {
                _nimImg = createImage("/im_n");
            }
            catch(Exception exception) { }
        return _nimImg;
    }

    public static void setNimImg(Image image)
    {
        _nimImg = image;
    }

    public static Image himImg()
    {
        if(_himImg == null)
            try
            {
                _himImg = createImage("/im_h");
            }
            catch(Exception exception) { }
        return _himImg;
    }

    public static void setHimImg(Image image)
    {
        _himImg = image;
    }

    public static Image imHintImg()
    {
        if(_imHintImg == null)
            try
            {
                _imHintImg = createImage("/im_hint");
            }
            catch(Exception exception) { }
        return _imHintImg;
    }

    public static void setIMHintImg(Image image)
    {
        _imHintImg = image;
    }

    public static Image sExclusive()
    {
        if(_sExclusive == null)
            try
            {
                _sExclusive = createImage("/circle-1");
            }
            catch(Exception exception) { }
        return _sExclusive;
    }

    public static void setSExclusive(Image image)
    {
        _sExclusive = image;
    }

    public static Image uExclusive()
    {
        if(_uExclusive == null)
            try
            {
                _uExclusive = createImage("/circle-11");
            }
            catch(Exception exception) { }
        return _uExclusive;
    }

    public static void setUExclusive(Image image)
    {
        _uExclusive = image;
    }

    public static Image sMultiple()
    {
        if(_sMultiple == null)
            try
            {
                _sMultiple = createImage("/rectangle-1");
            }
            catch(Exception exception) { }
        return _sMultiple;
    }

    public static void setSMultiple(Image image)
    {
        _sMultiple = image;
    }

    public static Image uMultiple()
    {
        if(_uMultiple == null)
            try
            {
                _uMultiple = createImage("/rectangle-11");
            }
            catch(Exception exception) { }
        return _uMultiple;
    }

    public static void setUMultiple(Image image)
    {
        _uMultiple = image;
    }

    public static Image sBackImg()
    {
        if(_sBackImg == null)
            try
            {
                _sBackImg = createImage("/s-back");
            }
            catch(Exception exception) { }
        return _sBackImg;
    }

    public static void setSBackImg(Image image)
    {
        _sBackImg = image;
    }

    public static Image gBackImg()
    {
        if(_gBackImg == null)
            try
            {
                _gBackImg = createImage("/g-1");
            }
            catch(Exception exception) { }
        return _gBackImg;
    }

    public static void setGBackImg(Image image)
    {
        _gBackImg = image;
    }

    public static Image gForeImg()
    {
        if(_gForeImg == null)
            try
            {
                _gForeImg = createImage("/g-11");
            }
            catch(Exception exception) { }
        return _gForeImg;
    }

    public static void setGForeImg(Image image)
    {
        _gForeImg = image;
    }

    public static Image appImg()
    {
        if(_appImg == null)
            try
            {
                _appImg = createImage("/app");
            }
            catch(Exception exception) { }
        return _appImg;
    }

    public static Image scrollImg()
    {
        if(_scrollImg == null)
            try
            {
                _scrollImg = createImage("/scrollbar");
            }
            catch(Exception exception) { }
        return _scrollImg;
    }

    public static void setScrollImg(Image image)
    {
        _scrollImg = image;
    }

    public static Image castleImg()
    {
        if(_castle == null)
            try
            {
                _castle = createImage("/castle");
            }
            catch(Exception exception) { }
        return _castle;
    }

    public static void paintPopup(String s, String s1)
    {
        paintPopup(s, s1, true);
    }

    public static void paintPopup(String s, String s1, boolean flag)
    {
    }

    public static boolean isHebrew(char c)
    {
        return c >= '\u05D0' && c <= '\u05EA';
    }

    public static boolean isHebrew(char ac[])
    {
        return isHebrew(ac, ac.length);
    }

    public static boolean isHebrew(char ac[], int i)
    {
        boolean flag = false;
        for(int j = 0; j < i; j++)
        {
            if(!isHebrew(ac[j]))
                continue;
            flag = true;
            break;
        }

        return flag;
    }

    public static int MAX_CHARWIDTH;
    public static boolean IS_KOREAN;
    public static boolean IS_HEBREW;
    public static final Font DEFAULT_FONT;
    public static final int FONT_HEIGHT;
    public static final int FONT_GAP;
    public static int IMG_HEIGHT;
    public static final int BLACK = 0;
    public static final int DK_GRAY = 84;
    public static final int LT_GRAY = 171;
    public static final int WHITE = 255;
    public static final int black = 0;
    public static final int dk_gray = 0x545454;
    public static final int lt_gray = 0xababab;
    public static final int white = 0xffffff;
    public static int selected;
    private static Image _titleImg;
    private static Image _buttonBackLeftImg;
    private static Image _buttonBackRightImg;
    private static Image _screenIconImg;
    private static Image _backIconImg;
    private static Image _cancelIconImg;
    private static Image _helpIconImg;
    private static Image _okIconImg;
    private static Image _stopIconImg;
    private static Image _exitIconImg;
    private static Image _itemIconImg;
    private static Image _menuIconImg;
    private static Image _ueimImg;
    private static Image _leimImg;
    private static Image _kimImg;
    private static Image _simImg;
    private static Image _nimImg;
    private static Image _himImg;
    private static Image _imHintImg;
    private static Image _sExclusive;
    private static Image _uExclusive;
    private static Image _sMultiple;
    private static Image _uMultiple;
    private static Image _sBackImg;
    private static Image _gBackImg;
    private static Image _gForeImg;
    private static Image _appImg;
    private static Image _scrollImg;
    private static Image _castle;
    public static String ext = System.getProperty("graphics.ext");
    public static final Graphics graphics = new Graphics(null);
    public static String iconsDir;
    public static MIDPRes MIDP_RES;

    static 
    {
        DEFAULT_FONT = Font.getDefaultFont();
        FONT_HEIGHT = DEFAULT_FONT.getHeight();
        FONT_GAP = FONT_HEIGHT > 12 ? 1 : 2;
        if(XDisplay.is_color)
            selected = 0x6d6dff;
        else
            selected = 0;
        String s = System.getProperty("microedition.locale");
        IMG_HEIGHT = FONT_HEIGHT >= 14 ? FONT_HEIGHT : 14;
        try
        {
            if(s.equals("ko-KR"))
            {
                IS_KOREAN = true;
                IS_HEBREW = false;
                MAX_CHARWIDTH = DEFAULT_FONT.charWidth('\uAE40');
            } else
            if(s.equals("iw-IL"))
            {
                IS_KOREAN = false;
                IS_HEBREW = true;
                MAX_CHARWIDTH = DEFAULT_FONT.charWidth('A');
            } else
            {
                IS_KOREAN = false;
                IS_HEBREW = false;
                MAX_CHARWIDTH = DEFAULT_FONT.charWidth('A');
            }
            int i = s.indexOf('-');
            if(i != -1)
                s = s.substring(0, i);
            String s1 = System.getProperty("m.COLOR");
            if(s1.equals("2"))
                s1 = "4g";
            else
            if(s1.equals("4"))
                s1 = "256c";
            else
            if(s1.equals("7"))
            {
                s1 = "64kc";
            } else
            {
                Debug.debugOut("m.COLOR=" + s1 + " : wrong");
                s1 = "";
            }
            if(System.getProperty("microedition.locale").equals("iw-IL"))
            {
                iconsDir = (new StringBuffer(23)).append("iw_IL_").append("image_").append(System.getProperty("m.LCD_PIXEL")).append("_" + s1).toString();
                Image image = kimImg();
                if(image == null)
                {
                    Debug.debugOut(iconsDir + " not exist");
                    iconsDir = "image_";
                }
            } else
            {
                iconsDir = (new StringBuffer(14)).append("image_").append(System.getProperty("m.LCD_PIXEL").substring(0, 3)).toString();
                Image image1 = kimImg();
                if(image1 == null)
                {
                    Debug.debugOut(iconsDir + " not exist");
                    iconsDir = "";
                }
            }
        }
        catch(Exception exception) { }
        try
        {
            if(s.equals("iw"))
                MIDP_RES = (MIDPRes)Class.forName("com.xce.lcdui.MIDPRes_en").newInstance();
            else
                MIDP_RES = (MIDPRes)Class.forName("com.xce.lcdui.MIDPRes_" + s).newInstance();
        }
        catch(Exception exception1)
        {
            try
            {
                MIDP_RES = (MIDPRes)Class.forName("com.xce.lcdui.MIDPRes_en").newInstance();
            }
            catch(Exception exception2) { }
        }
    }
}

