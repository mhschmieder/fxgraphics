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
import com.mhschmieder.fxgraphics.layers.LayerManagement;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import org.apache.commons.math3.util.FastMath;

/**
 * The <code>PolarLine</code> class is the concrete class for Polar Lines.
 * It mostly follows the Wiki definition for Polar Lines as there is an
 * inclinometer involved alongside an initial offset before the line starts.
 * <p>
 * At this top level of the Polar Line sub-hierarchy, vector math is not yet
 * involved but simple point containment tests (especially from mouse clicks) are
 * critical, so the coordinates are expressed using immutable JavaFX Point2D and
 * Point3D instances.
 * <p>
 * TODO: Review whether the inclinometer should be modeled using vector offsets.
 */
public class PolarLine extends LinearObject {

    // Declare the default Polar Line label.
    public static final String POLAR_LINE_LABEL_DEFAULT    = "Polar Line";

    public static final double START_ANGLE_DEGREES_DEFAULT = 0.0d;
    public static final double START_DISTANCE_DEFAULT      = 1.0d;
    public static final double END_ANGLE_DEGREES_DEFAULT   = 45d;
    public static final double END_DISTANCE_DEFAULT        = 1.0d;

    public static final PolarLine getDefaultPolarLine() {
        return new PolarLine();
    }

    // Declare variables for Cartesian Space Inclinometer Position (meters), and
    // Polar Space angle/distance pairs (degrees/meters).
    private Point2D _inclinometerPosition = new Point2D( X_DEFAULT, Y_DEFAULT );
    private double  _startAngleDegrees    = START_ANGLE_DEGREES_DEFAULT;
    private double  _startDistance        = START_DISTANCE_DEFAULT;
    private double  _endAngleDegrees      = END_ANGLE_DEGREES_DEFAULT;
    private double  _endDistance          = END_DISTANCE_DEFAULT;

    // NOTE: Since this class declares additional fields to the parent class,
    //  we cannot just invoke the super-constructor from each constructor, but
    //  need to invoke incrementally more complex local constructors instead.
    // TODO: Make better use of parent class constructors and setters.
    public PolarLine() {
        this( X_DEFAULT,
              Y_DEFAULT,
              START_ANGLE_DEGREES_DEFAULT,
              START_DISTANCE_DEFAULT,
              END_ANGLE_DEGREES_DEFAULT,
              END_DISTANCE_DEFAULT,
              POLAR_LINE_LABEL_DEFAULT,
              LayerManagement.makeDefaultLayer(),
              USE_AS_PROJECTOR_DEFAULT,
              NUMBER_OF_PROJECTION_ZONES_DEFAULT );
    }

    public PolarLine( final double x,
                      final double y,
                      final double startAngleDegrees,
                      final double startDistance,
                      final double endAngleDegrees,
                      final double endDistance,
                      final String polarLineLabel,
                      final Layer layer,
                      final boolean useAsProjector,
                      final int numberOfProjectionZones ) {
        super( polarLineLabel, useAsProjector, numberOfProjectionZones );

        setPolarLine( x,
                      y,
                      startAngleDegrees,
                      startDistance,
                      endAngleDegrees,
                      endDistance,
                      polarLineLabel,
                      layer,
                      useAsProjector,
                      numberOfProjectionZones );
    }

    public PolarLine( final PolarLine polarLine ) {
        super( polarLine );

        setPolarLine( polarLine );
    }

    public PolarLine( final Point2D referencePoint,
                      final double startAngleDegrees,
                      final double startDistance,
                      final double endAngleDegrees,
                      final double endDistance,
                      final String polarLineLabel,
                      final Layer layer,
                      final boolean useAsProjector,
                      final int numberOfProjectionZones ) {
        this( referencePoint.getX(),
              referencePoint.getY(),
              startAngleDegrees,
              startDistance,
              endAngleDegrees,
              endDistance,
              polarLineLabel,
              layer,
              useAsProjector,
              numberOfProjectionZones );
    }

