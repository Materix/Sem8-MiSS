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
	private double energy;

	private double energyConsumedPerUnit;

	private double cohesianRuleWeight;

	private double separationRuleWeight;

	private double velocityAlignmentRuleWeight;

	private double neighborhoodRadius;

	private double minDistance;

	private double predatorDetectRadius;

	private double energyThreshold;

	private boolean predatorDetected;

	public Bird(ContinuousSpace<Object> space, double initialEnergy) {
		super(space);
		energy = initialEnergy;
		predatorDetected = false;
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
		predatorDetectRadius = Double.parseDouble(model.getModelParam(
				BirdProperties.PREDATOR_DETECT_RADIUS).toString());
		energyConsumedPerUnit = Double.parseDouble(model.getModelParam(
				BirdProperties.ENERGY_CONSUMED_PER_UNIT).toString());
		energyThreshold = Double.parseDouble(model.getModelParam(
				BirdProperties.ENERGY_THRESHOLD).toString());

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
		propertiesMap.addPropertyChangeListener(
				BirdProperties.PREDATOR_DETECT_RADIUS,
				event -> predatorDetectRadius = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.ENERGY_CONSUMED_PER_UNIT,
				event -> energyConsumedPerUnit = Double.parseDouble(event
						.getNewValue().toString()));
		propertiesMap.addPropertyChangeListener(
				BirdProperties.ENERGY_THRESHOLD,
				event -> energyThreshold = Double.parseDouble(event
						.getNewValue().toString()));
	}

	@Override
	public void forward() {
		avoidPredators();
		if (energy < energyThreshold) {
			findGrass();
		}
		consumeEnergy();
		super.forward();

	}

	private void findGrass() {
		Grass nearestGrass = getNearestGrass();
		if (distance(nearestGrass) < 1) { // TODO change to parameter maybe
			energy += 40; // TODO change to parameter
			nearestGrass.relocate();
		} else {
			NdPoint grassLocation = space.getLocation(nearestGrass);
			NdPoint thisLocation = space.getLocation(this);
			NdPoint displacement = new NdPoint(space.getDisplacement(
					thisLocation, grassLocation));
			velocity = new NdPoint(velocity.getX() + displacement.getX(),
					velocity.getY() + displacement.getY());
		}
	}

	private void consumeEnergy() {
		energy = energy - getSpeed() * energyConsumedPerUnit;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void cohesianRule() {
		if (!predatorDetected) {
			List<Bird> neighborhood = getNeighborhood();
			if (neighborhood.size() > 0 && cohesianRuleWeight != 0) {
				double averageDistance = neighborhood.stream()
						.mapToDouble(this::distance).average().getAsDouble();
				if (averageDistance - 0.5 > minDistance) {
					averageDistance -= 0.5;
				}
				for (Bird bird : neighborhood) {
					NdPoint thisLocation = space.getLocation(this);
					NdPoint birdLocation = space.getLocation(bird);
					NdPoint displacement = new NdPoint(space.getDisplacement(
							thisLocation, birdLocation));
					velocity = new NdPoint(
							velocity.getX()
									+ cohesianRuleWeight
									* ((displacement.getX() * (distance(bird) - averageDistance)) / (distance(bird)))
									/ neighborhood.size(),
							velocity.getY()
									+ cohesianRuleWeight
									* ((displacement.getY() * (distance(bird) - averageDistance)) / (distance(bird)))
									/ neighborhood.size());
				}
			}
		}
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void separationRule() {
		if (!predatorDetected) {
			List<Bird> neighborhood = getNeighborhood();
			if (neighborhood.size() > 0 && separationRuleWeight != 0) {
				for (Bird bird : neighborhood) {
					if (distance(bird) < minDistance) {
						NdPoint thisLocation = space.getLocation(this);
						NdPoint birdLocation = space.getLocation(bird);
						NdPoint displacement = new NdPoint(
								space.getDisplacement(thisLocation,
										birdLocation));
						velocity = new NdPoint(
								velocity.getX()
										- separationRuleWeight
										* (((minDistance * (displacement.getX())) / (distance(bird))) - (displacement.getX()))
										/ neighborhood.size(),
								velocity.getY()
										- separationRuleWeight
										* (((minDistance * (displacement.getY())) / (distance(bird))) - (displacement
												.getY())) / neighborhood.size());
					}
				}
			}
		}
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void velocityAlignmentRule() {
		if (!predatorDetected) {
			List<Bird> neighborhood = getNeighborhood();
			if (neighborhood.size() > 0 && velocityAlignmentRuleWeight != 0) {
				NdPoint averageVelocity = new NdPoint(neighborhood.stream()
						.mapToDouble(bird -> bird.getVelocity().getX())
						.average().getAsDouble(), neighborhood.stream()
						.mapToDouble(bird -> bird.getVelocity().getY())
						.average().getAsDouble());
				velocity = new NdPoint(
						velocity.getX()
								+ (velocityAlignmentRuleWeight * (averageVelocity
										.getX() - velocity.getX())),
						velocity.getY()
								+ (velocityAlignmentRuleWeight * (averageVelocity
										.getY() - velocity.getY())));
			}
		}
	}

	// TODO add weight
	public void avoidPredators() {
		List<Predator> predators = getPredators();
		if (predators.size() > 0) {
			predatorDetected = true;
			for (Predator predator : predators) {
				NdPoint thisLocation = space.getLocation(this);
				NdPoint predatorLocation = space.getLocation(predator);
				NdPoint displacement = new NdPoint(space.getDisplacement(
						thisLocation, predatorLocation));
				velocity = new NdPoint(velocity.getX() - 0.5
						/ displacement.getX(), velocity.getY() - 0.5
						/ displacement.getY());
			}
		} else {
			predatorDetected = false;
		}
	}

	private List<Predator> getPredators() {
		List<Predator> circularNeighborhood = new ArrayList<>();
		for (Object object : space.getObjects()) {
			if (object instanceof Predator) {
				Predator predator = (Predator) object;
				if (distance(predator) <= predatorDetectRadius) {
					circularNeighborhood.add(predator);
				}
			}
		}
		SimUtilities.shuffle(circularNeighborhood, RandomHelper.getUniform());
		return circularNeighborhood;
	}

	private List<Bird> getNeighborhood() {
		List<Bird> circularNeighborhood = new ArrayList<>();
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

	private Grass getNearestGrass() {
		List<Grass> grass = new ArrayList<>();
		for (Object object : space.getObjects()) {
			if (object instanceof Grass) {
				grass.add((Grass) object);

			}
		}
		grass.sort((grass1, grass2) -> {
			return Double.compare(distance(grass1), distance(grass2));
		});
		return grass.get(0);
	}
}
