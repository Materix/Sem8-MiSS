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

public class SheepBuilder implements ContextBuilder<Sheep> {

	@Override
	public Context<Sheep> build(Context<Sheep> context) {
		context.setId("MiSS");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Sheep> space = spaceFactory.createContinuousSpace(
				"space", context, new RandomCartesianAdder<>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 50,
				50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Sheep> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Sheep>(new WrapAroundBorders(),
						new SimpleGridAdder<>(), true, 50, 50));

		for (int i = 0; i < 10; i++) {
			context.add(new Sheep(space, grid));
		}

		for (Sheep sheep : context) {
			NdPoint point = space.getLocation(sheep);
			grid.moveTo(sheep, (int) point.getX(), (int) point.getY());
		}

		return context;
	}
}
