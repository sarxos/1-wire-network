package com.sarxos.ow.client.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphSelectionModel;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import com.sarxos.ow.client.ApplicationContext;
import com.sarxos.ow.client.graph.devices.ThermometerPopup;


/**
 * Klasa obs�uguj�ca dzia�alno�ci myszy - czyli zaznaczanie, klikanie, dragowanie oraz 
 * scrollowanie. Klasa ta dziedziczy po prostym uchwycie zaznacze� oraz implementuje
 * nas�uchiwacza popup�w - PopupMenuListener. Listener ten jest o tyle wa�ny ze 
 * okresla kiedy popup jest widoczny a kiedy niewidoczny, poniewaz w czasie widocznosci 
 * popupa zadna akcja maloweania na plaszczyznie grafu nie moze byc wykonana.<br>
 * @author Bartosz Firyn (SarXos)
 * @version 0.5 2006-10-04
 */
public class MarqueeHandler extends BasicMarqueeHandler implements PopupMenuListener {

	
	/**
	 * Port nad kt�rym aktualnie znajduje si� mysz w stadium dragownia.<br>
	 */
	protected PortView actPort;
	/**
	 * Port od kt�rego rozpocz�a si� procedura dragowania.<br>
	 */
	protected PortView firstPort;
	/**
	 * Aktualny punkt dragowania.<br>
	 */
	protected Point2D actPoint; 
	/**
	 * Punkt w kt�rym rozpocz�o sie dragowanie.<br>
	 */
	protected Point2D startPoint;
	protected Point2D viewStartPoint = null;
	
	/**
	 * Czy narysowano focus selekcji.<br>
	 */
	boolean isSelectionPainted = false;
	/**
	 * Czy usuni�to focus selekcji.<br> 
	 */
	boolean isSelectionRemoved = false;
	/**
	 * Kolor focusa selekcji.<br> 
	 */
	protected Color selectionColor = new Color(153, 153, 255, 25);
	/**
	 * Okresla czy PopupMenu jest widoczne czy nie. Chodzi bowiem o to, aby zaznaczenie 
	 * (selection marquee) nie by�o rysowane gdy jest wy�wietlony Popup poniewa� nachodzi
	 * na jego boundsy i niszczy struktur� GUI Popupa (po prostu �le wygl�da i koniec).<br>
	 */
	boolean isPopupVisible = false;
	/**
	 * Kod lewego klawisza myszy.<br>
	 */
	public final static int MOUSE_LEFT = 1;
	/**
	 * Kod �rodkowego klawisza myszy.<br>
	 */
	public final static int MOUSE_MIDDLE = 2;
	/**
	 * Kod prawego klawisza myszy.<br>
	 */
	public final static int MOUSE_RIGHT = 3;

	/**
	 * Konstruktor.<br> 
	 */
	public MarqueeHandler() {}
	
	/**
	 * Zwraca tablic� zaznaczonych kom�rek (wraz z ich podkom�rkami).<br>
	 * @return Zwraca typ <b>Object[]</b>
	 */
	public static Object[] getSelectedCells() {
		JGraph graph = ApplicationContext.getInstance().getCurrentGraph();
		Object cells[] = graph.getSelectionCells();
		cells = graph.getDescendants(cells);
		return cells;
	}

	/**
	 * Tworzymy wyskakuj�ce menu dla prawoklikni�tego elementu
	 * @param point Punkt w ktorym klikn�li�my
	 * @param cell Kom�rka na kt�rej nast�pi�o klikni�cie
	 * @return Nowe PopupMenu gotowe do otwarcia
	 */
	public JPopupMenu makePopupMenu(final Point point, final Object cell) {
		
		JPopupMenu popupMenu = new ThermometerPopup(cell);

		
		
//		// Je�li kom�rka nie jest pusta (tzn czyli �e wog�le jest)
//		if(cell != null) {
//
//			boolean isEdge = false;
//			boolean isSelection = false;
//			boolean isVertex = false;
//			boolean isText = false;
//			boolean isGroup = false;
//			
//			if(cell instanceof DefaultEdge) {
//				isEdge = true;
//			} else {
//				isVertex = true;
//			}
//
//			OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();
//			
//			// Sprawdzamy czy jest grupa
//			Object[] cells = graph.getSelectionCells();
//			if(cells.length > 1) {
//				isSelection = true;	// selekcja
//				isVertex = false;
//				isEdge = false;
//				isGroup = false;
//				isText = false;
//			}
//			
//			// sprawdzamy czy jest vertexem
//			Object groupcell = graph.getSelectionCell();
//			if(groupcell instanceof GroupVertex) {
//				isGroup = true;
//				isVertex = false;
//			}
//
//			// czy jest indykatorem tekstowym
//			if(graph.getModel().isTextIndicator(cell)) {
//				isVertex = false;
//				isText = true;
//			}
//
//			/* NOTE!
//			 * Dla kazdego typu jest przewidzainy inny popup.
//			 */
//			
//			if(isSelection) {
//				popupMenu = new PopupMenuSelection();
//			}
//			
//			if(isVertex) {
//				popupMenu = new PopupMenuVertex();
//			}
//			
//			if(isText) {
//				popupMenu = new PopupMenuText();
//			}
//			
//			if(isGroup) {
//				popupMenu = new PopupMenuGroup();
//			}
//			
//			if(isEdge) {
//				popupMenu = new PopupMenuEdge();
//			}
//		} else {
//			
//			// jesli komorka jest null (tj. puste zaznaczenie)
//			popupMenu = new PopupMenuCanvas();
//			
//		}

		popupMenu.addPopupMenuListener(this);
		
		return popupMenu;
	}
	
