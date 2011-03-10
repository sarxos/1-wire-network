package com.sarxos.ow.client.graph;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathException;
import sun.dc.pr.PathDasher;
import sun.dc.pr.PathStroker;
import sun.dc.pr.Rasterizer;

/**
 * Domyœlny styl wyrysowania linii zaznaczenia komórek.<br>
 * @author Bartosz Firyn (SarXos)
 * @version 0.1 2007-01-19
 */
public class SXSelectionStroke implements Stroke, Cloneable {

	/**
	 * £¹czy segmenty poprzez rozszerzenie ich zewnetrznych czêœci zanim siê spotkaj¹.<br>
	 */
	public final static int JOIN_MITER = 0;

	/**
	 * £¹czy segmenty poprzez zaokr¹glenie ich zewnetrznych czêœci do promienia równego
	 * jednej drugiej gruboœci linii.<br> 
	 */
	public final static int JOIN_ROUND = 1;

	/**
	 * £¹czy segmenty poprzez po³¹czenie ich zewnetrznych czêœci liniami prostymi.<br>
	 */
	public final static int JOIN_BEVEL = 2;

	/**
	 * Zakoñczenie bez dekoracji.<br>
	 */
	public final static int CAP_BUTT = 0;

	/**
	 * Zakoñczenie zaokr¹glone.<br>
	 */
	public final static int CAP_ROUND = 1;

	/**
	 * Zakoñczenie kwadratowe.<br>
	 */
	public final static int CAP_SQUARE = 2;

	/**
	 * Gruboœæ linii.<br>
	 */
	float width;
	/**
	 * Styl po³¹czenia segmentów.<br>
	 */
	int join;
	/**
	 * Styl zakoñczenia segmentów.<br>
	 */
	int cap;
	/**
	 * Limit.<br>
	 */
	float miterlimit;
	/**
	 * Styl wyrysowania.<br>
	 */
	float dash[];
	/**
	 * Przesuniêcie pocz¹tku wyrysowania.<br>
	 */
	float dash_phase;

	/**
	 * Wyrysowanie.<br>
	 * @param width - gruboœæ linii
	 * @param cap - styl zakoñczeñ
	 * @param join - styl po³¹czeñ
	 * @param limit - limit wyrysowania
	 * @param dash - styl wyrysowania
	 * @param dash_phase - przesuniêcie pocz¹tkowe wyrysowania
	 */
	public SXSelectionStroke(float width, int cap, int join, float limit, float dash[], float dash_phase) {
		setLineWidth(width);
		setEndCap(cap);
		setMiterLimit(limit);
		setLineJoin(join);
		setDashArray(dash);
		setDashPhase(dash_phase);
	}

	/**
	 * Wyrysowanie.<br>
	 * @param width
	 * @param cap
	 * @param join
	 * @param miterlimit
	 */
	public SXSelectionStroke(float width, int cap, int join, float miterlimit) {
		this(width, cap, join, miterlimit, null, 0.0f);
	}

	/**
	 * Wyrysowanie.<br>
	 * @param width
	 * @param cap
	 * @param join
	 */
	public SXSelectionStroke(float width, int cap, int join) {
		this(width, cap, join, 10.0f, null, 0.0f);
	}

