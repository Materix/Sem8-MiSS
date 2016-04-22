package miss.model;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class Grass {

	private double mapSize;
	private ContinuousSpace<Object> space;

	public Grass(ContinuousSpace<Object> space, double mapSize) {
		this.space = space;
		this.mapSize = mapSize;
	}

	public void relocate() {
		space.moveTo(this,
				RandomHelper.getUniform().nextDoubleFromTo(0, mapSize),
				RandomHelper.getUniform().nextDoubleFromTo(0, mapSize));
	}
}