	/**
	 * Zwraca PortView z miejsca w ktorym klikn�li�my mych�.<br>
	 * @param point - punkt w kt�rym klikn�lismy
	 * @return zwraca widok dla portu kt�remu odpowiada punkt klikni�cia  
	 */
	public PortView getSourcePortAt(Point2D point) {

		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();

		// Wy��czamy skoki do port�w (po��cze� z vertexem)
		graph.setJumpToDefaultPort(false);
		PortView sourcePortView;
		
		try {
			// Znajdujemy widok Portu na podstawie koordynat�w klikni�cia
			sourcePortView = graph.getPortViewAt(point.getX(), point.getY());
		} finally {
			graph.setJumpToDefaultPort(true);
		}
		return sourcePortView;
	}
	
	protected Point2D getPointFromObject(Object o) {
		Point2D a = null;
		if(o == null) {
			return a;
		} else {
			if(o instanceof Point2D) {
				a = (Point2D)o;
			} else if(o instanceof PortView) {
				a = ((PortView)o).getLocation();
			} else {
				System.out.println("MouseManager: getPointFromObject(): B��d podczas rzutowania: ClassCastException.");
				return null;
			}
			return a;
		}
	}
	
	/**
	 * Czy zaznaczenie kom�rki jest wymuszone.<br>
	 * @see org.jgraph.graph.BasicMarqueeHandler#isForceMarqueeEvent(java.awt.event.MouseEvent)
	 */
	public boolean isForceMarqueeEvent(MouseEvent event) {
		
		if(event.isShiftDown() || event.isControlDown()) {
			return false;
		}

		ApplicationContext ctx = ApplicationContext.getInstance();
		OWGraph graph = ctx.getCurrentGraph();
		
		// Patrzymy czy jest prawoklik - wtedy wy�wietlamy popup menu
		if(SwingUtilities.isRightMouseButton(event)) {
			return true;
		}
		
		// Szukamy portu na kt�rym nast�pi�o klikni�cie, a nast�pnie patrzymy czy jest on
		// widzialny (tzn czy isPortVisible() == true) - to znaczy �e klikn�li�my na 
		// port danej kom�rki.
		PortView portView = getSourcePortAt(event.getPoint());
		actPort = portView;
		return super.isForceMarqueeEvent(event);
	}		
	
	/**
	 * Przyci�ni�cie klawisza myszy
	 * @see org.jgraph.graph.BasicMarqueeHandler#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {

		ApplicationContext ctx = ApplicationContext.getInstance();
		OWGraph graph = ctx.getCurrentGraph();
		Object cell = graph.getFirstCellForLocation(event.getX(), event.getY());

		switch(getMouseKeyNum(event)) {
			case MOUSE_LEFT:
				super.mousePressed(event);
				break;
			case MOUSE_RIGHT:
				JPopupMenu popupMenu = makePopupMenu(event.getPoint(), cell);
				popupMenu.show(graph, event.getX(), event.getY());
				super.mousePressed(event);
				break;
		}
		
		Point2D p = event.getPoint();
		
		boolean noCellInHitRegion = graph.getFirstCellForLocation(p.getX(), p.getY()) == null;
		boolean isShiftDown = event.isShiftDown();
		
		if(noCellInHitRegion && !isShiftDown) {
			graph.clearSelection();
		}
	}

	/**
	 * Poruszanie myszk�
	 * @see org.jgraph.graph.BasicMarqueeHandler#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent event) {

		if(event == null) {
			super.mouseMoved(event);
			return;
		}

		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();

		/* NOTE:
		 * W tym miejscu sprawdzamy czy mysza nie znajduje sie nad komorka ktora jest
		 * aktualnie w stanie zaznaczenia - a jesli tak to nalezy zmienic kursor na
		 * kursor przenoszenia. Bardzo wa�nym jest aby po ka�dorazowej zmianie 
		 * kursora event zosta� skonsumowany.
		 */
		
