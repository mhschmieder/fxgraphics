/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
 * This file is part of the FxCommonsToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxCommonsToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcommonstoolkit
 */
package com.mhschmieder.fxgraphicstoolkit;

import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.fxgraphicstoolkit.geometry.GeometryUtilities;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerAssignable;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxgraphicstoolkit.shape.ShapeGroup;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;

/**
 * The <code>GraphicalObject</code> class is the abstract base class for all
 * graphical objects. It describes the attributes that are common to all
 * graphical objects in a 2D view, such as whether they are selected, location
 * (x,y), and rotation angle (in the XY plane).
 * <p>
 * NOTE: All data should be private, in case of overrides on getter methods.
 * Also, this means member variables should not be accessed directly, in case of
 * overrides on getter methods in subclasses.
 * TODO: Move the label-based methods to a new LabeledObjectCollection class
 * that is derived from GraphicalObjectCollection, and be sure to declare other
 * downstream collections as of that type.
 */
public abstract class GraphicalObject implements Comparable< GraphicalObject >, LayerAssignable {

    // By default, Graphical Objects are not selected.
    protected static final boolean SELECTED_DEFAULT      = false;

    // Declare constants for Cartesian Space coordinates (meters) and rotation
    // angle (degrees).
    // NOTE: These have tentatively been made protected instead of private, as
    // there are some unfortunate recursions in certain derived classes during
    // update() methods if they invoke get() methods vs. working directly
    // with the instance variable instead.
    protected static final double  X_DEFAULT             = 0.0d;

    protected static final double  Y_DEFAULT             = 0.0d;
    protected static final Point2D LOCATION_DEFAULT      = new Point2D( X_DEFAULT, Y_DEFAULT );
    protected static final double  ANGLE_DEGREES_DEFAULT = 0.0d;

    // Declare a flag to indicate whether this Graphical Object is selected.
    // TODO: Remove this as the Node representation captures this status?
    private boolean                _selected;

    // NOTE: Layer Properties are Observable and thus can use data binding.
    private LayerProperties        _layer;

    // NOTE: We may want to add a z-coordinate at some point, for scalability.
    private double                 _locationX;
    private double                 _locationY;

    private double                 _angleDegrees;

    // Cache the Graphical Node on demand, so it can be recalled for comparison.
    private ShapeGroup             _cachedGraphicalNode;

    // Cache the Marker Node on demand, so it can be recalled for comparison.
    private ShapeGroup             _cachedMarkerNode;

    // Default constructor (disabled since this is an abstract class)
    protected GraphicalObject() {
        this( LayerUtilities.makeDefaultLayer(), X_DEFAULT, Y_DEFAULT, ANGLE_DEGREES_DEFAULT );
    }

    // Fully qualified constructor
    protected GraphicalObject( final LayerProperties layer,
                               final double locationX,
                               final double locationY,
                               final double angleDegrees ) {
        _layer = layer;

        _locationX = locationX;
        _locationY = locationY;

        _angleDegrees = angleDegrees;

        // All newly created objects -- even if copied -- are unselected.
        _selected = SELECTED_DEFAULT;
    }

    public final void cacheGraphicalNode( final ShapeGroup graphicalNode ) {
        _cachedGraphicalNode = graphicalNode;
    }

    public final void cacheMarkerNode( final ShapeGroup markerNode ) {
        _cachedMarkerNode = markerNode;
    }

    /**
     * {@code Integer.compare(this.hashCode(), o.hashCode())}
     */
    @Override
    public int compareTo( final GraphicalObject obj ) {
        return Integer.compare( hashCode(), obj.hashCode() );
    }

    public boolean contains( final Point2D clickPoint ) {
        // Try to use the cached vector graphics for its cached bounds, if
        // available, rather than wastefully requesting a new copy.
        ShapeGroup vectorGraphics = getCachedGraphicalNode();
        if ( vectorGraphics != null ) {
            return vectorGraphics.contains( clickPoint );
        }

        vectorGraphics = getVectorGraphics( false );
        if ( vectorGraphics != null ) {
            return vectorGraphics.contains( clickPoint );
        }

        return false;
    }

