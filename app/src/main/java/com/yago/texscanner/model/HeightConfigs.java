package com.yago.texscanner.model;

import android.graphics.Bitmap;
import android.support.v4.graphics.ColorUtils;
import com.yago.texscanner.GlobalContext;
import com.yago.texscanner.Utils;

public class HeightConfigs extends MapConfig {
	private int contrast = 1000;
	private int brightness = 256;
	private int fac = 50;
	private boolean inverted = false;

	public HeightConfigs(GlobalContext context) {
		super(context);
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public void setFac(int fac) {
		this.fac = fac;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	@Override
	public Bitmap render(Bitmap base) {
		final Bitmap map = Utils.rescale(base, contrast / 1000f, brightness - 256);
		final int[] pixels = new int[map.getWidth() * map.getHeight()];
		final float[][] hsls = new float[pixels.length][3];
		map.getPixels(pixels, 0, map.getWidth(), 0, 0, map.getWidth(), map.getHeight());

		for (int i = 0; i < pixels.length; i++) {
			ColorUtils.colorToHSL(pixels[i], hsls[i]);
		}
		for (int i = 0; i < pixels.length; i++) {
			adjust(i, pixels, hsls[i], fac);
		}

		map.setPixels(pixels, 0, map.getWidth(), 0, 0, map.getWidth(), map.getHeight());

		return Utils.toGrayscale(map, inverted);
	}

	private static void adjust(int i, int[] pixels, float[] hsl, int fac) {
		if (hsl[2] > 0.5f) {
			hsl[2] += (hsl[2] - 0.5f) * ((1 - hsl[2]) * (fac / 25f));
		} else if (hsl[2] < 0.5f) {
			hsl[2] -= (0.5f - hsl[2]) * (hsl[2] * (fac / 25f));
		}

		pixels[i] = ColorUtils.HSLToColor(hsl);
	}
}
