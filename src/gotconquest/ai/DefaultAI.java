package gotconquest.ai;

import gotconquest.core.City;
import gotconquest.core.Kingdom;
import gotconquest.core.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultAI extends AI {
	private static final long serialVersionUID = -4297992186250837650L;

	public DefaultAI(Player player) {
		super(player);
	}

	private Kingdom getInitialKingdom() {
		List<Kingdom> kingdoms = getGame().getKingdoms();
		Kingdom best = null;

		for (Kingdom kingdom : kingdoms) {
			Player player = kingdom.getPlayerHavingMaxCity();
			if (player == getPlayer() && !kingdom.isOwner(player)) {
				best = kingdom;
			}
		}
		return best;
	}

	private City getFirstCityWithFront(List<City> cities) {
		for (City city : cities) {
			for (City front : city.getFronts()) {
				if (front.getOwner() != getPlayer()) {
					return city;
				}
			}
		}
		return null;
	}

	@Override
	public City initStep(int maxReinforcements) {
		Kingdom kingdom = getInitialKingdom();

		if (kingdom == null) {
			List<City> cities = new ArrayList<City>(getPlayer().getCities());
			Collections.shuffle(cities);
			City city = getFirstCityWithFront(cities);
			return city == null ? cities.get(0) : city;
		} else {
			for (City city : kingdom.getCities()) {
				if (city.getOwner() == getPlayer()) {
					for (City front : city.getFronts()) {
						if (front.getOwner() != getPlayer()) {
							return city;
						}
					}
				}
			}
			return null;
		}
	}

	@Override
	public City addReinforcementStep(int maxReinforcements) {
		return initStep(maxReinforcements);
	}

	@Override
	public void attackStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public int attackMoveStep(int maxReinforcements) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int moveReinforcementStep(int maxReinforcements) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void moveStep() {
		// TODO Auto-generated method stub

	}

}