    // TODO: Remove loose tolerance here, and paste it into a
    // Microphone-specific version of this method, once we know whether Power
    // Users are happy with the new behavior.
    // TODO: Verify that the click point is in the correct origin/system.
    public boolean contains( final Point2D clickPoint,
                             final Bounds contextBounds,
                             final boolean allowTightFitContainment ) {
        // Look for small context bounds, and use simple shape boundary
        // containment in those cases, as bounding box based containment is very
        // coarse due to object rotation, odd shapes, and other factors.
        // NOTE: We have set this criteria to 10 meters, but might need to make
        // this more flexible or even user-defined and thus passed into this
        // method as a tolerance, as it would be zoom-sensitive in scale.
        // TODO: Replace this with a Shape based outline containment test.
        // NOTE: That is disabled for now, because currently we use a Group
        // derived ShapeGroup and thus would need to iterate all of its
        // Shapes using a new method, to avoid getting false negatives.
        final double contextWidth = contextBounds.getWidth();
        final double contextHeight = contextBounds.getHeight();
        final double contextLengthMinimum = FastMath.min( contextWidth, contextHeight );

        // NOTE: For tight fit, we use AWT as it is boundary path based vs.
        // enclosing bounding box based, so doesn't get false hits.
        if ( allowTightFitContainment && ( contextLengthMinimum <= 10.0d ) ) {
            final java.awt.Shape shape = getVectorGraphicsAwt();
            final java.awt.geom.Point2D awtPoint = GeometryUtilities.getPoint( clickPoint );
            return shape.contains( awtPoint );
        }

        // NOTE: We ensure a bounding rectangle of at least 1.5% of the minimum
        // dimension, for "easy picking" of extraordinarily small objects, such
        // as MM4's and point theoretical Loudspeakers (OMNI's).
        final double clickDiameter = 0.015d * contextLengthMinimum;
        final double clickRadius = 0.5d * clickDiameter;

        Bounds bbox = getBoundingBox();
        if ( bbox.isEmpty() ) {
            return false;
        }
        if ( bbox.getWidth() < clickDiameter ) {
            final double centerX = bbox.getMinX() + ( 0.5d * bbox.getWidth() );
            bbox = new BoundingBox( centerX - clickRadius,
                                    bbox.getMinY(),
                                    clickDiameter,
                                    bbox.getHeight() );
        }
        if ( bbox.getHeight() < clickDiameter ) {
            final double centerY = bbox.getMinY() + ( 0.5d * bbox.getHeight() );
            bbox = new BoundingBox( bbox.getMinX(),
                                    centerY - clickRadius,
                                    bbox.getWidth(),
                                    clickDiameter );
        }

        return bbox.contains( clickPoint );
    }

    public void deepCloneToCollection( final Collection< GraphicalObject > graphicalObjects ) {
        // Deep-clone this object, using class inheritance.
        final GraphicalObject graphicalObjectClone = getDeepClonedObject();
        if ( graphicalObjectClone != null ) {
            // Use the existing Scene Graph Node references, for now.
            // TODO: Make a deep clone of these nodes as well.
            // NOTE: Undo and Redo actions may need to regenerate these nodes.
            final ShapeGroup cachedGraphicalNode = getCachedGraphicalNode();
            graphicalObjectClone.cacheGraphicalNode( cachedGraphicalNode );
            final ShapeGroup cachedMarkerNode = getCachedMarkerNode();
            graphicalObjectClone.cacheMarkerNode( cachedMarkerNode );

            // NOTE: The "selected" status bit must be set separately, as that
            // would not normally carry over from a copy constructor since it is
            // instance-specific and not a shared property.
            graphicalObjectClone.setSelected( true );

            graphicalObjects.add( graphicalObjectClone );
        }
    }

    public void drag( final double deltaX, final double deltaY ) {
        // Compute the new location of the Graphical Object by combining the
        // deltas with the original location.
        final double referencePointX = getReferencePointX();
        final double referencePointY = getReferencePointY();
        final double qx = referencePointX + deltaX;
        final double qy = referencePointY + deltaY;
        setReferencePoint2D( qx, qy );

        // Drag the associated Scene Graph Nodes by the specified amount.
        dragNode( deltaX, deltaY );
    }

