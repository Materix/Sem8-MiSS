package miss.model;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

public class Sheep {
	private static final double MIN_DISTANCE_TO_BOUNDARY = 1;

	private static final int NEIGHBORHOOD_RADIUS = 3;

	private static final int OBSTACLE_DETECT_RADIUS = 1;

	private static final double MAX_VELOCITY = 0.5;

	private static final double MIN_DISTANCE = 1;

	private static final double MIN_VELOCITY = 0.1;

	private ContinuousSpace<Object> space;

	private NdPoint speed;

	public Sheep(ContinuousSpace<Object> space) {
		this.space = space;
		speed = new NdPoint(RandomHelper.nextDoubleFromTo(-1, 1),
				RandomHelper.nextDoubleFromTo(-1, 1));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		cohesianRule();
		separationRule();
		speedAlignmentRule();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void forward() {
		slowDown();
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + speed.getX();
		double moveY = pt.getY() + speed.getY();
		space.moveTo(this, moveX, moveY);
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void avoid() {
		avoidObstacles();
		avoidBoundary();
	}

	private void avoidBoundary() {
		NdPoint myPoint = space.getLocation(this);
		if (myPoint.getX() < MIN_DISTANCE_TO_BOUNDARY
				&& (getRotation() < -Math.PI / 2 || getRotation() > Math.PI / 2)) {
			speed = new NdPoint(-speed.getX(), speed.getY());
		} else if (myPoint.getX() > space.getDimensions().getDimension(0)
				- MIN_DISTANCE_TO_BOUNDARY
				&& (getRotation() > -Math.PI / 2 || getRotation() < Math.PI / 2)) {
			speed = new NdPoint(-speed.getX(), speed.getY());
		}
		if (myPoint.getY() < MIN_DISTANCE_TO_BOUNDARY && getRotation() < 0) {
			speed = new NdPoint(speed.getX(), -speed.getY());
		} else if (myPoint.getY() > space.getDimensions().getDimension(1)
				- MIN_DISTANCE_TO_BOUNDARY
				&& getRotation() > 0) {
			speed = new NdPoint(speed.getX(), -speed.getY());
		}
	}

	// cohesion
	// TODO tu jest problem z liczeniem œredniej. Jak przekroczy granicê to siê
	// nagle œrodek grupy umieszcze w œrodku planszy
	private void cohesianRule() {
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
			result = new NdPoint(displacement[0] / 50, displacement[1] / 50);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}
	}

	private void separationRule() {
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
			result = new NdPoint(result.getX() / 8, result.getY() / 8);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}
	}

	private void speedAlignmentRule() {
		List<Sheep> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				result = new NdPoint(result.getX() + sheep.speed.getX(),
						result.getY() + sheep.speed.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			result = new NdPoint((result.getX() - speed.getX()) / 8,
					(result.getY() - speed.getY()) / 8);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}
	}

	private void avoidObstacles() {
		List<Obstacle> obstacles = getObstacles();
		if (obstacles.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			double avgDistance = 0;
			for (Obstacle obstacle : obstacles) {
				NdPoint myPoint = space.getLocation(this);
				NdPoint otherPoint = space.getLocation(obstacle);
				double[] displacement = space.getDisplacement(myPoint,
						otherPoint);
				avgDistance += distance(obstacle)
						- obstacle.getObstacleRadius();
				result = new NdPoint(result.getX() - displacement[0],
						result.getY() - displacement[1]);
			}
			avgDistance /= obstacles.size();
			double weight = 20 * avgDistance;
			result = new NdPoint(result.getX() / weight, result.getY() / weight);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}
	}

	// speed limit
	private void slowDown() {
		if (Math.hypot(speed.getX(), speed.getY()) > MAX_VELOCITY) {
			speed = new NdPoint(speed.getX() * 0.8, speed.getY() * 0.8);
		}
		if (Math.hypot(speed.getX(), speed.getY()) < MIN_VELOCITY) {
			speed = new NdPoint(speed.getX() * 1.3, speed.getY() * 1.3);
		}
	}

	private double distance(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		return space.getDistance(myPoint, otherPoint);
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

	private List<Obstacle> getObstacles() {
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		for (Object object : space.getObjects()) {
			if (object instanceof Obstacle) {
				Obstacle obstacle = (Obstacle) object;
				if (!obstacle.equals(this)) {
					if (distance(obstacle) <= OBSTACLE_DETECT_RADIUS
							+ obstacle.getObstacleRadius()
							&& isInVisibleRange(obstacle)) {
						obstacles.add(obstacle);
					}
				}
			}
		}
		return obstacles;
	}

	private boolean isInVisibleRange(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		double radian = Math.atan2(otherPoint.getY() - myPoint.getY(),
				otherPoint.getX() - myPoint.getX());
		return Math.abs(radian - getRotation()) < Math.toRadians(120);
	}

	public NdPoint getSpeed() {
		return speed;
	}

	public double getRotation() {
		return Math.atan2(speed.getY(), speed.getX());
	}
}
