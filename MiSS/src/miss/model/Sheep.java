package miss.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Sheep {
	private static final double turnSpeed = 0.4;

	private final double desiredDistance = 0.5;

	private double distance;

	private double heading;

	private ContinuousSpace<Sheep> space;

	private Grid<Sheep> grid;

	final double separationWeight = RandomHelper.nextDoubleFromTo(0.0, 1.0);
	final double cohesionWeight = 1.0 - separationWeight;

	public Sheep(ContinuousSpace<Sheep> space, Grid<Sheep> grid) {
		this.space = space;
		this.grid = grid;
		distance = 0.2;
		heading = Math.random() * (2 * Math.PI);
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		assert (heading <= Math.PI * 2);
		forward();
		// heading = alignmentDirection();
		heading = averageTwoDirections(cohesionDirection(),
				separationDirection());
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

	private void forward() {
		NdPoint point = space.getLocation(this);
		float moveX = (float) (point.getX() + Math.cos(heading) * distance);
		float moveY = (float) (point.getY() + Math.sin(heading) * distance);
		space.moveTo(this, moveX, moveY);
		grid.moveTo(this, Math.round(moveX), Math.round(moveY));
	}

	private List<Sheep> getNeighborhood() {
		GridPoint point = grid.getLocation(this);
		MooreQuery<Sheep> query = new MooreQuery<>(grid, this);
		List<Sheep> neighborhoodSheep = StreamSupport.stream(
				query.query().spliterator(), false)
				.collect(Collectors.toList());

		for (Sheep sheep : grid.getObjectsAt(point.getX(), point.getY())) {
			neighborhoodSheep.add(sheep);
		}
		neighborhoodSheep.remove(this);
		SimUtilities.shuffle(neighborhoodSheep, RandomHelper.getUniform());
		return neighborhoodSheep;
	}

	private double alignmentDirection() {
		List<Sheep> neighborhoodSheep = getNeighborhood();
		double avgHeading = heading;
		for (Sheep sheep : neighborhoodSheep) {
			avgHeading = averageTwoDirections(avgHeading, towards(sheep));
		}
		return avgHeading;
	}

	private double averageTwoDirections(double angle1, double angle2) {
		assert (angle1 <= Math.PI * 2);
		assert (angle2 <= Math.PI * 2);
		angle1 = Math.toDegrees(angle1);
		angle2 = Math.toDegrees(angle2);
		if (Math.abs(angle1 - angle2) < 180) {
			return Math.toRadians((angle1 + angle2) / 2);
		} else {
			return oppositeDirection((oppositeDirection(angle1) + oppositeDirection(angle2)) / 2);
		}
	}

	public double oppositeDirection(double angle) {
		assert (angle <= Math.PI * 2);
		angle = Math.toDegrees(angle);
		return Math.toRadians((angle + 180) % 360);
	}

	public double towards(Sheep boid) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(boid);
		return SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
	}

	public double cohesionDirection() {
		List<Sheep> boidSet = getNeighborhood();
		if (boidSet.size() > 0) {
			double avgBoidDirection = heading;
			double avgBoidDistance = desiredDistance;
			for (Sheep boid : boidSet) {
				avgBoidDirection = averageTwoDirections(boid.getHeading(),
						avgBoidDirection);
				avgBoidDistance = avgBoidDistance + distance(boid) / 2;
			}
			if (avgBoidDistance > desiredDistance) {
				moveTowards(avgBoidDirection, 0.2 * cohesionWeight);
				return avgBoidDirection;
			} else {
				return heading;
			}
		} else {
			return heading;
		}
	}

	public void moveTowards(double direction, double dist) {
		NdPoint pt = space.getLocation(this);
		double moveX = pt.getX() + Math.cos(heading) * dist;
		double moveY = pt.getY() + Math.sin(heading) * dist;
		space.moveTo(this, moveX, moveY);
	}

	public double distance(Sheep boid) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(boid);
		double differenceX = Math.abs(myPoint.getX() - otherPoint.getX());
		double differenceY = Math.abs(myPoint.getY() - otherPoint.getY());
		return Math.hypot(differenceX, differenceY);
	}

	public double separationDirection() {
		List<Sheep> boidSet = getNeighborhood();
		ArrayList<Sheep> boidsTooClose = new ArrayList<Sheep>();
		for (Sheep boid : boidSet) {
			if (distance(boid) < desiredDistance) {
				boidsTooClose.add(boid);
			}
		}
		if (boidsTooClose.size() > 0) {
			double avgBoidDirection = heading;
			double distanceToClosestBoid = Double.MAX_VALUE;
			for (Sheep boid : boidsTooClose) {
				avgBoidDirection = averageTwoDirections(boid.getHeading(),
						avgBoidDirection);
				if (distance(boid) < distanceToClosestBoid) {
					distanceToClosestBoid = distance(boid);
				}
			}
			if (distanceToClosestBoid < desiredDistance * 0.5) {
				moveTowards(oppositeDirection(avgBoidDirection),
						0.2 * separationWeight);
			}
			if (toMyLeft(avgBoidDirection)) {
				return (heading + turnSpeed) % Math.PI * 2;
			} else {
				return (heading - turnSpeed) % Math.PI * 2;
			}
		}
		return heading;
	}

	public boolean toMyLeft(double angle) {
		double angleDegrees = Math.toDegrees(angle);
		double headingDegrees = Math.toDegrees(heading);
		if (Math.abs(angleDegrees - headingDegrees) < 180) {
			if (angleDegrees < headingDegrees) {
				return true;
			} else
				return false;
		} else {
			if (angleDegrees < headingDegrees) {
				return false;
			} else
				return true;
		}
	}

	public double getHeading() {
		return heading;
	}
}