    public final void dragNode( final double deltaX, final double deltaY ) {
        // If present, translate the associated Graphical Node, along with any
        // Markers, by the accumulated drag amount, in meters.
        // NOTE: We chain a Translate instance to the transforms instead of
        // setting the translation coordinates, as the graphics may be in a
        // custom user Distance Unit instead of Meters, and thus the updated
        // graphics won't be visible if not scaled correctly.
        if ( _cachedGraphicalNode != null ) {
            final ObservableList< Transform > transforms = _cachedGraphicalNode.getTransforms();
            transforms.add( Transform.translate( deltaX, deltaY ) );
        }
        if ( _cachedMarkerNode != null ) {
            final ObservableList< Transform > transforms = _cachedMarkerNode.getTransforms();
            transforms.add( Transform.translate( deltaX, deltaY ) );
        }
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof GraphicalObject ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        // members, so that derived classes produce the correct results when
        // comparing two objects.
        final GraphicalObject other = ( GraphicalObject ) obj;
        if ( !super.equals( obj ) ) {
            return false;
        }

        if ( !getLayer().equals( other.getLayer() ) ) {
            return false;
        }

        final double thisLocationX = getLocationX();
        final double thisLocationY = getLocationY();
        final double otherLocationX = other.getLocationX();
        final double otherLocationY = other.getLocationY();
        final boolean locationXIdentical = 0.0001 >= FastMath.abs( otherLocationX - thisLocationX );
        final boolean locationYIdentical = 0.0001 >= FastMath.abs( otherLocationY - thisLocationY );
        if ( !locationXIdentical || !locationYIdentical ) {
            return false;
        }

        // TODO: Add some slop in the angle comparison?
        if ( getAngleDegrees() != other.getAngleDegrees() ) {
            return false;
        }

        // NOTE: The "selected" status is exempt, as it is transitory.
        return true;
    }

    public double getAngleDegrees() {
        return _angleDegrees;
    }

    /**
     * If a subclass returns null from its implementation of
     * {@link #getVectorGraphics}, this method returns an empty bounding box.
     * <p>
     * NOTE: The Bounds are 3D but support 2D degenerate cases, and are what
     * Nodes return from their Bounds queries so are preferred over Rectangle2D.
     */
    public Bounds getBoundingBox() {
        // Try to use the cached vector graphics for its cached bounds, if
        // available, rather than wastefully requesting a new copy.
        ShapeGroup vectorGraphics = getCachedGraphicalNode();
        if ( vectorGraphics != null ) {
            // Use the local vs. parent bounds -- even though the latter applies
            // any translation, rotation, scale, and transforms -- as
            // containment tests are generally run in Meters but the graphics
            // may have a custom user Distance Unit preference applied.
            return vectorGraphics.getBoundsInLocal();
        }

        vectorGraphics = getVectorGraphics( false );
        if ( vectorGraphics != null ) {
            // Use the local vs. parent bounds -- even though the latter applies
            // any translation, rotation, scale, and transforms -- as
            // containment tests are generally run in Meters but the graphics
            // may have a custom user Distance Unit preference applied.
            return vectorGraphics.getBoundsInLocal();
        }

        return new BoundingBox( 0.0d, 0.0d, 0.0d, 0.0d );
    }

    public java.awt.geom.Rectangle2D getBoundingBoxAwt() {
        final java.awt.Shape shape = getVectorGraphicsAwt();
        return ( shape != null ) ? shape.getBounds2D() : new java.awt.geom.Rectangle2D.Double();
    }

    public final ShapeGroup getCachedGraphicalNode() {
        return _cachedGraphicalNode;
    }

    public final ShapeGroup getCachedMarkerNode() {
        return _cachedMarkerNode;
    }

    // Concrete classes must override this to provide copy-constructor based
    // clones. Generally, only top-level object types need to do this.
    public GraphicalObject getDeepClonedObject() {
        return null;
    }

    @Override
    public final LayerProperties getLayer() {
        return _layer;
    }

    public final Color getLayerColor() {
        return ( _layer == null ) ? LayerUtilities.LAYER_COLOR_DEFAULT : _layer.getLayerColor();
    }

