package miss.model;

import groovy.util.ObservableMap;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.relogo.ReLogoModel;
import repast.simphony.space.SpatialException;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

public abstract class Animal {

	private double avoidObstacleRuleWeight;

	private double perturbationRuleWeight;

	private double boudaryPushForce;

	private double minDistanceToBoundary;

	private double obstacleDetectionRadius;

	private double maxVelocity;

	private double minVelocity;

	private double angleOfSight;

	protected ContinuousSpace<Object> space;

	protected NdPoint velocity;

	public Animal(ContinuousSpace<Object> space) {
		this.space = space;
		velocity = new NdPoint(RandomHelper.nextDoubleFromTo(-1, 1),
				RandomHelper.nextDoubleFromTo(-1, 1));
	}

	// @Setup
	@ScheduledMethod(start = 0, duration = 0)
	public void setup() {
		ReLogoModel model = ReLogoModel.getInstance();

		minDistanceToBoundary = Double.parseDouble(model.getModelParam(
				AnimalProperties.MIN_DISTANCE_TO_BOUNDARY).toString());
		obstacleDetectionRadius = Double.parseDouble(model.getModelParam(
				AnimalProperties.OBSTACLE_DETECTION_RADIUS).toString());
		maxVelocity = Double.parseDouble(model.getModelParam(
				AnimalProperties.MAX_VELOCITY).toString());
		minVelocity = Double.parseDouble(model.getModelParam(
				AnimalProperties.MIN_VELOCITY).toString());
		angleOfSight = Double.parseDouble(model.getModelParam(
				AnimalProperties.ANGLE_OF_SIGHT).toString());
		avoidObstacleRuleWeight = Double.parseDouble(model.getModelParam(
				AnimalProperties.AVOID_OBSTACLE_RULE_WEIGHT).toString());
		perturbationRuleWeight = Double.parseDouble(model.getModelParam(
				AnimalProperties.PERTURBATION_RULE_WEIGHT).toString());
		boudaryPushForce = Double.parseDouble(model.getModelParam(
				AnimalProperties.BOUNDARY_PUSH_FORCE).toString());

		ObservableMap propertiesMap = ((ObservableMap) model.getModelParams());

		propertiesMap.addPropertyChangeListener(
				AnimalProperties.MIN_DISTANCE_TO_BOUNDARY,
				event -> minDistanceToBoundary = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.OBSTACLE_DETECTION_RADIUS,
				event -> obstacleDetectionRadius = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.MAX_VELOCITY,
				event -> maxVelocity = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.MIN_VELOCITY,
				event -> minVelocity = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.ANGLE_OF_SIGHT,
				event -> angleOfSight = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.AVOID_OBSTACLE_RULE_WEIGHT,
				event -> avoidObstacleRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.PERTURBATION_RULE_WEIGHT,
				event -> perturbationRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				AnimalProperties.BOUNDARY_PUSH_FORCE,
				event -> boudaryPushForce = Double.parseDouble(event
						.getNewValue().toString()));

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void forward() {
		// avoidBoundary();
		avoidObstacles();
		perturbation();
		limitVelocity();
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + velocity.getX();
		double moveY = pt.getY() + velocity.getY();
		try {
			space.moveTo(this, moveX, moveY);
		} catch (SpatialException e) {
			System.err.println("Catched SpatialException:" + e.getMessage());
		}
	}

	private void perturbation() {
		velocity = new NdPoint(perturbationRuleWeight * maxVelocity
				* RandomHelper.nextDoubleFromTo(-0.5, 0.5) + velocity.getX(),
				perturbationRuleWeight * maxVelocity
						* RandomHelper.nextDoubleFromTo(-0.5, 0.5)
						+ velocity.getY());
	}

	private void limitVelocity() {
		while (getSpeed() > maxVelocity) {
			velocity = new NdPoint(velocity.getX() * 0.8, velocity.getY() * 0.8);
		}
		while (getSpeed() < minVelocity) {
			velocity = new NdPoint(velocity.getX() * 1.3, velocity.getY() * 1.3);
		}
	}

	private void avoidBoundary() {
		NdPoint myPoint = space.getLocation(this);
		NdPoint result = new NdPoint(0, 0);
		if (myPoint.getX() < minDistanceToBoundary
				&& (getRotation() < -Math.PI / 2 || getRotation() > Math.PI / 2)) {
			result = new NdPoint(result.getX() + boudaryPushForce,
					result.getY());
		} else if (myPoint.getX() > space.getDimensions().getDimension(0)
				- minDistanceToBoundary
				&& (getRotation() > -Math.PI / 2 || getRotation() < Math.PI / 2)) {
			result = new NdPoint(result.getX() - boudaryPushForce,
					result.getY());
		}
		if (myPoint.getY() < minDistanceToBoundary && getRotation() < 0) {
			result = new NdPoint(result.getX(), result.getY()
					+ boudaryPushForce);
		} else if (myPoint.getY() > space.getDimensions().getDimension(1)
				- minDistanceToBoundary
				&& getRotation() > 0) {
			result = new NdPoint(result.getX(), result.getY()
					- boudaryPushForce);
		}
		velocity = new NdPoint(velocity.getX() + result.getX(), velocity.getY()
				+ result.getY());
	}

	private void avoidObstacles() {
		List<Obstacle> obstacles = getObstacles();
		if (obstacles.size() > 0) {
			for (Obstacle obstacle : obstacles) {
				NdPoint thisLocation = space.getLocation(this);
				NdPoint obstacleLocation = space.getLocation(obstacle);
				double[] displacement = space.getDisplacement(obstacleLocation,
						thisLocation);
				velocity = new NdPoint(velocity.getX()
						+ avoidObstacleRuleWeight / displacement[0],
						velocity.getY() + avoidObstacleRuleWeight
								/ displacement[1]);
			}
		}
	}

	protected double getSpeed() {
		return Math.hypot(velocity.getX(), velocity.getY());
	}

	protected double distance(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		return space.getDistance(myPoint, otherPoint);
	}

	private List<Obstacle> getObstacles() {
		List<Obstacle> obstacles = new ArrayList<Obstacle>();
		for (Object object : space.getObjects()) {
			if (object instanceof Obstacle) {
				Obstacle obstacle = (Obstacle) object;
				if (!obstacle.equals(this)) {
					if (distance(obstacle) <= obstacleDetectionRadius
							+ obstacle.getObstacleRadius()
							&& isInVisibleRange(obstacle)
							&& isOnTheRoad(obstacle)) {
						obstacles.add(obstacle);
					}
				}
			}
		}
		return obstacles;
	}

	private boolean isOnTheRoad(Obstacle obstacle) {
		NdPoint obstacleLocation = space.getLocation(obstacle);
		NdPoint thisLocation = space.getLocation(this);
		double A = velocity.getY() / velocity.getX();
		double B = -1;
		double C = thisLocation.getY() - A * thisLocation.getX();
		return Math.abs(A * obstacleLocation.getX() + B
				* obstacleLocation.getY() + C)
				/ Math.hypot(A, B) < obstacle.getObstacleRadius() + 0.2;
	}

	protected boolean isInVisibleRange(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		double radian = Math.atan2(otherPoint.getY() - myPoint.getY(),
				otherPoint.getX() - myPoint.getX());
		return Math.abs(radian - getRotation()) < Math.toRadians(angleOfSight);
	}

	public NdPoint getVelocity() {
		return velocity;
	}

	public double getRotation() {
		return Math.atan2(velocity.getY(), velocity.getX());
	}
}