		Point2D p = null;			// poczatkowo pusty
		p = event.getPoint();		// potem pozycja z ekranu
		p = graph.fromScreen(p);	// a nastepnie translacja na wspolrzedne grafu
		
		updateCellSelection(event);
		updateMouseCursor(event);
		
		//super.mouseMoved(event);
	}

	
	
	/** 
	 * Mysz puszczona.<br>
	 * @see org.jgraph.graph.BasicMarqueeHandler#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {

		ApplicationContext ctx = ApplicationContext.getInstance(); 
//		NNDProcessTool tool = ctx.getCurrentTool();		
		OWGraph graph = ctx.getCurrentGraph(); 

		/* NOTE!
		 * 
		 */
		
		GraphSelectionModel gsm = graph.getSelectionModel();
//		if(gsm instanceof CellSelectionModel) {
//			CellSelectionModel csm = (CellSelectionModel) gsm;
//			csm.resumeAnimation();
//		}

		// Jesli zdarznie sie zgadza ora pierwszy i drugi port
		boolean isNull = event == null || actPort == null || firstPort == null;
		
//		if(tool == NNDProcessTool.TOOL_LINKER) { 
//			/* NOTE:
//			 * W tym miejscu sprawdzamy czy da si� utworzyc po�aczenie pomiedzy dwoma portami,
//			 * tj. czy pierwszy i drugi nie s� null oraz czy event nie jest null. Dalsze 
//			 * sterowanie po��czeniem odbywa si� z poziomu GraphManagera oraz modelu 
//			 * strukturalnego grafu.
//			 */
//			
//			if(!isNull) {
//				
//				// to ustanawiamy po��czenie
//				graph.createConnection(
//						(Port) firstPort.getCell(), 
//						(Port) actPort.getCell()
//				);
//				event.consume();
//
//			} else {
//				graph.repaint();
//			}
//		}
//		
//		if(tool == NNDProcessTool.TOOL_EDGE) {
//			
//			if(startPoint != null && actPoint != null) {
//
//				double x1 = startPoint.getX();
//				double x2 = actPoint.getX();
//				double y1 = startPoint.getY();
//				double y2 = actPoint.getY();
//				double mod2 = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
//				
//				// jesli krawedz jest dluzsza niz 20px
//				if(Math.sqrt(mod2) >= 10) {
//				
//					ProcessEdge edge = null;
//					DefaultGraphVertex dock_1 = null;
//					DefaultGraphVertex dock_2 = null;
//
//					try {
//						edge = graph.getProducer().createEdgeFromXML("res/cxp/edge.xml");
//						dock_1 = graph.getProducer().createVertexFromXML("res/cxp/edge_docker.xml");
//						dock_2 = graph.getProducer().createVertexFromXML("res/cxp/edge_docker.xml");
//					} catch(Exception ex) {
//						DialogException.showException(ex.getMessage());
//						ex.printStackTrace();
//					}
//
//					Rectangle2D bounds1 = GraphConstants.getBounds(dock_1.getAttributes());
//					bounds1.setFrame(
//							startPoint.getX() - bounds1.getWidth() / 2, 
//							startPoint.getY() - bounds1.getHeight() / 2,
//							bounds1.getWidth(),
//							bounds1.getHeight()
//					);
//
//					Rectangle2D bounds2 = GraphConstants.getBounds(dock_2.getAttributes());
//					bounds2.setFrame(
//							actPoint.getX() - bounds2.getWidth() / 2,
//							actPoint.getY() - bounds2.getHeight() / 2,
//							bounds2.getWidth(), 
//							bounds2.getHeight()
//					);			
//
//					CellConstants.setBounds(dock_1.getAttributes(), bounds1);
//					CellConstants.setBounds(dock_2.getAttributes(), bounds2);
//
//					graph.getGraphLayoutCache().insert(new Object[] {dock_1, dock_2});
//					graph.getGraphLayoutCache().insertEdge(edge, dock_1.getFirstChild(), dock_2.getFirstChild());
//					graph.getGraphLayoutCache().toFront(new Object[] {dock_1, dock_2});
//
//					Logger.logMessage(
//							getClass(),
//							"Wstawiono kraw�d� niedokowaln�."
//					);
//				}
//			}
//		}
		
		// Resetujemy zmienne dla port�w.
		firstPort = actPort = null;
		startPoint = actPoint = null;

		/* NOTE:
		 * Tutaj sprawdzamy czy aktualnie s� zaznaczone jakie� kom�rki. Bo jesli nie s�, to
		 * trzeba wy��czy� akcje kopiowania i wycinania obiekt�w grafu. Czyli wyzwalamy
		 * metod� updateActions.
		 */