    public final double getLocationX() {
        return _locationX;
    }

    public final double getLocationY() {
        return _locationY;
    }

    /**
     * This method, by default, combines the individual coordinate extraction
     * methods and can get overridden for determining what should serve as the
     * Reference Point for each derived object type. By default it returns the
     * cached Location, which itself may either be object-type-specific or may
     * refer to the GC.
     * <p>
     * Note that sometimes it is more efficient to override the Point2D getter,
     * and then individually extract the x-and-y coordinates from that, but at
     * other times it might be preferable to override the individual x-and-y
     * coordinate getters and generically combine to a Point2D object.
     * <p>
     * The renaming of this method from getLocation(), is meant to help
     * determine further refactoring and consolidation, and to reduce confusion
     * or incorrect usage of various competing reference points and other cached
     * coordinate data.
     *
     * @return A Point2D object representing the appropriate Reference Point for
     *         this Graphical Object
     */
    public Point2D getReferencePoint2D() {
        final double referencePointX = getReferencePointX();
        final double referencePointY = getReferencePointY();
        final Point2D referencePoint = new Point2D( referencePointX, referencePointY );
        return referencePoint;
    }

    /**
     * This method is to be overridden for determining what should serve as
     * the Reference Point for each derived object type. By default it returns
     * the cached Location, which itself may either be object-type-specific or
     * may refer to the GC.
     * <p>
     * The renaming of this method from getLocation(), is meant to help
     * determine further refactoring and consolidation, and to reduce confusion
     * or incorrect usage of various competing reference points and other cached
     * coordinate data.
     *
     * @return A double representing the appropriate Reference Point
     *         x-coordinate for this Graphical Object
     */
    public double getReferencePointX() {
        final double referencePointX = getLocationX();
        return referencePointX;
    }

    /**
     * This method is to be overridden for determining what should serve as
     * the Reference Point for each derived object type. By default it returns
     * the cached Location, which itself may either be object-type-specific or
     * may refer to the GC.
     * <p>
     * The renaming of this method from getLocation(), is meant to help
     * determine further refactoring and consolidation, and to reduce confusion
     * or incorrect usage of various competing reference points and other cached
     * coordinate data.
     *
     * @return A double representing the appropriate Reference Point
     *         y-coordinate for this Graphical Object
     */
    public double getReferencePointY() {
        final double referencePointY = getLocationY();
        return referencePointY;
    }

    /**
     * Constructs a node representing this Graphical Object.
     *
     * @param previewContext
     *            Flag for whether this is used in a preview context
     * @return A Shape Container representing this Graphical Object
     */
    public abstract ShapeGroup getVectorGraphics( final boolean previewContext );

    /**
     * Provides the legacy AWT-based graphical representation of this Graphical
     * Object.
     * <p>
     * NOTE: There is no default implementation; it must be provided by the
     * concrete derived classes.
     */
    public java.awt.Shape getVectorGraphicsAwt() {
        return null;
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }

    public final void highlight( final boolean highlightOn ) {
        // Switch the Graphical Node highlighting on or off.
        if ( _cachedGraphicalNode != null ) {
            _cachedGraphicalNode.highlight( highlightOn );
        }
        if ( _cachedMarkerNode != null ) {
            _cachedMarkerNode.highlight( highlightOn );
        }
    }

    /**
     * Determine if this Graphical Object interferes with any of the Graphical
     * Objects in the filtration list, based on whether its defined reference
     * location is within distance tolerance of another Graphical Object's
     * defined reference location.
     * <p>
     * This is especially useful for maintaining Near Field distance between
     * microphones and loudspeakers.
     */
    public final boolean interferesWith( final List< GraphicalObject > filter,
                                         final double distanceTolerance ) {
        // Check if the filtration list contains a solid that interferes with
        // this one (that is, one that is within distance tolerance of it).
        double distance = 0.0d;
        for ( final GraphicalObject graphicalObject : filter ) {
            distance = getReferencePoint2D().distance( graphicalObject.getReferencePoint2D() );
            if ( distance < distanceTolerance ) {
                return true;
            }
        }

        return false;
    }

