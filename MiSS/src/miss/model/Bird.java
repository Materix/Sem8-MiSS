package miss.model;

import groovy.util.ObservableMap;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.relogo.ReLogoModel;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.SimUtilities;

public class Bird extends Animal {
	private double cohesianRuleWeight;

	private double separationRuleWeight;

	private double velocityAlignmentRuleWeight;

	private double neighborhoodRadius;

	private double minDistance;

	public Bird(ContinuousSpace<Object> space) {
		super(space);
	}

	// @Setup
	@ScheduledMethod(start = 0, duration = 0)
	public void setup() {
		super.setup();
		ReLogoModel model = ReLogoModel.getInstance();

		neighborhoodRadius = Double.parseDouble(model.getModelParam(
				BirdProperties.NEIGHBORHOOD_RADIUS).toString());
		minDistance = Double.parseDouble(model.getModelParam(
				BirdProperties.MIN_DISTANCE).toString());
		cohesianRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.COHESIAN_RULE_WEIGHT).toString());
		separationRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.SEPARATION_RULE_WEIGHT).toString());
		velocityAlignmentRuleWeight = Double.parseDouble(model.getModelParam(
				BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT).toString());

		ObservableMap propertiesMap = ((ObservableMap) model.getModelParams());

		propertiesMap.addPropertyChangeListener(
				BirdProperties.NEIGHBORHOOD_RADIUS,
				event -> neighborhoodRadius = Double.parseDouble(event
						.getNewValue().toString()));
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
				event -> velocityAlignmentRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void cohesianRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && cohesianRuleWeight != 0) {
			double averageDistance = neighborhood.stream()
					.mapToDouble(this::distance).average().getAsDouble();
			for (Bird bird : neighborhood) {
				NdPoint thisLocation = space.getLocation(this);
				NdPoint birdLocation = space.getLocation(bird);
				velocity = new NdPoint(
						velocity.getX()
								+ cohesianRuleWeight
								* (((birdLocation.getX() - thisLocation.getX()) * (distance(bird) - averageDistance)) / (distance(bird)))
								/ neighborhood.size(),
						velocity.getY()
								+ cohesianRuleWeight
								* (((birdLocation.getY() - thisLocation.getY()) * (distance(bird) - averageDistance)) / (distance(bird)))
								/ neighborhood.size());
			}
			// NdPoint centerOfFlock = new NdPoint(0, 0);
			// for (Bird bird : neighborhood) {
			// NdPoint birdLocation = space.getLocation(bird);
			// centerOfFlock = new NdPoint(centerOfFlock.getX()
			// + birdLocation.getX(), centerOfFlock.getY()
			// + birdLocation.getY());
			// }
			// centerOfFlock = new NdPoint(centerOfFlock.getX()
			// / neighborhood.size(), centerOfFlock.getY()
			// / neighborhood.size());
			// NdPoint myPoint = space.getLocation(this);
			// double[] displacement = space.getDisplacement(myPoint,
			// centerOfFlock);
			//
			// centerOfFlock = new NdPoint(displacement[0] * cohesianRuleWeight,
			// displacement[1] * cohesianRuleWeight);
			//
			// velocity = new NdPoint(velocity.getX() + centerOfFlock.getX(),
			// velocity.getY() + centerOfFlock.getY());
		}
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void separationRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && separationRuleWeight != 0) {
			for (Bird bird : neighborhood) {
				if (distance(bird) < minDistance) {
					NdPoint thisLocation = space.getLocation(this);
					NdPoint birdLocation = space.getLocation(bird);
					velocity = new NdPoint(
							velocity.getX()
									- separationRuleWeight
									* (((minDistance * (birdLocation.getX() - thisLocation.getX())) / (distance(bird))) - (birdLocation.getX() - thisLocation.getX()))
									/ neighborhood.size(),
							velocity.getY()
									- separationRuleWeight
									* (((minDistance * (birdLocation.getY() - thisLocation
											.getY())) / (distance(bird))) - (birdLocation
											.getY() - thisLocation.getY()))
									/ neighborhood.size());
				}
			}
			// NdPoint result = new NdPoint(0, 0);
			// for (Bird bird : neighborhood) {
			// if (distance(bird) < minDistance) {
			// NdPoint myPoint = space.getLocation(this);
			// NdPoint otherPoint = space.getLocation(bird);
			// double[] displacement = space.getDisplacement(myPoint,
			// otherPoint);
			// result = new NdPoint(result.getX() - displacement[0],
			// result.getY() - displacement[1]);
			// }
			// }
			// // turnTo(result, 0);
			// result = new NdPoint(result.getX() / separationRuleWeight,
			// result.getY() / separationRuleWeight);
			// speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
			// + result.getY());
		}
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void velocityAlignmentRule() {
		List<Bird> neighborhood = getNeighborhood();
		if (neighborhood.size() > 0 && velocityAlignmentRuleWeight != 0) {
			NdPoint averageVelocity = new NdPoint(neighborhood.stream()
					.mapToDouble(bird -> bird.getVelocity().getX()).average()
					.getAsDouble(), neighborhood.stream()
					.mapToDouble(bird -> bird.getVelocity().getY()).average()
					.getAsDouble());
			velocity = new NdPoint(
					velocity.getX()
							+ (velocityAlignmentRuleWeight * (averageVelocity
									.getX() - velocity.getX())),
					velocity.getY()
							+ (velocityAlignmentRuleWeight * (averageVelocity
									.getY() - velocity.getY())));
			// NdPoint result = new NdPoint(0, 0);
			// for (Bird bird : neighborhood) {
			// result = new NdPoint(result.getX() + bird.speed.getX(),
			// result.getY() + bird.speed.getY());
			// }
			// result = new NdPoint(result.getX() / neighborhood.size(),
			// result.getY() / neighborhood.size());
			// result = new NdPoint((result.getX() - speed.getX())
			// / speedAlignmentRuleWeight, (result.getY() - speed.getY())
			// / speedAlignmentRuleWeight);
			// speed = new NdPoint(speed.getX() + result.getX(), speed.getY()
			// + result.getY());
		}
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
		SimUtilities.shuffle(circularNeighborhood, RandomHelper.getUniform());
		return circularNeighborhood;
	}
}
