package miss;

import miss.model.Obstacle;
import miss.model.Sheep;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousAdder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.WrapAroundBorders;

public class SheepBuilder implements ContextBuilder<Object> {
	private static final int SIZE = 100;

	private static final int BOUND_SIZE = 3;

	private static final int SHEEP_COUNT = 100;

	private static final int OBSTACLE_COUNT = 15;

	private static final int OBSTACLE_RADIUS = 2;

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("MiSS");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new NotOnBoundRandomCartesianAdder<>(
						BOUND_SIZE), new WrapAroundBorders(), SIZE, SIZE);

		for (int i = 0; i < SHEEP_COUNT; i++) {
			Sheep sheep = new Sheep(space);
			context.add(sheep);
		}

		for (int i = 0; i < OBSTACLE_COUNT; i++) {
			Obstacle obstacle = new Obstacle(OBSTACLE_RADIUS);
			context.add(obstacle);
		}

		return context;
	}

	private class NotOnBoundRandomCartesianAdder<T> implements
			ContinuousAdder<T> {
		private final double boundSize;

		public NotOnBoundRandomCartesianAdder(double boundSize) {
			this.boundSize = boundSize;
		}

		@Override
		public void add(ContinuousSpace<T> space, T obj) {
			Dimensions dims = space.getDimensions();
			double[] location = new double[dims.size()];
			findLocation(location, dims);
			while (!space.moveTo(obj, location)) {
				findLocation(location, dims);
			}
		}

		private void findLocation(double[] location, Dimensions dims) {
			double[] origin = dims.originToDoubleArray(null);
			for (int i = 0; i < location.length; i++) {
				try {
					assert dims.getDimension(i) - boundSize > boundSize;
					location[i] = RandomHelper.getUniform().nextDoubleFromTo(
							boundSize, dims.getDimension(i) - boundSize)
							- origin[i];
				} catch (Exception e) {

				}
			}
		}

	}
}
