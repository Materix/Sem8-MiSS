package miss;

import miss.model.Sheep;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class SheepBuilder implements ContextBuilder<Object> {
	private static final int SIZE = 25;

	private static final int SHEEP_COUNT = 10;

	private static final int OBSTACLE_COUNT = 10;

	private static final int OBSTACLE_RADIUS = 5;

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("MiSS");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), SIZE,
				SIZE);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<>(), true, SIZE, SIZE));

		for (int i = 0; i < SHEEP_COUNT; i++) {
			Sheep sheep = new Sheep(space, grid);
			context.add(sheep);
			NdPoint point = space.getLocation(sheep);
			grid.moveTo(sheep, (int) point.getX(), (int) point.getY());

		}

		return context;
	}
}
