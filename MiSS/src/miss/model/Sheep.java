package miss.model;

import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Sheep {

	private ContinuousSpace<Sheep> space;

	private Grid<Sheep> grid;

	public Sheep(ContinuousSpace<Sheep> space, Grid<Sheep> grid) {
		this.space = space;
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Sheep> nghCreator = new GridCellNgh<Sheep>(grid, pt,
				Sheep.class, 5, 5);
		List<GridCell<Sheep>> gridCells = nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		int maxCount = -1;
		GridCell<Sheep> cellWithMostSheeps = null;
		for (GridCell<Sheep> cell : gridCells) {
			if (cell.size() > maxCount) {
				cellWithMostSheeps = cell;
				maxCount = cell.size();
			}
		}
		moveTowards(cellWithMostSheeps);
	}

	public void moveTowards(GridCell<Sheep> targetCell) {
		GridPoint point = targetCell.getPoint();
		if (!point.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(point.getX(), point.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
	}
}
