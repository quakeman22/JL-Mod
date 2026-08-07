package javax.microedition.lcdui.keyboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import com.caverock.androidsvg.SVG;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.graphics.CanvasWrapper;
import javax.microedition.lcdui.overlay.Overlay;
import javax.microedition.shell.MicroActivity;
import javax.microedition.util.ContextHolder;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.config.ProfileModel;
import ru.playsoftware.j2meloader.config.ProfilesManager;

/* JADX INFO: loaded from: classes3.dex */
public class VirtualKeyboard implements Overlay, Runnable {
    private static final String ARROW_DOWN = "↓";
    private static final String ARROW_DOWN_LEFT = "↙";
    private static final String ARROW_DOWN_RIGHT = "↘";
    private static final String ARROW_LEFT = "←";
    private static final String ARROW_RIGHT = "→";
    private static final String ARROW_UP = "↑";
    private static final String ARROW_UP_LEFT = "↖";
    private static final String ARROW_UP_RIGHT = "↗";
    private static final int FEEDBACK_DURATION = 50;
    private static final int KEYBOARD_SIZE = 28;
    private static final int KEY_A = 25;
    private static final int KEY_B = 26;
    private static final int KEY_C = 15;
    private static final int KEY_D = 14;
    private static final int KEY_DOWN = 22;
    private static final int KEY_DOWN_LEFT = 21;
    private static final int KEY_DOWN_RIGHT = 23;
    private static final int KEY_FIRE = 24;
    private static final int KEY_LEFT = 19;
    private static final int KEY_MENU = 27;
    private static final int KEY_NUM0 = 9;
    private static final int KEY_NUM1 = 0;
    private static final int KEY_NUM2 = 1;
    private static final int KEY_NUM3 = 2;
    private static final int KEY_NUM4 = 3;
    private static final int KEY_NUM5 = 4;
    private static final int KEY_NUM6 = 5;
    private static final int KEY_NUM7 = 6;
    private static final int KEY_NUM8 = 7;
    private static final int KEY_NUM9 = 8;
    private static final int KEY_POUND = 11;
    private static final int KEY_RIGHT = 20;
    private static final int KEY_SOFT_LEFT = 12;
    private static final int KEY_SOFT_RIGHT = 13;
    private static final int KEY_STAR = 10;
    private static final int KEY_UP = 17;
    private static final int KEY_UP_LEFT = 16;
    private static final int KEY_UP_RIGHT = 18;
    public static final int LAYOUT_COLORS = 2;
    public static final int LAYOUT_EOF = -1;
    public static final int LAYOUT_HIDE = 5;
    public static final int LAYOUT_JOYSTICK = 4;
    public static final int LAYOUT_KEYS = 0;
    public static final int LAYOUT_SCALES = 1;
    private static final int LAYOUT_SIGNATURE = 1447775232;
    public static final int LAYOUT_TYPE = 3;
    private static final int LAYOUT_VERSION = 5;
    private static final int ORIENTATION_LANDSCAPE = 1;
    private static final int ORIENTATION_PORTRAIT = 0;
    private static final float PHONE_KEY_ROWS = 5.0f;
    private static final float PHONE_KEY_SCALE_X = 2.0f;
    private static final float PHONE_KEY_SCALE_Y = 0.75f;
    private static final int SCREEN = -1;
    private static final int SHAPE_OVAL = 0;
    private static final int SHAPE_RECT = 1;
    public static final int SHAPE_ROUND_RECT = 2;
    private static final int TYPE_ARROWS = 6;
    private static final int TYPE_ARR_NUM = 4;
    public static final int TYPE_CUSTOM = 0;
    private static final int TYPE_NUMBERS = 5;
    private static final int TYPE_NUM_ARR = 3;
    private static final int TYPE_PHONE = 1;
    private static final int TYPE_PHONE_ARROWS = 2;
    private int editedIndex;
    private final Handler handler;
    private boolean initialResizeDone;
    private Bitmap joySkinBitmap;
    private NinePatchSkin joySkinNinePatch;
    private Picture joySkinPicture;
    private Bitmap joyThumbBitmap;
    private NinePatchSkin joyThumbNinePatch;
    private Picture joyThumbPicture;
    private int layoutVariant;
    private boolean obscuresVirtualScreen;
    private float offsetX;
    private float offsetY;
    private View overlayView;
    private float pinchStartDistX;
    private float pinchStartDistY;
    private float prevScaleX;
    private float prevScaleY;
    private final File saveFile;
    private RectF screen;
    private final ProfileModel settings;
    private float[] skinAspectRatios;
    private Bitmap skinBitmap;
    private Bitmap[] skinKeyBitmaps;
    private Bitmap[] skinKeyIconBitmaps;
    private Picture[] skinKeyIconPictures;
    private NinePatchSkin[] skinKeyNinePatches;
    private Picture[] skinKeyPictures;
    private Bitmap[] skinKeyPressedBitmaps;
    private NinePatchSkin[] skinKeyPressedNinePatches;
    private Picture[] skinKeyPressedPictures;
    private NinePatchSkin skinNinePatch;
    private Picture skinPicture;
    private Bitmap skinPressedBitmap;
    private NinePatchSkin skinPressedNinePatch;
    private Picture skinPressedPicture;
    private float snapRadius;
    private Canvas target;
    private static final String TAG = VirtualKeyboard.class.getSimpleName();
    private static final long[] REPEAT_INTERVALS = {200, 400, 128, 128, 128, 128, 128};
    public static final int[][] JOY_PRESETS = {new int[]{20, -1, 17, -1, 19, -1, 22, -1}, new int[]{20, 18, 17, 16, 19, 21, 22, 23}, new int[]{20, 2, 17, 0, 19, 6, 22, 8}, new int[]{5, 2, 1, 0, 3, 6, 7, 8}, new int[]{20, 17, 17, 17, 19, 22, 22, 22}};
    private final float[] keyScales = new float[56];
    private final int[][] keyScaleGroups = {new int[]{16, 17, 18, 19, 20, 21, 22, 23}, new int[]{12, 13}, new int[]{25, 26, 15, 14}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, new int[]{24}, new int[]{27}};
    private final VirtualKey[] keypad = new VirtualKey[28];
    private final VirtualKey[] associatedKeys = new VirtualKey[10];
    private final int[] snapStack = new int[28];
    private int currentOrientation = 0;
    private final RectF virtualScreen = new RectF();
    private boolean visible = true;
    private int layoutEditMode = -1;
    private boolean layoutLocked = true;
    private boolean showGrid = true;
    private boolean snapToGrid = true;
    private float gridSize = 24.0f;
    private int dragPointer = -1;
    private int pinchPointer = -1;
    private final float[] ptrX = new float[10];
    private final float[] ptrY = new float[10];
    private float keySize = Math.min(ContextHolder.getDisplayWidth(), ContextHolder.getDisplayHeight()) / 6.0f;
    private final Joystick joystick = new Joystick();

    public VirtualKeyboard(ProfileModel settings) throws IllegalAccessException, InvocationTargetException {
        this.settings = settings;
        this.saveFile = new File(settings.dir + Config.MIDLET_KEY_LAYOUT_FILE);
        for (int i = 0; i < 9; i++) {
            this.keypad[i] = new VirtualKey(i + 49, Integer.toString(i + 1));
        }
        this.keypad[9] = new VirtualKey(48, "0");
        this.keypad[10] = new VirtualKey(42, "*");
        this.keypad[11] = new VirtualKey(35, "#");
        this.keypad[12] = new VirtualKey(-6, "L");
        this.keypad[13] = new VirtualKey(-7, "R");
        this.keypad[25] = new VirtualKey(-13, "A");
        this.keypad[26] = new VirtualKey(-14, "B");
        this.keypad[15] = new VirtualKey(-11, "C");
        this.keypad[14] = new VirtualKey(-10, "D");
        this.keypad[16] = new DualKey(-1, -3, ARROW_UP_LEFT);
        this.keypad[17] = new VirtualKey(-1, ARROW_UP);
        this.keypad[18] = new DualKey(-1, -4, ARROW_UP_RIGHT);
        this.keypad[19] = new VirtualKey(-3, ARROW_LEFT);
        this.keypad[20] = new VirtualKey(-4, ARROW_RIGHT);
        this.keypad[21] = new DualKey(-2, -3, ARROW_DOWN_LEFT);
        this.keypad[22] = new VirtualKey(-2, ARROW_DOWN);
        this.keypad[23] = new DualKey(-2, -4, ARROW_DOWN_RIGHT);
        this.keypad[24] = new VirtualKey(-5, "F");
        this.keypad[27] = new MenuKey();
        for (int i2 = 0; i2 < 28; i2++) {
            this.keypad[i2].keyIndex = i2;
        }
        Arrays.fill(this.keyScales, 1.0f);
        this.layoutVariant = readLayoutType();
        if (this.layoutVariant == -1) {
            this.layoutVariant = settings.vkType;
            if (this.layoutVariant == 0) {
                this.layoutVariant = 3;
            }
        }
        resetLayout(this.layoutVariant);
        if (this.layoutVariant == 0) {
            readLayout();
        }
        HandlerThread thread = new HandlerThread("MidletVirtualKeyboard");
        thread.start();
        this.handler = new Handler(thread.getLooper());
        loadSkin();
    }