//		graph.updateActions();
		
		/* NOTE:
		 * Uwaga! Przy zwolnieniu prawokliku event musi koniecznie by� konsumowany. Inaczej
		 * nie dzia�aj� poprawnie zaznaczenia - tzn. zamiast przenosi� focus na klikni�t� 
		 * kom�rk�, to przenosi ca�e zaznaczenie. W ten oto spos�b nie dzia�aj�  
		 * popupy bo zamiast dla kom�rki, mamy dla grupy :(
		 */
		if(!isNull || SwingUtilities.isRightMouseButton(event)) {
		//if(SwingUtilities.isRightMouseButton(event)) {			
			event.consume();
		}

		//updateCellSelection(event);
		
		// i przekazujemy wywo�anie do superklasy
		super.mouseReleased(event);
	}
	
	/**
	 * Dragowanie myszy.<br>
	 * @see org.jgraph.graph.BasicMarqueeHandler#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {

		ApplicationContext ctx = ApplicationContext.getInstance();
//		NNDProcessTool tool = ctx.getCurrentTool();
		OWGraph graph = ctx.getCurrentGraph();
		Point2D point = event.getPoint();
		
//		if(tool == NNDProcessTool.TOOL_LINKER) {
//
//			/* NOTE!
//			 * Sprawdzamy czy punkt startowy nie jest pusty - je�li nie jest to zaczynamy
//			 * rysowanie kraw�dzi.
//			 */			
//			if(startPoint != null) {
//
//				// pobieramy punkt z eventu klikni�cia i z tego miejsca bierzemy PortView
//				PortView newPort = graph.getPortViewAt(point.getX(), point.getY());
//
//				// Jesli nowy port nie jest pusty i jest r�zny od aktualnego 
//				if(newPort == null || newPort != actPort) {
//
//					// Pobieramy obiekt Graphics z grafu
//					Graphics2D g = (Graphics2D) graph.getGraphics();
//
//					// Rysujemy w trybie XOR i ukrywamy wczesniejsze polaczenia
//					g.setXORMode(Color.BLACK);
//					paintConnector(graph.getBackground(), Color.BLACK, g);
//
//					// Jesli znleziono port wtedy podpinamy do jego lokalizacji
//					actPort = newPort;
//					if(actPort != null) {
//						actPoint = graph.toScreen(actPort.getLocation());
//						// Jesli nie znaleziono portu wtedy podpinamy do lokalizacji mychy
//					} else {
//						actPoint = graph.snap(event.getPoint());
//					}
//
//					g.setXORMode(graph.getBackground());
//					paintConnector(Color.BLACK, graph.getBackground(), g);
//				}
//			}
//		}
//		
//		if(tool == NNDProcessTool.TOOL_EDGE) {
//			
//			/* NOTE!
//			 * Sprawdzamy czy punkt startowy nie jest pusty - je�li nie jest to zaczynamy
//			 * rysowanie kraw�dzi.
//			 */			
//			if(startPoint != null && actPoint != null) {
//				Graphics2D g = (Graphics2D) graph.getGraphics();
//				g.setXORMode(Color.BLACK);
//				paintConnector(graph.getBackground(), Color.BLACK, g);
//				
//				actPoint = graph.toScreen(event.getPoint());
//				actPoint = graph.snap(actPoint);
//				
//				g.setXORMode(graph.getBackground());
//				paintConnector(Color.BLACK, graph.getBackground(), g);				
//			}
//		}

		//event.consume();
		// Dalej przekazujemy do superklasy
		super.mouseDragged(event);
	}

	/**
	 * Zwraca kod klawisza myszy kt�ry zosta� nacisni�ty.<br>
	 * @param event
	 * @return int
	 */
	protected int getMouseKeyNum(MouseEvent event) {
		
		// Sprawdzamy kt�ry klawisz myszy zosta� nacisni�ty.
		int mouseKey = 0;  
		
		if(SwingUtilities.isRightMouseButton(event)) {
			// prawy
			mouseKey = 3;
		} else if(SwingUtilities.isMiddleMouseButton(event)) {
			// srodkowy
			mouseKey = 2;
		} else if(SwingUtilities.isLeftMouseButton(event)) {
			// lewy
			mouseKey = 1;
		}
		
		return mouseKey;
	}

	/**
	 * Rysujemy tymczasowe po��czenie.
	 * @param fg Foreground
	 * @param bg Background
	 * @param g Graphics
	 */
	protected void paintConnector(Color fg, Color bg, Graphics2D g) {

		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();
		
		// ustawiamy wypelnienie
		g.setColor(fg);
		
		// ustawiamy tryb XOR
		//g.setXORMode(bg);
		
		// Pod�wietlamy aktualny Port (je�li nie jest pusty)
		if(actPort != null) {
			
			// If Not Floating Port...
			boolean o = (GraphConstants.getOffset(actPort.getAllAttributes()) != null);
			// ...Then use Parent's Bounds
			Rectangle2D r = (o) ? actPort.getBounds() : actPort.getParentView().getBounds();
			// Scale from Model to Screen
			r = graph.toScreen((Rectangle2D) r.clone());
			// Add Space For the Highlight Border
			r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);

			// Paint Port in Preview (=Highlight) Mode
			graph.getUI().paintCell(g, actPort, r, true);
		}
		
		// Jesli port zrod�owy, punkt pocz�tkowy i koncowy nie sa puste wtedy
		if(startPoint != null && actPoint != null) {
			
			// Rysujemy lini� z po�o�enia pocz�tkoqwego w miejsce aktualne
			int startX = (int) startPoint.getX();
			int startY = (int) startPoint.getY();
			int actX = (int) actPoint.getX();
			int actY = (int) actPoint.getY();

			g.drawLine(startX, startY ,actX, actY);

			//int startAngle = 0;
			//int arcAngle = ((actY + actX) % 360);
			//g.drawArc(actX - 10, actY - 10, 10, 10, startAngle, arcAngle);
		}
	}
	
	/**
	 * Rysowanie focusu dla zanzaczonej kom�rki nad kt�r� aktualnie znajduje si� mysz.<br>
	 * @param cell - kom�rka 
	 * @param color - kolor focusu
	 */
	protected void paintSelectionFocus(Object cell, Color color) {
		
		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();
		
		if(cell != null) {

			DefaultGraphModel.isGroup(graph.getModel(), cell);
			//if(cell instanceof GroupVertex) {
			//	return;
			//}
				
			CellView cv = graph.getGraphLayoutCache().getMapping(cell, false);
			
			if(cv != null) {

				AttributeMap map = cv.getAllAttributes();
				Rectangle2D bounds = GraphConstants.getBounds(map);
				
				Rectangle2D selbounds = new Rectangle2D.Double();
				selbounds.setFrame(
						bounds.getX() - 5, 
						bounds.getY() - 5, 
						bounds.getWidth() + 11, 
						bounds.getHeight() + 11
				);
				selbounds = graph.toScreen(selbounds);

				/* NOTE:
				 * W tym miejscu nast�puje rysowanie zaznaczenia dla kom�rek z selekcji
				 * na kt�rymi znajduje si� mycha.
				 */

				if(!isSelectionPainted && !isPopupVisible && isSelectionRemoved) {

					Graphics g = graph.getGraphics();

					g.setColor(color);
					g.fillRect(
							(int) selbounds.getX(), 
							(int) selbounds.getY(), 
							(int) selbounds.getWidth(), 
							(int) selbounds.getHeight()
					);

					isSelectionPainted = true;
					isSelectionRemoved = false;

				}
			}
		}
	}

	protected void updateCellSelection(MouseEvent event) {
		
		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();
		Point p = event.getPoint();
		Object cell = graph.getFirstCellForLocation(p.getX(), p.getY());

		if(cell != null) {

			CellView cv = graph.getGraphLayoutCache().getMapping(cell, false);

			if(cv != null) {

				/* NOTE:
				 * Sprawdzamy czy punkt p znajduje si� w boundsach danej kom�rki jednak
				 * z dodatkowym odstepem (marginesem wynosz�cym 3px) potrzebnym do dzia�ania
				 * rozszerzajek, kt�re te� dzia�aja na zasadzie lokacji punktu - ale na 
				 * kraw�dziach poszerzaj�cych. 
				 */
				
				AttributeMap map = cv.getAllAttributes();
				Rectangle2D bounds = GraphConstants.getBounds(map);
				
				if(bounds != null) {
					
					Rectangle2D mb = new Rectangle2D.Double();

					float resizerTolerance = 3;		//px

					mb.setFrame(
							bounds.getX() + resizerTolerance, 
							bounds.getY() + resizerTolerance, 
							bounds.getWidth() - 2 * resizerTolerance, 
							bounds.getHeight() - 2 * resizerTolerance
					);

					boolean isPointInside = mb.contains(p);

					Rectangle2D cb = new Rectangle2D.Double();
					cb.setFrame(
							bounds.getX() - 5, 
							bounds.getY() - 5, 
							bounds.getWidth() + 11, 
							bounds.getHeight() + 11
					);
					cb = graph.toScreen(cb);

					if(graph.isCellSelected(cell) && isPointInside) {

						graph.setCursor(new Cursor(Cursor.MOVE_CURSOR));
						//paintSelectionFocus(cell, selectionColor);
						event.consume();

					} else {
						if(!isSelectionRemoved) {
							graph.repaint(graph.getVisibleRect());
							isSelectionPainted = false;
							isSelectionRemoved = true;
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Update kursora myszy.<br>
	 * @param event
	 */
	protected void updateMouseCursor(MouseEvent event) {

		if(event == null) {
			return;
		}
		
		ApplicationContext ctx = ApplicationContext.getInstance();
		OWGraph graph = ctx.getCurrentGraph();
		
		/* NOTE:
		 * A tutaj sprawdzamy czy mysz nie jest przypadkiem nad kt�rym� z port�w - jesli 
		 * tak to nalezy zmienic jej kursor na �apk� - czyli mozliwo�� przeniesienia 
		 * po�aczenia do innego portu. Bardzo wa�nym jest aby po ka�dorazowej zmianie 
		 * kursora event zosta� skonsumowany.
		 */
		
//		boolean isMouseUnderPort = getSourcePortAt(event.getPoint()) != null;
//		boolean isPortsVisible = graph.isPortsVisible();
//		boolean isToolLinker = ctx.getCurrentTool() == NNDProcessTool.TOOL_LINKER; 
		
//		if(isMouseUnderPort && isPortsVisible && isToolLinker) {
//			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
//			event.consume();
//		}
		
		boolean isMouseUnderCel = graph.getFirstCellForLocation(event.getPoint().x, event.getPoint().y) != null;
		
		if(!isMouseUnderCel) {
			graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			event.consume();
		}
	}

	/** 
	 * Anulowano PopupMenu.<br>
	 * @param event
	 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuCanceled(PopupMenuEvent event) {}

	/** 
	 * PopupMenu sta�o si� niewidoczne. W tym miejscu ustawia isPopupVisible na false,
	 * mo�e odbywa� si� rysowanie na p�aszczy�nie grafu.<br>
	 * @param event
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
		isPopupVisible = false;
	}
	
	/** 
	 * PopupMenu sta�o si� widoczne. W tym miejscu ustawia isPopupVisible na true, i NIE
	 * mo�e odbywa� si� rysowanie na p�aszczy�nie grafu.<br>
	 * @param event
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
		isPopupVisible = true;
	}
	
	/** 
	 * Wykonuje zaznaczenie kom�rek.<br>
	 * @param e - zdarzenie wyzwalaj�ce
	 * @param graph - graph w kt�rym zaznaczenie nast�puje
	 * @param bounds - boundsy zaznaczenia
	 * @see org.jgraph.graph.BasicMarqueeHandler#handleMarqueeEvent(java.awt.event.MouseEvent, org.jgraph.JGraph, java.awt.geom.Rectangle2D)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handleMarqueeEvent(MouseEvent e, JGraph graph, Rectangle2D bounds) {
		
		CellView[] views = graph.getGraphLayoutCache().getRoots(bounds);
		ArrayList list = new ArrayList();
		
		for(int i = 0; i < views.length; i++) {
			if(bounds.contains(views[i].getBounds())) {
				list.add(views[i].getCell());
			}
		}
		
		if(list.size() > 0) {
			Object[] cells = list.toArray();
			graph.getUI().selectCellsForEvent(graph, cells, e);
		}
	}
}
