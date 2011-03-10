package com.sarxos.ow.client.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Domyœlny renderer vertexów.
 * @author Bartosz Firyn (SarXos)
 * @version 0.1 2006-09-01
 */
public class OWCellRenderer extends JLabel implements CellViewRenderer, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Domyœlny wygl¹d linii zaznaczenia komórek. Pole to jest niezmienialne.<br>
	 */
	public static final SXSelectionStroke DEFAULT_SELECTION_STROKE = new SXSelectionStroke(
			1, 
			SXSelectionStroke.CAP_BUTT, 
			SXSelectionStroke.JOIN_MITER, 
			6.0f, 
			new float[] {3f, 3f}, 
			0
	);	
	/**
	 * numer instancji renderera (czyli ile rendererów powsta³o). Pole to jest 
	 * wykorzystywane podczas tworzenia obrazu SVG - prefix numerowy jest dodawany na koñcu
	 * nazwy aby zosta³ unikalnym identyfikatorem.<br>
	 */
	protected static int rendererInstanceNumber = 0;
	
	/** 
	 * Widok vertexu.<br>
	 */
	protected VertexView view = null;
	/**
	 * Czy vertex ma facus (w danej chwili)<br>
	 */
	protected boolean focused = false; 
	/**
	 * Czy vertex jest zaznaczony (w danej chwili)<br>
	 */
	protected boolean selected = false;
	/**
	 * Czy jest widok w³¹czony (w danej chwili)<br>
	 */
	protected boolean preview = false; 
	/**
	 * Czy potomkowie s¹ zaznaczeni.<br>
	 */
	protected boolean isChildrenSelected = false;
	/**
	 * Czy vertex jest edytowany (w danej chwili)<br>
	 */
	protected boolean isEdited = false;
	/**
	 * Czy vertex jest rozszerzalny.<br>
	 */
	// FIXME: A co z tym zrobic - co to wogole jest ??? Jak smie tu stac !!
	protected boolean isSizable = false;
	/**
	 * Czy zawartoœæ vertexu jest autoskalowalna.<br>
	 */
	protected boolean isAutoscalable = false;
	/** 
	 * Kolor gradientu<br> 
	 */
	protected Color gradientColor = null;
	/** 
	 * Kolor domyœlny wyrysowania<br> 
	 */
	protected Color defaultForeground = null; 
	/** 
	 * Kolor domyœlny t³a<br> 
	 */
	protected Color defaultBackground = null;
	/** 
	 * Kolor domyœlny gradientu<br> 
	 */
	protected Color defaultGradientColor = null; 
	/**
	 * Kolor borderów.<br>
	 */
	protected Color borderColor = null;
	/**
	 * Domyslny wymiar krawêdzi wyrysowania<br>
	 */
	protected int borderWidth = 1;
	/**
	 * Czy jest podwójne buforowanie.<br> 
	 */
	protected boolean isDoubleBuffered = false;
	/** 
	 * Kolor siatki<br> 
	 */
	protected Color gridColor = null;
	/** 
	 * Kolor zaznaczenia<br> 
	 */
	protected Color highlightColor = null;
	/** 
	 * Kolor obramowania zblokowanego vertexu<br> 
	 */
	protected Color lockedHandleColor = null;
	/**
	 * Nazwa obiektu.<br>
	 */
	protected String objectName = null;
	/** 
	 * User object z renderowanego vertexu<br> 
	 */
	protected Object userObject = null; 
	/**
	 * Czy vertex jest kontenerem dla subprocesu.<br>
	 */
	protected boolean isSubprocessContainer = false;
	/**
	 * Czy graf jest przygotowany do exportu.<br>
	 */
	protected boolean exportPrepared = false;

	/**
	 * Constructs a renderer that may be used to render vertices.
	 */
	public OWCellRenderer() {
		defaultForeground = UIManager.getColor("Tree.textForeground");
		defaultBackground = UIManager.getColor("Tree.textBackground");
	}

	/**
	 * Configure and return the renderer component based on the passed in cell.
	 * The value is typically set from messaging the graph with
	 * <code>convertValueToString</code>. We recommend you check the value's
	 * class and throw an illegal argument exception if it's not correct.
	 * 
	 * @param graph
	 *            the graph that that defines the rendering context.
	 * @param view
	 *            the cell view that should be rendered.
	 * @param sel
	 *            whether the object is selected.
	 * @param focus
	 *            whether the object has the focus.
	 * @param preview
	 *            whether we are drawing a preview.
	 * @return the component used to render the value.
	 */
	public Component getRendererComponent(JGraph graph, CellView view, 
			boolean sel, boolean focus, boolean preview) {
		
		this.gridColor = graph.getGridColor();
		this.highlightColor = graph.getHighlightColor();
		this.lockedHandleColor = graph.getLockedHandleColor();
		this.isDoubleBuffered = graph.isDoubleBuffered();
		
		if(view instanceof VertexView) {
			this.view = (VertexView) view;
			setComponentOrientation(graph.getComponentOrientation());
			if (graph.getEditingCell() != view.getCell()) {
				Object label = graph.convertValueToString(view);
				if (label != null)
					setText(label.toString());
				else
					setText(null);
			} else {
				setText(null);
			}
			
			this.focused = focus;
			this.isChildrenSelected = graph.getSelectionModel().isChildrenSelected(view.getCell());
			this.selected = sel;
			this.preview = preview;
			
			if(this.view.isLeaf() || GraphConstants.isGroupOpaque(view.getAllAttributes())) {
				installAttributes(view);
			} else {
				resetAttributes();
			}

			return this;
		}
		return null;
	}

	/**
	 * Ustawia kolor gradientu
	 * @param gradientColor
	 */
	public void setGradientColor(Color gradientColor) {
		if(gradientColor != null) {
			this.gradientColor = gradientColor;
		} else {
			this.gradientColor = defaultGradientColor;
		}
	}

	/**
	 * Zwraca kolor gradientu
	 * @return Kolor gradientu
	 */
	public Color getGradientColor() {
		return gradientColor;
	}

	/**
	 * Hook for subclassers that is invoked when the installAttributes is not
	 * called to reset all attributes to the defaults. <br>
	 * Subclassers must invoke the superclass implementation.
	 * 
	 */
	protected void resetAttributes() {
		setText(null);
		setBorder(null);
		setOpaque(false);
		setGradientColor(null);
		setIcon(null);
	}

	/**
	 * Install the attributes of specified cell in this renderer instance. This
	 * means, retrieve every published key from the cells hashtable and set
	 * global variables or superclass properties accordingly.
	 * 
	 * @param view
	 *            the cell view to retrieve the attribute values from.
	 */
	protected void installAttributes(CellView view) {
		Map map = view.getAllAttributes();
		setIcon(GraphConstants.getIcon(map));
		setOpaque(GraphConstants.isOpaque(map));
		setBorder(GraphConstants.getBorder(map));
		setVerticalAlignment(GraphConstants.getVerticalAlignment(map));
		setHorizontalAlignment(GraphConstants.getHorizontalAlignment(map));
		setVerticalTextPosition(GraphConstants.getVerticalTextPosition(map));
		setHorizontalTextPosition(GraphConstants.getHorizontalTextPosition(map));
		borderColor = GraphConstants.getBorderColor(map);
		borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(map)));
		
		if (getBorder() == null && borderColor != null) {
			setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
		}
		
		Color foreground = GraphConstants.getForeground(map);
		setForeground((foreground != null) ? foreground : defaultForeground);
		Color gradientColor = GraphConstants.getGradientColor(map);
		setGradientColor(gradientColor);
		Color background = GraphConstants.getBackground(map);
		setBackground((background != null) ? background : defaultBackground);
		setFont(GraphConstants.getFont(map));
		
		
	}

	/** 
	 * Metoda rysuj¹ca komponent na grafie
	 * @param g - obiekt Graphics na którym rysujemy
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		try {
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(
					RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE
			);
			
			if(gradientColor != null && !preview && isOpaque()) {
				//setOpaque(false);
				GradientPaint gp = new GradientPaint(
						0, 0, 						// Cieniowanie
						getBackground(), 			// t³o
						getWidth(), getHeight(), 	// rozmiar rysowania
						getGradientColor(),			// kolor gradientu
						true						// true if the gradient pattern should cycle repeatedly between the two colors; false otherwise
				); 
				g2.setPaint(gp);
				int w = getWidth();
				int h = getHeight();
				
				g2.fillRect(0, 0, w, h);
			} 

			if(!isPreview()) {
				super.paint(g);
			}
			
			paintSelectionBorder(g);
			
		} catch (IllegalArgumentException e) {}
	}

	/**
	 * Rysuje obramowanie okreœlaj¹ce selekcjê komórki
	 * @param g - obiekt Graphics na którym rysujemy
	 */
	protected void paintSelectionBorder(Graphics g) {

		if(isChildrenSelected || selected) {

			Graphics2D g2 = (Graphics2D) g;
			Stroke previousStroke = g2.getStroke();
			
			Object oldAliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			
			g2.setStroke(DEFAULT_SELECTION_STROKE);
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_OFF
			);
			
			Dimension d = getSize();
			
			SXSelectionStroke stroke_1 = DEFAULT_SELECTION_STROKE;
			
			g2.setColor(highlightColor);
			g2.setStroke(stroke_1);
			g2.drawRect(1, 1, d.width - 2, d.height - 2);
			
			SXSelectionStroke stroke_2 = stroke_1.clone();
			stroke_2.setDashPhase(stroke_2.getDashPhase() + stroke_2.getDashArray()[0]);
			
			g2.setColor(lockedHandleColor);
			g2.setStroke(stroke_2);
			g2.drawRect(1, 1, d.width - 2, d.height - 2);
			
			g2.setStroke(previousStroke);
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING, 
					oldAliasing
			);
		}
	}

	/**
	 * Zwraca punkt przeciêcia trasowanej krawêdzi z obramowaniem (bounds) elementu, czyli
	 * w naszym przypadku prostok¹tu.
	 * @param view - widok vertexu
	 * @param source - Ÿród³o z którego wychodzi krawêdz
	 * @param p - jakiœ punkt
	 * @return Zwraca punkt w przestrzeni 2D okreœlaj¹cy przeciêcie
	 * @author Gaudenz Alder
	 */
	public Point2D getPerimeterPoint(VertexView view, Point2D source, Point2D p) {
		
		Rectangle2D bounds = view.getBounds();
		
		double x = bounds.getX();
		double y = bounds.getY();
		
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		
		return new Point2D.Double(xout, yout);
	}

	/**
	 * Czy renderer ma focus w danej chwili.<br>
	 * @return boolean
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Ustawia czy renderer ma focus w danej chwili.<br>
	 * @param focused
	 */
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	/**
	 * Czy renderer robi w danej chwili podgl¹d komórki przy przenoszeniu.<br>
	 * @return boolean
	 */
	public boolean isPreview() {
		return preview;
	}

	/**
	 * Ustawia czy renderer robi w danej chwili podgl¹d komórki przy przenoszeniu.<br>
	 * @param preview
	 */
	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	/**
	 * Czy renderowana w danej chwili komórka jest w stadium zaznaczenia.<br>
	 * @return boolean
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Ustawia czy renderowana w danej chwili komórka jest w stadium zaznaczenia.<br>
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the isAutoscalable
	 */
	public boolean isAutoscaled() {
		return isAutoscalable;
	}

	/**
	 * @param isAutoscalable the isAutoscalable to set
	 */
	public void setAutoscaled(boolean isAutoscalable) {
		this.isAutoscalable = isAutoscalable;
	}
}
