package miss;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import repast.simphony.space.Dimensions;
import repast.simphony.space.SpatialException;
import repast.simphony.space.continuous.ContinuousAdder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.WrapAroundBorders;
import cern.jet.random.Uniform;

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
		ObstacleWrapAroundBorders pointTranslator = new ObstacleWrapAroundBorders();
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new NotOnBoundRandomCartesianAdder<>(
						BOUND_SIZE), pointTranslator, size, size);

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
		pointTranslator.setSpace(space);

		return context;
	}

	private class ObstacleWrapAroundBorders extends WrapAroundBorders {
		private ContinuousSpace<Object> space;

		private List<Obstacle> obstacle;

		@Override
		public void transform(double[] transformedLocation,
				double... targetLocation) {
			setNewLocationWrapped(false);
			double[] newLocation = new double[targetLocation.length];
			for (int i = 0; i < targetLocation.length; i++) {
				newLocation[i] = getNewCoord(i, targetLocation[i]);
			}
			NdPoint newLocationPoint = new NdPoint(newLocation);
			if (obstacle == null
					|| obstacle.stream().allMatch(
							obstacle -> space.getDistance(
									space.getLocation(obstacle),
									newLocationPoint) > obstacle
									.getObstacleRadius())) {
				for (int i = 0; i < targetLocation.length; i++) {
					transformedLocation[i] = newLocation[i];
				}
			} else {
				throw new SpatialException("");
			}
		}

		public void setSpace(ContinuousSpace<Object> space) {
			this.space = space;
			obstacle = StreamSupport
					.stream(space.getObjects().spliterator(), false)
					.filter(ob -> ob instanceof Obstacle)
					.map(ob -> Obstacle.class.cast(ob))
					.collect(Collectors.toList());
		}
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
			while (!space.moveTo(obj, findLocation(space, dims))) {
			}
		}

		private double[] findLocation(ContinuousSpace<T> space, Dimensions dims) {
			boolean valid = false;
			int step = 0;
			double[] location = new double[dims.size()];
			while (!valid && step < 500) {
				double[] origin = dims.originToDoubleArray(null);
				Uniform uniform = RandomHelper.getUniform();
				for (int i = 0; i < location.length; i++) {
					try {
						assert dims.getDimension(i) - boundSize > boundSize;
						location[i] = uniform.nextDoubleFromTo(boundSize,
								dims.getDimension(i) - boundSize)
								- origin[i];
					} catch (Exception e) {
					}
				}
				NdPoint newLocation = new NdPoint(location);
				valid = StreamSupport
						.stream(space.getObjects().spliterator(), false)
						.map(space::getLocation)
						.filter(objectLocation -> objectLocation != null)
						.allMatch(
								objectLocation -> space.getDistance(
										objectLocation, newLocation) > boundSize);
				step++;
			}
			return location;
		}
	}
}
