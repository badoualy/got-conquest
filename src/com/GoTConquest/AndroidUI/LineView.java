package com.GoTConquest.AndroidUI;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

class LineView extends View {
	Paint paint = new Paint();
	List<CityButton> cities;
	boolean drawSrc = false, drawTarget = false;
	float startX;
	float startY;
	float endX;
	float endY;

	public LineView(Context context, List<CityButton> cities, float ratioX, float ratioY) {
		super(context);
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);

		this.cities = cities;

		setMinimumHeight((int) (ratioY * MapUI.WESTEROS_HEIGHT));
		setMinimumWidth((int) (ratioX * MapUI.WESTEROS_WIDTH));
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (drawSrc) {
			if (drawTarget)
				canvas.drawLine(startX, startY, endX, endY, paint);
			else
				for (CityButton cityButton : cities)
					if (cityButton.isInPath()) {
						cityButton.setStrokeColor(Color.rgb(29, 168, 208));
						cityButton.setSelected(true);
					}
		}
	}

	public void setSrcCoordinates(float startX, float startY) {
		this.startX = startX;
		this.startY = startY;
	}

	public void setTargetCoordinates(float endX, float endY) {
		this.endX = endX;
		this.endY = endY;
	}

	public void setColor(int color) {
		paint.setColor(color);
	}

	public void setDrawSrc(boolean draw) {
		drawSrc = draw;
	}

	public void setDrawTarget(boolean draw) {
		drawTarget = draw;
	}
}
