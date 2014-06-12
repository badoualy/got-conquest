package gotconquest.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kingdom implements Serializable {
	private static final long serialVersionUID = 8395578131370103137L;
	private String name;
	private int reinforcements;
	private List<City> cities = new ArrayList<City>(5);

	public Kingdom(String name, int reinforcements) {
		this.name = name;
		this.reinforcements = reinforcements;
	}

	public List<City> getCities() {
		return cities;
	}

	public void addCity(City city) {
		cities.add(city);
	}

	public boolean isOwner(Player player) {
		for (City city : cities) {
			if (city.getOwner() != player) {
				return false;
			}
		}
		return true;
	}

	public Player getPlayerHavingMaxCity() {
		Player[] players = new Player[9];
		Player player = null;
		int[] nbCities = new int[9];
		int max = 0;
		for (City city : cities) {
			int i = city.getOwner().getColor();
			players[i] = city.getOwner();
			nbCities[i]++;
			if (nbCities[i] > max) {
				max = nbCities[i];
				player = players[i];
			}
		}
		return player;
	}

	public int getReinforcements() {
		return reinforcements;
	}

	public String getName() {
		return name;
	}
}
