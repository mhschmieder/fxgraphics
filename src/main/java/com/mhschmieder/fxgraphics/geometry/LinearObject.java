/*
 * MIT License
 *
 * Copyright (c) 2020, 2026 Mark Schmieder. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the fxgraphics Library
 *
 * You should have received a copy of the MIT License along with the fxgraphics
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgraphics
 */
package com.mhschmieder.fxgraphics.geometry;

import com.mhschmieder.fxgraphics.layers.Layer;
import com.mhschmieder.fxgraphics.shape.ShapeGroup;
import com.mhschmieder.fxgraphics.shape.ShapeUtilities;
import com.mhschmieder.jcommons.lang.LabeledObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>LinearObject</code> class is the abstract base class for all linear
 * objects, which are essentially enhanced 2D Lines that can act as Projectors,
 * with concrete derived classes for Cartesian or Polar Coordinates. Note that
 * curvilinear edges may end up falling within the current abstraction as well.
 * <p>
 * At this top level of the Linear Object sub-hierarchy, vector math is not yet
 * involved but simple point containment tests (especially from mouse clicks) are
 * critical, so the coordinates are expressed using immutable JavaFX Point2D and
 * Point3D instances.
 */
public abstract class LinearObject extends GraphicalObject
        implements LabeledObject {

    public static final String  LINEAR_OBJECT_LABEL_DEFAULT        = "Linear Object";
    public static final boolean USE_AS_PROJECTOR_DEFAULT           = false;
    public static final int     NUMBER_OF_PROJECTION_ZONES_DEFAULT = 1;

    private String              _label;
    private boolean             _useAsProjector;
    private int                 _numberOfProjectionZones;

    public LinearObject() {
        this( LINEAR_OBJECT_LABEL_DEFAULT );
    }

    public LinearObject( final String label ) {
        this( label, USE_AS_PROJECTOR_DEFAULT, NUMBER_OF_PROJECTION_ZONES_DEFAULT );
    }

    public LinearObject( final String label,
                         final boolean pUseAsProjector,
                         final int pNumberOfProjectionZones ) {
        super();

        _label = label;
        _useAsProjector = pUseAsProjector;
        _numberOfProjectionZones = pNumberOfProjectionZones;
    }

    public LinearObject( final LinearObject linearObject ) {
        this( linearObject.getLabel(),
              linearObject.isUseAsProjector(),
              linearObject.getNumberOfProjectionZones() );
    }

    /**
     * Constructs the lines representing this graphical object.
     *
     * @param lines
     *            A pre-constructed collection to hold the lines
     */
    public final void constructLines( final List< Line > lines ) {
        // Draw the main graphic for the Linear Object, which is a simple Line.
        final Line line = getLine();
        ShapeUtilities.drawLine( lines,
                                 line.getStartX(),
                                 line.getStartY(),
                                 line.getEndX(),
                                 line.getEndY() );

        // Conditionally add the Projector cues for the Projection Zones.
        if ( isUseAsProjector() ) {
            // First, draw the baseline below the Linear Object's minimum y
            // point.
            final double baselineY = FastMath.min( line.getStartY(), line.getEndY() );
            ShapeUtilities
                    .drawLine( lines, line.getStartX(), baselineY, line.getEndX(), baselineY );

            // Now, draw as many additional drop-lines as there are Projection
            // Zones.
            final double xDiff = line.getEndX() - line.getStartX();
            final double yDiff = line.getEndY() - line.getStartY();
            final double xDelta = xDiff / _numberOfProjectionZones;
            final double yDelta = yDiff / _numberOfProjectionZones;
            for ( int i = 0; i <= _numberOfProjectionZones; i++ ) {
                final double baselineX = line.getStartX() + ( xDelta * i );
                final double slopelineY = line.getStartY() + ( yDelta * i );
                ShapeUtilities.drawLine( lines, baselineX, baselineY, baselineX, slopelineY );
            }
        }
    }

    /**
     * Constructs the path elements representing this graphical object, by first
     * constructing the Line Shapes and then converting those to MoveTo/LineTo
     * PathElement pairs.
     * <p>
     * TODO: Make more efficient by avoiding redundant MoveTo's.
     *
     * @param pathElements
     *            A pre-constructed collection to hold the path elements
     */
    public final void constructPathElements( final ObservableList< PathElement > pathElements ) {
        // Construct the lines for the Linear Object.
        final List< Line > lines = new ArrayList<>();
        constructLines( lines );

        // Convert the Line Shapes to MoveTo/LineTo PathElement pairs.
        for ( final Line line : lines ) {
            // Convert the Line as a simple MoveTo/LineTo pair.
            pathElements.add( new MoveTo( line.getStartX(), line.getStartY() ) );
            pathElements.add( new LineTo( line.getEndX(), line.getEndY() ) );
        }
    }

    @Override
    public final boolean contains( final Point2D clickPoint,
                                   final Bounds contextBounds,
                                   final boolean allowTightFitContainment ) {
        // Look for small context bounds, and use simple line segment distance
        // calculations, as bounding box based containment is very coarse due to
        // object rotation, odd shapes, and other factors.
        // NOTE: We have set this criteria to 10 meters, but might need to make
        //  this more flexible or even user-defined and thus passed into this
        //  method as a tolerance.
        final double contextWidth = contextBounds.getWidth();
        final double contextHeight = contextBounds.getHeight();
        final double contextLengthMinimum = FastMath.min( contextWidth, contextHeight );

        // NOTE: We conditionally use a fudge factor (for small context bounds)
        //  of 0.2 meters (roughly 2% minimum context bounds; whereas we used to
        //  use 0.5% maximum context bounds across the board).
        // final double fudgeFactor = ( contextLengthMinimum <= 10.0d ) ? 0.0d :
        // 0.2d;

        // NOTE: We ensure a bounding rectangle of at least 1.5% of the minimum
        // dimension, for "easy picking" of extraordinarily small lines.
        final double clickDiameter = 0.015d * contextLengthMinimum;
        final double clickRadius = ( 0.5d * clickDiameter ); // + fudgeFactor;

        // Construct the lines for the Linear Object.
        final List< Line > lines = new ArrayList<>();
        constructLines( lines );

        // Find out if the click point is within tolerance of any of the
        // representational lines.
        for ( final Line line : lines ) {
            final double pointToLineDistance = GeometryUtilities.ptSegDist( line, clickPoint );
            if ( pointToLineDistance <= clickRadius ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof LinearObject ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        //  members, so that derived classes produce the correct results when
        //  comparing two objects.
        final LinearObject other = ( LinearObject ) obj;
        if ( !super.equals( obj ) 
                || !getLabel().equals( other.getLabel() ) 
                || ( isUseAsProjector() != other.isUseAsProjector() ) ) {
            return false;
        }
        return ( getNumberOfProjectionZones() == other.getNumberOfProjectionZones() );
    }

    @Override
    public Bounds getBoundingBox() {
        final Line line = getLine();
        final Bounds bounds = line.getBoundsInLocal();
        return bounds;
    }

    @Override
    public final String getLabel() {
        return _label;
    }

    /*
     * Each sub-class must determine what to return as its basic line.
     */
    public abstract Line getLine();

    public final int getNumberOfProjectionZones() {
        return _numberOfProjectionZones;
    }

    public final Point2D getP1() {
        final Line line = getLine();
        final Point2D p1 = new Point2D( line.getStartX(), line.getStartY() );
        return p1;
    }

    public final Point2D getP2() {
        final Line line = getLine();
        final Point2D p2 = new Point2D( line.getEndX(), line.getEndY() );
        return p2;
    }

    /**
     * Constructs anew, a node representing this graphical object.
     *
     * @param previewContext
     *            Flag for whether this is used in a preview context
     * @return a graphical node representing this Linear Object
     */
    @Override
    public ShapeGroup getVectorGraphics( final boolean previewContext ) {
        // We need to use a Path with multiple visual elements so that we have a
        // common object for all cases, including Projectors.
        final Path path = new Path();
        final ObservableList< PathElement > pathElements = path.getElements();

        // Construct the path elements for the Linear Object.
        constructPathElements( pathElements );

        // NOTE: Unless we set Inside Stroke Type, the computed bounds seem
        //  overly large, which results in graphic previews being too small.
        // NOTE: Unlike with Microphones etc., this makes it invisible, so we
        //  have to apply Outside or Centered instead -- due to no closure?
        path.setStrokeType( previewContext ? StrokeType.OUTSIDE : StrokeType.CENTERED );

        // Butt end caps improve perceived regularity of the highlight dash
        // pattern and also make it less likely that an empty gap will be the
        // final mark for a graphic and thus cause confusion over its extrusion.
        path.setStrokeLineCap( StrokeLineCap.BUTT );

        // Treat the bounds-picking criteria separately for the main graphics,
        // so that it is easier to pick than using shape outline.
        path.setMouseTransparent( false );
        path.setPickOnBounds( true );

        final ShapeGroup vectorGraphics = new ShapeGroup();
        vectorGraphics.addShape( path );

        return vectorGraphics;
    }

    public final double getX1() {
        final Line line = getLine();
        return line.getStartX();
    }

    public final double getX2() {
        final Line line = getLine();
        return line.getEndX();
    }

    public final double getY1() {
        final Line line = getLine();
        return line.getStartY();
    }

    public final double getY2() {
        final Line line = getLine();
        return line.getEndY();
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }

    /**
     * 2D intersection test
     *
     * @see GeometryUtilities#intersects(Bounds, Line)
     */
    @Override
    public final boolean intersects( final Bounds dragBoxInModelCoordinates ) {
        final Line line = getLine();
        return GeometryUtilities.intersects( dragBoxInModelCoordinates, line );
    }

    /**
     * 2D intersection test
     *
     * @see GeometryUtilities#intersects(Rectangle2D, Line)
     */
    @Override
    public final boolean intersects( final Rectangle2D dragBoxInModelCoordinates ) {
        final Line line = getLine();
        return GeometryUtilities.intersects( dragBoxInModelCoordinates, line );
    }

    @Override
    public boolean isCloserThan( final GraphicalObject other, final Point2D clickPoint ) {
        // If the other Graphical Object is null, then this one is closer.
        if ( other == null ) {
            return true;
        }

        // Get the current Line Object's and the other Line Object's Lines, so
        // that we can properly detect which Line Object is closest to the point
        // of mouse click.
        final Line thisLine = getLine();
        final Line otherLine = ( ( LinearObject ) other ).getLine();

        final boolean thisObjectIsCloserThanOtherObjectToClickPoint = ( GeometryUtilities
                .ptSegDist( thisLine,
                            clickPoint ) < GeometryUtilities.ptSegDist( otherLine, clickPoint ) );

        return thisObjectIsCloserThanOtherObjectToClickPoint;
    }

    /**
     * Given a proposed delta offset for each dimension, calculate the resulting
     * end points of this Line Object and make sure that they would not end up
     * being outside the supplied bounds.
     * <p>
     * This is done as a simple combined test of "too far left", "too far up",
     * "too far right", and "too far down", based on both end points. Some
     * subclasses may need to override this with more specialized criteria.
     *
     * @param deltaX
     *            The offset along the x-axis of the proposed new location
     * @param deltaY
     *            The offset along the y-axis of the proposed new location
     * @param bounds
     *            The bounds that must contain the proposed new location
     * @return Whether or not the proposed new location falls within the
     *         supplied bounds
     */
    @Override
    public boolean isDragTargetWithinBounds( final double deltaX,
                                             final double deltaY,
                                             final Bounds bounds ) {
        final double currentX1 = getX1();
        final double currentY1 = getY1();
        final double targetX1 = currentX1 + deltaX;
        final double targetY1 = currentY1 + deltaY;
        final boolean tooFarLeft1 = targetX1 < bounds.getMinX();
        final boolean tooFarUp1 = targetY1 < bounds.getMinY();
        final boolean tooFarRight1 = targetX1 > bounds.getMaxX();
        final boolean tooFarDown1 = targetY1 > bounds.getMaxY();
        final boolean dragTargetWithinBounds1 = !tooFarLeft1 && !tooFarUp1 && !tooFarRight1
                && !tooFarDown1;

        final double currentX2 = getX2();
        final double currentY2 = getY2();
        final double targetX2 = currentX2 + deltaX;
        final double targetY2 = currentY2 + deltaY;
        final boolean tooFarLeft2 = targetX2 < bounds.getMinX();
        final boolean tooFarUp2 = targetY2 < bounds.getMinY();
        final boolean tooFarRight2 = targetX2 > bounds.getMaxX();
        final boolean tooFarDown2 = targetY2 > bounds.getMaxY();
        final boolean dragTargetWithinBounds2 = !tooFarLeft2 && !tooFarUp2 && !tooFarRight2
                && !tooFarDown2;

        final boolean dragTargetWithinBounds = dragTargetWithinBounds1 && dragTargetWithinBounds2;

        return dragTargetWithinBounds;
    }

    public final boolean isUseAsProjector() {
        return _useAsProjector;
    }

    @Override
    public void rotate( final double rotateX,
                        final double rotateY,
                        final double rotateThetaRelativeDegrees,
                        final double dx,
                        final double dy,
                        final double cosTheta,
                        final double sinTheta ) {
        // Apply the rotation angle to the Line end points of all the selected
        // Line Objects that are editable, using the formulae:
        //
        // qx = ( px * cos( theta ) ) - ( py * sin( theta ) ) + dx
        // qy = ( px * sin( theta ) ) + ( py * cos( theta ) ) + dy
        //
        // where ( dx, dy ) is the translation vector, ( px, py ) is the GC of
        // the current Line Object, and ( qx, qy ) is the transformed GC of the
        // current Line Object.
        final Line line = getLine();

        final double x1 = line.getStartX();
        final double y1 = line.getStartY();
        final double x2 = line.getEndX();
        final double y2 = line.getEndY();

        final double qx1 = ( ( x1 * cosTheta ) - ( y1 * sinTheta ) ) + dx;
        final double qy1 = ( x1 * sinTheta ) + ( y1 * cosTheta ) + dy;
        final double qx2 = ( ( x2 * cosTheta ) - ( y2 * sinTheta ) ) + dx;
        final double qy2 = ( x2 * sinTheta ) + ( y2 * cosTheta ) + dy;

        setLine( qx1, qy1, qx2, qy2 );

        // Rotate the associated Scene Graph Nodes by the specified amount.
        rotateNode( rotateX, rotateY, rotateThetaRelativeDegrees );
    }

    // NOTE: This was an early attempt to export to DXF. It may get revived.
    // public final void saveToDxf( final DXFExport dxfExport ) {
    // final int x1 = ( int ) FastMath.round( getX1() );
    // final int y1 = ( int ) FastMath.round( getY1() );
    //
    // final int x2 = ( int ) FastMath.round( getX2() );
    // final int y2 = ( int ) FastMath.round( getY2() );
    //
    // DXFLayer dxfLayer = new DXFLayer( "Line" );
    // dxfExport.setCurrentLayer( dxfLayer );
    // DXFData dxfData = new DXFData();
    // dxfData.LayerName = dxfLayer.getName();
    // dxfData.Color = ( Constants.convertColorRGBToDXF( java.awt.Color.BLACK )
    // );
    // dxfData.Point = new DXFPoint( x1, -y1, 0 );
    // dxfData.Point1 = new DXFPoint( x2, -y2, 0 );
    // dxfExport.addLine( dxfData );
    //
    // // NOTE: This is just a text annotation label above the actual line,
    // //  useful for demo purposes but not in real DXF graphics output.
    // // dxfData.Point.setTo( x1, -( y2 - 20 ), 0 );
    // // dxfData.FHeight = 10;
    // // dxfData.Text = new String( "Line" );
    // // dxfExport.addText( dxfData );
    //
    // dxfData = null;
    // }

    @Override
    public final void setLabel( final String pLabel ) {
        _label = pLabel;
    }

    /*
     * Each sub-class must determine how to set its basic line.
     */
    public abstract void setLine( final double x1,
                                  final double y1,
                                  final double x2,
                                  final double y2 );

    public final void setNumberOfProjectionZones( final int pNumberOfProjectionZones ) {
        _numberOfProjectionZones = pNumberOfProjectionZones;
    }

    public final void setUseAsProjector( final boolean pUseAsProjector ) {
        _useAsProjector = pUseAsProjector;
    }

    protected final void setLineObject( final String pLineObjectLabel,
                                        final Layer pLayer,
                                        final boolean pUseAsProjector,
                                        final int pNumberOfProjectionZones ) {
        setLabel( pLineObjectLabel );
        setLayer( pLayer );
        setUseAsProjector( pUseAsProjector );
        setNumberOfProjectionZones( pNumberOfProjectionZones );
    }
}