    @Override
    public final void drag( final double deltaX, final double deltaY ) {
        // Compute the new Reference Point for the Polar Line by
        // combining the deltas with the original Reference Point.
        // TODO: Embed this logic in an overridden setLocation() method?
        final Point2D referencePoint = getInclinometerPosition();
        final double x = referencePoint.getX();
        final double y = referencePoint.getY();
        setInclinometerPosition( x + deltaX, y + deltaY );

        // Drag the associated Scene Graph Nodes to the same Reference Point.
        dragNode( deltaX, deltaY );
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof PolarLine ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        //  members, so that derived classes produce the correct results when
        //  comparing two objects.
        final PolarLine other = ( PolarLine ) obj;
        if ( !super.equals( obj ) 
                || !getInclinometerPosition().equals( other.getInclinometerPosition() ) 
                || ( getStartAngleDegrees() != other.getStartAngleDegrees() ) 
                || ( getStartDistance() != other.getStartDistance() ) ) {
            return false;
        }
        if ( getEndAngleDegrees() != other.getEndAngleDegrees() ) {
            return false;
        }

        return getEndDistance() == other.getEndDistance();

    }

    // NOTE: This is a method to determine the angle of incline.
    @Override
    public final double getAngleDegrees() {
        return _endAngleDegrees - _startAngleDegrees;
    }

    @Override
    public final GraphicalObject getDeepClonedObject() {
        final PolarLine polarLineClone = new PolarLine( this );

        return polarLineClone;
    }

    public final double getEndAngleDegrees() {
        return _endAngleDegrees;
    }

    public final double getEndDistance() {
        return _endDistance;
    }

    // Convert Polar coordinates to Cartesian coordinates.
    public final Point2D getEndPoint() {
        return new Point2D( getInclinometerPositionX()
                + ( _endDistance * FastMath.cos( FastMath.toRadians( _endAngleDegrees ) ) ),
                            getInclinometerPositioneY() + ( _endDistance
                                    * FastMath.sin( FastMath.toRadians( _endAngleDegrees ) ) ) );
    }

    public final Point2D getInclinometerPosition() {
        return _inclinometerPosition;
    }

    public final double getInclinometerPositioneY() {
        return _inclinometerPosition.getY();
    }

    public final double getInclinometerPositionX() {
        return _inclinometerPosition.getX();
    }

    /**
     * Get the line that connects the two points represented by the two
     * angle/distance pairs (Polar to Cartesian transformation).
     */
    @Override
    public final Line getLine() {
        final Point2D startPoint = getStartPoint();
        final Point2D endPoint = getEndPoint();
        return new Line( startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY() );
    }

    public final double getStartAngleDegrees() {
        return _startAngleDegrees;
    }

    public final double getStartDistance() {
        return _startDistance;
    }

    // Convert Polar coordinates to Cartesian coordinates.
    public final Point2D getStartPoint() {
        return new Point2D( getInclinometerPositionX()
                + ( _startDistance * FastMath.cos( FastMath.toRadians( _startAngleDegrees ) ) ),
                            getInclinometerPositioneY() + ( _startDistance
                                    * FastMath.sin( FastMath.toRadians( _startAngleDegrees ) ) ) );
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }

    public final void setEndAngleDegrees( final double endAngleDegrees ) {
        _endAngleDegrees = endAngleDegrees;
    }

    public final void setEndDistance( final double endDistance ) {
        _endDistance = endDistance;
    }

    public final void setInclinometerPosition( final double referencePointX,
                                               final double referencePointY ) {
        _inclinometerPosition = new Point2D( referencePointX, referencePointY );

        super.setReferencePoint2D( referencePointX, referencePointY );
    }

    public final void setInclinometerPosition( final Point2D referencePoint ) {
        final double referencePointX = referencePoint.getX();
        final double referencePointY = referencePoint.getY();
        setInclinometerPosition( referencePointX, referencePointY );
    }

