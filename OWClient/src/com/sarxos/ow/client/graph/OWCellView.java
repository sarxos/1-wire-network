package com.sarxos.ow.client.graph;

import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;


/**
 * Klasa widoku dla vertex�w zawieraj�cych w sobie jako user object obiekty dziedzicz�ce po 
 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du�o poprawek dlatego
 * trzeba nad ni� posiedzie�.<br>
 * 
 * @author Bartosz Firyn (SarXos)
 * @version 0.2 2006-08-01
 */
public class OWCellView extends VertexView {

	private static final long serialVersionUID = 1L;
	
	protected static CellViewRenderer renderer = new OWCellRenderer();
	
	/**
	 * Klasa widoku dla vertex�w zawieraj�cych w sobie jako user object obiekty dziedzicz�ce po 
	 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du�o poprawek dlatego
	 * trzeba nad ni� posiedzie�.<br>
	 * @author Bartosz Firyn (SarXos)
	 * @version 0.2 2006-08-01
	 */
	public OWCellView() {
		super();
		init();
	}

	/**
	 * Klasa widoku dla vertex�w zawieraj�cych w sobie jako user object obiekty dziedzicz�ce po 
	 * JComponent - czyli elementy Swing. W klasie tej potrzeba jeszcze du�o poprawek dlatego
	 * trzeba nad ni� posiedzie�.<br>
	 * @author Bartosz Firyn (SarXos)
	 * @version 0.2 2006-08-01
	 * @param cell - kom�rka kt�rej widok konstruujemy
	 */
	public OWCellView(Object cell) {
		super(cell);
		init();
	}
	
	protected void init() {
	}
	
	/** 
	 * Zwraca renderer dla danego widoku.
	 * @return Zwraca komponent renderuj�cy
	 * @see org.jgraph.graph.AbstractCellView#getRenderer()
	 */
	public CellViewRenderer getRenderer() {
		return (OWCellRenderer) renderer;
	}

	/** 
	 * Zwraca punkt przeci�cia obramowania bounds�w kom�rki grafu (czyli w tym przypadku
	 * vertexu) oraz prostej linii rozci�gni�tej pomi�dzy punktem identyfikowanym przez 
	 * �r�d�o linii (source) oraz specyficznym punktem p. Metoda ta wywo�uje tak� sam�
	 * metod� z tym, �e zawart� w rendererze elementu.<br><br>
	 * 
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.<br>
	 * 
	 * @param edge - kraw�dz, kt�rej przeci�cie badamy
	 * @param source - �r�d�o kraw�dzi
	 * @param p - punkt rozciagni�cia kraw�dzi
	 * @return Zwraca punkt przeci�cia tej kraw�dzi z boundsami
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
