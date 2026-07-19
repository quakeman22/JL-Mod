package com.lge.lcdui;

import javax.microedition.lcdui.Font;

/**
 * LG OEM font extension. Real LG devices exposed extra bitmap fonts here;
 * we just fall back to the default MIDP font, same as KEmulator does.
 */
public class OemFont {

	public static Font getFont(int n1, int n2, int n3, int n4) {
		return Font.getDefaultFont();
	}
}
