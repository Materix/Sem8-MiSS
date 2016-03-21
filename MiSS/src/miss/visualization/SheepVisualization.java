package miss.visualization;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import miss.model.Sheep;
import repast.simphony.visualization.gui.styleBuilder.IconFactory2D;
import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.NamedShapeCreator;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;

public class SheepVisualization implements StyleOGL2D<Sheep> {

	private static final String SHAPE_ID = "SheepVisualizationShapeId";

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
	public VSpatial getVSpatial(Sheep object, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.getNamedSpatial(SHAPE_ID);
		}
		return spatial;
	}

	@Override
	public Color getColor(Sheep object) {
		return Color.BLUE;
	}

	@Override
	public Color getBorderColor(Sheep object) {
		return Color.BLACK;
	}

	@Override
	public int getBorderSize(Sheep object) {
		return 0;
	}

	@Override
	public float getRotation(Sheep sheep) {
		// return 90;
		return 90 - ((float) Math.toDegrees(sheep.getRotation()));
	}

	@Override
	public float getScale(Sheep object) {
		return 1;
	}

	@Override
	public String getLabel(Sheep object) {
		return null;
	}

	@Override
	public Font getLabelFont(Sheep object) {
		return null;
	}

	@Override
	public float getLabelXOffset(Sheep object) {
		return 0;
	}

	@Override
	public float getLabelYOffset(Sheep object) {
		return 0;
	}

	@Override
	public Position getLabelPosition(Sheep object) {
		return Position.NORTH;
	}

	@Override
	public Color getLabelColor(Sheep object) {
		return Color.BLACK;
	}
}
