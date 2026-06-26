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
 * This file is part of the FxCadGraphics Library
 *
 * You should have received a copy of the MIT License along with the
 * FxCadGraphics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgraphics
 */
package com.mhschmieder.fxgraphics.geometry;

import com.mhschmieder.fxgraphics.layers.Layer;
import com.mhschmieder.fxgraphics.layers.LayerManager;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import org.apache.commons.math3.util.FastMath;

/**
 * The <code>CartesianLine</code> class is the concrete class for Cartesian
 * Lines. It is really not much more than a line that is selectable.
 * <p>
 * At this top level of the Cartesian Line sub-hierarchy, vector math is not yet
 * involved but simple point containment tests (especially from mouse clicks) are
 * critical, so the coordinates are expressed using immutable JavaFX Point2D and
 * Point3D instances.
 */
public class CartesianLine extends LinearObject {

    // Declare the default Cartesian Line label.
    public static final String    CARTESIAN_LINE_LABEL_DEFAULT = "Cartesian Line"; //$NON-NLS-1$

    // Declare default constants, where appropriate, for all fields.
    protected static final double X1_DEFAULT                   = 0.0d;
    protected static final double Y1_DEFAULT                   = 0.0d;
    protected static final double X2_DEFAULT                   = 1.0d;
    protected static final double Y2_DEFAULT                   = 1.0d;

    public static CartesianLine getDefaultCartesianLine() {
        return new CartesianLine();
    }

    // Declare variables for Cartesian Space coordinates.
    private Line _line = new Line();

    // NOTE: Since this class declares additional fields to the parent class,
    //  we cannot just invoke the super-constructor from each constructor, but
    //  need to invoke incrementally more complex local constructors instead.
    // TODO: Make better use of parent class constructors and setters.
    public CartesianLine() {
        this( X1_DEFAULT,
              Y1_DEFAULT,
              X2_DEFAULT,
              Y2_DEFAULT,
              CARTESIAN_LINE_LABEL_DEFAULT,
              LayerManager.makeDefaultLayer(),
              USE_AS_PROJECTOR_DEFAULT,
              NUMBER_OF_PROJECTION_ZONES_DEFAULT );
    }

    public CartesianLine( final double x1,
                          final double y1,
                          final double x2,
                          final double y2,
                          final String cartesianLineLabel,
                          final Layer layer,
                          final boolean useAsProjector,
                          final int numberOfProjectionZones ) {
        super( cartesianLineLabel, useAsProjector, numberOfProjectionZones );

        setCartesianLine( x1,
                          y1,
                          x2,
                          y2,
                          cartesianLineLabel,
                          layer,
                          useAsProjector,
                          numberOfProjectionZones );
    }

    public CartesianLine( final Line line,
                          final String cartesianLineLabel,
                          final Layer layer ) {
        this( line,
              cartesianLineLabel,
              layer,
              USE_AS_PROJECTOR_DEFAULT,
              NUMBER_OF_PROJECTION_ZONES_DEFAULT );
    }

    public CartesianLine( final Line line,
                          final String cartesianLineLabel,
                          final Layer layer,
                          final boolean useAsProjector,
                          final int numberOfProjectionZones ) {
        this( line.getStartX(),
              line.getStartY(),
              line.getEndX(),
              line.getEndY(),
              cartesianLineLabel,
              layer,
              useAsProjector,
              numberOfProjectionZones );
    }

    public CartesianLine( final Point2D p1,
                          final Point2D p2,
                          final String cartesianLineLabel,
                          final Layer layer ) {
        this( p1,
              p2,
              cartesianLineLabel,
              layer,
              USE_AS_PROJECTOR_DEFAULT,
              NUMBER_OF_PROJECTION_ZONES_DEFAULT );
    }

    public CartesianLine( final Point2D p1,
                          final Point2D p2,
                          final String cartesianLineLabel,
                          final Layer layer,
                          final boolean useAsProjector,
                          final int numberOfProjectionZones ) {
        super();

        setCartesianLine( p1,
                          p2,
                          cartesianLineLabel,
                          layer,
                          useAsProjector,
                          numberOfProjectionZones );
    }

    public CartesianLine( final CartesianLine cartesianLine ) {
        super( cartesianLine );

        setCartesianLine( cartesianLine );
    }

    @Override
    public final void drag( final double deltaX, final double deltaY ) {
        // Compute the new Line End Points for the Cartesian Line by
        // combining the deltas with the original Line End Points.
        // TODO: Embed this logic in an overridden setLocation() method?
        final Line line = getLine();
        final double x1 = line.getStartX();
        final double y1 = line.getStartY();
        final double x2 = line.getEndX();
        final double y2 = line.getEndY();
        setLine( x1 + deltaX, y1 + deltaY, x2 + deltaX, y2 + deltaY );

        // Drag the associated Scene Graph Nodes to the same End Points.
        dragNode( deltaX, deltaY );
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof CartesianLine ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        //  members, so that derived classes produce the correct results when
        //  comparing two objects.
        final CartesianLine other = ( CartesianLine ) obj;
        if ( !super.equals( obj ) ) {
            return false;
        }

        return getLine().equals( other.getLine() );
    }

