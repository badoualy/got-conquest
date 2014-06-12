package com.GoTConquest.AndroidUI;

import gotconquest.core.City;
import gotconquest.core.GameCore;
import gotconquest.core.GameCore.Step;
import gotconquest.core.GameCore.SubStep;
import gotconquest.core.Player;
import gotconquest.database.StatisticsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MapUI implements OnClickListener {
	public static final int WESTEROS_HEIGHT = 1335;
	public static final int WESTEROS_WIDTH = 560;

	private Activity activity;
	private GameCore gameCore;
	private AIHandler aiHandler;

	private Timer timer = new Timer();

	private List<CityButton> citiesButton;

	private int buttonColors[][];
	private int blasons[] = { R.drawable.arryn, R.drawable.tyrell, R.drawable.baratheon, R.drawable.martell, R.drawable.tully,
			R.drawable.lannister, R.drawable.stark, R.drawable.targaryen };

	private LinearLayout reinforcementLayout;
	private RelativeLayout citiesLayout;
	private LinearLayout topBand;
	private TextView currentStateText;
	private Button doneButton;
	private Button validButton;
	private ImageView blasonView;
	private ScrollView scrollView;
	private View infosView;
	private LineView currentLine;

	private float ratioX;
	private float ratioY;

	private Player currentPlayer = null;
	private CityButton currentCity = null;
	private CityButton currentTargetedCity = null;

	public MapUI(final GameCore gameCore, Activity activity) {
		this.activity = activity;
		this.gameCore = gameCore;
		this.aiHandler = new AIHandler(this);

		citiesButton = new ArrayList<CityButton>(gameCore.getCities().size());

		reinforcementLayout = (LinearLayout) activity.findViewById(R.id.ReinforcementLayout);
		citiesLayout = (RelativeLayout) (activity.findViewById(R.id.relativeLayout));
		topBand = ((LinearLayout) (activity.findViewById(R.id.topBand)));
		currentStateText = (TextView) activity.findViewById(R.id.StateText);
		doneButton = (Button) activity.findViewById(R.id.DoneView);
		validButton = (Button) activity.findViewById(R.id.ValidView);
		blasonView = (ImageView) activity.findViewById(R.id.ColorView);
		scrollView = (ScrollView) activity.findViewById(R.id.ScrollView);
		infosView = activity.findViewById(R.id.infos);
		infosView.setVisibility(View.INVISIBLE);

		ImageView mapView = (ImageView) (activity.findViewById(R.id.Map));
		double ratio = (double) (WESTEROS_HEIGHT / WESTEROS_WIDTH);
		int newWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
		int newHeight = newWidth * (int) (ratio);
		LayoutParams layoutParams = new LayoutParams(newWidth, newHeight);
		mapView.setLayoutParams(layoutParams);

		this.ratioX = (float) newWidth / (float) WESTEROS_WIDTH;
		this.ratioY = (float) newHeight / (float) WESTEROS_HEIGHT;

		this.initButtonColors();
		this.initCitiesButtons();
		this.setListeners();

		currentLine = new LineView(activity, citiesButton, ratioX, ratioY);
		citiesLayout.addView(currentLine, 1);
	}

	private void initButtonColors() {
		buttonColors = new int[][] {
				{ activity.getResources().getColor(R.color.darkblue), activity.getResources().getColor(R.color.blue) },
				{ activity.getResources().getColor(R.color.darkgreen), activity.getResources().getColor(R.color.green) },
				{ activity.getResources().getColor(R.color.darkyellow), activity.getResources().getColor(R.color.yellow) },
				{ activity.getResources().getColor(R.color.darkorange), activity.getResources().getColor(R.color.orange) },
				{ activity.getResources().getColor(R.color.darkpurple), activity.getResources().getColor(R.color.purple) },
				{ activity.getResources().getColor(R.color.darkred), activity.getResources().getColor(R.color.red) },
				{ activity.getResources().getColor(R.color.darkgrey), activity.getResources().getColor(R.color.grey) },
				{ activity.getResources().getColor(R.color.darkblack), activity.getResources().getColor(R.color.black) } };
	}

	private void initCitiesButtons() {
		for (City city : gameCore.getCities())
			addCityButton(city);
	}

	private void addCityButton(City city) {
		CityButton newCity = new CityButton(activity, city, ratioX, ratioY);
		newCity.setBackgroundResource(buttonColors[city.getOwner().getColor()]);
		citiesLayout.addView(newCity);
		newCity.setOffset(city.getX(), city.getY());
		newCity.setOnClickListener(this);

		citiesButton.add(newCity);
	}

	public void saveCities() {
		if (currentCity != null)
			gameCore.setSrcCity(currentCity.getCity());
		if (currentTargetedCity != null)
			gameCore.setTargetCity(currentTargetedCity.getCity());
	}

	public void startGame() {
		initStep(gameCore.getNextPlayer());
	}

	public void nextInitTurn() {
		if (gameCore.getCurrentPlayer() == gameCore.getPlayers().get(gameCore.getAlivePlayers() - 1)) {
			addReinforcementStep(gameCore.getNextPlayer());
			return;
		}

		initStep(gameCore.getNextPlayer());
		updateTopBand();
	}

	public void nextTurn() {
		addReinforcementStep(gameCore.getNextPlayer());
		updateTopBand();
	}

	private void clearLine() {
		currentLine.setDrawSrc(false);
		currentLine.setDrawTarget(false);
		currentLine.invalidate();
	}

	private void setSelectedSrc(boolean selected) {
		int color;
		if (gameCore.getCurrentStep() == Step.ATK)
			color = Color.rgb(223, 27, 29);
		else
			color = Color.rgb(36, 223, 27);
		currentLine.setColor(color);

		if (selected) {
			currentCity.setStrokeColor(color);
			currentLine.setSrcCoordinates(currentCity.getCenterX(), currentCity.getCenterY());
			currentCity.setSelected(true);
		} else if (currentCity != null) {
			currentCity.setSelected(false);
		}

		currentLine.setDrawSrc(selected);
		currentLine.invalidate();
	}

	private void setSelectedTarget(boolean selected) {
		if (selected) {
			currentTargetedCity.setStrokeColor(currentCity.getStrokeColor());
			currentTargetedCity.setSelected(true);
			currentLine.setTargetCoordinates(currentTargetedCity.getCenterX(), currentTargetedCity.getCenterY());
		} else if (currentTargetedCity != null) {
			currentTargetedCity.setSelected(false);
		}
		currentLine.setDrawTarget(selected);
		currentLine.invalidate();
	}

	private void updateTopBand() {
		topBand.removeAllViews();

		int index = gameCore.indexOf(currentPlayer);
		final int WIDTH = activity.getWindowManager().getDefaultDisplay().getWidth();
		int leftWidth = WIDTH;
		int width;
		int color;

		for (int i = index; i < gameCore.getAlivePlayers() + index; i++) {
			if (i == gameCore.getAlivePlayers() + index - 1)
				width = leftWidth;
			else
				width = (gameCore.getPlayer(i % gameCore.getAlivePlayers()).getNumberOfTerritory() * WIDTH)
						/ gameCore.getNumberOfTerritory();
			color = gameCore.getPlayer(i % gameCore.getAlivePlayers()).getColorCode();

			LinearLayout layout = new LinearLayout(activity);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.FILL_PARENT);
			layout.setMinimumWidth(width);
			layout.setBackgroundColor(color);
			layout.setLayoutParams(params);

			topBand.addView(layout);

			leftWidth -= width;
		}
	}

	private void endGame() {
		currentPlayer.setWon(true);

		StatisticsDAO dao = new StatisticsDAO(activity);
		dao.addGame(gameCore);
		dao.close();

		endPopup();
	}

	public void showInfos() {
		updateInfos();
		infosView.setVisibility(View.VISIBLE);
	}

	private void updateInfos() {
		int i = 0;
		Player player = null;
		LinearLayout infosLayout = (LinearLayout) activity.findViewById(R.id.infosLayout);
		LinearLayout currentLayout = null;
		LinearLayout listLayout = null;

		for (i = 0; i < gameCore.getAlivePlayers(); ++i) {
			player = gameCore.getPlayer(i);
			currentLayout = (LinearLayout) infosLayout.getChildAt(i + 1);
			listLayout = (LinearLayout) ((LinearLayout) currentLayout.getChildAt(1)).getChildAt(1);

			((TextView) ((LinearLayout) currentLayout.getChildAt(0)).getChildAt(0)).setText(player.getName());

			((ImageView) ((LinearLayout) currentLayout.getChildAt(1)).getChildAt(0)).setImageResource(blasons[player.getColor()]);

			((TextView) listLayout.getChildAt(0)).setText(getString(R.string.nextturnreinforcements) + " "
					+ player.getNextTurnReinforcements() + " + " + gameCore.getReinforcementBonus(player));

			((TextView) listLayout.getChildAt(1)).setText(getString(R.string.ownedcities) + " " + player.getNumberOfTerritory());

			((TextView) listLayout.getChildAt(2)).setText(getString(R.string.ownedkingdoms) + " " + gameCore.getNumberOfKingdom(player));

			((TextView) listLayout.getChildAt(3)).setText(getString(R.string.totaltroops) + " " + player.getNumberOfTroops());
		}

		for (int j = (infosLayout.getChildCount() - 1); j > i; j--)
			infosLayout.removeViewAt(j);
	}

	public void resume() {
		Step currentStep = gameCore.getCurrentStep();
		SubStep currentSubStep = gameCore.getCurrentSubStep();
		currentPlayer = gameCore.getCurrentPlayer();
		updateTopBand();
		blasonView.setBackgroundResource(blasons[currentPlayer.getColor()]);
		((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());

		if (gameCore.getSrcCity() != null)
			currentCity = getCityButton(gameCore.getSrcCity());
		if (gameCore.getTargetCity() != null)
			currentTargetedCity = getCityButton(gameCore.getTargetCity());

		if (currentPlayer.isAI()) {
			citiesLayout.setClickable(false);
			reinforcementLayout.setVisibility(android.view.View.INVISIBLE);
			doneButton.setVisibility(android.view.View.INVISIBLE);
			validButton.setVisibility(android.view.View.INVISIBLE);
		}

		if (currentSubStep == SubStep.MOVE || currentSubStep == SubStep.REINFORCEMENT) {
			setSelectedSrc(true);
			setSelectedTarget(true);
		}

		if (currentStep == Step.REINFORCEMENT) {
			addReinforcementStep(currentPlayer);
		} else if (currentStep == Step.ATK) {
			if (currentSubStep == SubStep.SRC || currentSubStep == SubStep.TARGET)
				attackStep();
			else if (currentSubStep == SubStep.REINFORCEMENT)
				attackReinforcementStep();
			else
				attackMoveStep();
		} else if (currentStep == Step.MOVE) {
			if (currentSubStep == SubStep.SRC || currentSubStep == SubStep.TARGET)
				moveStep();
			if (currentSubStep == SubStep.REINFORCEMENT)
				moveReinforcementStep();
		}
	}

	private void showEndPopup() {
		cancelTasks();

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(getString(R.string.congrats))
				.setMessage(getString(R.string.congrats) + " " + currentPlayer.getName() + ", " + getString(R.string.youwon))
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.setResult(GoTActivity.REPLAY_RESULT_CODE);
						activity.finish();
					}
				}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.setResult(GoTActivity.STOP_RESULT_CODE);
						activity.finish();
					}
				}).setNeutralButton(getString(R.string.showstats), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.setResult(GoTActivity.STATS_RESULT_CODE);
						activity.finish();
					}
				}).setCancelable(false);
		builder.create().show();
	}

	public void cancelTasks() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void endPopup() {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showEndPopup();
			}
		});
	}

	public void initStep(Player player) {
		gameCore.setCurrentStep(GameCore.Step.INIT);
		gameCore.setMaxReinforcement(gameCore.getReinforcement(player) + GameActivity.BONUS_INIT_STEP);
		initReinforcementStep(player);

		if (currentPlayer.isAI()) {
			aiHandler.initStep(currentPlayer.getAI(), gameCore.getMaxReinforcement());
		}
	}

	public void addReinforcementStep(Player player) {
		gameCore.setCurrentStep(GameCore.Step.REINFORCEMENT);
		gameCore.setMaxReinforcement(gameCore.getReinforcement(player));
		initReinforcementStep(player);

		if (currentPlayer.isAI()) {
			aiHandler.addReinforcementStep(currentPlayer.getAI(), gameCore.getMaxReinforcement());
		}
	}

	private void initReinforcementStep(Player player) {
		currentPlayer = player;
		gameCore.setReinforcement(gameCore.getMaxReinforcement());
		((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
		currentStateText.setText(" " + getString(R.string.placereinforcements) + " (" + gameCore.getMaxReinforcement() + " "
				+ getString(R.string.available) + ") : " + currentPlayer.getName());
		blasonView.setBackgroundResource(blasons[currentPlayer.getColor()]);

		if (!currentPlayer.isAI()) {
			citiesLayout.setClickable(true);
			reinforcementLayout.setVisibility(android.view.View.VISIBLE);
			doneButton.setVisibility(android.view.View.INVISIBLE);
			validButton.setVisibility(android.view.View.INVISIBLE);
		} else {
			citiesLayout.setClickable(false);
			reinforcementLayout.setVisibility(android.view.View.INVISIBLE);
			doneButton.setVisibility(android.view.View.INVISIBLE);
			validButton.setVisibility(android.view.View.INVISIBLE);
		}
	}

	public void attackStep() {
		gameCore.setCurrentStep(GameCore.Step.ATK);
		gameCore.setCurrentSubStep(GameCore.SubStep.SRC);
		currentCity = null;
		currentTargetedCity = null;
		currentStateText.setText(" " + getString(R.string.attackstep) + " : " + currentPlayer.getName());

		if (currentPlayer.isAI()) {
			aiHandler.attackStep(currentPlayer.getAI());
		} else {
			reinforcementLayout.setVisibility(android.view.View.INVISIBLE);
			validButton.setVisibility(android.view.View.INVISIBLE);
			doneButton.setVisibility(android.view.View.VISIBLE);
		}
	}

	public void attackReinforcementStep() {
		gameCore.setCurrentSubStep(GameCore.SubStep.REINFORCEMENT);
		gameCore.setMaxReinforcement(currentCity.getReinforcements() - 1);
		gameCore.setReinforcement(gameCore.getMaxReinforcement());
		((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
		currentStateText.setText(" " + getString(R.string.attackstep) + " : " + currentCity.getName() + " vs "
				+ currentTargetedCity.getName());

		if (currentPlayer.isAI()) {
			aiHandler.attackReinforcementStep(currentPlayer.getAI(), gameCore.getMaxReinforcement());
		} else {
			reinforcementLayout.setVisibility(android.view.View.VISIBLE);
			validButton.setVisibility(android.view.View.VISIBLE);
		}

	}

	public void attackMoveStep() {
		gameCore.setCurrentSubStep(GameCore.SubStep.MOVE);
		gameCore.setMaxReinforcement(gameCore.getReinforcement());
		((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
		currentStateText.setText(" " + getString(R.string.placereinforcements) + " : " + currentTargetedCity.getName());

		if (currentPlayer.isAI()) {
			aiHandler.attackMoveStep(currentPlayer.getAI(), gameCore.getMaxReinforcement());
		} else {
			reinforcementLayout.setVisibility(android.view.View.VISIBLE);
			doneButton.setVisibility(android.view.View.INVISIBLE);
		}

	}

	public void moveStep() {
		gameCore.setCurrentStep(GameCore.Step.MOVE);
		gameCore.setCurrentSubStep(GameCore.SubStep.SRC);
		currentCity = null;
		currentTargetedCity = null;
		currentStateText.setText(" " + getString(R.string.movestep) + " : " + currentPlayer.getName());

		if (currentPlayer.isAI()) {
			aiHandler.moveStep(currentPlayer.getAI());
		} else {
			reinforcementLayout.setVisibility(android.view.View.INVISIBLE);
			doneButton.setVisibility(android.view.View.VISIBLE);
		}
	}

	public void moveReinforcementStep() {
		gameCore.setCurrentSubStep(GameCore.SubStep.MOVE);
		gameCore.setCurrentSubStep(GameCore.SubStep.REINFORCEMENT);
		gameCore.setMaxReinforcement(currentCity.getReinforcements() - 1);
		gameCore.setReinforcement(gameCore.getMaxReinforcement());
		((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
		currentStateText.setText(" " + getString(R.string.movefrom) + " " + currentCity.getName() + " " + getString(R.string.to) + " "
				+ currentTargetedCity.getName());

		if (currentPlayer.isAI()) {
			aiHandler.moveReinforcementStep(currentPlayer.getAI(), gameCore.getMaxReinforcement());
		} else {
			reinforcementLayout.setVisibility(android.view.View.VISIBLE);
			validButton.setVisibility(android.view.View.VISIBLE);
			doneButton.setVisibility(android.view.View.VISIBLE);
		}
	}

	@Override
	public void onClick(View cityButtonView) {
		if (!currentPlayer.isAI())
			onCityClick((CityButton) cityButtonView);
	}

	public void onCityClick(CityButton cityButton) {
		City clickedCity = cityButton.getCity();

		switch (gameCore.getCurrentStep()) {
		case INIT:
		case REINFORCEMENT:
			if (clickedCity.getOwner() == currentPlayer) {
				cityButton.addReinforcements(gameCore.getReinforcement());
				gameCore.setMaxReinforcement(gameCore.getMaxReinforcement() - gameCore.getReinforcement());

				currentStateText.setText(" " + getString(R.string.placereinforcements) + " (" + gameCore.getMaxReinforcement() + " "
						+ getString(R.string.available) + ") : " + currentPlayer.getName());

				if (gameCore.getReinforcement() > gameCore.getMaxReinforcement()) {
					gameCore.setReinforcement(gameCore.getMaxReinforcement());
					((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getMaxReinforcement());
				}
				if (gameCore.getReinforcement() == 0) {
					if (gameCore.getCurrentStep() == Step.INIT)
						nextInitTurn();
					else
						attackStep();
				}
			}
			break;
		case ATK:
			if (gameCore.getCurrentSubStep() != GameCore.SubStep.MOVE) {
				if ((currentCity != null && clickedCity.getOwner() != currentPlayer && !clickedCity.isFront(currentCity.getCity()))
						|| (clickedCity.getOwner() == currentPlayer && !(clickedCity.getReinforcements() > 1)))
					return;

				if (gameCore.getCurrentSubStep() == GameCore.SubStep.REINFORCEMENT)
					reinforcementLayout.setVisibility(android.view.View.INVISIBLE);

				if (clickedCity.getOwner() == currentPlayer) {
					if (currentCity != null) {
						setSelectedSrc(false);
						setSelectedTarget(false);
						currentTargetedCity = null;
					}

					currentCity = cityButton;
					gameCore.initPath();
					gameCore.checkTargetability(clickedCity);
					setSelectedSrc(true);
					currentStateText.setText(" " + getString(R.string.attackstep) + " : " + clickedCity.getName() + " vs ?");
					gameCore.setCurrentSubStep(GameCore.SubStep.TARGET);

					if (currentTargetedCity != null) {
						View targetView = (View) (currentTargetedCity);
						setSelectedTarget(false);
						currentTargetedCity = null;
						clearLine();
						onClick(targetView);
					}
				} else if (currentCity != null && clickedCity.getOwner() != currentPlayer) {
					if (currentTargetedCity != null)
						setSelectedTarget(false);

					currentTargetedCity = cityButton;
					setSelectedTarget(true);
					attackReinforcementStep();
				}
			}
			break;
		case MOVE:
			if (clickedCity.getOwner() == currentPlayer) {
				if (gameCore.getCurrentSubStep() == GameCore.SubStep.REINFORCEMENT
						&& (clickedCity.getReinforcements() > 1 || clickedCity.isInPath())) {
					reinforcementLayout.setVisibility(android.view.View.INVISIBLE);
					currentStateText.setText(" " + getString(R.string.movestep) + " : " + currentPlayer.getName());

					this.clearLine();

					if (clickedCity.getReinforcements() > 1) {
						gameCore.setCurrentSubStep(GameCore.SubStep.SRC);
						setSelectedSrc(false);
						setSelectedTarget(false);
					} else {
						gameCore.setCurrentSubStep(GameCore.SubStep.TARGET);
						setSelectedTarget(false);
					}
				}

				if (gameCore.getCurrentSubStep() == GameCore.SubStep.TARGET && clickedCity != currentCity.getCity()
						&& clickedCity.isInPath()) {
					currentTargetedCity = cityButton;
					setSelectedTarget(true);
					moveReinforcementStep();
				} else if (clickedCity.getReinforcements() > 1) {
					if (currentCity != null)
						setSelectedSrc(false);

					currentCity = cityButton;
					currentStateText.setText(" " + getString(R.string.movefrom) + " " + clickedCity.getName() + " to ?");

					gameCore.initPath();
					gameCore.checkTargetability(clickedCity);
					gameCore.setCurrentSubStep(GameCore.SubStep.TARGET);

					setSelectedSrc(true);
				}
			}
			break;

		default:
			break;
		}
	}

	private void setListeners() {
		this.setReinforcementLayoutListeners(reinforcementLayout);

		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentCity != null)
					setSelectedSrc(false);
				if (currentTargetedCity != null)
					setSelectedTarget(false);
				clearLine();

				if (gameCore.getCurrentStep() == GameCore.Step.ATK)
					moveStep();
				else if (gameCore.getCurrentStep() == GameCore.Step.MOVE)
					nextTurn();
			}
		});

		validButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gameCore.getCurrentStep() == GameCore.Step.ATK && gameCore.getCurrentSubStep() == GameCore.SubStep.REINFORCEMENT) {
					Player player = currentTargetedCity.getOwner();
					int leftReinforcement = gameCore.attack(currentCity.getCity(), currentTargetedCity.getCity(),
							gameCore.getReinforcement());

					currentTargetedCity.updateText();
					currentCity.updateText();
					updateTopBand();

					if (leftReinforcement > 0) {
						if (!gameCore.isAlive(player))
							Toast.makeText(activity, player.getName() + " " + getString(R.string.lost), Toast.LENGTH_SHORT).show();

						currentTargetedCity.setBackgroundResource(buttonColors[currentPlayer.getColor()]);
						gameCore.setReinforcement(leftReinforcement);

						if (gameCore.getAlivePlayers() == 1)
							endGame();
						else
							attackMoveStep();
					} else {
						setSelectedSrc(false);
						setSelectedTarget(false);
						clearLine();
						attackStep();
					}
				} else if (gameCore.getCurrentStep() == GameCore.Step.ATK && gameCore.getCurrentSubStep() == GameCore.SubStep.MOVE) {
					currentCity.addReinforcements(-gameCore.getReinforcement());
					currentTargetedCity.addReinforcements(gameCore.getReinforcement());
					setSelectedSrc(false);
					setSelectedTarget(false);
					clearLine();
					attackStep();
				} else if (gameCore.getCurrentStep() == GameCore.Step.MOVE
						&& gameCore.getCurrentSubStep() == GameCore.SubStep.REINFORCEMENT) {
					currentCity.addReinforcements(-gameCore.getReinforcement());
					currentTargetedCity.addReinforcements(gameCore.getReinforcement());
					setSelectedSrc(false);
					setSelectedTarget(false);
					clearLine();
					nextTurn();
				}
			}
		});
	}

	private void setReinforcementLayoutListeners(LinearLayout layout) {
		layout.getChildAt(1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gameCore.getReinforcement() > 1) {
					gameCore.setReinforcement(gameCore.getReinforcement() - 1);
					((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
				}
			}
		});

		layout.getChildAt(3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gameCore.getReinforcement() < gameCore.getMaxReinforcement()) {
					gameCore.setReinforcement(gameCore.getReinforcement() + 1);
					((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
				}
			}
		});

		layout.getChildAt(0).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameCore.setReinforcement(1);
				((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
			}
		});

		layout.getChildAt(4).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameCore.setReinforcement(gameCore.getMaxReinforcement());
				((TextView) (reinforcementLayout.getChildAt(2))).setText("" + gameCore.getReinforcement());
			}
		});
	}

	public void doValidClick() {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				((Button) activity.findViewById(R.id.ValidView)).performClick();
			}
		});
	}

	public void doEndClick() {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				((Button) activity.findViewById(R.id.DoneView)).performClick();
			}
		});
	}

	public void doCityClick(final CityButton cityButton) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				onCityClick(cityButton);
			}
		});
	}

	public void scrollOnCity(CityButton cityButton) {
		final int pos = (int) (cityButton.getCenterY() - activity.findViewById(R.id.ScrollView).getHeight() / 2);

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				scrollView.smoothScrollTo(0, pos);
			}
		});
	}

	public CityButton getCityButton(City city) {
		for (CityButton cityButton : citiesButton)
			if (cityButton.getCity() == city)
				return cityButton;

		return null;
	}

	public void setCurrentReinforcements(int currentReinforcements) {
		gameCore.setReinforcement(currentReinforcements);
	}

	public GameCore getGameCore() {
		return gameCore;
	}

	public Timer getTimer() {
		return timer;
	}

	private String getString(int resId) {
		return activity.getString(resId);
	}

	public void setCurrentCity(CityButton currentCity) {
		this.currentCity = currentCity;
	}

	public void setCurrentTargetedCity(CityButton currentTargetedCity) {
		this.currentTargetedCity = currentTargetedCity;
	}

	public void setCurrentMaxReinforcements(int currentMaxReinforcements) {
		gameCore.setMaxReinforcement(currentMaxReinforcements);
	}
}