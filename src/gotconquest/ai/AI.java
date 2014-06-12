package gotconquest.ai;

import gotconquest.core.City;
import gotconquest.core.GameCore;
import gotconquest.core.Player;

import java.io.Serializable;

public abstract class AI implements Serializable {
	private static final long serialVersionUID = 3529234642782877470L;
	private final Player player;
	private GameCore game;
	private City source;
	private City target;

	public AI(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setGame(GameCore game) {
		this.game = game;
	}

	public GameCore getGame() {
		return game;
	}

	public City getSource() {
		return source;
	}

	public void setSource(City source) {
		this.source = source;
	}

	public City getTarget() {
		return target;
	}

	public void setTarget(City target) {
		this.target = target;
	}

	public abstract City initStep(int maxReinforcements);

	public abstract City addReinforcementStep(int maxReinforcements);

	public abstract void attackStep();

	public abstract int attackMoveStep(int maxReinforcements);

	public abstract void moveStep();

	public abstract int moveReinforcementStep(int maxReinforcements);

}
