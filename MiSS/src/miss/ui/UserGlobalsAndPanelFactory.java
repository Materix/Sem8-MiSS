package miss.ui;

import miss.model.AnimalProperties;
import miss.model.BirdProperties;
import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory;

public class UserGlobalsAndPanelFactory extends
		AbstractReLogoGlobalsAndPanelFactory {

	@Override
	public void addGlobalsAndPanelComponents() {

		addSliderWL(AnimalProperties.MAX_ROTATION_SPEED,
				AnimalProperties.MAX_ROTATION_SPEED, 0.1, 0.1, 1d, 0.5);
		addSliderWL(AnimalProperties.MIN_ROTATION_SPEED,
				AnimalProperties.MIN_ROTATION_SPEED, 0.1, 0.1, 1d, 0.2);
		addSliderWL(AnimalProperties.MIN_DISTANCE_TO_BOUNDARY,
				AnimalProperties.MIN_DISTANCE_TO_BOUNDARY, 0d, 0.5, 5d, 1d);
		addSliderWL(AnimalProperties.OBSTACLE_DETECTION_RADIUS,
				AnimalProperties.OBSTACLE_DETECTION_RADIUS, 0, 0.5, 10d, 1d);
		addSliderWL(AnimalProperties.MAX_VELOCITY,
				AnimalProperties.MAX_VELOCITY, 0d, 0.01, 0.5d, 0.2);
		addSliderWL(AnimalProperties.MIN_VELOCITY,
				AnimalProperties.MIN_VELOCITY, 0d, 0.01, 0.5d, 0.05);
		addSliderWL(AnimalProperties.ANGLE_OF_SIGHT,
				AnimalProperties.ANGLE_OF_SIGHT, 0d, 1, 360d, 120);

		addSliderWL(BirdProperties.COHESIAN_RULE_WEIGHT,
				BirdProperties.COHESIAN_RULE_WEIGHT, 0d, 0.5, 50, 20);
		addSliderWL(BirdProperties.SEPARATION_RULE_WEIGHT,
				BirdProperties.SEPARATION_RULE_WEIGHT, 0d, 0.5, 50, 15);
		addSliderWL(BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT,
				BirdProperties.SPEED_ALIGNMENT_RULE_WEIGHT, 0d, 0.5, 50, 8);
		addSliderWL(BirdProperties.MIN_DISTANCE, BirdProperties.MIN_DISTANCE,
				0d, 0.1, 3d, 1.5);
		addSliderWL(BirdProperties.NEIGHBORHOOD_RADIUS,
				BirdProperties.NEIGHBORHOOD_RADIUS, 1d, 0.5, 10d, 3d);
		// addMonitor("currentDensity", 1);
	}
}
