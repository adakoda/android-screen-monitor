package com.adakoda.android.asm;

import java.awt.image.BufferedImage;

public class FBImage extends BufferedImage {

	private int mRawWidth;
	private int mRawHeight;

	public FBImage(int width, int height, int imageType, int rawWidth,
			int rawHeight) {
		super(width, height, imageType);
		mRawWidth = rawWidth;
		mRawHeight = rawHeight;
	}

	public int getRawWidth() {
		return mRawWidth;
	}

	public int getRawHeight() {
		return mRawHeight;
	}
}
