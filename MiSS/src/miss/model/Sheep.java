package miss.model;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.SimUtilities;

public class Sheep {
	private static final int NEIGHBORHOOD_RADIUS = 3;

	private static final double MAX_VELOCITY = 0.5;

	private ContinuousSpace<Sheep> space;

	private Grid<Sheep> grid;

	private NdPoint velocity;

	public Sheep(ContinuousSpace<Sheep> space, Grid<Sheep> grid) {
		this.space = space;
		this.grid = grid;

		velocity = new NdPoint(RandomHelper.nextDoubleFromTo(-1, 1),
				RandomHelper.nextDoubleFromTo(-1, 1));
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		NdPoint v1 = rule1();
		NdPoint v2 = rule2();
		NdPoint v3 = rule3();

		velocity = new NdPoint(velocity.getX() + v1.getX() + v2.getX()
				+ v3.getX(), velocity.getY() + v1.getY() + v2.getY()
				+ v3.getY());

		slowDown();
		forward();
	}

	private NdPoint rule1() {
		ArrayList<Sheep> neighborhood = neighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				NdPoint otherPoint = space.getLocation(sheep);
				result = new NdPoint(result.getX() + otherPoint.getX(),
						result.getY() + otherPoint.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			NdPoint myPoint = space.getLocation(this);
			return new NdPoint((result.getX() - myPoint.getX()) / 50,
					(result.getY() - myPoint.getY()) / 50);
		}
		return new NdPoint(0, 0);
	}

	private NdPoint rule2() {
		ArrayList<Sheep> neighborhood = neighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				if (distance(sheep) < 0.5) {
					NdPoint myPoint = space.getLocation(this);
					NdPoint otherPoint = space.getLocation(sheep);
					result = new NdPoint(result.getX()
							- (otherPoint.getX() - myPoint.getX()),
							result.getY()
									- (otherPoint.getY() - myPoint.getY()));
					// c = c - (b.position - bJ.position)
				}
			}
			return result;
		}
		return new NdPoint(0, 0);
	}

	private NdPoint rule3() {
		ArrayList<Sheep> neighborhood = neighborhood();
		if (neighborhood.size() > 0) {
			NdPoint result = new NdPoint(0, 0);
			for (Sheep sheep : neighborhood) {
				result = new NdPoint(result.getX() + sheep.velocity.getX(),
						result.getY() + sheep.velocity.getY());
			}
			result = new NdPoint(result.getX() / neighborhood.size(),
					result.getY() / neighborhood.size());
			return new NdPoint((result.getX() - velocity.getX()) / 8,
					(result.getY() - velocity.getY()) / 8);
		}
		return new NdPoint(0, 0);
	}

	private void slowDown() {
		if (Math.hypot(velocity.getX(), velocity.getY()) > MAX_VELOCITY) {
			velocity = new NdPoint(velocity.getX() * 0.8, velocity.getY() * 0.8);
		}
	}

	private double distance(Sheep sheep) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(sheep);
		return space.getDistance(myPoint, otherPoint);
	}

	private void forward() {
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + velocity.getX();
		double moveY = pt.getY() + velocity.getY();
		space.moveTo(this, moveX, moveY);
		grid.moveTo(this, (int) moveX, (int) moveY);
	}

	private ArrayList<Sheep> neighborhood() {
		MooreQuery<Sheep> query = new MooreQuery<>(grid, this,
				NEIGHBORHOOD_RADIUS, NEIGHBORHOOD_RADIUS);
		Iterator<Sheep> iter = query.query().iterator();
		ArrayList<Sheep> sheepSet = new ArrayList<Sheep>();
		while (iter.hasNext()) {
			sheepSet.add(iter.next());
		}
		Iterable<Sheep> list = grid.getObjectsAt(grid.getLocation(this).getX(),
				grid.getLocation(this).getY());
		for (Sheep sheep : list) {
			sheepSet.add(sheep);
		}
		SimUtilities.shuffle(sheepSet, RandomHelper.getUniform());
		sheepSet.remove(this);
		ArrayList<Sheep> circularNeighborhood = new ArrayList<Sheep>();
		for (Sheep sheep : sheepSet) {
			if (distance(sheep) <= NEIGHBORHOOD_RADIUS
					&& isInVisibleRange(sheep)) {
				circularNeighborhood.add(sheep);
			}
		}
		return circularNeighborhood;
	}

	private boolean isInVisibleRange(Sheep sheep) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(sheep);
		double degree = Math.atan2(otherPoint.getY() - myPoint.getY(),
				otherPoint.getX() - myPoint.getX());
		return true; // tODO
	}

	public NdPoint getVelocity() {
		return velocity;
	}
}
