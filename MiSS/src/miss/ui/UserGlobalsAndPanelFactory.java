package miss.ui;

import miss.model.SheepProperties;
import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory;

public class UserGlobalsAndPanelFactory extends
		AbstractReLogoGlobalsAndPanelFactory {

	@Override
	public void addGlobalsAndPanelComponents() {
		addSliderWL(SheepProperties.COHESIAN_RULE_WEIGHT,
				SheepProperties.COHESIAN_RULE_WEIGHT, 0d, 0.5, 50, 20);
		addSliderWL(SheepProperties.SEPARATION_RULE_WEIGHT,
				SheepProperties.SEPARATION_RULE_WEIGHT, 0d, 0.5, 50, 15);
		addSliderWL(SheepProperties.SPEED_ALIGNMENT_RULE_WEIGHT,
				SheepProperties.SPEED_ALIGNMENT_RULE_WEIGHT, 0d, 0.5, 50, 8);
		addSliderWL(SheepProperties.MAX_ROTATION_SPEED,
				SheepProperties.MAX_ROTATION_SPEED, 0.1, 0.1, 1d, 0.5);
		addSliderWL(SheepProperties.MIN_ROTATION_SPEED,
				SheepProperties.MIN_ROTATION_SPEED, 0.1, 0.1, 1d, 0.2);
		addSliderWL(SheepProperties.MIN_DISTANCE_TO_BOUNDARY,
				SheepProperties.MIN_DISTANCE_TO_BOUNDARY, 0d, 0.5, 5d, 1d);
		addSliderWL(SheepProperties.NEIGHBORHOOD_RADIUS,
				SheepProperties.NEIGHBORHOOD_RADIUS, 1d, 0.5, 10d, 3d);
		addSliderWL(SheepProperties.OBSTACLE_DETECTION_RADIUS,
				SheepProperties.OBSTACLE_DETECTION_RADIUS, 0, 0.5, 10d, 1d);
		addSliderWL(SheepProperties.MAX_VELOCITY, SheepProperties.MAX_VELOCITY,
				0d, 0.01, 0.5d, 0.2);
		addSliderWL(SheepProperties.MIN_VELOCITY, SheepProperties.MIN_VELOCITY,
				0d, 0.01, 0.5d, 0.2);
		addSliderWL(SheepProperties.MIN_DISTANCE, SheepProperties.MIN_DISTANCE,
				0d, 0.1, 3d, 1.5);
		addSliderWL(SheepProperties.ANGLE_OF_SIGHT,
				SheepProperties.ANGLE_OF_SIGHT, 0d, 1, 360d, 120);
		// addMonitor("currentDensity", 1);
	}
}
