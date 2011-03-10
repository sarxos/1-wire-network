package com.sarxos.ow.client.graph;

import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;


/**
 * Klasa widoku dla vertexów zawieraj¹cych w sobie jako user object obiekty dziedzicz¹ce po 
 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du¿o poprawek dlatego
 * trzeba nad ni¹ posiedzieæ.<br>
 * 
 * @author Bartosz Firyn (SarXos)
 * @version 0.2 2006-08-01
 */
public class OWCellView extends VertexView {

	private static final long serialVersionUID = 1L;
	
	protected static CellViewRenderer renderer = new OWCellRenderer();
	
	/**
	 * Klasa widoku dla vertexów zawieraj¹cych w sobie jako user object obiekty dziedzicz¹ce po 
	 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du¿o poprawek dlatego
	 * trzeba nad ni¹ posiedzieæ.<br>
	 * @author Bartosz Firyn (SarXos)
	 * @version 0.2 2006-08-01
	 */
	public OWCellView() {
		super();
		init();
	}

	/**
	 * Klasa widoku dla vertexów zawieraj¹cych w sobie jako user object obiekty dziedzicz¹ce po 
	 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du¿o poprawek dlatego
	 * trzeba nad ni¹ posiedzieæ.<br>
	 * @author Bartosz Firyn (SarXos)
	 * @version 0.2 2006-08-01
	 * @param cell - komórka której widok konstruujemy
	 */
	public OWCellView(Object cell) {
		super(cell);
		init();
	}
	
	protected void init() {
	}
	
	/** 
	 * Zwraca renderer dla danego widoku.
	 * @return Zwraca komponent renderuj¹cy
	 * @see org.jgraph.graph.AbstractCellView#getRenderer()
	 */
	public CellViewRenderer getRenderer() {
		return (OWCellRenderer) renderer;
	}

	/** 
	 * Zwraca punkt przeciêcia obramowania boundsów komórki grafu (czyli w tym przypadku
	 * vertexu) oraz prostej linii rozci¹gniêtej pomiêdzy punktem identyfikowanym przez 
	 * Ÿród³o linii (source) oraz specyficznym punktem p. Metoda ta wywo³uje tak¹ sam¹
	 * metodê z tym, ¿e zawart¹ w rendererze elementu.<br><br>
	 * 
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.<br>
	 * 
	 * @param edge - krawêdz, której przeciêcie badamy
	 * @param source - Ÿród³o krawêdzi
	 * @param p - punkt rozciagniêcia krawêdzi
	 * @return Zwraca punkt przeciêcia tej krawêdzi z boundsami
	 * @see org.jgraph.graph.CellView#getPerimeterPoint(org.jgraph.graph.EdgeView, java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Point2D pp = null;
		if(renderer != null) {
			OWCellRenderer myRenderer = (OWCellRenderer) getRenderer();
			pp = myRenderer.getPerimeterPoint(this, source, p);
		} else {
			pp = super.getPerimeterPoint(edge, source, p);
		}
		return pp;
	}
}
