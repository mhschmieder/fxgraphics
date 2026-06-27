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
import com.mhschmieder.jmath.geometry.euclidean.Axis;
import com.mhschmieder.jmath.geometry.euclidean.FacingDirection;
import com.mhschmieder.jmath.geometry.euclidean.Orientation;
import com.mhschmieder.jmath.geometry.euclidean.OrthogonalAxes;
import com.mhschmieder.jmath.geometry.euclidean.VectorUtilities;
import javafx.scene.transform.Affine;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

/**
 * The <code>SolidObject</code> class is the abstract base class for all solid
 * objects. It describes the attributes that are common to all solid objects in
 * a 3D view, such as orientation, and whether inverted in that orientation.
 * <p>
 * Many, but not all, of the derived classes are GC-centric. This means that the
 * GC is used for placement. This is not always the appropriate reference point
 * though; the "Location" field in the parent class is generally the common
 * denominator there, but currently only has two dimensional coordinates vs.
 * three. At some point, the parent class may add a z-axis coordinate.
 * <p>
 * At this point in the class hierarchy, most coordinates will be in vector form
 * unless used for containment and intersection tests via the JavaFX Graphics
 * library, in which case we use simple Poin2D and Point3D immutable instances.
 * <p>
 * NOTE: All data should be private, in case of overrides on getter methods.
 *  Also, this means member variables should not be accessed directly, in case
 *  of overrides on getter methods in subclasses.
 */
public abstract class SolidObject extends GraphicalObject {

    public static final Vector3D           GC_IN_VENUE_COORDINATES_DEFAULT = Vector3D.ZERO;

    protected static final Orientation     ORIENTATION_DEFAULT             = Orientation.VERTICAL;
    protected static final FacingDirection FACING_DIRECTION_DEFAULT        = FacingDirection.RIGHT;
    protected static final boolean         INVERTED_DEFAULT                = false;

    // Declare a variable for Geometric Center in Venue coordinates.
    // NOTE: This naming is a bit redundant, as we enforce Venue coordinates.
    private Vector3D                       _gcInVenueCoordinates;

    // Orientation is arbitrary in its reference, but distinguishes cases.
    private Orientation                    _orientation;

    // Facing Direction is not yet used by all sub-classes, constructors, or
    // pseudo-constructors. All geometry is Right-Facing by default.
    private FacingDirection                _facingDirection;

    // Inverted is absolute in its reference, and augments Orientation.
    private boolean                        _inverted;

    // NOTE: Since this class declares additional fields to the parent class,
    //  we cannot just invoke the super-constructor from each constructor, but
    //  need to invoke incrementally more complex local constructors instead.
    //
    // Default constructor (disabled since this is an abstract class)
    protected SolidObject() {
        this( X_DEFAULT, Y_DEFAULT, ANGLE_DEGREES_DEFAULT );
    }

    private SolidObject( final double locationX,
                         final double locationY,
                         final double angleDegrees ) {
        this( locationX,
              locationY,
              angleDegrees,
              ORIENTATION_DEFAULT,
              FACING_DIRECTION_DEFAULT,
              INVERTED_DEFAULT );
    }

    protected SolidObject( final double locationX,
                           final double locationY,
                           final double angleDegrees,
                           final Orientation orientation,
                           final FacingDirection facingDirection,
                           final boolean inverted ) {
        this( LayerManagement.makeDefaultLayer(),
              locationX,
              locationY,
              angleDegrees,
              orientation,
              facingDirection,
              inverted );
    }

    // Fully qualified constructor, using standard Location.
    protected SolidObject( final Layer layer,
                           final double locationX,
                           final double locationY,
                           final double angleDegrees,
                           final Orientation orientation,
                           final FacingDirection facingDirection,
                           final boolean inverted ) {
        // NOTE: Do not call any non-final "set" methods in the constructor
        //  as this would invoke overridden methods in subclasses that can
        //  cause recursion and null pointer problems at construction time.
        // TODO: Review which fields should have final get/set methods.
        super( layer, locationX, locationY, angleDegrees );

        _orientation = orientation;
        _facingDirection = facingDirection;
        _inverted = inverted;

        // Just in case this constructor is called directly and no GC is
        // supplied, we need to default the GC to avoid null pointer exceptions.
        _gcInVenueCoordinates = GC_IN_VENUE_COORDINATES_DEFAULT;
    }