	/**
	 * Wyrysowanie.<br>
	 * @param width
	 */
	public SXSelectionStroke(float width) {
		this(width, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
	}

	/**
	 * Wyrysowanie.<br>
	 */
	public SXSelectionStroke() {
		this(1.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
	}


	/** 
	 * Zwraca kszta³t Shape narysowany poprzez to wyrysowanie.<br>
	 * @param shape - kszta³t do wyrysowania
	 * @return Shape
	 * @see java.awt.Stroke#createStrokedShape(java.awt.Shape)
	 */
	public Shape createStrokedShape(Shape shape) {
		
		FillAdapter filler = new FillAdapter();
		PathStroker stroker = new PathStroker(filler);
		PathDasher dasher = null;

		try {
			PathConsumer consumer;

			stroker.setPenDiameter(width);
			stroker.setPenT4(null);
			stroker.setCaps(RasterizerCaps[cap]);
			stroker.setCorners(RasterizerCorners[join], miterlimit);
			if (dash != null) {
				dasher = new PathDasher(stroker);
				dasher.setDash(dash, dash_phase);
				dasher.setDashT4(null);
				consumer = dasher;
			} else {
				consumer = stroker;
			}

			feedConsumer(consumer, shape.getPathIterator(null));
		} finally {
			stroker.dispose();
			if (dasher != null) {
				dasher.dispose();
			}
		}

		return filler.getShape();
	}

	private void feedConsumer(PathConsumer consumer, PathIterator pi) {
		
		try {
			
			consumer.beginPath();
			
			boolean pathClosed = false;
			float mx = 0.0f;
			float my = 0.0f;
			float point[]  = new float[6];

			while(!pi.isDone()) {
				
				int type = pi.currentSegment(point);
				if (pathClosed == true) {
					pathClosed = false;
					if (type != PathIterator.SEG_MOVETO) {
						// Force current point back to last moveto point
						consumer.beginSubpath(mx, my);
					}
				}
				
				switch(type) {
				
					case PathIterator.SEG_MOVETO:
						mx = point[0];
						my = point[1];
						consumer.beginSubpath(point[0], point[1]);
						break;
						
					case PathIterator.SEG_LINETO:
						consumer.appendLine(point[0], point[1]);
						break;
						
					case PathIterator.SEG_QUADTO:
						// Quadratic curves take two points
						consumer.appendQuadratic(point[0], point[1],
								point[2], point[3]);
						break;
						
					case PathIterator.SEG_CUBICTO:
						// Cubic curves take three points
						consumer.appendCubic(point[0], point[1],
								point[2], point[3],
								point[4], point[5]);
						break;
						
					case PathIterator.SEG_CLOSE:
						consumer.closedSubpath();
						pathClosed = true;
						break;
				}
				
				pi.next();
			}

			consumer.endPath();
			
		} catch(PathException e) {
			throw new InternalError("Unable to Stroke shape ("+
					e.getMessage()+")");
		}
	}

	/**
	 * Ustawia szerokoœæ wyrysowywanej linii.<br>
	 * @param width
	 */
	public void setLineWidth(float width) {
		if (width < 0.0f) {
			throw new IllegalArgumentException("Gruboœæ linii nie mo¿e byæ ujemna (" + width + ")");
		}
		this.width	= width;
	}
	
	/**
	 * Zwraca gruboœæ linii wyrysowania.<br>
	 * @return float
	 */
	public float getLineWidth() {
		return width;
	}

	/**
	 * Ustawia styl zakoñczenia.<br>
	 * @param cap
	 */
	public void setEndCap(int cap) {
		if (cap != CAP_BUTT && cap != CAP_ROUND && cap != CAP_SQUARE) {
			throw new IllegalArgumentException("Z³a wartoœæ stylu zakoñczenia");
		}
		this.cap	= cap;
	}
	
	/**
	 * Zwraca styl zakoñczenia.<br>
	 * @return int
	 */
	public int getEndCap() {
		return cap;
	}

	/**
	 * Ustawia styl ³¹czenia segmentów.<br>
	 * @param join 
	 */
	public void setLineJoin(int join) {
		if (join == JOIN_MITER) {
			if (this.miterlimit < 1.0f) {
				throw new IllegalArgumentException("Miter limit < 1 - musisz najpierw ustawiæ wiêkszy miter");
			}
		} else if (join != JOIN_ROUND && join != JOIN_BEVEL) {
			throw new IllegalArgumentException("Niew³aœciwy styl po³¹czenia");
		}
		this.join	= join;
	}
	
	/**
	 * Zwraca styl po³¹zcenia.<br>
	 * @return int
	 */
	public int getLineJoin() {
		return join;
	}

	/**
	 * Ustawia limit wyrysowania.<br>
	 * @param miterlimit
	 */
	public void setMiterLimit(float miterlimit) {
		this.miterlimit = miterlimit;
	}
	
	/**
	 * Zwraca limit po³¹czeñ.<br>
	 * @return float
	 */
	public float getMiterLimit() {
		return miterlimit;
	}
	
	/**
	 * Ustawia styl wyrysowania.<br>
	 * @param dash
	 */
	public void setDashArray(float[] dash) {
		if (dash != null) {
			if (dash_phase < 0.0f) {
				throw new IllegalArgumentException("negative dash phase");
			}
			boolean allzero = true;
			for (int i = 0; i < dash.length; i++) {
				float d = dash[i];
				if (d > 0.0) {
					allzero = false;
				} else if (d < 0.0) {
					throw new IllegalArgumentException("negative dash length");
				}
			}
			if (allzero) {
				throw new IllegalArgumentException("dash lengths all zero");
			}
		}
		if (dash != null) {
			this.dash = (float []) dash.clone();
		}
	}

	/**
	 * Zwraca styl wyrysowania.<br>
	 * @return float[]
	 */
	public float[] getDashArray() {
		if (dash == null) {
			return null;
		}
		return (float[]) dash.clone();
	}

	/**
	 * Ustawia przesuniêcie wyrysowania pocz¹tku wzorca.<br>
	 * @param phase
	 */
	public void setDashPhase(float phase) {
		this.dash_phase	= phase;
	}
	
	/**
	 * Zwraca przesuniêcie wyrysowania.<br>
	 * @return float 
	 */
	public float getDashPhase() {
		return dash_phase;
	}

	/**
	 * Returns the hashcode for this stroke.
	 * @return      a hash code for this stroke.
	 */
	public int hashCode() {
		int hash = Float.floatToIntBits(width);
		hash = hash * 31 + join;
		hash = hash * 31 + cap;
		hash = hash * 31 + Float.floatToIntBits(miterlimit);
		if (dash != null) {
			hash = hash * 31 + Float.floatToIntBits(dash_phase);
			for (int i = 0; i < dash.length; i++) {
				hash = hash * 31 + Float.floatToIntBits(dash[i]);
			}
		}
		return hash;
	}

	/**
	 * Returns true if this BasicStroke represents the same
	 * stroking operation as the given argument.
	 */
	/**
	 * Tests if a specified object is equal to this <code>BasicStroke</code>
	 * by first testing if it is a <code>BasicStroke</code> and then comparing 
	 * its width, join, cap, miter limit, dash, and dash phase attributes with 
	 * those of this <code>BasicStroke</code>.
	 * @param  obj the specified object to compare to this 
	 *              <code>BasicStroke</code>
	 * @return <code>true</code> if the width, join, cap, miter limit, dash, and
	 *            dash phase are the same for both objects;
	 *            <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof SXSelectionStroke)) {
			return false;
		}

		SXSelectionStroke bs = (SXSelectionStroke) obj;
		if (width != bs.width) {
			return false;
		}

		if (join != bs.join) {
			return false;
		}

		if (cap != bs.cap) {
			return false;
		}

		if (miterlimit != bs.miterlimit) {
			return false;
		}

		if (dash != null) {
			if (dash_phase != bs.dash_phase) {
				return false;
			}

			if (!java.util.Arrays.equals(dash, bs.dash)) {
				return false;
			}
		}
		else if (bs.dash != null) {
			return false;
		}

		return true;
	}

	private static final int RasterizerCaps[] = {
		Rasterizer.BUTT, Rasterizer.ROUND, Rasterizer.SQUARE
	};

	private static final int RasterizerCorners[] = {
		Rasterizer.MITER, Rasterizer.ROUND, Rasterizer.BEVEL
	};

	/** 
	 * Klonuje obiekt.<br>
	 * @return {@link SXSelectionStroke}
	 * @throws CloneNotSupportedException
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SXSelectionStroke clone() {
		try {
			return (SXSelectionStroke) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Adapter wype³niania œcie¿ki wyrysowania.<br>
	 * @author Bartosz (SarXos) Firyn
	 */
	private static class FillAdapter implements PathConsumer {
		
		boolean closed;
		GeneralPath path;

		/**
		 * Adapter wype³niania œcie¿ki wyrysowania.<br>
		 */
		public FillAdapter() {
			path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
		}

		/**
		 * Zwraca kszta³t wyrysowani.<br>
		 * @return Shape
		 */
		public Shape getShape() {
			return path;
		}

		/** 
		 * Zamyka adapter.<br>
		 * @see sun.dc.path.PathConsumer#dispose()
		 */
		public void dispose() {
		}

		/** 
		 * Zwraca obiekt konsumuj¹cy (null w tym wypadku).<br>
		 * @return null
		 * @see sun.dc.path.PathConsumer#getConsumer()
		 */
		public PathConsumer getConsumer() {
			return null;
		}

		/** 
		 * Pocz¹tek œciezki.<br>
		 * @see sun.dc.path.PathConsumer#beginPath()
		 */
		public void beginPath() {}

		/** 
		 * Pocz¹tek podœcie¿ki.<br>
		 * @param x0
		 * @param y0
		 * @see sun.dc.path.PathConsumer#beginSubpath(float, float)
		 */
		public void beginSubpath(float x0, float y0) {
			if (closed) {
				path.closePath();
				closed = false;
			}
			path.moveTo(x0, y0);
		}

		/** 
		 * Dodaj liniê do œcie¿ki.<br>
		 * @param x1
		 * @param y1
		 * @see sun.dc.path.PathConsumer#appendLine(float, float)
		 */
		public void appendLine(float x1, float y1) {
			path.lineTo(x1, y1);
		}

		/** 
		 * Dodaj krzyw¹ kwadratow¹ do œciezki.<br>
		 * @param xm
		 * @param ym
		 * @param x1
		 * @param y1
		 * @see sun.dc.path.PathConsumer#appendQuadratic(float, float, float, float)
		 */
		public void appendQuadratic(float xm, float ym, float x1, float y1) {
			path.quadTo(xm, ym, x1, y1);
		}

		/** 
		 * Dodaj krzyw¹ kubiczn¹ do œciezki.<br>
		 * @param xm
		 * @param ym
		 * @param xn
		 * @param yn
		 * @param x1
		 * @param y1
		 * @see sun.dc.path.PathConsumer#appendCubic(float, float, float, float, float, float)
		 */
		public void appendCubic(float xm, float ym,
				float xn, float yn,
				float x1, float y1) {
			path.curveTo(xm, ym, xn, yn, x1, y1);
		}

		/** 
		 * Podœcie¿ki zamkniête.<br>
		 * @see sun.dc.path.PathConsumer#closedSubpath()
		 */
		public void closedSubpath() {
			closed = true;
		}

		/** 
		 * Zakoñcz œciezkê.<br>
		 * @see sun.dc.path.PathConsumer#endPath()
		 */
		public void endPath() {
			if (closed) {
				path.closePath();
				closed = false;
			}
		}

		/** 
		 * U¿yj proxy dla tworzenia œcie¿ek.<br>
		 * @param proxy
		 * @throws PathException
		 * @see sun.dc.path.PathConsumer#useProxy(sun.dc.path.FastPathProducer)
		 */
		public void useProxy(FastPathProducer proxy) throws PathException {
			proxy.sendTo(this);
		}

		/** 
		 * @return 0
		 * @see sun.dc.path.PathConsumer#getCPathConsumer()
		 */
		public long getCPathConsumer() {
			return 0;
		}
	}
}
