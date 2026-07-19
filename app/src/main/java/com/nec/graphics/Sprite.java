package com.nec.graphics;

import javax.microedition.lcdui.Image;

public final class Sprite {
	protected int x;
	protected int y;
	protected int flags;
	protected int priority;
	protected boolean visible;
	protected Image image;
	static final int DEFAULT_PRIORITY = 15;

	public Sprite() {
		this.image = null;
	}

	public Sprite(Image image) throws NullPointerException {
		this.image = image;
	}

	public Sprite(Image image, int x, int y, boolean visible)
			throws IllegalArgumentException, NullPointerException {
		this.image = image;
		this.x = x;
		this.y = y;
		this.visible = visible;
	}

	public void setLocation(int i, int j) throws IllegalArgumentException {
		this.x = i;
		this.y = j;
	}

	public void setImage(Image image) throws NullPointerException {
		this.image = image;
	}

	public void setVisible(boolean flag) {
		this.visible = flag;
	}

	public boolean isVisible() {
		return visible;
	}
}
