package miss.visualization;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import miss.model.Bird;
import repast.simphony.visualization.gui.styleBuilder.IconFactory2D;
import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.NamedShapeCreator;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

public class BirdVisualization implements StyleOGL2D<Bird> {

	private static final String SHAPE_ID = "BirdVisualizationShapeId";

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;

		Shape shape = IconFactory2D.getShape("triangle");
		Rectangle2D bounds = shape.getBounds2D();
		float size = 20;
		float scaleX = size / (float) bounds.getWidth();
		float scaleY = size / (float) bounds.getWidth();
		shape = AffineTransform.getScaleInstance(0.5 * scaleX, scaleY)
				.createTransformedShape(shape);
		GeneralPath path = new GeneralPath(shape);
		path.closePath();
		NamedShapeCreator creator = factory.createNamedShape(SHAPE_ID);
		creator.addShape(path.createTransformedShape(new AffineTransform()),
				Color.BLACK, true);
		creator.registerShape();
	}

	@Override
	public VSpatial getVSpatial(Bird object, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.getNamedSpatial(SHAPE_ID);
		}
		return spatial;
	}

	@Override
	public Color getColor(Bird object) {
		return Color.BLUE;
	}

	@Override
	public Color getBorderColor(Bird object) {
		return Color.BLACK;
	}

	@Override
	public int getBorderSize(Bird object) {
		return 1;
	}

	@Override
	public float getRotation(Bird bird) {
		return 90 - ((float) Math.toDegrees(bird.getRotation()));
	}

	@Override
	public float getScale(Bird object) {
		return 1;
	}

	@Override
	public String getLabel(Bird object) {
		return null;
	}

	@Override
	public Font getLabelFont(Bird object) {
		return null;
	}

	@Override
	public float getLabelXOffset(Bird object) {
		return 0;
	}

	@Override
	public float getLabelYOffset(Bird object) {
		return 0;
	}

	@Override
	public Position getLabelPosition(Bird object) {
		return Position.NORTH;
	}

	@Override
	public Color getLabelColor(Bird object) {
		return Color.BLACK;
	}
}
