package gotconquest.core;

import java.io.Serializable;

public class Map implements Serializable {
	private static final long serialVersionUID = -1109812871650260834L;
	private final String name = "Westeros";
	private final int height = 1335;
	private final int width = 560;

	public String getName() {
		return name;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