    @Override
    public final void setLine( final double x1,
                               final double y1,
                               final double x2,
                               final double y2 ) {
        final double referenceX = getInclinometerPositionX();
        final double referenceY = getInclinometerPositioneY();

        final double xdiff1 = x1 - referenceX;
        final double ydiff1 = y1 - referenceY;

        // Convert Cartesian coordinates to Polar coordinates.
        _startAngleDegrees = FastMath.toDegrees( FastMath.atan2( ydiff1, xdiff1 ) );
        _startDistance = FastMath.hypot( xdiff1, ydiff1 );

        final double xdiff2 = x2 - referenceX;
        final double ydiff2 = y2 - referenceY;

        // Convert Cartesian coordinates to Polar coordinates.
        _endAngleDegrees = FastMath.toDegrees( FastMath.atan2( ydiff2, xdiff2 ) );
        _endDistance = FastMath.hypot( xdiff2, ydiff2 );
    }

    public final void setLine( final double inclinometerPositionX,
                               final double inclinometerPositionY,
                               final double startAngleDegrees,
                               final double startDistance,
                               final double endAngleDegrees,
                               final double endDistance ) {
        setInclinometerPosition( inclinometerPositionX, inclinometerPositionY );

        _startAngleDegrees = startAngleDegrees;
        _startDistance = startDistance;
        _endAngleDegrees = endAngleDegrees;
        _endDistance = endDistance;
    }

    public final void setLine( final Point2D inclinometerPosition,
                               final double startAngleDegrees,
                               final double startDistance,
                               final double endAngleDegrees,
                               final double endDistance ) {
        setLine( inclinometerPosition.getX(),
                 inclinometerPosition.getY(),
                 startAngleDegrees,
                 startDistance,
                 endAngleDegrees,
                 endDistance );
    }

    public final void setPolarLine( final double inclinometerPositionX,
                                    final double inclinometerPositionY,
                                    final double startAngleDegrees,
                                    final double startDistance,
                                    final double endAngleDegrees,
                                    final double endDistance,
                                    final String polarLineLabel,
                                    final Layer layer,
                                    final boolean useAsProjector,
                                    final int numberOfProjectionZones ) {
        setLine( inclinometerPositionX,
                 inclinometerPositionY,
                 startAngleDegrees,
                 startDistance,
                 endAngleDegrees,
                 endDistance );

        setLineObject( polarLineLabel, layer, useAsProjector, numberOfProjectionZones );
    }

    public final void setPolarLine( final PolarLine polarLine ) {
        setPolarLine( polarLine.getInclinometerPosition(),
                      polarLine.getStartAngleDegrees(),
                      polarLine.getStartDistance(),
                      polarLine.getEndAngleDegrees(),
                      polarLine.getEndDistance(),
                      polarLine.getLabel(),
                      polarLine.getLayer(),
                      polarLine.isUseAsProjector(),
                      polarLine.getNumberOfProjectionZones() );
    }

    public final void setPolarLine( final Point2D referencePoint,
                                    final double startAngleDegrees,
                                    final double startDistance,
                                    final double endAngleDegrees,
                                    final double endDistance,
                                    final String polarLineLabel,
                                    final Layer layer,
                                    final boolean useAsProjector,
                                    final int numberOfProjectionZones ) {
        setPolarLine( referencePoint.getX(),
                      referencePoint.getY(),
                      startAngleDegrees,
                      startDistance,
                      endAngleDegrees,
                      endDistance,
                      polarLineLabel,
                      layer,
                      useAsProjector,
                      numberOfProjectionZones );
    }

    @Override
    public void setReferencePoint2D( final double inclinometerPositionX,
                                     final double inclinometerPositionY ) {
        setInclinometerPosition( inclinometerPositionX, inclinometerPositionY );
    }

    @Override
    public void setReferencePoint2D( final Point2D inclinometerPosition ) {
        setInclinometerPosition( inclinometerPosition );
    }

    public final void setStartAngleDegrees( final double startAngleDegrees ) {
        _startAngleDegrees = startAngleDegrees;
    }

    public final void setStartDistance( final double startDistance ) {
        _startDistance = startDistance;
    }
}
