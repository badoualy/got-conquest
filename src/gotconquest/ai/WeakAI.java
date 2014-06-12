package gotconquest.ai;

import gotconquest.core.City;
import gotconquest.core.Player;

import java.util.Random;

public class WeakAI extends AI {
	private static final long serialVersionUID = -290798059638618619L;

	public WeakAI(Player player) {
		super(player);
	}

	@Override
	public City initStep(int maxReinforcements) {
		City reinforcedCity = getPlayer().getCities().get(new Random().nextInt(getPlayer().getNumberOfTerritory()));
		int reinforcementDelta = 0;

		for (City city : getPlayer().getCities()) {
			for (City front : city.getFronts())
				if (front.getOwner() != getPlayer() && city.getReinforcements() - front.getReinforcements() > reinforcementDelta) {
					reinforcedCity = city;
					reinforcementDelta = front.getReinforcements() - city.getReinforcements();
				}
		}
		return reinforcedCity;
	}

	@Override
	public City addReinforcementStep(int maxReinforcements) {
		City reinforcedCity = getPlayer().getCities().get(new Random().nextInt(getPlayer().getNumberOfTerritory()));
		int reinforcementDelta = 0;

		for (City city : getPlayer().getCities()) {
			for (City front : city.getFronts())
				if (front.getOwner() != getPlayer() && city.getReinforcements() - front.getReinforcements() > reinforcementDelta) {
					reinforcedCity = city;
					reinforcementDelta = front.getReinforcements() - city.getReinforcements();
				}

		}
		return reinforcedCity;
	}

	@Override
	public void attackStep() {

		int reinforcementDelta = -1;
		City source = null;
		City target = null;

		for (City city : getPlayer().getCities()) {
			if (city.getReinforcements() == 1)
				continue;
			for (City front : city.getFronts())
				if (front.getOwner() != getPlayer() && city.getReinforcements() - front.getReinforcements() > reinforcementDelta) {
					source = city;
					target = front;
					reinforcementDelta = city.getReinforcements() - front.getReinforcements();
				}
		}
		setSource(source);
		setTarget(target);
	}

	@Override
	public int attackMoveStep(int maxReinforcements) {
		int reinforcement = new Random().nextInt(maxReinforcements);
		return (++reinforcement);
	}

	@Override
	public void moveStep() {
		int reinforcementsDelta = 0;
		int frontDelta = Integer.MAX_VALUE, tmpFrontDelta = 0;
		City source = null;
		City target = null;

		for (City city : getPlayer().getCities()) {
			if (!(city.getReinforcements() > 1))
				continue;
			else if (source == null) {
				source = city;
				continue;
			} else if (city.getReinforcements() > source.getReinforcements()) {
				for (City front : city.getFronts())
					if (front.getOwner() != getPlayer())
						tmpFrontDelta++;

				if (tmpFrontDelta < frontDelta) {
					frontDelta = tmpFrontDelta;
					source = city;
				}
			}
		}

		if (source != null) {
			getGame().initPath();
			getGame().checkTargetability(source);
			reinforcementsDelta = 0;

			for (City city : getPlayer().getCities()) {
				if (city != source && city.isInPath()) {
					for (City front : city.getFronts()) {
						if (front.getOwner() != getPlayer() && front.getReinforcements() - city.getReinforcements() > reinforcementsDelta) {
							reinforcementsDelta = front.getReinforcements() - city.getReinforcements();
							target = city;
						}
					}
				}
			}
		}

		setSource(source);
		setTarget(target);
	}

	@Override
	public int moveReinforcementStep(int maxReinforcements) {
		return attackMoveStep(maxReinforcements);
	}
}