    public boolean intersects( final Bounds area ) {
        // NOTE: Intersection doesn't detect for edge cases at the vertices, so
        // we must also check for containment and reverse-containment.
        // NOTE: For performance reasons, work backwards from the simplest and
        // most common case so that we don't always compute all three.
        // NOTE: The JavaFX bounds are too large currently, so we have reverted
        // to using the AWT graphics for the bounding box for now.
        // final Bounds bbox = getBoundingBox();
        // return area.contains( bbox ) || bbox.intersects( area ) ||
        // bbox.contains( area );
        final java.awt.geom.Rectangle2D areaAwt = getBoundingBoxAwt();
        final java.awt.geom.Rectangle2D bboxAwt = GeometryUtilities
                .rectangleAwtFromRectangle2D( area );
        return areaAwt.contains( bboxAwt ) || bboxAwt.intersects( areaAwt )
                || bboxAwt.contains( areaAwt );
    }

    public boolean intersects( final Rectangle area2D ) {
        final Bounds area = GeometryUtilities.boundsFromRectangle( area2D );
        return intersects( area );
    }

    public boolean intersects( final Rectangle2D area2D ) {
        final Bounds area = GeometryUtilities.boundsFromRectangle2D( area2D );
        return intersects( area );
    }

    public boolean isCloserThan( final GraphicalObject other, final Point2D clickPoint ) {
        // If the other Graphical Object is null, then this one is closer.
        if ( other == null ) {
            return true;
        }

        // Get the current Graphical Object's and the other Graphical Object's
        // bounding rectangles, so that we can properly detect which Graphical
        // Object is closest to the point of mouse click.
        final Bounds thisBbox = getBoundingBox();
        final Bounds otherBbox = other.getBoundingBox();

        final double centerX1 = thisBbox.getMinX() + ( 0.5d * thisBbox.getWidth() );
        final double centerY1 = thisBbox.getMinY() + ( 0.5d * thisBbox.getHeight() );
        final double centerX2 = otherBbox.getMinX() + ( 0.5d * otherBbox.getWidth() );
        final double centerY2 = otherBbox.getMinY() + ( 0.5d * otherBbox.getHeight() );

        final boolean thisObjectIsCloserThanOtherObjectToClickPoint = ( FastMath
                .abs( clickPoint.distance( centerX1, centerY1 ) ) < FastMath
                        .abs( clickPoint.distance( centerX2, centerY2 ) ) );

        return thisObjectIsCloserThanOtherObjectToClickPoint;
    }

