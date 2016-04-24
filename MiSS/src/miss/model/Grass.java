package miss.model;

import repast.simphony.space.continuous.ContinuousSpace;

public class Grass {
	private ContinuousSpace<Object> space;

	public Grass(ContinuousSpace<Object> space, double mapSize) {
		this.space = space;
	}

	public void relocate() {
		space.getAdder().add(space, this);
	}
}
