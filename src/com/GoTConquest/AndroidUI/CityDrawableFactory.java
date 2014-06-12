package com.GoTConquest.AndroidUI;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public abstract class CityDrawableFactory {
	public static GradientDrawable getDrawable(int[] colors) {
		GradientDrawable drawable = new GradientDrawable(Orientation.TOP_BOTTOM, colors);
		drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		drawable.setStroke(2, Color.rgb(255, 255, 255));
		drawable.setGradientRadius(20);
		drawable.setCornerRadius(5);
		drawable.setShape(GradientDrawable.OVAL);

		return drawable;
	}
}