    /**
     * Given a proposed delta offset for each dimension, calculate the resulting
     * location of this Graphical Object and make sure that it would not end up
     * being outside the supplied bounds.
     * <p>
     * This is done as a simple combined test of "too far left", "too far up",
     * "too far right", and "too far down", based on a single reference point.
     * Some subclasses may need to override this with more specialized criteria.
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
    public boolean isDragTargetWithinBounds( final double deltaX,
                                             final double deltaY,
                                             final Bounds bounds ) {
        final double currentX = getLocationX();
        final double currentY = getLocationY();
        final double targetX = currentX + deltaX;
        final double targetY = currentY + deltaY;
        final boolean tooFarLeft = targetX < bounds.getMinX();
        final boolean tooFarUp = targetY < bounds.getMinY();
        final boolean tooFarRight = targetX > bounds.getMaxX();
        final boolean tooFarDown = targetY > bounds.getMaxY();
        final boolean dragTargetWithinBounds = !tooFarLeft && !tooFarUp && !tooFarRight
                && !tooFarDown;

        return dragTargetWithinBounds;
    }

    public final boolean isEditable() {
        final LayerProperties layer = getLayer();
        final boolean visible = layer.isLayerVisible();
        final boolean locked = layer.isLayerLocked();
        return !locked && visible;
    }

    public final boolean isFilterable( final boolean allowUneditable ) {
        final boolean filterable = allowUneditable ? isVisible() : isEditable();
        return filterable;

    }

    public final boolean isFilterableByArea( final Bounds filterArea,
                                             final boolean allowUneditable ) {
        final boolean filterableByArea =
                                       isFilterable( allowUneditable ) && intersects( filterArea );
        return filterableByArea;
    }

    public final boolean isFilterableByArea( final Rectangle filterArea,
                                             final boolean allowUneditable ) {
        final boolean filterableByArea =
                                       isFilterable( allowUneditable ) && intersects( filterArea );
        return filterableByArea;
    }

    public final boolean isFilterableByArea( final Rectangle2D filterArea,
                                             final boolean allowUneditable ) {
        final boolean filterableByArea =
                                       isFilterable( allowUneditable ) && intersects( filterArea );
        return filterableByArea;
    }

    public final boolean isFilterableByPoint( final Point2D filterPoint ) {
        final boolean filterableByPoint = isFilterable( false ) && contains( filterPoint );
        return filterableByPoint;
    }

    public final boolean isFilterableByPoint( final Point2D filterPoint,
                                              final Bounds contextBounds,
                                              final boolean allowTightFitContainment ) {
        final boolean filterableByPoint = isFilterable( false )
                && contains( filterPoint, contextBounds, allowTightFitContainment );
        return filterableByPoint;
    }

    public final boolean isLocked() {
        final LayerProperties layer = getLayer();
        final boolean locked = layer.isLayerLocked();
        return locked;
    }

    public final boolean isSelected() {
        return _selected;
    }

    public final boolean isVisible() {
        // Determine visibility based on Layer Status.
        final boolean visible = _layer.isLayerVisible();
        return visible;
    }

    public void rotate( final double rotateX,
                        final double rotateY,
                        final double rotateThetaRelativeDegrees,
                        final double dx,
                        final double dy,
                        final double cosTheta,
                        final double sinTheta ) {
        // Apply the rotation angle to the locations and then the angles of all
        // the selected Graphical Objects that are editable, using the formulae:
        //
        // qx = ( px * cos( theta ) ) - ( py * sin( theta ) ) + dx
        // qy = ( px * sin( theta ) ) + ( py * cos( theta ) ) + dy
        //
        // where ( dx, dy ) is the translation vector, ( px, py ) is the GC of
        // the current Graphical Object, and ( qx, qy ) is the transformed GC of
        // the current Graphical Object.
        // NOTE: We have to get a point vs. individual coordinates, due to the
        // potential override of the location query to return something other
        // than the cached location. For instance, the CRDM of a Loudspeaker.
        final double referencePointX = getReferencePointX();
        final double referencePointY = getReferencePointY();

        final double qx = ( ( referencePointX * cosTheta ) - ( referencePointY * sinTheta ) ) + dx;
        final double qy = ( referencePointX * sinTheta ) + ( referencePointY * cosTheta ) + dy;

        // NOTE: We have to set a point vs. individual coordinates, due to the
        // potential override of the location setter to set something other
        // than the cached location. For instance, the CRDM of a Loudspeaker.
        setReferencePoint2D( qx, qy );

        final double theta = getAngleDegrees() + rotateThetaRelativeDegrees;

        setAngleDegrees( theta );

        // Rotate the associated Scene Graph Nodes by the specified amount.
        rotateNode( rotateX, rotateY, rotateThetaRelativeDegrees );
    }

    public final void rotateNode( final double rotateX,
                                  final double rotateY,
                                  final double rotateThetaRelativeDegrees ) {
        // If present, rotate the associated Graphical Node and Markers by the
        // accumulated drag amount, in degrees, and account for the pivot point.
        // NOTE: We chain a Rotate instance to the transforms instead of
        // setting the translation coordinates and rotation angle, as the
        // graphics may be in a custom user Distance Unit instead of Meters, and
        // thus the updated graphics won't be visible if not scaled correctly.
        if ( _cachedGraphicalNode != null ) {
            final ObservableList< Transform > transforms = _cachedGraphicalNode.getTransforms();
            transforms.add( Transform.rotate( rotateThetaRelativeDegrees, rotateX, rotateY ) );
        }
        if ( _cachedMarkerNode != null ) {
            final ObservableList< Transform > transforms = _cachedMarkerNode.getTransforms();
            transforms.add( Transform.rotate( rotateThetaRelativeDegrees, rotateX, rotateY ) );
        }
    }

    public void setAngleDegrees( final double angleDegrees ) {
        _angleDegrees = angleDegrees;
    }

    @Override
    public final void setLayer( final LayerProperties layer ) {
        _layer = layer;
    }

    public final void setLocationX( final double locationX ) {
        _locationX = locationX;
    }

    public final void setLocationY( final double locationY ) {
        _locationY = locationY;
    }

    /**
     * This method is to be overridden for determining what should serve as
     * the Reference Point for each derived object type. By default it sets the
     * Location cache, which itself may either be object-type-specific or may
     * refer to the GC.
     * <p>
     * The renaming of this method from setLocation(), is meant to help
     * determine further refactoring and consolidation, and to reduce confusion
     * or incorrect usage of various competing reference points and other cached
     * coordinate data.
     *
     * @param referencePointX
     *            The x-coordinate representing the appropriate Reference Point
     *            for this Graphical Object
     * @param referencePointY
     *            The y-coordinate representing the appropriate Reference Point
     *            for this Graphical Object
     */
    public void setReferencePoint2D( final double referencePointX, final double referencePointY ) {
        setLocationX( referencePointX );
        setLocationY( referencePointY );
    }

