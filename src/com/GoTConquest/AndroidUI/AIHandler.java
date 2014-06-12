package com.GoTConquest.AndroidUI;

import gotconquest.ai.AI;
import gotconquest.core.City;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AIHandler {
	private MapUI ui;

	public AIHandler(MapUI ui) {
		this.ui = ui;
	}

	public void initStep(AI ai, int maxReinforcements) {
		City city = ai.initStep(maxReinforcements);
		CityButton cityButton = ui.getCityButton(city);
		ui.setCurrentReinforcements(maxReinforcements);
		ui.doCityClick(cityButton);
	}

	public void addReinforcementStep(AI ai, int maxReinforcements) {
		City city = ai.addReinforcementStep(maxReinforcements);
		CityButton cityButton = ui.getCityButton(city);
		ui.setCurrentReinforcements(maxReinforcements);
		scheduleCityClick(cityButton, 1250);
	}

	public void attackStep(AI ai) {
		if (new Random().nextInt(6) < 2) {
			scheduleEndClick(750);
		} else {
			ai.attackStep();
			City source = ai.getSource();
			City target = ai.getTarget();
			if (source == null) {
				scheduleEndClick(750);
			} else {
				ui.scrollOnCity(ui.getCityButton(source));
				ui.doCityClick(ui.getCityButton(source));
				ui.doCityClick(ui.getCityButton(target));
			}
		}
	}

	public void attackReinforcementStep(AI ai, int maxReinforcements) {
		ui.setCurrentReinforcements(maxReinforcements);
		scheduleValidClick(1000);
	}

	public void attackMoveStep(AI ai, int maxReinforcements) {
		int reinforcement = ai.attackMoveStep(maxReinforcements);
		ui.setCurrentReinforcements(reinforcement);
		scheduleValidClick(750);
	}

	private void scheduleValidClick(long delay) {
		Timer timer = ui.getTimer();
		if (timer == null)
			return;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				ui.doValidClick();
			}
		}, delay);
	}

	private void scheduleEndClick(long delay) {
		Timer timer = ui.getTimer();
		if (timer == null)
			return;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				ui.doEndClick();
			}
		}, delay);
	}

	private void scheduleCityClick(final CityButton cityButton, long delay) {
		Timer timer = ui.getTimer();
		if (timer == null)
			return;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				ui.scrollOnCity(cityButton);
				ui.doCityClick(cityButton);
			}
		}, delay);
	}

	public void moveStep(AI ai) {
		if (new Random().nextInt(5) == 0) {
			scheduleEndClick(750);
		} else {
			ai.moveStep();
			City source = ai.getSource();
			City target = ai.getTarget();

			if (source != null) {
				ui.getGameCore().initPath();
				ui.getGameCore().checkTargetability(source);
			}

			if (target != null && target.isInPath()) {
				ui.scrollOnCity(ui.getCityButton(source));
				ui.doCityClick(ui.getCityButton(source));
				ui.doCityClick(ui.getCityButton(target));
			} else
				scheduleEndClick(750);
		}
	}

	public void moveReinforcementStep(AI ai, int maxReinforcements) {
		int reinforcement = ai.moveReinforcementStep(maxReinforcements);
		ui.setCurrentReinforcements(reinforcement);
		scheduleValidClick(750);
	}
}