    @Override
    public final double getAngleDegrees() {
        final double xdiff = _line.getEndX() - _line.getStartX();
        final double ydiff = _line.getEndY() - _line.getStartY();

        // Convert Cartesian coordinates to Polar coordinates.
        // NOTE: The JavaFX Point2D class offers the angle(otherPoint)
        //  method, but it uses acos() instead of atan() and thus isn't likely
        //  as safe or as fast as our home-grown solution (atan() is native).
        final double angle = FastMath.atan2( ydiff, xdiff );

        return FastMath.toDegrees( angle );
    }

    @Override
    public final GraphicalObject getDeepClonedObject() {
        final CartesianLine cartesianLineClone = new CartesianLine( this );

        return cartesianLineClone;
    }

    public final double getDistance() {
        // Convert Cartesian coordinates to Polar coordinates (sqrt).
        final Point2D startPoint = getP1();
        final Point2D endPoint = getP2();
        final double distance = startPoint.distance( endPoint );

        return distance;
    }

    @Override
    public final Line getLine() {
        return _line;
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }

    public final void setCartesianLine( final CartesianLine cartesianLine ) {
        setCartesianLine( cartesianLine.getLine(),
                          cartesianLine.getLabel(),
                          cartesianLine.getLayer(),
                          cartesianLine.isUseAsProjector(),
                          cartesianLine.getNumberOfProjectionZones() );
    }

    public final void setCartesianLine( final double x1,
                                        final double y1,
                                        final double x2,
                                        final double y2,
                                        final String cartesianLineLabel,
                                        final Layer layer,
                                        final boolean useAsProjector,
                                        final int numberOfProjectionZones ) {
        setLine( x1, y1, x2, y2 );
        setLineObject( cartesianLineLabel, layer, useAsProjector, numberOfProjectionZones );
    }

    public final void setCartesianLine( final Line line,
                                        final String cartesianLineLabel,
                                        final Layer layer,
                                        final boolean useAsProjector,
                                        final int numberOfProjectionZones ) {
        setLine( line );
        setLineObject( cartesianLineLabel, layer, useAsProjector, numberOfProjectionZones );
    }

    public final void setCartesianLine( final Point2D p1,
                                        final double angleDegrees,
                                        final double distance,
                                        final String cartesianLineLabel,
                                        final Layer layer,
                                        final boolean useAsProjector,
                                        final int numberOfProjectionZones ) {
        setLine( p1, angleDegrees, distance );
        setLineObject( cartesianLineLabel, layer, useAsProjector, numberOfProjectionZones );
    }

    public final void setCartesianLine( final Point2D p1,
                                        final Point2D p2,
                                        final String cartesianLineLabel,
                                        final Layer layer,
                                        final boolean useAsProjector,
                                        final int numberOfProjectionZones ) {
        setLine( p1, p2 );
        setLineObject(
                cartesianLineLabel,
                layer,
                useAsProjector,
                numberOfProjectionZones );
    }

    @Override
    public final void setLine( final double x1,
                               final double y1,
                               final double x2,
                               final double y2 ) {
        _line = new Line( x1, y1, x2, y2 );

        setLocationX( x1 );
        setLocationY( y1 );
    }

    public final void setLine( final Line line ) {
        setLine( line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY() );
    }

    public final void setLine( final Point2D p1,
                               final double angleDegrees,
                               final double distance ) {
        // Convert Polar coordinates to Cartesian coordinates.
        final double angleRadians = FastMath.toRadians( angleDegrees );
        final double x1 = p1.getX();
        final double y1 = p1.getY();
        final double x2 = x1 + ( distance * FastMath.cos( angleRadians ) );
        final double y2 = y1 + ( distance * FastMath.sin( angleRadians ) );

        setLine( x1, y1, x2, y2 );
    }

    public final void setLine( final Point2D p1, final Point2D p2 ) {
        setLine( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
    }

    public final void setP1( final Point2D p1 ) {
        _line.setStartX( p1.getX() );
        _line.setStartY( p1.getY() );
    }

    public final void setP2( final Point2D p2 ) {
        _line.setEndX( p2.getX() );
        _line.setEndY( p2.getY() );
    }

    @Override
    public void setReferencePoint2D( final double referencePointX, final double referencePointY ) {
        final double deltaX = referencePointX - getX1();
        final double deltaY = referencePointY - getY1();
        drag( deltaX, deltaY );
    }

    @Override
    public void setReferencePoint2D( final Point2D referencePoint ) {
        final double referencePointX = referencePoint.getX();
        final double referencePointY = referencePoint.getY();
        setReferencePoint2D( referencePointX, referencePointY );
    }

    public final void setX1( final double x1 ) {
        _line.setStartX( x1 );
    }

    public final void setX2( final double x2 ) {
        _line.setEndX( x2 );
    }

    public final void setY1( final double y1 ) {
        _line.setStartY( y1 );
    }

    public final void setY2( final double y2 ) {
        _line.setEndY( y2 );
    }
}
