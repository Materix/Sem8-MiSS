package miss.visualization;

import java.awt.Color;
import java.awt.Font;

import miss.model.Obstacle;
import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

public class ObstacleVisualization implements StyleOGL2D<Obstacle> {

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
	}

	@Override
	public VSpatial getVSpatial(Obstacle object, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createCircle(
					10 * object.getObstacleRadius(), 16);
		}
		return spatial;
	}

	@Override
	public Color getColor(Obstacle object) {
		return Color.GRAY;
	}

	@Override
	public int getBorderSize(Obstacle object) {
		return 1;
	}

	@Override
	public Color getBorderColor(Obstacle object) {
		return Color.BLACK;
	}

	@Override
	public float getRotation(Obstacle object) {
		return 0;
	}

	@Override
	public float getScale(Obstacle object) {
		return 1;
	}

	@Override
	public String getLabel(Obstacle object) {
		return null;
	}

	@Override
	public Font getLabelFont(Obstacle object) {
		return null;
	}

	@Override
	public float getLabelXOffset(Obstacle object) {
		return 0;
	}

	@Override
	public float getLabelYOffset(Obstacle object) {
		return 0;
	}

	@Override
	public Position getLabelPosition(Obstacle object) {
		return Position.NORTH;
	}

	@Override
	public Color getLabelColor(Obstacle object) {
		return Color.BLACK;
	}

}
