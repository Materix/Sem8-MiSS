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

public class Bird {
	private double cohesianRuleWeight;

	private double separationRuleWeight;

	private double speedAlignmentRuleWeight;

	private double maxRotationSpeed;

	private double minRotationSpeed;

	private double minDistanceToBoundary;

	private double neighborhoodRadius;

	private double obstacleDetectionRadius;

	private double maxVelocity;

	private double minVelocity;

	private double minDistance;

	private double angleOfSight;

	private static final double BOUNDARY_PUSH_FORCE = 0.1;

	private static final double AVOID_OBSTACLE_RULE_WEIGHT = 0.2;

	private ContinuousSpace<Object> space;

	private NdPoint speed;

	public Bird(ContinuousSpace<Object> space) {
		this.space = space;
		speed = new NdPoint(RandomHelper.nextDoubleFromTo(-1, 1),
				RandomHelper.nextDoubleFromTo(-1, 1));
	}

	// @Setup
	@ScheduledMethod(start = 0, duration = 0)
	public void setup() {
		ReLogoModel model = ReLogoModel.getInstance();

		maxRotationSpeed = Double.parseDouble(model.getModelParam(
				BirdProperties.MAX_ROTATION_SPEED).toString());
		minRotationSpeed = Double.parseDouble(model.getModelParam(
				BirdProperties.MIN_ROTATION_SPEED).toString());
		minDistanceToBoundary = Double.parseDouble(model.getModelParam(
				BirdProperties.MIN_DISTANCE_TO_BOUNDARY).toString());
		neighborhoodRadius = Double.parseDouble(model.getModelParam(
				BirdProperties.NEIGHBORHOOD_RADIUS).toString());
		obstacleDetectionRadius = Double.parseDouble(model.getModelParam(
				BirdProperties.OBSTACLE_DETECTION_RADIUS).toString());
		maxVelocity = Double.parseDouble(model.getModelParam(
				BirdProperties.MAX_VELOCITY).toString());
		minVelocity = Double.parseDouble(model.getModelParam(
				BirdProperties.MIN_VELOCITY).toString());
		minDistance = Double.parseDouble(model.getModelParam(
				BirdProperties.MIN_DISTANCE).toString());
		cohesianRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.COHESIAN_RULE_WEIGHT).toString());
		separationRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.SEPARATION_RULE_WEIGHT).toString());
		speedAlignmentRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT).toString());
		angleOfSight = Double.parseDouble(model.getModelParam(
				BirdProperties.ANGLE_OF_SIGHT).toString());

		ObservableMap propertiesMap = ((ObservableMap) model.getModelParams());

		propertiesMap.addPropertyChangeListener(
				BirdProperties.MAX_ROTATION_SPEED,
				event -> maxRotationSpeed = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.MIN_ROTATION_SPEED,
				event -> minRotationSpeed = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.MIN_DISTANCE_TO_BOUNDARY,
				event -> minDistanceToBoundary = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.NEIGHBORHOOD_RADIUS,
				event -> neighborhoodRadius = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.OBSTACLE_DETECTION_RADIUS,
				event -> obstacleDetectionRadius = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.MAX_VELOCITY,
				event -> maxVelocity = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.MIN_VELOCITY,
				event -> minVelocity = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.MIN_DISTANCE,
				event -> minDistance = Double.parseDouble(event.getNewValue()
						.toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.COHESIAN_RULE_WEIGHT,
				event -> cohesianRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.SEPARATION_RULE_WEIGHT,
				event -> separationRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT,
				event -> speedAlignmentRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.ANGLE_OF_SIGHT,
				event -> angleOfSight = Double.parseDouble(event.getNewValue()
						.toString()));

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void forward() {
		limitSpeed();
		avoidBoundary();
		avoidObstacles();
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + speed.getX();
		double moveY = pt.getY() + speed.getY();
		try {
			space.moveTo(this, moveX, moveY);
		} catch (SpatialException e) {
			System.err.println("Catched SpatialException:" + e.getMessage());
		}
	}

	// speed limit
	private void limitSpeed() {
		while (getVelocity() > maxVelocity) {
			speed = new NdPoint(speed.getX() * 0.8, speed.getY() * 0.8);
		}
		while (getVelocity() < minVelocity) {
			speed = new NdPoint(speed.getX() * 1.3, speed.getY() * 1.3);
		}
	}

	// @ScheduledMethod(start = 1, interval = 1, priority =
	// ScheduleParameters.FIRST_PRIORITY)
	public void avoidBoundary() {
		NdPoint myPoint = space.getLocation(this);
		NdPoint result = new NdPoint(0, 0);
		if (myPoint.getX() < minDistanceToBoundary
				&& (getRotation() < -Math.PI / 2 || getRotation() > Math.PI / 2)) {
			// <-
			result = new NdPoint(result.getX() + BOUNDARY_PUSH_FORCE,
					result.getY());
		} else if (myPoint.getX() > space.getDimensions().getDimension(0)
				- minDistanceToBoundary
				&& (getRotation() > -Math.PI / 2 || getRotation() < Math.PI / 2)) {
			// ->
			result = new NdPoint(result.getX() - BOUNDARY_PUSH_FORCE,
					result.getY());
		}
		if (myPoint.getY() < minDistanceToBoundary && getRotation() < 0) {
			// |
			// v
			result = new NdPoint(result.getX(), result.getY()
					+ BOUNDARY_PUSH_FORCE);
		} else if (myPoint.getY() > space.getDimensions().getDimension(1)
				- minDistanceToBoundary
				&& getRotation() > 0) {
			// ^
			// |
			result = new NdPoint(result.getX(), result.getY()
					- BOUNDARY_PUSH_FORCE);
		}
		speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
				+ result.getY());
	}

	// @ScheduledMethod(start = 1, interval = 1, priority =
	// ScheduleParameters.FIRST_PRIORITY)
	public void avoidObstacles() {
		List<Obstacle> obstacles = getObstacles();
		if (obstacles.size() > 0) {
			for (Obstacle obstacle : obstacles) {
				NdPoint thisLocation = space.getLocation(this);
				NdPoint obstacleLocation = space.getLocation(obstacle);
				double[] displacement = space.getDisplacement(obstacleLocation,
						thisLocation);
				speed = new NdPoint(speed.getX() + AVOID_OBSTACLE_RULE_WEIGHT
						/ displacement[0], speed.getY()
						+ AVOID_OBSTACLE_RULE_WEIGHT / displacement[1]);
			}
		}
	}

	// cohesion
	// TODO tu jest problem z liczeniem œredniej. Jak przekroczy granicê to siê
	// nagle œrodek grupy umieszcze w œrodku planszy
	@ScheduledMethod(start = 1, interval = 1)
	public void cohesianRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && cohesianRuleWeight != 0) {
			NdPoint result = new NdPoint(0, 0);

			for (Bird bird : neighborhood) {
				NdPoint otherPoint = space.getLocation(bird);
				result = new NdPoint(result.getX() + otherPoint.getX(),
						result.getY() + otherPoint.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			// turnTo(result, 0.1);
			NdPoint myPoint = space.getLocation(this);
			double[] displacement = space.getDisplacement(myPoint, result);

			result = new NdPoint(displacement[0] / cohesianRuleWeight,
					displacement[1] / cohesianRuleWeight);

			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}

		forward();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void separationRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && separationRuleWeight != 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Bird bird : neighborhood) {
				if (distance(bird) < minDistance) {
					NdPoint myPoint = space.getLocation(this);
					NdPoint otherPoint = space.getLocation(bird);
					double[] displacement = space.getDisplacement(myPoint,
							otherPoint);
					result = new NdPoint(result.getX() - displacement[0],
							result.getY() - displacement[1]);
				}
			}
			// turnTo(result);
			result = new NdPoint(result.getX() / separationRuleWeight,
					result.getY() / separationRuleWeight);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}

		forward();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void speedAlignmentRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && speedAlignmentRuleWeight != 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Bird bird : neighborhood) {
				result = new NdPoint(result.getX() + bird.speed.getX(),
						result.getY() + bird.speed.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			result = new NdPoint((result.getX() - speed.getX())
					/ speedAlignmentRuleWeight, (result.getY() - speed.getY())
					/ speedAlignmentRuleWeight);
			speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
					+ result.getY());
		}

		forward();
	}

	double getVelocity() {
		return Math.hypot(speed.getX(), speed.getY());
	}

	private void turnTo(NdPoint point, double velocityDelta) {
		NdPoint myPoint = space.getLocation(this);
		double azimuth = Math.atan2(point.getY() - myPoint.getY(), point.getX()
				- myPoint.getX());
		double rotation = getRotation();
		double velocity = getVelocity();
		velocity += velocityDelta;

		double newRotation = ((azimuth - rotation) / Math.PI)
				* (maxRotationSpeed - minRotationSpeed) + minRotationSpeed;
		speed = new NdPoint(velocity * Math.cos(newRotation), velocity
				* Math.sin(newRotation));
	}

	private double distance(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		return space.getDistance(myPoint, otherPoint);
	}

	private List<Bird> getNeighborhood() {
		List<Bird> circularNeighborhood = new ArrayList<Bird>();
		for (Object object : space.getObjects()) {
			if (object instanceof Bird) {
				Bird bird = (Bird) object;
				if (!bird.equals(this)) {
					if (distance(bird) <= neighborhoodRadius
							&& isInVisibleRange(bird)) {
						circularNeighborhood.add(bird);
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
		double A = speed.getY() / speed.getX();
		double B = -1;
		double C = thisLocation.getY() - A * thisLocation.getX();
		return Math.abs(A * obstacleLocation.getX() + B
				* obstacleLocation.getY() + C)
				/ Math.hypot(A, B) < obstacle.getObstacleRadius() + 0.2;
	}

	private boolean isInVisibleRange(Object object) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(object);
		double radian = Math.atan2(otherPoint.getY() - myPoint.getY(),
				otherPoint.getX() - myPoint.getX());
		return Math.abs(radian - getRotation()) < Math.toRadians(angleOfSight);
	}

	public NdPoint getSpeed() {
		return speed;
	}

	public double getRotation() {
		return Math.atan2(speed.getY(), speed.getX());
	}
}