    // Fully qualified constructor, using Geometric Center.
    protected SolidObject( final Layer layer,
                           final Vector3D gcInVenueCoordinates,
                           final double angleDegrees,
                           final Orientation orientation,
                           final FacingDirection facingDirection,
                           final boolean inverted ) {
        this( layer, X_DEFAULT, Y_DEFAULT, angleDegrees, orientation, facingDirection, inverted );

        setGcInVenueCoordinates( gcInVenueCoordinates );
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof SolidObject ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        //  members, so that derived classes produce the correct results when
        //  comparing two objects.
        // NOTE: As the location is generally calculated on the fly, and often
        //  rounded to the fourth decimal place, we need to use a FuzzyEQ
        //  comparison for equality.
        // TODO: Pass in the level of precision so that it isn't hard-wired,
        //  which could become brittle if code changes elsewhere in the
        //  application. Pass in the allowed delta as a double, for simplicity.
        final SolidObject other = ( SolidObject ) obj;
        

        // NOTE: We are forced to use a copy constructor on the GC field, and
        //  thus the address is different for equivalent objects created during
        //  candidate/current object syncing on the insert/edit dialog, causing
        //  the generic "equals()" method to return a false negative.
        if ( !super.equals( obj ) 
                || !getGcInVenueCoordinates().equals( other.getGcInVenueCoordinates() ) 
                || !getOrientation().equals( other.getOrientation() ) 
                || !getFacingDirection().equals( other.getFacingDirection() ) ) {
            return false;
        }

        if ( isInverted() != other.isInverted() ) {
            return false;
        }

        return true;
    }

    // Get the transform from the ECS (element coordinate system) for theSolid,
    // to the general model coordinate system.
    // NOTE: This method might need to be overridden for any subclass whose ECS
    //  is NOT centered about the GC (geometric center).
    // TODO: Need to account for Orientation when getting the GC?
    public java.awt.geom.AffineTransform getElementToModelTransformAwt() {
        // Move to the GC as the origin, so that reflections and rotations do
        // not move the object to a new location.
        final Vector3D gc = getGcInVenueCoordinates();
        final java.awt.geom.AffineTransform affineTransform = java.awt.geom.AffineTransform
                .getTranslateInstance( gc.getX(), gc.getY() );

        // NOTE: Transforms are applied in reverse order, so we set the
        //  rotation before the inversion in order to match the behavior of the
        //  Vector3D based methods elsewhere.
        affineTransform.rotate( FastMath.toRadians( getAngleDegrees() ) );

        // NOTE: Although we often fetch geometry from a 3D grid of Rig Points,
        //  the Rig Point ordering is merely for getting the opposite face and
        //  doesn't actually vertically invert it on the screen.
        if ( isInverted() ) {
            affineTransform.scale( 1.0d, -1d );
        }

        return affineTransform;
    }

    /*
     * Get the transform from the ECS (element coordinate system) for the
     * SolidObject,
     * to the general Venue coordinate system.
     * NOTE: This method might need to be overridden for any subclass whose ECS
     *  is NOT centered about the GC (geometric center).
     * TODO: Need to account for Orientation when getting the GC?
     */
    public Affine getElementToVenueTransform() {
        final Affine affineTransform = new Affine();

        // Move to the GC as the origin, so that reflections and rotations do
        // not move the object to a new location.
        final Vector3D gc = getGcInVenueCoordinates();
        final double gcX = gc.getX();
        final double gcY = gc.getY();
        affineTransform.appendTranslation( gcX, gcY );

        // NOTE: Transforms are applied in reverse order, so we set the
        //  rotation after the translation so that it rotates in the ECS. Also,
        //  we set the rotation before the inversion in order to match the
        //  behavior of the Vector3D based methods elsewhere.
        final double rotationAngle = getAngleDegrees();
        affineTransform.appendRotation( rotationAngle );

        // NOTE: Although we often fetch geometry from a 3D grid of Rig Points,
        //  the Rig Point ordering is merely for getting the opposite face and
        //  doesn't actually vertically invert it on the screen.
        if ( isInverted() ) {
            affineTransform.appendScale( 1.0d, -1d );
        }

        return affineTransform;
    }

