package miss.model;

import groovy.util.ObservableMap;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.relogo.ReLogoModel;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

public class Bird extends Animal {
	private double cohesianRuleWeight;

	private double separationRuleWeight;

	private double speedAlignmentRuleWeight;

	private double neighborhoodRadius;

	private double minDistance;

	// private NdPoint speed;

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
		speedAlignmentRuleWeight = Double.parseDouble(model.getModelParam(
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
				event -> speedAlignmentRuleWeight = Double.parseDouble(event
						.getNewValue().toString()));
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
}
