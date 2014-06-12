package gotconquest.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class City implements Serializable {
	private static final long serialVersionUID = 6857117333966704407L;
	private String name;
	private int x;
	private int y;
	private int reinforcements = 1;

	private Player owner = null;
	private List<Integer> frontsIndex = new ArrayList<Integer>(4);
	private transient List<City> fronts = new ArrayList<City>(4);
	private transient boolean inPath = false;

	public City(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public void addFront(City city, int index) {
		fronts.add(city);

		if (!frontsIndex.contains(index))
			frontsIndex.add(index);
	}

	public void addReinforcements(int reinforcements) {
		this.reinforcements += reinforcements;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public void setInPath(boolean value) {
		inPath = value;
	}

	public Player getOwner() {
		return owner;
	}

	public int getReinforcements() {
		return reinforcements;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isInPath() {
		return inPath;
	}

	public List<City> getFronts() {
		return fronts;
	}

	public List<Integer> getFrontsIndex() {
		return frontsIndex;
	}

	public boolean isFront(City clickedCity) {
		for (City city : fronts) {
			if (city == clickedCity)
				return true;
		}
		return false;
	}

	public void removeReinforcement() {
		reinforcements--;
	}

	public void setReinforcements(int reinforcements) {
		this.reinforcements = reinforcements;
	}

	public void setFronts(List<City> fronts) {
		this.fronts = fronts;
	}
}
