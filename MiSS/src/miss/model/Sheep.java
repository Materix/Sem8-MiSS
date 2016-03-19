package miss.model;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Sheep {
	private static final int NEIGHBORHOOD_RADIUS = 3;

	private static final double MAX_VELOCITY = 0.5;

	private static final double MIN_DISTANCE = 1;

	private ContinuousSpace<Object> space;

	private NdPoint speed;

	public Sheep(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;

		speed = new NdPoint(RandomHelper.nextDoubleFromTo(-1, 1),
				RandomHelper.nextDoubleFromTo(-1, 1));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		NdPoint v1 = rule1();
		NdPoint v2 = rule2();
		NdPoint v3 = rule3();

		speed = new NdPoint(speed.getX() + v1.getX() + v2.getX() + v3.getX(),
				speed.getY() + v1.getY() + v2.getY() + v3.getY());

		slowDown();
		forward();
	}

	// cohesion
	// TODO tu jest problem z liczeniem œredniej. Jak przekroczy granicê to siê
	// nagle œrodek grupy umieszcze w œrodku planszy
	private NdPoint rule1() {
		List<Sheep> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				NdPoint otherPoint = space.getLocation(sheep);
				result = new NdPoint(result.getX() + otherPoint.getX(),
						result.getY() + otherPoint.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			NdPoint myPoint = space.getLocation(this);
			double[] displacement = space.getDisplacement(myPoint, result);
			return new NdPoint(displacement[0] / 50, displacement[1] / 50);
		}
		return new NdPoint(0, 0);
	}

	// separation
	private NdPoint rule2() {
		List<Sheep> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				if (distance(sheep) < MIN_DISTANCE) {
					NdPoint myPoint = space.getLocation(this);
					NdPoint otherPoint = space.getLocation(sheep);
					double[] displacement = space.getDisplacement(myPoint,
							otherPoint);
					result = new NdPoint(result.getX() - displacement[0],
							result.getY() - displacement[1]);
				}
			}
			return new NdPoint(result.getX() / 2, result.getY() / 2);
		}
		return new NdPoint(0, 0);
	}

	// speed alignment
	private NdPoint rule3() {
		List<Sheep> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				result = new NdPoint(result.getX() + sheep.speed.getX(),
						result.getY() + sheep.speed.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			return new NdPoint((result.getX() - speed.getX()) / 8,
					(result.getY() - speed.getY()) / 8);
		}
		return new NdPoint(0, 0);
	}

	// speed limit
	private void slowDown() {
		if (Math.hypot(speed.getX(), speed.getY()) > MAX_VELOCITY) {
			speed = new NdPoint(speed.getX() * 0.8, speed.getY() * 0.8);
		}
	}

	private double distance(Sheep sheep) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(sheep);
		double distance = space.getDistance(myPoint, otherPoint);
		return space.getDistance(myPoint, otherPoint);
	}

	private void forward() {
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + speed.getX();
		double moveY = pt.getY() + speed.getY();
		space.moveTo(this, moveX, moveY);
	}

	private List<Sheep> getNeighborhood() {
		List<Sheep> circularNeighborhood = new ArrayList<Sheep>();
		for (Object object : space.getObjects()) {
			if (object instanceof Sheep) {
				Sheep sheep = (Sheep) object;
				if (!sheep.equals(this)) {
					if (distance(sheep) <= NEIGHBORHOOD_RADIUS
							&& isInVisibleRange(sheep)) {
						circularNeighborhood.add(sheep);
					}
				}
			}
		}
		return circularNeighborhood;
	}

	private boolean isInVisibleRange(Sheep sheep) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(sheep);
		double degree = Math.atan2(otherPoint.getY() - myPoint.getY(),
				otherPoint.getX() - myPoint.getX());
		return true; // TODO
	}

	public NdPoint getSpeed() {
		return speed;
	}
}
