package com.GoTConquest.AndroidUI;

import gotconquest.core.City;
import gotconquest.core.Player;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.RelativeLayout;

public class CityButton extends Button {
	private double ratioX;
	private double ratioY;
	private int offsetX;
	private int offsetY;
	private City city;
	private int densityDpi;
	private int strokeColor;

	public CityButton(Activity activity, City city, double ratioX, double ratioY) {
		super(activity);

		this.city = city;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		densityDpi = metrics.densityDpi;

		setText("1");
		setTextColor(Color.WHITE);
		setPadding(0, -2, 0, 0);

		if (densityDpi >= 240)
			setTextSize(9);
		else
			setTextSize(14);

		this.ratioX = ratioX;
		this.ratioY = ratioY;

		setMinHeight(28);
		setMinWidth(35);
	}

	public void addReinforcements(int value) {
		city.addReinforcements(value);
		this.updateText();
	}

	public void setOffset(int posX, int posY) {
		offsetX = (int) (posX * this.ratioX) - 5;
		offsetY = (int) (posY * this.ratioY) - 5;

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.getLayoutParams());
		params.setMargins(offsetX, offsetY, 0, 0);
		this.setLayoutParams(params);

		this.updateText();
	}

	public void updateText() {
		this.setText("" + city.getReinforcements());
	}

	public float getCenterX() {
		float centerX = (float) offsetX;
		if (densityDpi >= 240)
			centerX += 15;
		else
			centerX += 10;

		return centerX;
	}

	public float getCenterY() {
		float centerY = (float) offsetY;
		if (densityDpi >= 240)
			centerY += 15;
		else
			centerY += 10;

		return centerY;
	}

	public City getCity() {
		return city;
	}

	public String getName() {
		return city.getName();
	}

	public Player getOwner() {
		return city.getOwner();
	}

	public int getReinforcements() {
		return city.getReinforcements();
	}

	public boolean isInPath() {
		return city.isInPath();
	}

	public void setBackgroundResource(int[] buttonColors) {
		super.setBackgroundDrawable(CityDrawableFactory.getDrawable(buttonColors));
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		GradientDrawable drawable = (GradientDrawable) getBackground();
		if (selected)
			drawable.setStroke(4, strokeColor);
		else {
			strokeColor = Color.WHITE;
			drawable.setStroke(2, strokeColor);
		}
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getStrokeColor() {
		return strokeColor;
	}
}
