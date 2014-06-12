package gotconquest.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameCore implements Serializable {
	public enum Step {
		INIT, REINFORCEMENT, ATK, MOVE
	}

	public enum SubStep {
		SRC, TARGET, REINFORCEMENT, MOVE
	}

	private static final long serialVersionUID = 8061310705752242645L;
	private Map map;
	private List<Kingdom> kingdoms;
	private List<City> cities;
	private List<Player> players;
	private final List<Player> constPlayers;

	private Step currentStep = null;
	private SubStep currentSubStep = null;
	private City srcCity = null;
	private City targetCity = null;
	private int maxReinforcement = 0;
	private int reinforcement = 0;

	private int currentPlayerNumber = -1;
	private int turnCount = 0;

	public GameCore(Map map, List<Player> players, List<Kingdom> kingdoms, List<City> cities) {
		this.map = map;
		constPlayers = new ArrayList<Player>(players);
		this.players = players;
		this.cities = cities;
		this.kingdoms = kingdoms;
		Collections.shuffle(players);
		divideCities();
	}

	public void load() {
		for (City city : cities) {
			city.setFronts(new ArrayList<City>(4));

			for (Integer fronts : city.getFrontsIndex())
				city.addFront(cities.get(fronts), fronts);
		}
	}

	private void divideCities() {
		List<City> cities = new ArrayList<City>(this.cities);
		Collections.shuffle(cities);
		for (int i = 0; i < cities.size(); ++i) {
			Player p = players.get(i % players.size());
			cities.get(i).setOwner(p);
			p.addTerritory(cities.get(i));
		}
	}

	public int getReinforcement(Player player) {
		return player.getNextTurnReinforcements() + getReinforcementBonus(player);
	}

	public int getReinforcementBonus(Player player) {
		int reinforcement = 0;

		for (Kingdom kingdom : kingdoms) {
			if (kingdom.isOwner(player)) {
				reinforcement += kingdom.getReinforcements();
			}
		}

		return reinforcement;
	}

	public void checkTargetability(City city) {
		if (currentStep == Step.MOVE) {
			for (City adjCity : city.getFronts()) {
				if (adjCity == city)
					continue;
				if (adjCity.getOwner() == city.getOwner() && !adjCity.isInPath()) {
					adjCity.setInPath(true);
					checkTargetability(adjCity);
				}
			}
		} else {
			for (City adjCity : city.getFronts())
				if (adjCity.getOwner() != city.getOwner())
					adjCity.setInPath(true);
		}
	}

	public int attack(City srcCity, City targetedCity, int reinforcements) {
		while (reinforcements > 0 && targetedCity.getReinforcements() > 0) {
			int offenseDiceNumber = reinforcements > 2 ? 3 : reinforcements;
			int defenseDiceNumber = targetedCity.getReinforcements() > 2 ? 2 : 1;

			int[] offenseDice = new int[offenseDiceNumber];
			int[] defenseDice = new int[defenseDiceNumber];
			Random random = new Random();

			for (int i = 0; i < offenseDiceNumber; ++i)
				offenseDice[i] = random.nextInt(6);
			for (int i = 0; i < defenseDiceNumber; ++i)
				defenseDice[i] = random.nextInt(6);

			Arrays.sort(offenseDice);
			Arrays.sort(defenseDice);

			for (int i = 1; i <= offenseDiceNumber && i <= defenseDiceNumber; ++i) {
				if (offenseDice[offenseDiceNumber - i] > defenseDice[defenseDiceNumber - i])
					targetedCity.removeReinforcement();
				else {
					srcCity.removeReinforcement();
					reinforcements--;
				}
			}
		}

		if (reinforcements > 0) {
			Player player = targetedCity.getOwner();
			player.removeTerritory(targetedCity);
			if (player.getNumberOfTerritory() == 0)
				players.remove(player);
			player = srcCity.getOwner();
			targetedCity.setOwner(player);
			player.addTerritory(targetedCity);
		}

		return reinforcements;
	}

	public void initPath() {
		for (City city : cities)
			city.setInPath(false);
	}

	public Player getNextPlayer() {
		currentPlayerNumber++;
		currentPlayerNumber %= players.size();
		if (currentPlayerNumber == 0)
			turnCount++;
		return players.get(currentPlayerNumber);
	}

	public List<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public List<City> getCities() {
		return cities;
	}

	public int getAlivePlayers() {
		return players.size();
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public int indexOf(Player player) {
		return players.indexOf(player);
	}

	public Player getPlayer(int i) {
		return players.get(i);
	}

	public boolean isAlive(Player player) {
		return players.contains(player);
	}

	public int getTurnCount() {
		return turnCount;
	}

	public int getNumberOfTerritory() {
		return cities.size();
	}

	public int getNumberOfKingdom(Player player) {
		int totalKingdoms = 0;
		for (Kingdom kingdom : kingdoms)
			if (kingdom.isOwner(player))
				totalKingdoms++;

		return totalKingdoms;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Step getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(Step currentStep) {
		this.currentStep = currentStep;
	}

	public SubStep getCurrentSubStep() {
		return currentSubStep;
	}

	public void setCurrentSubStep(SubStep currentSubStep) {
		this.currentSubStep = currentSubStep;
	}

	public Player getCurrentPlayer() {
		return players.get(currentPlayerNumber % players.size());
	}

	public City getSrcCity() {
		return srcCity;
	}

	public void setSrcCity(City srcCity) {
		this.srcCity = srcCity;
	}

	public City getTargetCity() {
		return targetCity;
	}

	public void setTargetCity(City targetCity) {
		this.targetCity = targetCity;
	}

	public int getMaxReinforcement() {
		return maxReinforcement;
	}

	public void setMaxReinforcement(int maxReinforcement) {
		this.maxReinforcement = maxReinforcement;
	}

	public int getReinforcement() {
		return reinforcement;
	}

	public void setReinforcement(int reinforcement) {
		this.reinforcement = reinforcement;
	}

	public List<Player> getConstPlayers() {
		return constPlayers;
	}

	public Map getMap() {
		return map;
	}
}