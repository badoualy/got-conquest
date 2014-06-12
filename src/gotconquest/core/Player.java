package gotconquest.core;

import gotconquest.ai.AI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
	private static final long serialVersionUID = -1177572729727841874L;
	private String name;
	private List<City> cities = new ArrayList<City>(5);
	private int color;
	private int colorCode;
	private AI ai = null;

	private boolean won = false;

	public Player(String name, int color, int colorCode) {
		this.name = name;
		this.color = color;
		this.colorCode = colorCode;
	}

	public boolean isAI() {
		return ai != null;
	}

	public void setAI(AI ai) {
		this.ai = ai;
	}

	public AI getAI() {
		return ai;
	}

	public boolean hasWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}

	public void addTerritory(City city) {
		cities.add(city);
	}

	public void removeTerritory(City city) {
		cities.remove(city);
	}

	public boolean isAlive() {
		return !cities.isEmpty();
	}

	public int getNumberOfTerritory() {
		return cities.size();
	}

	public String getName() {
		return this.name;
	}

	public int getColor() {
		return this.color;
	}

	public int getColorCode() {
		return colorCode;
	}

	public int getNumberOfTroops() {
		int troops = 0;

		for (City city : cities)
			troops += city.getReinforcements();

		return troops;
	}

	public int getNextTurnReinforcements() {
		int reinforcement = cities.size() / 3;
		if (reinforcement < 3)
			reinforcement = 3;

		return reinforcement;
	}

	public List<City> getCities() {
		return cities;
	}
}
