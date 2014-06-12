package gotconquest.core;

public class Statistics {
	String player;
	Line[] lines;
	int currentLine = 0;

	public Statistics(String player, int numberOfLines) {
		this.player = player;
		this.lines = new Line[7];
	}

	public void addLine(int nbPlayer, int nbGamePlayed, int nbVictory, int nbDefeat) {
		lines[nbPlayer - 2] = new Line(nbGamePlayed, nbVictory, nbDefeat);
	}

	public Line[] getLines() {
		return lines;
	}

	public int getVictories() {
		int victories = 0;
		for (Line line : lines)
			victories += line.nbVictory;

		return victories;
	}

	public int getDefeats() {
		int defeats = 0;
		for (Line line : lines)
			defeats += line.nbDefeat;

		return defeats;
	}

	public int getPlayedGames() {
		int playedGames = 0;
		for (Line line : lines)
			playedGames += line.nbGamePlayed;

		return playedGames;
	}

	public static class Line {
		public final int nbGamePlayed;
		public final int nbVictory;
		public final int nbDefeat;

		public Line(int nbGamePlayed, int nbVictory, int nbDefeat) {
			this.nbGamePlayed = nbGamePlayed;
			this.nbVictory = nbVictory;
			this.nbDefeat = nbDefeat;
		}
	}
}