    /**
     * This method is to be overridden for determining what should serve as
     * the Reference Point for each derived object type. By default it sets the
     * Location cache, which itself may either be object-type-specific or may
     * refer to the GC.
     * <p>
     * The renaming of this method from setLocation(), is meant to help
     * determine further refactoring and consolidation, and to reduce confusion
     * or incorrect usage of various competing reference points and other cached
     * coordinate data.
     *
     * @param referencePoint
     *            A Point2D object representing the appropriate Reference Point
     *            for
     *            this Graphical Object
     */
    public void setReferencePoint2D( final Point2D referencePoint ) {
        final double referencePointX = referencePoint.getX();
        final double referencePointY = referencePoint.getY();
        setReferencePoint2D( referencePointX, referencePointY );
    }

    public final void setSelected( final boolean selected ) {
        // Avoid wasteful work if nothing changed.
        if ( selected == _selected ) {
            return;
        }

        // Cache the new selected status.
        _selected = selected;

        // Switch the highlighting dashed pattern on or off.
        updateHighlighting();
    }

    public final void setVisible( final boolean visible ) {
        // Switch the Graphical Node visibility on or off.
        if ( _cachedGraphicalNode != null ) {
            _cachedGraphicalNode.setVisible( visible );
        }
        if ( _cachedMarkerNode != null ) {
            _cachedMarkerNode.setVisible( visible );
        }
    }

    // Highlight the associated Graphical Node and Marker if the Graphical
    // Object is Selected.
    public final void updateHighlighting() {
        // Switch the Graphical Node highlighting on or off.
        final boolean highlightOn = isSelected();
        highlight( highlightOn );
    }

    public final void updateLockedStatus( final Color backColor, final Color defaultColor ) {
        // Switch the Graphical Node color based on Layer Lock status, etc.
        if ( _cachedGraphicalNode != null ) {
            updateLockedStatus( _cachedGraphicalNode, backColor, defaultColor );
        }
    }

    public final void updateLockedStatus( final ShapeGroup graphicalNode, final Color backColor ) {
        // Switch the Graphical Node color based on Layer Lock status, etc.
        final Color defaultColor = ColorUtilities.getForegroundFromBackground( backColor );
        updateLockedStatus( graphicalNode, backColor, defaultColor );
    }

    public final void updateLockedStatus( final ShapeGroup graphicalNode,
                                          final Color backColor,
                                          final Color defaultColor ) {
        // Make sure the Vector Graphics are all visible against the new
        // Background Color, but only change Black and White vs. other Colors.
        // Otherwise, use the assigned Layer Color, unless on a Locked Layer.
        final Color layerColor = getLayerColor();
        final Color foreColor = isLocked()
            ? ColorConstants.OBJECT_LOCKED_COLOR
            : ( Color.WHITE.equals( layerColor ) || Color.BLACK.equals( layerColor )
                    || backColor.equals( layerColor ) ) ? defaultColor : layerColor;
        graphicalNode.setForeground( foreColor, true );
    }

    public final void updateVisibility() {
        // Switch the Graphical Node visibility on or off.
        final boolean visible = isVisible();
        setVisible( visible );
    }

}
