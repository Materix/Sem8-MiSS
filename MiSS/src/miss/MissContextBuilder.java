package miss;

import miss.model.Bird;
import miss.model.Grass;
import miss.model.Obstacle;
import miss.model.Predator;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.continuous.WrapAroundBorders;

public class MissContextBuilder implements ContextBuilder<Object> {
	private static final int BOUND_SIZE = 3;

	@Override
	public Context<Object> build(Context<Object> context) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		int size = params.getInteger("size");
		int birdCount = params.getInteger("birdCount");
		int obstacleCount = params.getInteger("obstacleCount");
		int obstacleRadius = params.getInteger("obstacleRadius");
		int predatorCount = params.getInteger("predatorCount");
		int initialEnergy = params.getInteger("initialEnergy");

		int grassCount = params.getInteger("grassCount");

		context.setId("MiSS");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<>(),
				new WrapAroundBorders(), size, size);

		for (int i = 0; i < birdCount; i++) {
			context.add(new Bird(space, initialEnergy
					+ RandomHelper.getUniform().nextDoubleFromTo(-10, 10)));
		}

		for (int i = 0; i < obstacleCount; i++) {
			context.add(new Obstacle(obstacleRadius));
		}

		for (int i = 0; i < predatorCount; i++) {
			context.add(new Predator(space));
		}

		for (int i = 0; i < grassCount; i++) {
			context.add(new Grass(space, size));
		}

		return context;
	}
	// private class NotOnBoundRandomCartesianAdder<T> implements
	// ContinuousAdder<T> {
	// private final double boundSize;
	//
	// public NotOnBoundRandomCartesianAdder(double boundSize) {
	// this.boundSize = boundSize;
	// }
	//
	// @Override
	// public void add(ContinuousSpace<T> space, T obj) {
	// Dimensions dims = space.getDimensions();
	// double[] location = new double[dims.size()];
	// findLocation(location, dims);
	// while (!space.moveTo(obj, location)) {
	// findLocation(location, dims);
	// }
	// }
	//
	// private void findLocation(double[] location, Dimensions dims) {
	// double[] origin = dims.originToDoubleArray(null);
	// for (int i = 0; i < location.length; i++) {
	// try {
	// assert dims.getDimension(i) - boundSize > boundSize;
	// location[i] = RandomHelper.getUniform().nextDoubleFromTo(
	// boundSize, dims.getDimension(i) - boundSize)
	// - origin[i];
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	//
	// }
}