    public FacingDirection getFacingDirection() {
        return _facingDirection;
    }

    public final Vector2D getGcInPlanarCoordinates() {
        final Vector2D gcInPlanarCoordinates = VectorUtilities
                .projectToPlane( _gcInVenueCoordinates, OrthogonalAxes.XY );
        return gcInPlanarCoordinates;
    }

    public final Vector3D getGcInVenueCoordinates() {
        return _gcInVenueCoordinates;
    }

    public Orientation getOrientation() {
        return _orientation;
    }

    // This method takes a generic 3D vector and projects it into the
    // coordinate system of the SolidObject within a known 2D axial plane.
    // NOTE: This method should generally only be invoked with offset
    //  vectors, and the result should be added to vectors that are in Venue
    //  Coordinates. This method does not return Venue Coordinate vectors, but
    //  its coordinates are compatible as they have been properly projected.
    public final Vector3D getVectorInProjectedObjectCoordinates( final Vector3D offsetVector ) {
        // NOTE: It is critical that we invert immediately in our original 3D
        //  axial space, as we are in the coordinate system of the GC as soon as
        //  we conditionally rotate into its known 2D axial Projection Plane.
        Vector3D vectorInProjectedObjectCoordinates = isInverted()
            ? VectorUtilities.negatePoint3D( offsetVector, Axis.Z )
            : VectorUtilities.copyPoint3D( offsetVector );

        final Orientation orientation = getOrientation();
        switch ( orientation ) {
        case HORIZONTAL:
            break;
        case VERTICAL:
            // The 3D vertical axis "Z" becomes the 2D vertical axis "Y".
            // NOTE: This is done to make it easier to trivially extract the
            //  only two relevant axial offsets into a simple Point2D object.
            vectorInProjectedObjectCoordinates = VectorUtilities
                    .exchangeCoordinates( vectorInProjectedObjectCoordinates, 
                                          OrthogonalAxes.YZ );
            break;
        default:
            break;
        }

        // Rotate into the known 2D axial Projection Plane of the GC.
        vectorInProjectedObjectCoordinates = VectorUtilities
                .rotateInPlane( vectorInProjectedObjectCoordinates,
                                OrthogonalAxes.XY,
                                FastMath.toRadians( getAngleDegrees() ) );

        return vectorInProjectedObjectCoordinates;
    }

    public final Vector3D getVectorInVenueCoordinatesFromObjectCoordinates( 
            final Vector3D cogInObjectCoordinates ) {
        final Vector3D vectorInProjectedObjectCoordinates 
                = getVectorInProjectedObjectCoordinates( cogInObjectCoordinates );

        final Vector3D vectorInVenueCoordinates = vectorInProjectedObjectCoordinates
                .add( _gcInVenueCoordinates );

        return vectorInVenueCoordinates;
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }

    public boolean isInverted() {
        return _inverted;
    }

    public void setFacingDirection( final FacingDirection facingDirection ) {
        _facingDirection = facingDirection;
    }

    public final void setGcInVenueCoordinates( final Vector3D gcInVenueCoordinates ) {
        _gcInVenueCoordinates = VectorUtilities.copyPoint3D( gcInVenueCoordinates );

        final Vector2D gcInVenueCoordinatesProjected = VectorUtilities
                .projectToPlane( gcInVenueCoordinates, OrthogonalAxes.XY );

        final double referencePointX = gcInVenueCoordinatesProjected.getX();
        final double referencePointY = gcInVenueCoordinatesProjected.getY();
        super.setReferencePoint2D( referencePointX, referencePointY );
    }

    public void setInverted( final boolean inverted ) {
        _inverted = inverted;
    }

    public void setOrientation( final Orientation orientation ) {
        _orientation = orientation;
    }

    // NOTE: Orientation must be set before calling setReferencePoint2D.
    @Override
    public void setReferencePoint2D( final double referencePointX, final double referencePointY ) {
        super.setReferencePoint2D( referencePointX, referencePointY );

        _gcInVenueCoordinates = new Vector3D( referencePointX, referencePointY, 0.0d );
    }
}