    private void loadSkin() {
        this.skinBitmap = null;
        this.skinPressedBitmap = null;
        this.skinNinePatch = null;
        this.skinPressedNinePatch = null;
        this.skinKeyBitmaps = null;
        this.skinKeyNinePatches = null;
        this.skinKeyPressedBitmaps = null;
        this.skinKeyPressedNinePatches = null;
        this.skinKeyIconBitmaps = null;
        this.skinKeyIconPictures = null;
        this.skinPicture = null;
        this.skinPressedPicture = null;
        this.skinKeyPictures = null;
        this.skinKeyPressedPictures = null;
        String skinPath = this.settings.vkSkinPath;
        if (skinPath != null && !skinPath.isEmpty()) {
            String fullPath = Config.getEmulatorDir() + Config.SKINS_DIR + skinPath;
            if (skinPath.endsWith(".9.png")) {
                this.skinNinePatch = NinePatchSkin.load(fullPath);
            }
            if (this.skinNinePatch == null) {
                if (skinPath.endsWith(".svg")) {
                    this.skinPicture = loadSvgPicture(fullPath);
                }
                if (this.skinPicture == null) {
                    this.skinBitmap = BitmapFactory.decodeFile(fullPath);
                }
            }
        }
        String skinPressedPath = this.settings.vkSkinPressedPath;
        if (skinPressedPath != null && !skinPressedPath.isEmpty()) {
            String fullPath2 = Config.getEmulatorDir() + Config.SKINS_DIR + skinPressedPath;
            if (skinPressedPath.endsWith(".9.png")) {
                this.skinPressedNinePatch = NinePatchSkin.load(fullPath2);
            }
            if (this.skinPressedNinePatch == null) {
                if (skinPressedPath.endsWith(".svg")) {
                    this.skinPressedPicture = loadSvgPicture(fullPath2);
                }
                if (this.skinPressedPicture == null) {
                    this.skinPressedBitmap = BitmapFactory.decodeFile(fullPath2);
                }
            }
        }
        String[] keys = this.settings.vkSkinKeys;
        boolean hasOldKeys = keys != null && (keys.length == 28 || keys.length == 27);
        if (hasOldKeys) {
            int loadedKeys = keys.length;
            this.skinKeyBitmaps = new Bitmap[28];
            this.skinKeyNinePatches = new NinePatchSkin[28];
            this.skinKeyPictures = new Picture[28];
            this.skinKeyPressedBitmaps = new Bitmap[28];
            this.skinKeyPressedNinePatches = new NinePatchSkin[28];
            this.skinKeyPressedPictures = new Picture[28];
            for (int i = 0; i < loadedKeys; i++) {
                String path = keys[i];
                if (path != null && !path.isEmpty()) {
                    String fullPath3 = Config.getEmulatorDir() + Config.SKINS_DIR + path;
                    if (path.endsWith(".9.png")) {
                        this.skinKeyNinePatches[i] = NinePatchSkin.load(fullPath3);
                    }
                    if (this.skinKeyNinePatches[i] == null) {
                        if (path.endsWith(".svg")) {
                            this.skinKeyPictures[i] = loadSvgPicture(fullPath3);
                        }
                        if (this.skinKeyPictures[i] == null) {
                            this.skinKeyBitmaps[i] = BitmapFactory.decodeFile(fullPath3);
                        }
                    }
                }
            }
        }
        String[] keysPressed = this.settings.vkSkinKeysPressed;
        boolean hasOldPressed = keysPressed != null && (keysPressed.length == 28 || keysPressed.length == 27);
        if (hasOldPressed) {
            int loadedKeys2 = keysPressed.length;
            if (this.skinKeyPressedBitmaps == null) {
                this.skinKeyPressedBitmaps = new Bitmap[28];
                this.skinKeyPressedNinePatches = new NinePatchSkin[28];
                this.skinKeyPressedPictures = new Picture[28];
            }
            for (int i2 = 0; i2 < loadedKeys2; i2++) {
                String path2 = keysPressed[i2];
                if (path2 != null && !path2.isEmpty()) {
                    String fullPath4 = Config.getEmulatorDir() + Config.SKINS_DIR + path2;
                    if (path2.endsWith(".9.png")) {
                        this.skinKeyPressedNinePatches[i2] = NinePatchSkin.load(fullPath4);
                    }
                    if (this.skinKeyPressedNinePatches[i2] == null) {
                        if (path2.endsWith(".svg")) {
                            this.skinKeyPressedPictures[i2] = loadSvgPicture(fullPath4);
                        }
                        if (this.skinKeyPressedPictures[i2] == null) {
                            this.skinKeyPressedBitmaps[i2] = BitmapFactory.decodeFile(fullPath4);
                        }
                    }
                }
            }
        }
        String[] icons = this.settings.vkSkinIcons;
        boolean hasOldIcons = icons != null && (icons.length == 28 || icons.length == 27);
        if (hasOldIcons) {
            int loadedKeys3 = icons.length;
            this.skinKeyIconBitmaps = new Bitmap[28];
            this.skinKeyIconPictures = new Picture[28];
            for (int i3 = 0; i3 < loadedKeys3; i3++) {
                String path3 = icons[i3];
                if (path3 != null && !path3.isEmpty()) {
                    String fullPath5 = Config.getEmulatorDir() + Config.SKINS_DIR + path3;
                    if (path3.endsWith(".svg")) {
                        this.skinKeyIconPictures[i3] = loadSvgPicture(fullPath5);
                    } else {
                        this.skinKeyIconBitmaps[i3] = BitmapFactory.decodeFile(fullPath5);
                    }
                }
            }
        }
        this.joySkinBitmap = null;
        this.joySkinNinePatch = null;
        this.joySkinPicture = null;
        this.joyThumbBitmap = null;
        this.joyThumbNinePatch = null;
        this.joyThumbPicture = null;
        String joySkin = this.settings.joySkinPath;
        if (joySkin != null && !joySkin.isEmpty()) {
            String fullPath6 = Config.getEmulatorDir() + Config.SKINS_DIR + joySkin;
            if (joySkin.endsWith(".9.png")) {
                this.joySkinNinePatch = NinePatchSkin.load(fullPath6);
            }
            if (this.joySkinNinePatch == null) {
                if (joySkin.endsWith(".svg")) {
                    this.joySkinPicture = loadSvgPicture(fullPath6);
                }
                if (this.joySkinPicture == null) {
                    this.joySkinBitmap = BitmapFactory.decodeFile(fullPath6);
                }
            }
        }
        String joyThumb = this.settings.joyThumbSkinPath;
        if (joyThumb != null && !joyThumb.isEmpty()) {
            String fullPath7 = Config.getEmulatorDir() + Config.SKINS_DIR + joyThumb;
            if (joyThumb.endsWith(".9.png")) {
                this.joyThumbNinePatch = NinePatchSkin.load(fullPath7);
            }
            if (this.joyThumbNinePatch == null) {
                if (joyThumb.endsWith(".svg")) {
                    this.joyThumbPicture = loadSvgPicture(fullPath7);
                }
                if (this.joyThumbPicture == null) {
                    this.joyThumbBitmap = BitmapFactory.decodeFile(fullPath7);
                }
            }
        }
        this.skinAspectRatios = new float[28];
        for (int i4 = 0; i4 < 28; i4++) {
            float natW = -1.0f;
            float natH = -1.0f;
            if (this.skinKeyNinePatches != null && this.skinKeyNinePatches[i4] != null) {
                natW = this.skinKeyNinePatches[i4].srcWidth;
                natH = this.skinKeyNinePatches[i4].srcHeight;
            } else if (this.skinKeyPictures != null && this.skinKeyPictures[i4] != null) {
                natW = this.skinKeyPictures[i4].getWidth();
                natH = this.skinKeyPictures[i4].getHeight();
            } else if (this.skinKeyBitmaps != null && this.skinKeyBitmaps[i4] != null) {
                natW = this.skinKeyBitmaps[i4].getWidth();
                natH = this.skinKeyBitmaps[i4].getHeight();
            }
            float[] fArr = this.skinAspectRatios;
            float f = 0.0f;
            if (natW > 0.0f && natH > 0.0f) {
                f = natW / natH;
            }
            fArr[i4] = f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void drawNinePatchSkin(CanvasWrapper g, NinePatchSkin np, RectF dst) {
        int srcW = np.srcWidth;
        int srcH = np.srcHeight;
        int lf = np.leftFixed;
        int rf = np.rightFixed;
        int tf = np.topFixed;
        int bf = np.bottomFixed;
        float dstW = dst.width();
        float dstH = dst.height();
        float f = (dstW - lf) - rf;
        float f2 = (dstH - tf) - bf;
        Rect[] srcRects = {new Rect(0, 0, lf, tf), new Rect(lf, 0, srcW - rf, tf), new Rect(srcW - rf, 0, srcW, tf), new Rect(0, tf, lf, srcH - bf), new Rect(lf, tf, srcW - rf, srcH - bf), new Rect(srcW - rf, tf, srcW, srcH - bf), new Rect(0, srcH - bf, lf, srcH), new Rect(lf, srcH - bf, srcW - rf, srcH), new Rect(srcW - rf, srcH - bf, srcW, srcH)};
        float f3 = dst.left;
        float f4 = dst.top;
        float f5 = dst.left + lf;
        float dstW2 = dst.top;
        float dstH2 = tf;
        RectF[] dstRects = {new RectF(f3, f4, f5, dstW2 + tf), new RectF(dst.left + lf, dst.top, dst.right - rf, dst.top + dstH2), new RectF(dst.right - rf, dst.top, dst.right, dst.top + tf), new RectF(dst.left, dst.top + tf, dst.left + lf, dst.bottom - bf), new RectF(dst.left + lf, dst.top + tf, dst.right - rf, dst.bottom - bf), new RectF(dst.right - rf, dst.top + tf, dst.right, dst.bottom - bf), new RectF(dst.left, dst.bottom - bf, dst.left + lf, dst.bottom), new RectF(dst.left + lf, dst.bottom - bf, dst.right - rf, dst.bottom), new RectF(dst.right - rf, dst.bottom - bf, dst.right, dst.bottom)};
        for (int i = 0; i < 9; i++) {
            g.drawBitmap(np.bitmap, srcRects[i], dstRects[i]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void drawIconCentered(CanvasWrapper g, Bitmap icon, RectF rect, float iconScale) {
        float iw = icon.getWidth();
        float ih = icon.getHeight();
        float scale = Math.min(rect.width() / iw, rect.height() / ih) * 0.8f * iconScale;
        float w = iw * scale;
        float h = ih * scale;
        float left = rect.centerX() - (w / PHONE_KEY_SCALE_X);
        float top = rect.centerY() - (h / PHONE_KEY_SCALE_X);
        g.drawBitmap(icon, new RectF(left, top, left + w, top + h));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void drawSvgCentered(CanvasWrapper g, Picture picture, RectF rect, float iconScale) {
        float pw = picture.getWidth();
        float ph = picture.getHeight();
        if (pw <= 0.0f || ph <= 0.0f) {
            return;
        }
        float scale = Math.min(rect.width() / pw, rect.height() / ph) * 0.8f * iconScale;
        float w = pw * scale;
        float h = ph * scale;
        float left = rect.centerX() - (w / PHONE_KEY_SCALE_X);
        float top = rect.centerY() - (h / PHONE_KEY_SCALE_X);
        android.graphics.Canvas canvas = g.getCanvas();
        int alpha = g.getAlpha();
        if (alpha < 255) {
            Paint alphaPaint = new Paint();
            alphaPaint.setAlpha(alpha);
            canvas.saveLayer(left, top, left + w, top + h, alphaPaint);
        }
        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        canvas.drawPicture(picture);
        canvas.restore();
        if (alpha < 255) {
            canvas.restore();
        }
    }

    private static Picture loadSvgPicture(String fullPath) {
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(fullPath);
                SVG svg = SVG.getFromInputStream(fis);
                Picture pictureRenderToPicture = svg.renderToPicture();
                try {
                    fis.close();
                } catch (Exception e) {
                }
                return pictureRenderToPicture;
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e2) {
                    }
                }
                Log.e(TAG, "Failed to load SVG: " + fullPath, th);
                return null;
            }
        } catch (Exception e3) {
            Log.w(TAG, "Failed to load SVG: " + fullPath, e3);
            if (fis == null) {
                return null;
            }
            try {
                fis.close();
                return null;
            } catch (Exception e4) {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void drawSvgSkin(CanvasWrapper g, Picture picture, RectF rect) {
        float pw = picture.getWidth();
        float ph = picture.getHeight();
        if (pw <= 0.0f || ph <= 0.0f) {
            return;
        }
        android.graphics.Canvas canvas = g.getCanvas();
        int alpha = g.getAlpha();
        if (alpha < 255) {
            Paint alphaPaint = new Paint();
            alphaPaint.setAlpha(alpha);
            canvas.saveLayer(rect, alphaPaint);
        }
        canvas.save();
        canvas.translate(rect.left, rect.top);
        canvas.scale(rect.width() / pw, rect.height() / ph);
        canvas.drawPicture(picture);
        canvas.restore();
        if (alpha < 255) {
            canvas.restore();
        }
    }

    private static void drawSvgFit(CanvasWrapper g, Picture picture, RectF rect) {
        float pw = picture.getWidth();
        float ph = picture.getHeight();
        if (pw <= 0.0f || ph <= 0.0f) {
            return;
        }
        android.graphics.Canvas canvas = g.getCanvas();
        int alpha = g.getAlpha();
        if (alpha < 255) {
            Paint alphaPaint = new Paint();
            alphaPaint.setAlpha(alpha);
            canvas.saveLayer(rect, alphaPaint);
        }
        float scale = Math.min(rect.width() / pw, rect.height() / ph);
        float w = pw * scale;
        float h = ph * scale;
        canvas.save();
        canvas.translate(rect.centerX() - (w / PHONE_KEY_SCALE_X), rect.centerY() - (h / PHONE_KEY_SCALE_X));
        canvas.scale(scale, scale);
        canvas.drawPicture(picture);
        canvas.restore();
        if (alpha < 255) {
            canvas.restore();
        }
    }

    public void onLayoutChanged(int variant) {
        if (variant == 0 && isPhone()) {
            float min = this.screen.width();
            float max = this.screen.height();
            if (min > max) {
                max = min;
                min = max;
            }
            float oldSize = min / 6.0f;
            float newSize = Math.min(oldSize, max / 12.0f);
            float s = oldSize / newSize;
            for (int i = 0; i < this.keyScales.length; i++) {
                float[] fArr = this.keyScales;
                fArr[i] = fArr[i] * s;
            }
        }
        this.layoutVariant = variant;
        saveLayout();
        if (this.target != null && this.target.isShown()) {
            this.target.updateSize();
        }
    }

    private void resetLayout(int variant) {
        switch (variant) {
            case 1:
                int j = 0;
                int len = this.keyScales.length;
                while (j < len) {
                    int j2 = j + 1;
                    this.keyScales[j] = 2.0f;
                    this.keyScales[j2] = 0.75f;
                    j = j2 + 1;
                }
                setSnap(9, -1, RectSnap.INT_SOUTH, true);
                setSnap(10, 9, RectSnap.EXT_WEST, true);
                setSnap(11, 9, RectSnap.EXT_EAST, true);
                setSnap(6, 10, 65540, true);
                setSnap(7, 6, RectSnap.EXT_EAST, true);
                setSnap(8, 7, RectSnap.EXT_EAST, true);
                setSnap(3, 6, 65540, true);
                setSnap(4, 3, RectSnap.EXT_EAST, true);
                setSnap(5, 4, RectSnap.EXT_EAST, true);
                setSnap(0, 3, 65540, true);
                setSnap(1, 0, RectSnap.EXT_EAST, true);
                setSnap(2, 1, RectSnap.EXT_EAST, true);
                setSnap(12, 0, 65540, true);
                setSnap(24, 1, 65540, true);
                setSnap(13, 2, 65540, true);
                setSnap(25, -1, RectSnap.INT_NORTHWEST, false);
                setSnap(26, -1, RectSnap.INT_NORTHEAST, false);
                setSnap(15, 25, RectSnap.EXT_SOUTH, false);
                setSnap(14, 26, RectSnap.EXT_SOUTH, false);
                setSnap(16, 15, RectSnap.EXT_SOUTH, false);
                setSnap(17, 15, RectSnap.EXT_SOUTHEAST, false);
                setSnap(18, 14, RectSnap.EXT_SOUTH, false);
                setSnap(19, 16, RectSnap.EXT_SOUTH, false);
                setSnap(27, 17, RectSnap.EXT_SOUTH, true);
                setSnap(20, 18, RectSnap.EXT_SOUTH, false);
                setSnap(21, 19, RectSnap.EXT_SOUTH, false);
                setSnap(22, 27, RectSnap.EXT_SOUTH, false);
                setSnap(23, 20, RectSnap.EXT_SOUTH, false);
                break;
            case 2:
                int j3 = 0;
                int len2 = this.keyScales.length;
                while (j3 < len2) {
                    int j4 = j3 + 1;
                    this.keyScales[j3] = 2.0f;
                    this.keyScales[j4] = 0.75f;
                    j3 = j4 + 1;
                }
                setSnap(9, -1, RectSnap.INT_SOUTH, true);
                setSnap(10, 9, RectSnap.EXT_WEST, true);
                setSnap(11, 9, RectSnap.EXT_EAST, true);
                setSnap(6, 10, 65540, true);
                setSnap(22, 6, RectSnap.EXT_EAST, true);
                setSnap(8, 22, RectSnap.EXT_EAST, true);
                setSnap(19, 6, 65540, true);
                setSnap(24, 19, RectSnap.EXT_EAST, true);
                setSnap(20, 24, RectSnap.EXT_EAST, true);
                setSnap(0, 19, 65540, true);
                setSnap(17, 0, RectSnap.EXT_EAST, true);
                setSnap(2, 17, RectSnap.EXT_EAST, true);
                setSnap(12, 0, 65540, true);
                setSnap(27, 17, 65540, true);
                setSnap(13, 2, 65540, true);
                setSnap(25, -1, RectSnap.INT_NORTHWEST, false);
                setSnap(26, -1, RectSnap.INT_NORTHEAST, false);
                setSnap(15, 25, RectSnap.EXT_SOUTH, false);
                setSnap(14, 26, RectSnap.EXT_SOUTH, false);
                setSnap(16, 15, RectSnap.EXT_SOUTH, false);
                setSnap(1, 15, RectSnap.EXT_SOUTHEAST, false);
                setSnap(18, 14, RectSnap.EXT_SOUTH, false);
                setSnap(3, 16, RectSnap.EXT_SOUTH, false);
                setSnap(4, 1, RectSnap.EXT_SOUTH, false);
                setSnap(5, 18, RectSnap.EXT_SOUTH, false);
                setSnap(21, 3, RectSnap.EXT_SOUTH, false);
                setSnap(23, 5, RectSnap.EXT_SOUTH, false);
                setSnap(7, 4, RectSnap.EXT_SOUTH, false);
                break;
            case 3:
            default:
                Arrays.fill(this.keyScales, 1.0f);
                setSnap(23, -1, RectSnap.INT_SOUTHEAST, true);
                setSnap(22, 23, RectSnap.EXT_WEST, true);
                setSnap(21, 22, RectSnap.EXT_WEST, true);
                setSnap(19, 21, 65540, true);
                setSnap(20, 23, 65540, true);
                setSnap(18, 20, 65540, true);
                setSnap(17, 18, RectSnap.EXT_WEST, true);
                setSnap(16, 17, RectSnap.EXT_WEST, true);
                setSnap(24, 23, RectSnap.EXT_NORTHWEST, true);
                setSnap(12, 16, 65540, true);
                setSnap(13, 18, 65540, true);
                setSnap(10, -1, RectSnap.INT_SOUTHWEST, true);
                setSnap(9, 10, RectSnap.EXT_EAST, true);
                setSnap(11, 9, RectSnap.EXT_EAST, true);
                setSnap(6, 10, 65540, true);
                setSnap(7, 6, RectSnap.EXT_EAST, true);
                setSnap(8, 7, RectSnap.EXT_EAST, true);
                setSnap(3, 6, 65540, true);
                setSnap(4, 3, RectSnap.EXT_EAST, true);
                setSnap(5, 4, RectSnap.EXT_EAST, true);
                setSnap(0, 3, 65540, true);
                setSnap(1, 0, RectSnap.EXT_EAST, true);
                setSnap(2, 1, RectSnap.EXT_EAST, true);
                setSnap(14, 0, 65540, false);
                setSnap(15, 2, 65540, false);
                setSnap(25, -1, RectSnap.INT_NORTHWEST, false);
                setSnap(26, -1, RectSnap.INT_NORTHEAST, false);
                setSnap(27, 17, 65540, true);
                break;
            case 4:
                Arrays.fill(this.keyScales, 1.0f);
                setSnap(21, -1, RectSnap.INT_SOUTHWEST, true);
                setSnap(22, 21, RectSnap.EXT_EAST, true);
                setSnap(23, 22, RectSnap.EXT_EAST, true);
                setSnap(19, 21, 65540, true);
                setSnap(20, 23, 65540, true);
                setSnap(18, 20, 65540, true);
                setSnap(17, 18, RectSnap.EXT_WEST, true);
                setSnap(16, 17, RectSnap.EXT_WEST, true);
                setSnap(24, 23, RectSnap.EXT_NORTHWEST, true);
                setSnap(12, 16, 65540, true);
                setSnap(13, 18, 65540, true);
                setSnap(11, -1, RectSnap.INT_SOUTHEAST, true);
                setSnap(9, 11, RectSnap.EXT_WEST, true);
                setSnap(10, 9, RectSnap.EXT_WEST, true);
                setSnap(6, 10, 65540, true);
                setSnap(7, 6, RectSnap.EXT_EAST, true);
                setSnap(8, 7, RectSnap.EXT_EAST, true);
                setSnap(3, 6, 65540, true);
                setSnap(4, 3, RectSnap.EXT_EAST, true);
                setSnap(5, 4, RectSnap.EXT_EAST, true);
                setSnap(0, 3, 65540, true);
                setSnap(1, 0, RectSnap.EXT_EAST, true);
                setSnap(2, 1, RectSnap.EXT_EAST, true);
                setSnap(14, 0, 65540, false);
                setSnap(15, 2, 65540, false);
                setSnap(25, -1, RectSnap.INT_NORTHWEST, false);
                setSnap(26, -1, RectSnap.INT_NORTHEAST, false);
                setSnap(27, 17, 65540, true);
                break;
            case 5:
                Arrays.fill(this.keyScales, 1.0f);
                setSnap(9, -1, RectSnap.INT_SOUTH, true);
                setSnap(10, 9, RectSnap.EXT_WEST, true);
                setSnap(11, 9, RectSnap.EXT_EAST, true);
                setSnap(6, 10, 65540, true);
                setSnap(7, 6, RectSnap.EXT_EAST, true);
                setSnap(8, 7, RectSnap.EXT_EAST, true);
                setSnap(3, 6, 65540, true);
                setSnap(4, 3, RectSnap.EXT_EAST, true);
                setSnap(5, 4, RectSnap.EXT_EAST, true);
                setSnap(0, 3, 65540, true);
                setSnap(1, 0, RectSnap.EXT_EAST, true);
                setSnap(2, 1, RectSnap.EXT_EAST, true);
                setSnap(12, 0, RectSnap.EXT_WEST, true);
                setSnap(13, 2, RectSnap.EXT_EAST, true);
                setSnap(17, -1, RectSnap.INT_NORTH, false);
                setSnap(16, 17, RectSnap.EXT_WEST, false);
                setSnap(18, 17, RectSnap.EXT_EAST, false);
                setSnap(24, 17, RectSnap.EXT_SOUTH, false);
                setSnap(19, 16, RectSnap.EXT_SOUTH, false);
                setSnap(20, 18, RectSnap.EXT_SOUTH, false);
                setSnap(21, 19, RectSnap.EXT_SOUTH, false);
                setSnap(23, 20, RectSnap.EXT_SOUTH, false);
                setSnap(22, 21, RectSnap.EXT_EAST, false);
                setSnap(25, 3, RectSnap.EXT_WEST, false);
                setSnap(26, 5, RectSnap.EXT_EAST, false);
                setSnap(15, 6, RectSnap.EXT_WEST, false);
                setSnap(14, 8, RectSnap.EXT_EAST, false);
                setSnap(27, -1, RectSnap.INT_NORTHEAST, true);
                break;
            case 6:
                Arrays.fill(this.keyScales, 1.0f);
                setSnap(22, -1, RectSnap.INT_SOUTH, true);
                setSnap(23, 22, RectSnap.EXT_EAST, true);
                setSnap(21, 22, RectSnap.EXT_WEST, true);
                setSnap(19, 21, 65540, true);
                setSnap(20, 23, 65540, true);
                setSnap(18, 20, 65540, true);
                setSnap(17, 18, RectSnap.EXT_WEST, true);
                setSnap(16, 17, RectSnap.EXT_WEST, true);
                setSnap(24, 23, RectSnap.EXT_NORTHWEST, true);
                setSnap(12, 16, RectSnap.EXT_WEST, true);
                setSnap(13, 18, RectSnap.EXT_EAST, true);
                setSnap(0, 1, RectSnap.EXT_WEST, false);
                setSnap(1, -1, RectSnap.INT_NORTH, false);
                setSnap(2, 1, RectSnap.EXT_EAST, false);
                setSnap(3, 0, RectSnap.EXT_SOUTH, false);
                setSnap(4, 1, RectSnap.EXT_SOUTH, false);
                setSnap(5, 2, RectSnap.EXT_SOUTH, false);
                setSnap(6, 3, RectSnap.EXT_SOUTH, false);
                setSnap(7, 4, RectSnap.EXT_SOUTH, false);
                setSnap(8, 5, RectSnap.EXT_SOUTH, false);
                setSnap(10, 6, RectSnap.EXT_SOUTH, false);
                setSnap(9, 7, RectSnap.EXT_SOUTH, false);
                setSnap(11, 8, RectSnap.EXT_SOUTH, false);
                setSnap(25, 19, RectSnap.EXT_WEST, false);
                setSnap(26, 20, RectSnap.EXT_EAST, false);
                setSnap(15, 21, RectSnap.EXT_WEST, false);
                setSnap(14, 23, RectSnap.EXT_EAST, false);
                setSnap(27, -1, RectSnap.INT_NORTHEAST, true);
                break;
        }
    }

    public ProfileModel getSettings() {
        return this.settings;
    }

    public int getLayout() {
        return this.layoutVariant;
    }

    public RectF getScreen() {
        return this.screen;
    }

    public float[] getKeyScales() {
        return this.keyScales;
    }

    public void getKeyRects(float[] out) {
        for (int i = 0; i < 28; i++) {
            RectF r = this.keypad[i].rect;
            out[i * 4] = r.left;
            out[(i * 4) + 1] = r.top;
            out[(i * 4) + 2] = r.right;
            out[(i * 4) + 3] = r.bottom;
        }
    }

    public void getJoystickData(float[] out) {
        out[0] = this.joystick.normCenterX;
        out[1] = this.joystick.normCenterY;
        out[2] = this.joystick.normRadius;
        out[3] = this.joystick.deadZone;
    }

    public float getPhoneKeyboardHeight(float w, float h) {
        return getKeySize(w, h) * PHONE_KEY_ROWS * 0.75f;
    }

    public void setLayout(int variant) {
        resetLayout(variant);
        if (variant == 0) {
            readLayout();
        }
        this.layoutVariant = variant;
        onLayoutChanged(variant);
        for (int i = 0; i < 28; i++) {
            resizeKey(i);
        }
        snapKeys();
        this.overlayView.postInvalidate();
        if (this.target != null && this.target.isShown()) {
            this.target.updateSize();
        }
    }

    private File getLayoutFile() {
        String suffix = this.currentOrientation == 1 ? "_land" : "_port";
        File file = new File(this.settings.dir + Config.MIDLET_KEY_LAYOUT_FILE + suffix);
        return file.exists() ? file : this.saveFile;
    }

    private boolean loadLayoutForOrientation() {
        String suffix = this.currentOrientation == 1 ? "_land" : "_port";
        File orientedFile = new File(this.settings.dir + Config.MIDLET_KEY_LAYOUT_FILE + suffix);
        if (!orientedFile.exists()) {
            int defaultType = this.settings.vkType;
            if (defaultType == 0) {
                defaultType = 3;
            }
            this.layoutVariant = defaultType;
            resetLayout(defaultType);
            return false;
        }
        int type = readLayoutType();
        if (type >= 0) {
            this.layoutVariant = type;
            if (type == 0) {
                Arrays.fill(this.keyScales, 1.0f);
                resetLayout(3);
                readLayout();
            } else {
                resetLayout(type);
            }
        }
        return false;
    }

    private void saveLayout() {
        int length;
        String suffix = this.currentOrientation == 1 ? "_land" : "_port";
        File file = new File(this.settings.dir + Config.MIDLET_KEY_LAYOUT_FILE + suffix);
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            try {
                int variant = this.layoutVariant;
                if (variant != 0 && raf.length() > 16) {
                    try {
                        if (raf.readInt() != LAYOUT_SIGNATURE) {
                            throw new IOException("file signature not found");
                        }
                        int version = raf.readInt();
                        if (version < 1 || version > 5) {
                            throw new IOException("incompatible file version");
                        }
                        do {
                            int block = raf.readInt();
                            length = raf.readInt();
                            switch (block) {
                                case -1:
                                    raf.seek(raf.getFilePointer() - 8);
                                    raf.writeInt(3);
                                    raf.writeInt(1);
                                    raf.write(variant);
                                    raf.writeInt(-1);
                                    raf.writeInt(0);
                                    raf.close();
                                    return;
                                case 0:
                                    if (version >= 2) {
                                        int count = raf.readInt();
                                        length = count * 21;
                                    }
                                    break;
                                case 3:
                                    raf.write(variant);
                                    raf.close();
                                    return;
                            }
                        } while (raf.skipBytes(length) == length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                raf.seek(0L);
                raf.writeInt(LAYOUT_SIGNATURE);
                raf.writeInt(5);
                raf.writeInt(3);
                raf.writeInt(1);
                raf.write(variant);
                if (variant != 0) {
                    raf.writeInt(-1);
                    raf.writeInt(0);
                    raf.setLength(raf.getFilePointer());
                    raf.close();
                    return;
                }
                raf.writeInt(0);
                raf.writeInt((this.keypad.length * 21) + 4);
                raf.writeInt(this.keypad.length);
                for (VirtualKey key : this.keypad) {
                    raf.writeInt(key.hashCode());
                    raf.writeBoolean(key.visible);
                    raf.writeInt(key.snapOrigin);
                    raf.writeInt(key.snapMode);
                    PointF snapOffset = key.snapOffset;
                    float dispW = ContextHolder.getDisplayWidth();
                    float dispH = ContextHolder.getDisplayHeight();
                    raf.writeFloat(dispW > 0.0f ? snapOffset.x / dispW : 0.0f);
                    raf.writeFloat(dispH > 0.0f ? snapOffset.y / dispH : 0.0f);
                }
                raf.writeInt(1);
                raf.writeInt((this.keyScales.length * 4) + 4);
                raf.writeInt(this.keyScales.length);
                for (float keyScale : this.keyScales) {
                    raf.writeFloat(keyScale);
                }
                raf.writeInt(4);
                raf.writeInt(20);
                raf.writeInt(1);
                raf.writeFloat(this.joystick.normCenterX);
                raf.writeFloat(this.joystick.normCenterY);
                raf.writeFloat(this.joystick.normRadius);
                raf.writeFloat(this.joystick.deadZone);
                raf.writeInt(-1);
                raf.writeInt(0);
                raf.setLength(raf.getFilePointer());
                raf.close();
            } finally {
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private int readLayoutType() {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(getLayoutFile()));
            try {
                if (dis.readInt() != LAYOUT_SIGNATURE) {
                    throw new IOException("file signature not found");
                }
                int version = dis.readInt();
                if (version < 1 || version > 5) {
                    throw new IOException("incompatible file version");
                }
                int custom = 0;
                while (true) {
                    int block = dis.readInt();
                    int length = dis.readInt();
                    switch (block) {
                        case -1:
                            int i = custom == 3 ? 0 : -1;
                            dis.close();
                            return i;
                        case 0:
                            if (version >= 2) {
                                int count = dis.readInt();
                                length = count * 21;
                            }
                            int count2 = dis.skipBytes(length);
                            if (count2 != length) {
                                dis.close();
                                return -1;
                            }
                            custom |= 1;
                            break;
                        case 1:
                            if (dis.skipBytes(length) != length) {
                                dis.close();
                                return -1;
                            }
                            custom |= 2;
                            break;
                        case 2:
                        default:
                            if (dis.skipBytes(length) != length) {
                                dis.close();
                                return -1;
                            }
                            break;
                        case 3:
                            int i2 = dis.read();
                            dis.close();
                            return i2;
                    }
                }
            } catch (Throwable th) {
                try {
                    dis.close();
                } catch (IOException ignored) {
                }
                Log.e(TAG, "readLayoutType failed", th);
                return -1;
            }
        } catch (FileNotFoundException e) {
            Log.w(TAG, "readLayoutType() threw an FileNotFoundException: " + e.getMessage());
            return -1;
        } catch (IOException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    private void readLayout() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(getLayoutFile()));
            if (dis.readInt() != LAYOUT_SIGNATURE) {
                throw new IOException("file signature not found");
            }
            int version = dis.readInt();
            if (version >= 1) {
                int i = 5;
                if (version <= 5) {
                    while (true) {
                        int block = dis.readInt();
                        int length = dis.readInt();
                        switch (block) {
                            case -1:
                                dis.close();
                                return;
                            case 0:
                                int count = dis.readInt();
                                int i2 = 0;
                                while (i2 < count) {
                                    int hash = dis.readInt();
                                    boolean found = false;
                                    VirtualKey[] virtualKeyArr = this.keypad;
                                    int length2 = virtualKeyArr.length;
                                    int i3 = 0;
                                    while (i3 < length2) {
                                        VirtualKey key = virtualKeyArr[i3];
                                        if (key.hashCode() != hash) {
                                            i3++;
                                            i = 5;
                                        } else {
                                            if (version >= 2) {
                                                key.visible = dis.readBoolean();
                                            }
                                            key.snapOrigin = dis.readInt();
                                            key.snapMode = dis.readInt();
                                            float ox = dis.readFloat();
                                            float oy = dis.readFloat();
                                            if (version >= i) {
                                                float dispW = ContextHolder.getDisplayWidth();
                                                float dispH = ContextHolder.getDisplayHeight();
                                                key.snapOffset.x = ox * dispW;
                                                key.snapOffset.y = oy * dispH;
                                            } else {
                                                key.snapOffset.x = ox;
                                                key.snapOffset.y = oy;
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        dis.skipBytes(version >= 2 ? 17 : 16);
                                    }
                                    i2++;
                                    i = 5;
                                }
                                break;
                            case 1:
                                int count2 = dis.readInt();
                                if (version >= 4) {
                                    for (int i4 = 0; i4 < count2 && i4 < this.keyScales.length; i4++) {
                                        this.keyScales[i4] = dis.readFloat();
                                    }
                                    if (count2 < this.keyScales.length) {
                                        Arrays.fill(this.keyScales, count2, this.keyScales.length, 1.0f);
                                    }
                                } else {
                                    dis.skipBytes(count2 * 4);
                                }
                                break;
                            case 2:
                            case 3:
                            default:
                                dis.skipBytes(length);
                                break;
                            case 4:
                                int count3 = dis.readInt();
                                if (count3 > 0) {
                                    this.joystick.normCenterX = dis.readFloat();
                                    this.joystick.normCenterY = dis.readFloat();
                                    this.joystick.normRadius = dis.readFloat();
                                    this.joystick.deadZone = dis.readFloat();
                                }
                                break;
                        }
                        i = 5;
                    }
                }
            }
            throw new IOException("incompatible file version");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "readLayout() threw an FileNotFoundException: " + e.getMessage());
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (Throwable th) {
            Log.e(TAG, "readLayout failed", th);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public String[] getKeyNames() {
        String[] names = new String[28];
        for (int i = 0; i < 28; i++) {
            names[i] = this.keypad[i].label;
        }
        return names;
    }

    public boolean[] getKeysVisibility() {
        boolean[] states = new boolean[28];
        for (int i = 0; i < 28; i++) {
            states[i] = !this.keypad[i].visible;
        }
        return states;
    }

    public void setKeysVisibility(boolean[] states) {
        for (int i = 0; i < 28; i++) {
            this.keypad[i].visible = !states[i];
        }
        this.overlayView.postInvalidate();
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public void setTarget(Canvas canvas) {
        this.target = canvas;
        highlightGroup(-1);
    }

    private boolean isExceptionKey(int key) {
        boolean[] exceptions = this.settings.vkSkinKeepAspectExceptions;
        return exceptions != null && key < exceptions.length && exceptions[key];
    }

    private void setSnap(int key, int origin, int mode, boolean visible) {
        VirtualKey vKey = this.keypad[key];
        vKey.snapOrigin = origin;
        vKey.snapMode = mode;
        vKey.snapOffset.set(0.0f, 0.0f);
        vKey.snapValid = false;
        vKey.visible = visible;
    }

    private boolean findSnap(int target, int origin) {
        VirtualKey tk = this.keypad[target];
        VirtualKey ok = this.keypad[origin];
        tk.snapMode = RectSnap.getSnap(tk.rect, ok.rect, this.snapRadius, RectSnap.COARSE_MASK, true);
        if (tk.snapMode != 0) {
            tk.snapOrigin = origin;
            tk.snapOffset.set(0.0f, 0.0f);
            for (int i = 0; i < this.keypad.length; i++) {
                origin = this.keypad[origin].snapOrigin;
                if (origin == -1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void snapKey(int key, int level) {
        if (level >= this.snapStack.length) {
            Log.d(TAG, "Snap loop detected: ");
            for (int i = 1; i < this.snapStack.length; i++) {
                System.out.print(this.snapStack[i]);
                System.out.print(", ");
            }
            Log.d(TAG, String.valueOf(key));
            return;
        }
        this.snapStack[level] = key;
        VirtualKey vKey = this.keypad[key];
        if (vKey.snapOrigin == -1) {
            RectSnap.snap(vKey.rect, this.screen, vKey.snapMode, vKey.snapOffset);
        } else {
            if (!this.keypad[vKey.snapOrigin].snapValid) {
                snapKey(vKey.snapOrigin, level + 1);
            }
            RectSnap.snap(vKey.rect, this.keypad[vKey.snapOrigin].rect, vKey.snapMode, vKey.snapOffset);
        }
        vKey.snapValid = true;
    }

    private void snapKeys() {
        this.obscuresVirtualScreen = false;
        for (int i = 0; i < this.keypad.length; i++) {
            snapKey(i, 0);
            VirtualKey key = this.keypad[i];
            RectF rect = key.rect;
            key.corners = (int) (Math.min(rect.width(), rect.height()) * 0.25f);
            if (RectF.intersects(rect, this.virtualScreen)) {
                if (key.visible) {
                    this.obscuresVirtualScreen = true;
                }
                key.opaque = false;
            } else {
                key.opaque = this.settings.vkForceOpacity;
            }
        }
    }

    public void flattenKeysToScreenAnchored() {
        float screenW = this.screen.width();
        float screenH = this.screen.height();
        ArrayList<RectF> occupied = new ArrayList<>(28);
        float gap = this.keySize * 0.08f;
        int i = 0;
        for (int i2 = 28; i < i2; i2 = 28) {
            VirtualKey key = this.keypad[i];
            RectF r = key.rect;
            float w = r.width();
            float h = r.height();
            if (r.left >= 0.0f && r.top >= 0.0f && r.right <= screenW && r.bottom <= screenH && !overlapsAny(r, occupied)) {
                occupied.add(new RectF(r));
                key.snapOrigin = -1;
                key.snapMode = RectSnap.INT_NORTHWEST;
                key.snapOffset.set(r.left, r.top);
            } else {
                float tryX = Math.max(0.0f, Math.min(r.left, screenW - w));
                float tryY = Math.max(0.0f, Math.min(r.top, screenH - h));
                float bestX = tryX;
                float bestDist = Float.MAX_VALUE;
                float bestY = tryY;
                float sy = screenH - h;
                while (sy >= 0.0f) {
                    for (float sx = 0.0f; sx + w <= screenW; sx += w + gap) {
                        if (!overlapsAny(sx, sy, w, h, occupied)) {
                            float dist = ((sx - tryX) * (sx - tryX)) + ((sy - tryY) * (sy - tryY));
                            if (dist < bestDist) {
                                bestDist = dist;
                                bestX = sx;
                                bestY = sy;
                            }
                        }
                    }
                    float sx2 = h + gap;
                    sy -= sx2;
                }
                r.left = bestX;
                r.top = bestY;
                r.right = bestX + w;
                r.bottom = bestY + h;
                key.snapOrigin = -1;
                key.snapMode = RectSnap.INT_NORTHWEST;
                key.snapOffset.set(bestX, bestY);
                occupied.add(new RectF(r));
            }
            i++;
        }
        saveLayout();
        this.joystick.resize(screenW, screenH);
        this.overlayView.postInvalidate();
    }

    private static boolean overlapsAny(RectF rect, ArrayList<RectF> list) {
        for (RectF other : list) {
            if (RectF.intersects(rect, other)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlapsAny(float x, float y, float w, float h, ArrayList<RectF> list) {
        RectF tmp = new RectF(x, y, x + w, y + h);
        return overlapsAny(tmp, list);
    }

    public boolean isPhone() {
        return this.layoutVariant == 1 || this.layoutVariant == 2;
    }

    private void highlightGroup(int keyIndex) {
        for (VirtualKey aKeypad : this.keypad) {
            aKeypad.selected = false;
        }
        if (keyIndex >= 0) {
            this.keypad[keyIndex].selected = true;
        }
    }

    public int getLayoutEditMode() {
        return this.layoutEditMode;
    }

    public void setLayoutEditMode(int mode) {
        this.layoutEditMode = mode;
        this.editedIndex = -1;
        this.dragPointer = -1;
        this.pinchPointer = -1;
        this.joystick.selected = false;
        highlightGroup(-1);
        this.handler.removeCallbacks(this);
        this.visible = true;
        this.overlayView.postInvalidate();
        hide();
    }

    public boolean isLayoutLocked() {
        return this.layoutLocked;
    }

    public void setLayoutLocked(boolean locked) {
        this.layoutLocked = locked;
    }

    public void applyJoyPreset(int preset) {
        if (preset < 0 || preset >= JOY_PRESETS.length) {
            return;
        }
        this.settings.joyPreset = preset;
        this.settings.joyMap = (int[]) JOY_PRESETS[preset].clone();
        this.settings.joyMode = preset == 0 ? 1 : 0;
        this.joystick.alternateKey = -1;
        this.joystick.alternatePhase = false;
        postInvalidate();
    }

    public boolean isShowGrid() {
        return this.showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        postInvalidate();
    }

    public boolean isSnapToGrid() {
        return this.snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public float getGridSize() {
        return this.gridSize;
    }

    public void setGridSize(float gridSize) {
        this.gridSize = Math.max(4.0f, gridSize);
        postInvalidate();
    }

    private void resizeKey(int key) {
        float sizeX = this.keySize * this.keyScales[key * 2];
        float sizeY = this.keySize * this.keyScales[(key * 2) + 1];
        if (this.skinAspectRatios != null && key < this.skinAspectRatios.length) {
            float aspect = this.skinAspectRatios[key];
            if (aspect > 0.0f && this.settings.vkSkinKeepAspect && !isExceptionKey(key)) {
                float curAspect = sizeX / sizeY;
                if (Math.abs(curAspect - aspect) > 0.01f) {
                    float area = sizeX * sizeY;
                    sizeX = (float) Math.sqrt(area * aspect);
                    sizeY = (float) Math.sqrt(area / aspect);
                }
            }
        }
        VirtualKey vKey = this.keypad[key];
        vKey.resize(sizeX, sizeY);
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public void resize(RectF screen, float left, float top, float right, float bottom) {
        this.screen = screen;
        this.virtualScreen.set(left, top, right, bottom);
        int newOrientation = screen.width() < screen.height() ? 0 : 1;
        boolean orientationChanged = newOrientation != this.currentOrientation;
        if (orientationChanged) {
            this.currentOrientation = newOrientation;
        }
        if (!this.initialResizeDone || orientationChanged) {
            loadLayoutForOrientation();
        }
        if (this.layoutLocked && this.initialResizeDone && !orientationChanged) {
            return;
        }
        float minScale = this.keyScales[0];
        for (int i = 1; i < this.keyScales.length; i++) {
            if (this.keyScales[i] < minScale) {
                minScale = this.keyScales[i];
            }
        }
        float keySize = getKeySize(screen.width(), screen.height());
        this.snapRadius = (keySize * minScale) / 8.0f;
        this.keySize = keySize;
        this.gridSize = keySize / 4.0f;
        for (int i2 = 0; i2 < 28; i2++) {
            resizeKey(i2);
        }
        snapKeys();
        this.joystick.resize(screen.width(), screen.height());
        this.overlayView.postInvalidate();
        this.initialResizeDone = true;
        int delay = this.settings.vkHideDelay;
        if (delay > 0 && this.obscuresVirtualScreen && this.layoutEditMode == -1) {
            for (VirtualKey key : this.associatedKeys) {
                if (key != null) {
                    return;
                }
            }
            this.handler.postDelayed(this, delay);
        }
    }

    private float getKeySize(float screenWidth, float screenHeight) {
        if (isPhone()) {
            return screenWidth / 6.0f;
        }
        if (screenWidth <= screenHeight) {
            return Math.min(screenWidth / 6.0f, screenHeight / 12.0f);
        }
        return Math.min(screenWidth / 12.0f, screenHeight / 6.0f);
    }

    @Override // javax.microedition.lcdui.overlay.Layer
    public void paint(CanvasWrapper g) {
        if (this.visible) {
            if (this.layoutEditMode != -1 || this.settings.vkAlpha > 0) {
                if ((this.layoutEditMode == 0 || this.layoutEditMode == 5) && this.showGrid && this.gridSize > 1.0f) {
                    float gw = this.screen.width();
                    float gh = this.screen.height();
                    g.setStrokeWidth(1.0f);
                    int pass = 0;
                    while (pass < 2) {
                        g.setDrawColor(pass == 0 ? 1140850688 : 1728053247);
                        float dx = pass == 0 ? 1.0f : 0.0f;
                        float x = 0.0f;
                        while (x <= gw) {
                            g.drawLine(x + dx, 0.0f, x + dx, gh);
                            x += this.gridSize;
                        }
                        float y = 0.0f;
                        while (y <= gh) {
                            g.drawLine(0.0f, y + dx, gw, y + dx);
                            y += this.gridSize;
                        }
                        pass++;
                    }
                }
                for (VirtualKey key : this.keypad) {
                    if (key.visible || this.layoutEditMode == 5) {
                        key.paint(g);
                    }
                }
                this.joystick.paint(g);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean pointerPressed(int pointer, float x, float y) {
        switch (this.layoutEditMode) {
            case -1:
                if (this.joystick.activePointer == -1 && this.joystick.contains(x, y)) {
                    this.joystick.pointerPressed(x, y, pointer);
                    this.overlayView.postInvalidate();
                } else {
                    if (pointer > this.associatedKeys.length) {
                        return false;
                    }
                    VirtualKey[] virtualKeyArr = this.keypad;
                    int length = virtualKeyArr.length;
                    int i = 0;
                    while (true) {
                        if (i < length) {
                            VirtualKey key = virtualKeyArr[i];
                            if (!key.contains(x, y)) {
                                i++;
                            } else {
                                vibrate();
                                this.associatedKeys[pointer] = key;
                                key.onDown();
                                this.overlayView.postInvalidate();
                            }
                        }
                    }
                }
                return false;
            case 0:
                this.ptrX[pointer] = x;
                this.ptrY[pointer] = y;
                if (this.dragPointer == -1) {
                    this.dragPointer = pointer;
                    this.editedIndex = -1;
                    int i2 = 0;
                    while (i2 < this.keypad.length) {
                        if (!this.keypad[i2].contains(x, y)) {
                            i2++;
                        } else {
                            this.editedIndex = i2;
                            RectF rect = this.keypad[i2].rect;
                            this.offsetX = x - rect.left;
                            this.offsetY = y - rect.top;
                            break;
                        }
                    }
                    int i3 = this.editedIndex;
                    if (i3 < 0 && this.joystick.contains(x, y)) {
                        this.editedIndex = -2;
                        this.joystick.selected = true;
                        this.offsetX = x - this.joystick.centerX;
                        this.offsetY = y - this.joystick.centerY;
                        this.overlayView.postInvalidate();
                    }
                } else if (this.pinchPointer == -1 && this.editedIndex != -1) {
                    this.pinchPointer = pointer;
                    this.pinchStartDistX = Math.abs(this.ptrX[this.dragPointer] - x);
                    this.pinchStartDistY = Math.abs(this.ptrY[this.dragPointer] - y);
                    if (this.pinchStartDistX < 1.0f) {
                        this.pinchStartDistX = 1.0f;
                    }
                    if (this.pinchStartDistY < 1.0f) {
                        this.pinchStartDistY = 1.0f;
                    }
                    if (this.editedIndex >= 0) {
                        this.prevScaleX = this.keyScales[this.editedIndex * 2];
                        this.prevScaleY = this.keyScales[(this.editedIndex * 2) + 1];
                    } else if (this.editedIndex == -2) {
                        this.prevScaleX = this.joystick.normRadius;
                    }
                }
                return false;
            case 5:
                int i4 = 0;
                while (i4 < this.keypad.length) {
                    if (!this.keypad[i4].rect.contains(x, y)) {
                        i4++;
                    } else {
                        this.keypad[i4].visible = true ^ this.keypad[i4].visible;
                        this.overlayView.postInvalidate();
                        break;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean pointerDragged(int pointer, float x, float y) {
        boolean z;
        switch (this.layoutEditMode) {
            case -1:
                if (this.joystick.activePointer == pointer) {
                    this.joystick.pointerDragged(x, y, pointer);
                    this.overlayView.postInvalidate();
                    return false;
                }
                if (pointer > this.associatedKeys.length) {
                    return false;
                }
                VirtualKey aKey = this.associatedKeys[pointer];
                if (aKey == null) {
                    pointerPressed(pointer, x, y);
                    return false;
                }
                if (aKey.contains(x, y)) {
                    return false;
                }
                this.associatedKeys[pointer] = null;
                aKey.onUp();
                this.overlayView.postInvalidate();
                pointerPressed(pointer, x, y);
                return false;
            case 0:
                this.ptrX[pointer] = x;
                this.ptrY[pointer] = y;
                if (pointer == this.dragPointer) {
                    if (this.editedIndex >= 0) {
                        RectF rect = this.keypad[this.editedIndex].rect;
                        float w = rect.width();
                        float h = rect.height();
                        float nx = Math.max(this.screen.left, Math.min(this.screen.right - w, x - this.offsetX));
                        float ny = Math.max(this.screen.top, Math.min(this.screen.bottom - h, y - this.offsetY));
                        if (this.snapToGrid && this.gridSize > 1.0f) {
                            nx = Math.round(nx / this.gridSize) * this.gridSize;
                            ny = Math.round(ny / this.gridSize) * this.gridSize;
                        }
                        rect.offsetTo(nx, ny);
                        this.overlayView.postInvalidate();
                    } else if (this.editedIndex == -2) {
                        float sw = this.screen.width();
                        float sh = this.screen.height();
                        float px = Math.max(0.0f, Math.min(sw, x - this.offsetX));
                        float py = Math.max(0.0f, Math.min(sh, y - this.offsetY));
                        if (this.snapToGrid && this.gridSize > 1.0f) {
                            px = Math.round(px / this.gridSize) * this.gridSize;
                            py = Math.round(py / this.gridSize) * this.gridSize;
                        }
                        this.joystick.normCenterX = Math.max(0.0f, Math.min(1.0f, px / sw));
                        this.joystick.normCenterY = Math.max(0.0f, Math.min(1.0f, py / sh));
                        this.joystick.resize(sw, sh);
                        this.overlayView.postInvalidate();
                    }
                }
                if (this.pinchPointer != -1 && this.dragPointer != -1 && this.editedIndex != -1) {
                    if (pointer != this.dragPointer && pointer != this.pinchPointer) {
                        return false;
                    }
                    if (this.editedIndex >= 0) {
                        float curX = Math.abs(this.ptrX[this.dragPointer] - this.ptrX[this.pinchPointer]);
                        float curY = Math.abs(this.ptrY[this.dragPointer] - this.ptrY[this.pinchPointer]);
                        float scaleX = Math.max(0.01f, curX / this.pinchStartDistX);
                        float scaleY = Math.max(0.01f, curY / this.pinchStartDistY);
                        if (!this.settings.vkSkinKeepAspect || isExceptionKey(this.editedIndex)) {
                            this.keyScales[this.editedIndex * 2] = Math.max(0.01f, this.prevScaleX * scaleX);
                            this.keyScales[(this.editedIndex * 2) + 1] = Math.max(0.01f, this.prevScaleY * scaleY);
                        } else {
                            float avg = (scaleX + scaleY) / PHONE_KEY_SCALE_X;
                            this.keyScales[this.editedIndex * 2] = Math.max(0.01f, this.prevScaleX * avg);
                            this.keyScales[(this.editedIndex * 2) + 1] = Math.max(0.01f, this.prevScaleY * avg);
                        }
                        if (this.snapToGrid && this.gridSize > 1.0f) {
                            float sizeX = this.keySize * this.keyScales[this.editedIndex * 2];
                            float sizeY = this.keySize * this.keyScales[(this.editedIndex * 2) + 1];
                            float snappedX = Math.round(sizeX / this.gridSize) * this.gridSize;
                            float snappedY = Math.round(sizeY / this.gridSize) * this.gridSize;
                            if (snappedX >= this.keySize * 0.01f) {
                                z = false;
                                this.keyScales[this.editedIndex * 2] = snappedX / this.keySize;
                            } else {
                                z = false;
                            }
                            if (snappedY >= this.keySize * 0.01f) {
                                this.keyScales[(this.editedIndex * 2) + 1] = snappedY / this.keySize;
                            }
                        } else {
                            z = false;
                        }
                        resizeKey(this.editedIndex);
                    } else {
                        z = false;
                        if (this.editedIndex == -2) {
                            float dx = this.ptrX[this.dragPointer] - this.ptrX[this.pinchPointer];
                            float dy = this.ptrY[this.dragPointer] - this.ptrY[this.pinchPointer];
                            float dist = (float) Math.sqrt((dx * dx) + (dy * dy));
                            if (dist < 1.0f) {
                                dist = 1.0f;
                            }
                            float scale = dist / ((float) Math.sqrt((this.pinchStartDistX * this.pinchStartDistX) + (this.pinchStartDistY * this.pinchStartDistY)));
                            this.joystick.normRadius = Math.max(0.03f, Math.min(0.5f, this.prevScaleX * scale));
                            if (this.snapToGrid && this.gridSize > 1.0f) {
                                float minDim = Math.min(this.screen.width(), this.screen.height());
                                float radiusPx = this.joystick.normRadius * minDim;
                                this.joystick.normRadius = (Math.round(radiusPx / this.gridSize) * this.gridSize) / minDim;
                            }
                            this.joystick.resize(this.screen.width(), this.screen.height());
                        }
                    }
                    this.overlayView.postInvalidate();
                    return z;
                }
                return false;
            default:
                return false;
        }
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean pointerReleased(int pointer, float x, float y) {
        VirtualKey key;
        if (this.layoutEditMode == -1) {
            if (this.joystick.activePointer == pointer) {
                this.joystick.pointerReleased(pointer);
                this.overlayView.postInvalidate();
            } else if (pointer <= this.associatedKeys.length && (key = this.associatedKeys[pointer]) != null) {
                this.associatedKeys[pointer] = null;
                key.onUp();
                this.overlayView.postInvalidate();
            }
        } else if (this.layoutEditMode == 0) {
            if (pointer == this.dragPointer) {
                this.dragPointer = -1;
            }
            if (pointer == this.pinchPointer) {
                this.pinchPointer = -1;
            }
            if (this.dragPointer == -1 && this.pinchPointer == -1) {
                if (this.editedIndex >= 0) {
                    VirtualKey key2 = this.keypad[this.editedIndex];
                    key2.snapMode = RectSnap.INT_NORTHWEST;
                    key2.snapOrigin = -1;
                    key2.snapOffset.set(key2.rect.left - this.screen.left, key2.rect.top - this.screen.top);
                    key2.snapValid = false;
                    for (int i = 0; i < this.keypad.length; i++) {
                        if (this.keypad[i].snapOrigin == this.editedIndex) {
                            this.keypad[i].snapMode = RectSnap.INT_NORTHWEST;
                            this.keypad[i].snapOrigin = -1;
                            this.keypad[i].snapOffset.set(this.keypad[i].rect.left - this.screen.left, this.keypad[i].rect.top - this.screen.top);
                            this.keypad[i].snapValid = false;
                        }
                    }
                } else if (this.editedIndex == -2) {
                    this.joystick.selected = false;
                    this.overlayView.postInvalidate();
                }
                this.editedIndex = -1;
            }
        }
        return false;
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public void show() {
        if (this.settings.vkHideDelay > 0 && this.obscuresVirtualScreen) {
            this.handler.removeCallbacks(this);
            if (!this.visible) {
                this.visible = true;
                this.overlayView.postInvalidate();
            }
        }
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public void hide() {
        long delay = this.settings.vkHideDelay;
        if (delay > 0 && this.obscuresVirtualScreen && this.layoutEditMode == -1) {
            this.handler.postDelayed(this, delay);
        }
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public void cancel() {
        for (VirtualKey key : this.keypad) {
            key.selected = false;
            this.handler.removeCallbacks(key);
        }
        this.joystick.clearState();
    }

    @Override // java.lang.Runnable
    public void run() {
        this.visible = false;
        this.overlayView.postInvalidate();
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean keyPressed(int keyCode) {
        int hashCode = (keyCode + 31) * 31;
        VirtualKey[] virtualKeyArr = this.keypad;
        int length = virtualKeyArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            VirtualKey key = virtualKeyArr[i];
            if (key.hashCode() != hashCode) {
                i++;
            } else {
                key.selected = true;
                this.overlayView.postInvalidate();
                break;
            }
        }
        return false;
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean keyRepeated(int keyCode) {
        return false;
    }

    @Override // javax.microedition.lcdui.overlay.Overlay
    public boolean keyReleased(int keyCode) {
        int hashCode = (keyCode + 31) * 31;
        VirtualKey[] virtualKeyArr = this.keypad;
        int length = virtualKeyArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            VirtualKey key = virtualKeyArr[i];
            if (key.hashCode() != hashCode) {
                i++;
            } else {
                key.selected = false;
                this.overlayView.postInvalidate();
                break;
            }
        }
        return false;
    }

    private void vibrate() {
        if (this.settings.vkFeedback) {
            ContextHolder.vibrateKey(50);
        }
    }

    public void setView(View view) {
        this.overlayView = view;
    }

    public void postInvalidate() {
        if (this.overlayView != null) {
            this.overlayView.postInvalidate();
        }
    }

    public int getKeyStatesVodafone() {
        int keyStates = 0;
        for (int i = 0; i < this.keypad.length; i++) {
            VirtualKey key = this.keypad[i];
            if (key.selected) {
                keyStates |= getKeyBit(i);
            }
        }
        return keyStates;
    }

    private int getKeyBit(int vKey) {
        switch (vKey) {
            case 0:
                return 2;
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 16;
            case 4:
                return 32;
            case 5:
                return 64;
            case 6:
                return 128;
            case 7:
                return 256;
            case 8:
                return 512;
            case 9:
                return 1;
            case 10:
                return 1024;
            case 11:
                return 2048;
            case 12:
                return 131072;
            case 13:
                return 262144;
            case 14:
            default:
                return 0;
            case 15:
                return 524288;
            case 16:
                return 2097152;
            case 17:
                return 4096;
            case 18:
                return 1048576;
            case 19:
                return 8192;
            case 20:
                return 16384;
            case 21:
                return 8388608;
            case 22:
                return 32768;
            case 23:
                return 4194304;
            case 24:
                return 65536;
        }
    }

    public void saveScreenParams() {
        float scale = this.virtualScreen.width() / this.screen.width();
        this.settings.screenScaleRatio = Math.round(100.0f * scale);
        this.settings.screenGravity = 1;
        ProfilesManager.saveConfig(this.settings);
    }

    private class VirtualKey implements Runnable {
        int corners;
        private final int hashCode;
        final int keyCode;
        final String label;
        private int repeatCount;
        boolean selected;
        int snapMode;
        int snapOrigin;
        boolean snapValid;
        int keyIndex = -1;
        final RectF rect = new RectF();
        final PointF snapOffset = new PointF();
        boolean visible = true;
        boolean opaque = true;

        VirtualKey(int keyCode, String label) {
            this.keyCode = keyCode;
            this.label = label;
            this.hashCode = (this.keyCode + 31) * 31;
        }

        void resize(float width, float height) {
            this.rect.right = this.rect.left + width;
            this.rect.bottom = this.rect.top + height;
        }

        boolean contains(float x, float y) {
            return this.visible && this.rect.contains(x, y);
        }

        void paint(CanvasWrapper g) {
            int bgColor;
            int fgColor;
            Bitmap iconBmp;
            float iconScale;
            if (this.selected) {
                bgColor = VirtualKeyboard.this.settings.vkBgColorSelected;
                fgColor = VirtualKeyboard.this.settings.vkFgColorSelected;
            } else {
                bgColor = VirtualKeyboard.this.settings.vkBgColor;
                fgColor = VirtualKeyboard.this.settings.vkFgColor;
            }
            int alphaInt = 255;
            if (VirtualKeyboard.this.layoutEditMode == 5) {
                if (!this.visible) {
                    alphaInt = 68;
                }
            } else if (!this.opaque && VirtualKeyboard.this.layoutEditMode == -1) {
                alphaInt = VirtualKeyboard.this.settings.vkAlpha;
            }
            int alpha = alphaInt << 24;
            g.setAlpha(alphaInt);
            boolean hasSkin = false;
            NinePatchSkin npPerKey = null;
            Bitmap perKeyBmp = null;
            Picture perKeyPic = null;
            NinePatchSkin npPerKeyPressed = null;
            Bitmap perKeyPressedBmp = null;
            Picture perKeyPressedPic = null;
            if (this.keyIndex >= 0) {
                if (VirtualKeyboard.this.skinKeyNinePatches != null) {
                    npPerKey = VirtualKeyboard.this.skinKeyNinePatches[this.keyIndex];
                }
                if (VirtualKeyboard.this.skinKeyBitmaps != null) {
                    perKeyBmp = VirtualKeyboard.this.skinKeyBitmaps[this.keyIndex];
                }
                if (VirtualKeyboard.this.skinKeyPictures != null) {
                    perKeyPic = VirtualKeyboard.this.skinKeyPictures[this.keyIndex];
                }
                if (VirtualKeyboard.this.skinKeyPressedNinePatches != null) {
                    npPerKeyPressed = VirtualKeyboard.this.skinKeyPressedNinePatches[this.keyIndex];
                }
                if (VirtualKeyboard.this.skinKeyPressedBitmaps != null) {
                    perKeyPressedBmp = VirtualKeyboard.this.skinKeyPressedBitmaps[this.keyIndex];
                }
                if (VirtualKeyboard.this.skinKeyPressedPictures != null) {
                    perKeyPressedPic = VirtualKeyboard.this.skinKeyPressedPictures[this.keyIndex];
                }
            }
            if (this.selected && npPerKeyPressed != null) {
                hasSkin = true;
                VirtualKeyboard.drawNinePatchSkin(g, npPerKeyPressed, this.rect);
            } else if (this.selected && perKeyPressedPic != null) {
                hasSkin = true;
                VirtualKeyboard.drawSvgSkin(g, perKeyPressedPic, this.rect);
            } else if (this.selected && perKeyPressedBmp != null) {
                hasSkin = true;
                g.drawBitmap(perKeyPressedBmp, this.rect);
            } else if (npPerKey != null) {
                hasSkin = true;
                VirtualKeyboard.drawNinePatchSkin(g, npPerKey, this.rect);
                if (this.selected) {
                    g.setFillColor(Integer.MIN_VALUE);
                    drawShapeFill(g);
                }
            } else if (perKeyPic != null) {
                hasSkin = true;
                VirtualKeyboard.drawSvgSkin(g, perKeyPic, this.rect);
                if (this.selected) {
                    g.setFillColor(Integer.MIN_VALUE);
                    drawShapeFill(g);
                }
            } else if (perKeyBmp != null) {
                hasSkin = true;
                g.drawBitmap(perKeyBmp, this.rect);
                if (this.selected) {
                    g.setFillColor(Integer.MIN_VALUE);
                    drawShapeFill(g);
                }
            }
            NinePatchSkin npSkin = VirtualKeyboard.this.skinNinePatch;
            NinePatchSkin npPressedSkin = VirtualKeyboard.this.skinPressedNinePatch;
            Picture skinPic = VirtualKeyboard.this.skinPicture;
            int bgColor2 = bgColor;
            Picture skinPressedPic = VirtualKeyboard.this.skinPressedPicture;
            int fgColor2 = fgColor;
            Bitmap skin = VirtualKeyboard.this.skinBitmap;
            Bitmap pressedSkin = VirtualKeyboard.this.skinPressedBitmap;
            if (!hasSkin) {
                if (npSkin != null && npPressedSkin != null) {
                    boolean hasSkin2 = this.selected;
                    VirtualKeyboard.drawNinePatchSkin(g, hasSkin2 ? npPressedSkin : npSkin, this.rect);
                    hasSkin = true;
                } else if (npSkin != null) {
                    hasSkin = true;
                    VirtualKeyboard.drawNinePatchSkin(g, npSkin, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else if (npPressedSkin != null) {
                    hasSkin = true;
                    VirtualKeyboard.drawNinePatchSkin(g, npPressedSkin, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else if (skinPic != null && skinPressedPic != null) {
                    VirtualKeyboard.drawSvgSkin(g, this.selected ? skinPressedPic : skinPic, this.rect);
                    hasSkin = true;
                } else if (skinPic != null) {
                    hasSkin = true;
                    VirtualKeyboard.drawSvgSkin(g, skinPic, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else if (skinPressedPic != null) {
                    hasSkin = true;
                    VirtualKeyboard.drawSvgSkin(g, skinPressedPic, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else if (skin != null && pressedSkin != null) {
                    hasSkin = true;
                    g.drawBitmap(this.selected ? pressedSkin : skin, this.rect);
                } else if (skin != null) {
                    hasSkin = true;
                    g.drawBitmap(skin, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else if (pressedSkin != null) {
                    hasSkin = true;
                    g.drawBitmap(pressedSkin, this.rect);
                    if (this.selected) {
                        g.setFillColor(Integer.MIN_VALUE);
                        drawShapeFill(g);
                    }
                } else {
                    g.setFillColor((VirtualKeyboard.this.layoutEditMode != -1 ? 1426063360 : alpha) | bgColor2);
                    drawShapeFill(g);
                }
            }
            if (this.keyIndex >= 0) {
                Picture iconPic = null;
                if (VirtualKeyboard.this.skinKeyIconBitmaps != null) {
                    iconBmp = VirtualKeyboard.this.skinKeyIconBitmaps[this.keyIndex];
                } else {
                    iconBmp = null;
                }
                if (VirtualKeyboard.this.skinKeyIconPictures != null) {
                    iconPic = VirtualKeyboard.this.skinKeyIconPictures[this.keyIndex];
                }
                if (VirtualKeyboard.this.settings.vkSkinIconScales != null && this.keyIndex < VirtualKeyboard.this.settings.vkSkinIconScales.length) {
                    iconScale = VirtualKeyboard.this.settings.vkSkinIconScales[this.keyIndex];
                } else {
                    iconScale = 1.0f;
                }
                if (iconBmp != null) {
                    VirtualKeyboard.drawIconCentered(g, iconBmp, this.rect, iconScale);
                } else if (iconPic != null) {
                    VirtualKeyboard.drawSvgCentered(g, iconPic, this.rect, iconScale);
                }
            }
            if (!hasSkin) {
                g.setTextColor(alpha | fgColor2);
                g.setDrawColor(alpha | VirtualKeyboard.this.settings.vkOutlineColor);
                drawShapeOutline(g);
                g.drawString(this.label, this.rect.centerX(), this.rect.centerY());
            }
        }

        private void drawShapeFill(CanvasWrapper g) {
            switch (VirtualKeyboard.this.settings.vkButtonShape) {
                case 0:
                    g.fillArc(this.rect, 0, 360);
                    break;
                case 1:
                    g.fillRect(this.rect);
                    break;
                case 2:
                    g.fillRoundRect(this.rect, this.corners, this.corners);
                    break;
            }
        }

        private void drawShapeOutline(CanvasWrapper g) {
            switch (VirtualKeyboard.this.settings.vkButtonShape) {
                case 0:
                    g.drawArc(this.rect, 0, 360);
                    break;
                case 1:
                    g.drawRect(this.rect);
                    break;
                case 2:
                    g.drawRoundRect(this.rect, this.corners, this.corners);
                    break;
            }
        }

        public String toString() {
            return "[" + this.label + ": " + this.rect.left + ", " + this.rect.top + ", " + this.rect.right + ", " + this.rect.bottom + "]";
        }

        public int hashCode() {
            return this.hashCode;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (VirtualKeyboard.this.target == null) {
                this.selected = false;
                this.repeatCount = 0;
            } else if (this.selected) {
                onRepeat();
            } else {
                this.repeatCount = 0;
            }
        }

        public void onRepeat() {
            long j;
            Handler handler = VirtualKeyboard.this.handler;
            if (this.repeatCount > 6) {
                j = 80;
            } else {
                long[] jArr = VirtualKeyboard.REPEAT_INTERVALS;
                int i = this.repeatCount;
                this.repeatCount = i + 1;
                j = jArr[i];
            }
            handler.postDelayed(this, j);
            VirtualKeyboard.this.target.postKeyRepeated(this.keyCode);
        }

        protected void onDown() {
            this.selected = true;
            VirtualKeyboard.this.target.postKeyPressed(this.keyCode);
            VirtualKeyboard.this.handler.postDelayed(this, 400L);
        }

        public void onUp() {
            this.selected = false;
            VirtualKeyboard.this.handler.removeCallbacks(this);
            VirtualKeyboard.this.target.postKeyReleased(this.keyCode);
        }
    }

    private class DualKey extends VirtualKey {
        private final int hashCode;
        final int secondKeyCode;

        DualKey(int keyCode, int secondKeyCode, String label) {
            super(keyCode, label);
            if (secondKeyCode == 0) {
                throw new IllegalArgumentException();
            }
            this.secondKeyCode = secondKeyCode;
            this.hashCode = ((this.keyCode + 31) * 31) + this.secondKeyCode;
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        protected void onDown() {
            super.onDown();
            VirtualKeyboard.this.target.postKeyPressed(this.secondKeyCode);
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        public void onUp() {
            super.onUp();
            VirtualKeyboard.this.target.postKeyReleased(this.secondKeyCode);
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        public void onRepeat() {
            super.onRepeat();
            VirtualKeyboard.this.target.postKeyRepeated(this.secondKeyCode);
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        public int hashCode() {
            return this.hashCode;
        }
    }

    private class MenuKey extends VirtualKey {
        MenuKey() {
            super(0, "M");
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        protected void onDown() {
            this.selected = true;
            VirtualKeyboard.this.handler.postDelayed(this, 500L);
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey
        public void onUp() {
            if (this.selected) {
                this.selected = false;
                VirtualKeyboard.this.handler.removeCallbacks(this);
                MicroActivity activity = ContextHolder.getActivity();
                if (activity != null) {
                    activity.openOptionsMenu();
                }
            }
        }

        @Override // javax.microedition.lcdui.keyboard.VirtualKeyboard.VirtualKey, java.lang.Runnable
        public void run() {
            this.selected = false;
            final MicroActivity activity = ContextHolder.getActivity();
            if (activity != null) {
                activity.getClass();
                activity.runOnUiThread(new Runnable() { // from class: javax.microedition.lcdui.keyboard.VirtualKeyboard$MenuKey$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        activity.showExitConfirmation();
                    }
                });
            }
        }
    }

    private class Joystick implements Runnable {
        private static final int DIR_DOWN = 6;
        private static final int DIR_DOWN_LEFT = 5;
        private static final int DIR_DOWN_RIGHT = 7;
        private static final int DIR_LEFT = 4;
        private static final int DIR_RIGHT = 0;
        private static final int DIR_UP = 2;
        private static final int DIR_UP_LEFT = 3;
        private static final int DIR_UP_RIGHT = 1;
        int activePointer;
        int alternateKey;
        boolean alternatePhase;
        float centerX;
        float centerY;
        int currentDir;
        float deadZone;
        float normCenterX;
        float normCenterY;
        float normRadius;
        float radius;
        int repeatCount;
        boolean selected;
        final PointF thumbOffset;
        boolean visible;

        private Joystick() {
            this.normCenterX = 0.5f;
            this.normCenterY = 0.5f;
            this.normRadius = 0.12f;
            this.deadZone = 0.0f;
            this.activePointer = -1;
            this.currentDir = -1;
            this.alternateKey = -1;
            this.thumbOffset = new PointF();
            this.selected = false;
            this.visible = true;
        }

        void resize(float screenW, float screenH) {
            this.centerX = this.normCenterX * screenW;
            this.centerY = this.normCenterY * screenH;
            this.radius = this.normRadius * Math.min(screenW, screenH);
        }

        boolean isEnabled() {
            return VirtualKeyboard.this.settings.joyEnabled;
        }

        boolean contains(float x, float y) {
            if (!isEnabled() || !this.visible || !VirtualKeyboard.this.visible) {
                return false;
            }
            if (VirtualKeyboard.this.layoutEditMode == -1 && VirtualKeyboard.this.settings.vkAlpha == 0) {
                return false;
            }
            float dx = x - this.centerX;
            float dy = y - this.centerY;
            return (dx * dx) + (dy * dy) <= this.radius * this.radius;
        }

        void pointerPressed(float x, float y, int pointer) {
            this.activePointer = pointer;
            this.thumbOffset.set(0.0f, 0.0f);
            this.currentDir = -1;
        }

        void pointerDragged(float x, float y, int pointer) {
            int dir;
            int newKeyIdx;
            if (pointer != this.activePointer) {
                return;
            }
            float dx = x - this.centerX;
            float dy = y - this.centerY;
            float dist = (float) Math.sqrt((dx * dx) + (dy * dy));
            float deadPx = this.deadZone * this.radius;
            if (dist < deadPx) {
                this.thumbOffset.set(0.0f, 0.0f);
                return;
            }
            if (dist > this.radius) {
                float ratio = this.radius / dist;
                dx *= ratio;
                dy *= ratio;
            }
            this.thumbOffset.set(dx, dy);
            float angle = (float) Math.atan2(-dy, dx);
            if (angle < 0.0f) {
                angle += 6.2831855f;
            }
            if (VirtualKeyboard.this.settings.joyMode != 1) {
                dir = Math.round(angle / 0.7853982f) % 8;
            } else if (angle < 0.7853981633974483d) {
                dir = 0;
            } else if (angle < 2.356194490192345d) {
                dir = 2;
            } else if (angle < 3.9269908169872414d) {
                dir = 4;
            } else {
                dir = ((double) angle) < 5.497787143782138d ? 6 : 0;
            }
            if (dir != this.currentDir) {
                VirtualKeyboard.this.handler.removeCallbacks(this);
                boolean z = false;
                if (this.alternateKey != -1 && VirtualKeyboard.this.target != null) {
                    if (this.alternateKey >= 0 && this.alternateKey < VirtualKeyboard.this.keypad.length) {
                        VirtualKeyboard.this.keypad[this.alternateKey].selected = false;
                        VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                    }
                    this.alternateKey = -1;
                    this.alternatePhase = false;
                }
                if (dir >= 0 && dir < VirtualKeyboard.this.settings.joyMap.length && VirtualKeyboard.this.target != null && (newKeyIdx = VirtualKeyboard.this.settings.joyMap[dir]) >= 0 && newKeyIdx < VirtualKeyboard.this.keypad.length) {
                    VirtualKeyboard.this.keypad[newKeyIdx].selected = true;
                    VirtualKeyboard.this.target.postKeyPressed(VirtualKeyboard.this.keypad[newKeyIdx].keyCode);
                }
                int newKeyIdx2 = this.currentDir;
                if (newKeyIdx2 != -1) {
                    int newKeyIdx3 = (dir < 0 || dir >= VirtualKeyboard.this.settings.joyMap.length) ? -1 : VirtualKeyboard.this.settings.joyMap[dir];
                    int[] iArr = VirtualKeyboard.this.settings.joyMap;
                    int length = iArr.length;
                    int i = 0;
                    while (i < length) {
                        int keyIdx = iArr[i];
                        if (keyIdx >= 0 && keyIdx < VirtualKeyboard.this.keypad.length && keyIdx != newKeyIdx3) {
                            VirtualKeyboard.this.keypad[keyIdx].selected = z;
                            if (VirtualKeyboard.this.target != null) {
                                VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[keyIdx].keyCode);
                            }
                        }
                        i++;
                        z = false;
                    }
                }
                int altKey = getAlternateKey(dir);
                if (altKey != -1) {
                    this.alternateKey = altKey;
                    this.alternatePhase = false;
                    if (VirtualKeyboard.this.settings.joyRepeatDelay == 0 && altKey >= 0 && altKey < VirtualKeyboard.this.keypad.length && VirtualKeyboard.this.target != null) {
                        VirtualKeyboard.this.keypad[altKey].selected = true;
                        VirtualKeyboard.this.target.postKeyPressed(VirtualKeyboard.this.keypad[altKey].keyCode);
                    }
                } else {
                    this.alternateKey = -1;
                }
                this.repeatCount = 0;
                VirtualKeyboard.this.handler.postDelayed(this, 100L);
                this.currentDir = dir;
            }
        }

        void pointerReleased(int pointer) {
            if (pointer != this.activePointer) {
                return;
            }
            this.activePointer = -1;
            if (this.currentDir != -1) {
                releaseAllDirections();
                this.currentDir = -1;
            }
            this.thumbOffset.set(0.0f, 0.0f);
        }

        void clearState() {
            this.activePointer = -1;
            if (this.currentDir != -1) {
                releaseAllDirections();
                this.currentDir = -1;
            }
            this.thumbOffset.set(0.0f, 0.0f);
        }

        private void pressDirection(int dir) {
            int keyIdx;
            if (dir < 0 || dir >= VirtualKeyboard.this.settings.joyMap.length || VirtualKeyboard.this.target == null || (keyIdx = VirtualKeyboard.this.settings.joyMap[dir]) < 0 || keyIdx >= VirtualKeyboard.this.keypad.length) {
                return;
            }
            VirtualKeyboard.this.keypad[keyIdx].selected = true;
            VirtualKeyboard.this.target.postKeyPressed(VirtualKeyboard.this.keypad[keyIdx].keyCode);
            this.repeatCount = 0;
            VirtualKeyboard.this.handler.postDelayed(this, 400L);
        }

        private void releaseAllDirections() {
            VirtualKeyboard.this.handler.removeCallbacks(this);
            for (int keyIdx : VirtualKeyboard.this.settings.joyMap) {
                if (keyIdx >= 0 && keyIdx < VirtualKeyboard.this.keypad.length) {
                    VirtualKeyboard.this.keypad[keyIdx].selected = false;
                    if (VirtualKeyboard.this.target != null) {
                        VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[keyIdx].keyCode);
                    }
                }
            }
            if (this.alternateKey != -1 && VirtualKeyboard.this.target != null) {
                if (this.alternateKey >= 0 && this.alternateKey < VirtualKeyboard.this.keypad.length) {
                    VirtualKeyboard.this.keypad[this.alternateKey].selected = false;
                    VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                }
                this.alternateKey = -1;
                this.alternatePhase = false;
            }
        }

        private int getAlternateKey(int dir) {
            if (VirtualKeyboard.this.settings.joyPreset != 4) {
                return -1;
            }
            switch (dir) {
            }
            return -1;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.currentDir < 0 || this.currentDir >= VirtualKeyboard.this.settings.joyMap.length || VirtualKeyboard.this.target == null) {
                return;
            }
            long d = 80;
            if (this.alternateKey != -1) {
                int delay = VirtualKeyboard.this.settings.joyRepeatDelay;
                if (delay > 0) {
                    int primary = VirtualKeyboard.this.settings.joyMap[this.currentDir];
                    if (this.alternatePhase) {
                        if (primary >= 0 && primary < VirtualKeyboard.this.keypad.length) {
                            VirtualKeyboard.this.keypad[this.alternateKey].selected = false;
                            VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                            VirtualKeyboard.this.keypad[primary].selected = true;
                            VirtualKeyboard.this.target.postKeyPressed(VirtualKeyboard.this.keypad[primary].keyCode);
                            VirtualKeyboard.this.target.postKeyRepeated(VirtualKeyboard.this.keypad[primary].keyCode);
                        }
                    } else if (this.alternateKey >= 0 && this.alternateKey < VirtualKeyboard.this.keypad.length) {
                        VirtualKeyboard.this.keypad[primary].selected = false;
                        VirtualKeyboard.this.target.postKeyReleased(VirtualKeyboard.this.keypad[primary].keyCode);
                        VirtualKeyboard.this.keypad[this.alternateKey].selected = true;
                        VirtualKeyboard.this.target.postKeyPressed(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                        VirtualKeyboard.this.target.postKeyRepeated(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                    }
                    this.alternatePhase = !this.alternatePhase;
                    VirtualKeyboard.this.handler.postDelayed(this, delay);
                    return;
                }
                int keyIdx = VirtualKeyboard.this.settings.joyMap[this.currentDir];
                if (keyIdx >= 0 && keyIdx < VirtualKeyboard.this.keypad.length) {
                    VirtualKeyboard.this.target.postKeyRepeated(VirtualKeyboard.this.keypad[keyIdx].keyCode);
                }
                if (this.alternateKey >= 0 && this.alternateKey < VirtualKeyboard.this.keypad.length) {
                    VirtualKeyboard.this.target.postKeyRepeated(VirtualKeyboard.this.keypad[this.alternateKey].keyCode);
                }
                if (this.repeatCount <= 6) {
                    long[] jArr = VirtualKeyboard.REPEAT_INTERVALS;
                    int i = this.repeatCount;
                    this.repeatCount = i + 1;
                    d = jArr[i];
                }
                VirtualKeyboard.this.handler.postDelayed(this, d);
                return;
            }
            int keyIdx2 = VirtualKeyboard.this.settings.joyMap[this.currentDir];
            if (keyIdx2 < 0 || keyIdx2 >= VirtualKeyboard.this.keypad.length) {
                return;
            }
            VirtualKeyboard.this.target.postKeyRepeated(VirtualKeyboard.this.keypad[keyIdx2].keyCode);
            if (this.repeatCount <= 6) {
                long[] jArr2 = VirtualKeyboard.REPEAT_INTERVALS;
                int i2 = this.repeatCount;
                this.repeatCount = i2 + 1;
                d = jArr2[i2];
            }
            VirtualKeyboard.this.handler.postDelayed(this, d);
        }

        void paint(CanvasWrapper g) {
            if (this.visible && VirtualKeyboard.this.visible && isEnabled()) {
                if (VirtualKeyboard.this.layoutEditMode == -1 && VirtualKeyboard.this.settings.vkAlpha == 0) {
                    return;
                }
                int alphaInt = VirtualKeyboard.this.layoutEditMode != -1 ? 255 : VirtualKeyboard.this.settings.vkAlpha;
                int alpha = alphaInt << 24;
                g.setAlpha(alphaInt);
                int bgColor = VirtualKeyboard.this.settings.vkBgColor;
                int fgColor = VirtualKeyboard.this.settings.vkFgColor;
                int outlineColor = VirtualKeyboard.this.settings.vkOutlineColor;
                RectF outer = new RectF(this.centerX - this.radius, this.centerY - this.radius, this.centerX + this.radius, this.centerY + this.radius);
                if (this.selected) {
                    g.setFillColor(-2130706433);
                    g.fillArc(outer, 0, 360);
                } else if (VirtualKeyboard.this.joySkinNinePatch != null) {
                    VirtualKeyboard.drawNinePatchSkin(g, VirtualKeyboard.this.joySkinNinePatch, outer);
                } else if (VirtualKeyboard.this.joySkinPicture != null) {
                    VirtualKeyboard.drawSvgSkin(g, VirtualKeyboard.this.joySkinPicture, outer);
                } else if (VirtualKeyboard.this.joySkinBitmap != null) {
                    g.drawBitmap(VirtualKeyboard.this.joySkinBitmap, outer);
                } else {
                    g.setFillColor(alpha | bgColor);
                    g.fillArc(outer, 0, 360);
                    if (!this.selected) {
                        g.setDrawColor(alpha | outlineColor);
                        g.drawArc(outer, 0, 360);
                    }
                }
                float thumbR = this.radius * VirtualKeyboard.this.settings.joyThumbRadius;
                float tx = this.centerX + this.thumbOffset.x;
                float ty = this.centerY + this.thumbOffset.y;
                RectF inner = new RectF(tx - thumbR, ty - thumbR, tx + thumbR, ty + thumbR);
                if (VirtualKeyboard.this.joyThumbNinePatch != null) {
                    VirtualKeyboard.drawNinePatchSkin(g, VirtualKeyboard.this.joyThumbNinePatch, inner);
                } else if (VirtualKeyboard.this.joyThumbPicture != null) {
                    VirtualKeyboard.drawSvgSkin(g, VirtualKeyboard.this.joyThumbPicture, inner);
                } else if (VirtualKeyboard.this.joyThumbBitmap != null) {
                    g.drawBitmap(VirtualKeyboard.this.joyThumbBitmap, inner);
                } else {
                    g.setFillColor(alpha | fgColor);
                    g.fillArc(inner, 0, 360);
                    g.setDrawColor(alpha | outlineColor);
                    g.drawArc(inner, 0, 360);
                }
                if (this.selected) {
                    g.setFillColor(alpha | outlineColor);
                    g.fillRect(new RectF(this.centerX - this.radius, this.centerY - VirtualKeyboard.PHONE_KEY_SCALE_X, this.centerX + this.radius, this.centerY + VirtualKeyboard.PHONE_KEY_SCALE_X));
                    g.fillRect(new RectF(this.centerX - VirtualKeyboard.PHONE_KEY_SCALE_X, this.centerY - this.radius, this.centerX + VirtualKeyboard.PHONE_KEY_SCALE_X, this.centerY + this.radius));
                }
            }
        }
    }

    private static class NinePatchSkin {
        static final int PINK = -65281;
        final Bitmap bitmap;
        final int bottomFixed;
        final int leftFixed;
        final int rightFixed;
        final int srcHeight;
        final int srcWidth;
        final int topFixed;

        NinePatchSkin(Bitmap bitmap, int srcWidth, int srcHeight, int leftFixed, int rightFixed, int topFixed, int bottomFixed) {
            this.bitmap = bitmap;
            this.srcWidth = srcWidth;
            this.srcHeight = srcHeight;
            this.leftFixed = leftFixed;
            this.rightFixed = rightFixed;
            this.topFixed = topFixed;
            this.bottomFixed = bottomFixed;
        }

        static NinePatchSkin load(String fullPath) {
            Bitmap fullBmp = BitmapFactory.decodeFile(fullPath);
            if (fullBmp == null) {
                return null;
            }
            int bmpW = fullBmp.getWidth();
            int bmpH = fullBmp.getHeight();
            if (bmpW < 3 || bmpH < 3) {
                return null;
            }
            int firstPinkX = -1;
            int lastPinkX = -1;
            for (int x = 1; x < bmpW - 1; x++) {
                if (fullBmp.getPixel(x, 0) == PINK) {
                    if (firstPinkX == -1) {
                        firstPinkX = x;
                    }
                    lastPinkX = x;
                }
            }
            int firstPinkY = -1;
            int lastPinkY = -1;
            for (int y = 1; y < bmpH - 1; y++) {
                if (fullBmp.getPixel(0, y) == PINK) {
                    if (firstPinkY == -1) {
                        firstPinkY = y;
                    }
                    lastPinkY = y;
                }
            }
            if (firstPinkX == -1 || firstPinkY == -1) {
                return null;
            }
            int leftFixed = firstPinkX - 1;
            int rightFixed = (bmpW - 2) - lastPinkX;
            int topFixed = firstPinkY - 1;
            int bottomFixed = (bmpH - 2) - lastPinkY;
            Bitmap cropped = Bitmap.createBitmap(fullBmp, 1, 1, bmpW - 2, bmpH - 2);
            fullBmp.recycle();
            return new NinePatchSkin(cropped, bmpW - 2, bmpH - 2, leftFixed, rightFixed, topFixed, bottomFixed);
        }
    }
}
