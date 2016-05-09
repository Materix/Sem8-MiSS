package miss.ui;

import miss.model.AnimalProperties;
import miss.model.BirdProperties;
import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory;

public class UserGlobalsAndPanelFactory extends
		AbstractReLogoGlobalsAndPanelFactory {

	@Override
	public void addGlobalsAndPanelComponents() {
		addSliderWL(AnimalProperties.AVOID_OBSTACLE_RULE_WEIGHT,
				AnimalProperties.AVOID_OBSTACLE_RULE_WEIGHT, 0, 0.01, 1, 0.4);
		addSliderWL(AnimalProperties.PERTURBATION_RULE_WEIGHT,
				AnimalProperties.PERTURBATION_RULE_WEIGHT, 0, 0.01, 1, 0.2);
		addSliderWL(AnimalProperties.OBSTACLE_DETECTION_RADIUS,
				AnimalProperties.OBSTACLE_DETECTION_RADIUS, 0, 0.1, 10d, 3.5d);
		addSliderWL(AnimalProperties.MAX_VELOCITY,
				AnimalProperties.MAX_VELOCITY, 0d, 0.01, 1d, 0.5);
		addSliderWL(AnimalProperties.MIN_VELOCITY,
				AnimalProperties.MIN_VELOCITY, 0d, 0.01, 1d, 0.25);
		addSliderWL(AnimalProperties.ANGLE_OF_SIGHT,
				AnimalProperties.ANGLE_OF_SIGHT, 0d, 1, 360d, 160);

		addSliderWL(BirdProperties.COHESIAN_RULE_WEIGHT,
				BirdProperties.COHESIAN_RULE_WEIGHT, 0d, 0.01, 1, 0.15);
		addSliderWL(BirdProperties.SEPARATION_RULE_WEIGHT,
				BirdProperties.SEPARATION_RULE_WEIGHT, 0d, 0.01, 1, 0.1);
		addSliderWL(BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT,
				BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT, 0d, 0.01, 1, 0.15);
		addSliderWL(BirdProperties.MIN_DISTANCE, BirdProperties.MIN_DISTANCE,
				0d, 0.1, 3d, 1);
		addSliderWL(BirdProperties.NEIGHBORHOOD_RADIUS,
				BirdProperties.NEIGHBORHOOD_RADIUS, 1, 0.1, 10, 4);
		addSliderWL(BirdProperties.PREDATOR_DETECT_RADIUS,
				BirdProperties.PREDATOR_DETECT_RADIUS, 1, 0.1, 10, 4);
		addSliderWL(BirdProperties.ENERGY_CONSUMED_PER_UNIT,
				BirdProperties.ENERGY_CONSUMED_PER_UNIT, 0.01, 0.01, 1, 0.5);
		addSliderWL(BirdProperties.ENERGY_THRESHOLD,
				BirdProperties.ENERGY_THRESHOLD, 5, 0.1, 30, 10);
		// addMonitor("currentDensity", 1);
	}
}
