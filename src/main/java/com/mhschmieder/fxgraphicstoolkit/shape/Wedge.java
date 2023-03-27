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
 * This file is part of the FxGraphicsToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxGraphicsToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgraphicstoolkit
 */
package com.mhschmieder.fxgraphicstoolkit.shape;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.fxgraphicstoolkit.geometry.GeometryUtilities;
import com.mhschmieder.fxgraphicstoolkit.geometry.Orientation;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/*
 * <code>Wedge</code> stores the three dimensional locations of the corner
 * points (when relevant) of a <code>PhysicsObject</code>. It mostly has
 * methods for drawing a wedge from the corner points. This is used to
 * generate an outline of a generic wedge, in a way that the returned shape (as
 * a Path) can be used as part of a static initializer for a more complex
 * shape, such as those defined in EnclosureHash.
 */
public class Wedge {

    // :WARNING: COPY CONSTRUCTOR This class has a copy constructor, therefore
    // if you add any instance variables, you must also add them to the copy
    // constructor

    // The dimensions of _cornerPointArray are [x][y][z]
    // where x=rear/front, y=right/left, z=bottom/top
    // [0] means the negative direction of each dimension (rear, right, bottom)
    // [1] means the positive direction of each dimension (front, left, top)
    // TODO: Replace this declaration and all associated method signatures with
    //  Vector3D from Apache Math and use VectorUtilities to work with it.
    private Point3D[][][] _cornerPointArray = new Point3D[ 2 ][ 2 ][ 2 ];

    public Wedge() {
        this( 0.0d, 0.0d, 0.0d, 0.0d, 0.0d );
    }

    public Wedge( final double depth,
                  final double widthRear,
                  final double widthFront,
                  final double heightRear,
                  final double heightFront ) {
        // Rear Right
        _cornerPointArray[ 0 ][ 0 ][ 0 ] = new Point3D( -0.5d * depth,
                                                        -0.5d * widthRear,
                                                        -0.5d * heightRear ); // Bottom
        _cornerPointArray[ 0 ][ 0 ][ 1 ] = new Point3D( -0.5d * depth,
                                                        -0.5d * widthRear,
                                                        0.5d * heightRear ); // Top

        // Rear Left
        _cornerPointArray[ 0 ][ 1 ][ 0 ] = new Point3D( -0.5d * depth,
                                                        0.5d * widthRear,
                                                        -0.5d * heightRear ); // Bottom
        _cornerPointArray[ 0 ][ 1 ][ 1 ] = new Point3D( -0.5d * depth,
                                                        0.5d * widthRear,
                                                        0.5d * heightRear ); // Top

        // Front Right
        _cornerPointArray[ 1 ][ 0 ][ 0 ] = new Point3D( 0.5d * depth,
                                                        -0.5d * widthFront,
                                                        -0.5d * heightFront ); // Bottom
        _cornerPointArray[ 1 ][ 0 ][ 1 ] = new Point3D( 0.5d * depth,
                                                        -0.5d * widthFront,
                                                        0.5d * heightFront ); // Top

        // Front Left
        _cornerPointArray[ 1 ][ 1 ][ 0 ] = new Point3D( 0.5d * depth,
                                                        0.5d * widthFront,
                                                        -0.5d * heightFront ); // Bottom
        _cornerPointArray[ 1 ][ 1 ][ 1 ] = new Point3D( 0.5d * depth,
                                                        0.5d * widthFront,
                                                        0.5d * heightFront ); // Top
    }

    public Wedge( final double topFrontX,
                  final double topRearX,
                  final double bottomFrontX,
                  final double bottomRearX,
                  final double height,
                  final double width ) {
        final double halfHeight = 0.5d * height;
        final double halfWidth = 0.5d * width;

        // Rear Right
        _cornerPointArray[ 0 ][ 0 ][ 0 ] = new Point3D( bottomRearX, -halfWidth, -halfHeight ); // Bottom
        _cornerPointArray[ 0 ][ 0 ][ 1 ] = new Point3D( topRearX, -halfWidth, halfHeight ); // Top

        // Rear Left
        _cornerPointArray[ 0 ][ 1 ][ 0 ] = new Point3D( bottomRearX, halfWidth, -halfHeight ); // Bottom
        _cornerPointArray[ 0 ][ 1 ][ 1 ] = new Point3D( topRearX, halfWidth, halfHeight ); // Top

        // Front Right
        _cornerPointArray[ 1 ][ 0 ][ 0 ] = new Point3D( bottomFrontX, -halfWidth, -halfHeight ); // Bottom
        _cornerPointArray[ 1 ][ 0 ][ 1 ] = new Point3D( topFrontX, -halfWidth, halfHeight ); // Top

        // Front Left
        _cornerPointArray[ 1 ][ 1 ][ 0 ] = new Point3D( bottomFrontX, halfWidth, -halfHeight ); // Bottom
        _cornerPointArray[ 1 ][ 1 ][ 1 ] = new Point3D( topFrontX, halfWidth, halfHeight ); // Top
    }

    public Wedge( final double topFrontX,
                  final double topRearX,
                  final double bottomFrontX,
                  final double bottomRearX,
                  final double height,
                  final double widthRear,
                  final double widthFront ) {
        final double halfHeight = 0.5d * height;

        // Rear Right
        _cornerPointArray[ 0 ][ 0 ][ 0 ] =
                                         new Point3D( bottomRearX, -0.5d * widthRear, -halfHeight ); // Bottom
        _cornerPointArray[ 0 ][ 0 ][ 1 ] = new Point3D( topRearX, -0.5d * widthRear, halfHeight ); // Top

        // Rear Left
        _cornerPointArray[ 0 ][ 1 ][ 0 ] =
                                         new Point3D( bottomRearX, 0.5d * widthRear, -halfHeight ); // Bottom
        _cornerPointArray[ 0 ][ 1 ][ 1 ] = new Point3D( topRearX, 0.5d * widthRear, halfHeight ); // Top

        // Front Right
        _cornerPointArray[ 1 ][ 0 ][ 0 ] = new Point3D( bottomFrontX,
                                                        -0.5d * widthFront,
                                                        -halfHeight ); // Bottom
        _cornerPointArray[ 1 ][ 0 ][ 1 ] = new Point3D( topFrontX, -0.5d * widthFront, halfHeight ); // Top

        // Front Left
        _cornerPointArray[ 1 ][ 1 ][ 0 ] = new Point3D( bottomFrontX,
                                                        0.5d * widthFront,
                                                        -halfHeight ); // Bottom
        _cornerPointArray[ 1 ][ 1 ][ 1 ] = new Point3D( topFrontX, 0.5d * widthFront, halfHeight ); // Top
    }

    public Wedge( final double topFrontX,
                  final double topRearX,
                  final double bottomFrontX,
                  final double bottomRearX,
                  final double topFrontZ,
                  final double topRearZ,
                  final double bottomFrontZ,
                  final double bottomRearZ,
                  final double width ) {
        final double halfWidth = 0.5d * width;

        // Rear Right
        _cornerPointArray[ 0 ][ 0 ][ 0 ] = new Point3D( bottomRearX, -halfWidth, bottomRearZ ); // Bottom
        _cornerPointArray[ 0 ][ 0 ][ 1 ] = new Point3D( topRearX, -halfWidth, topRearZ ); // Top

        // Rear Left
        _cornerPointArray[ 0 ][ 1 ][ 0 ] = new Point3D( bottomRearX, halfWidth, bottomRearZ ); // Bottom
        _cornerPointArray[ 0 ][ 1 ][ 1 ] = new Point3D( topRearX, halfWidth, topRearZ ); // Top

        // Front Right
        _cornerPointArray[ 1 ][ 0 ][ 0 ] = new Point3D( bottomFrontX, -halfWidth, bottomFrontZ ); // Bottom
        _cornerPointArray[ 1 ][ 0 ][ 1 ] = new Point3D( topFrontX, -halfWidth, topFrontZ ); // Top

        // Front Left
        _cornerPointArray[ 1 ][ 1 ][ 0 ] = new Point3D( bottomFrontX, halfWidth, bottomFrontZ ); // Bottom
        _cornerPointArray[ 1 ][ 1 ][ 1 ] = new Point3D( topFrontX, halfWidth, topFrontZ ); // Top
    }

    public Wedge( final Wedge wedge ) {
        _cornerPointArray = wedge.copyCornerPointArray();
    }

    public Point3D[][][] copyCornerPointArray() {
        final Point3D[][][] cornerPointArray = new Point3D[ 3 ][ 3 ][ 3 ];

        cornerPointArray[ 0 ][ 0 ][ 0 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 0 ][ 0 ][ 0 ] );
        cornerPointArray[ 0 ][ 0 ][ 1 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 0 ][ 0 ][ 1 ] );

        cornerPointArray[ 0 ][ 1 ][ 0 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 0 ][ 1 ][ 0 ] );
        cornerPointArray[ 0 ][ 1 ][ 1 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 0 ][ 1 ][ 1 ] );

        cornerPointArray[ 1 ][ 0 ][ 0 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 1 ][ 0 ][ 0 ] );
        cornerPointArray[ 1 ][ 0 ][ 1 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 1 ][ 0 ][ 1 ] );

        cornerPointArray[ 1 ][ 1 ][ 0 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 1 ][ 1 ][ 0 ] );
        cornerPointArray[ 1 ][ 1 ][ 1 ] = GeometryUtilities
                .copyPoint3D( cornerPointArray[ 1 ][ 1 ][ 1 ] );

        return cornerPointArray;
    }

    protected void drawBottomViewHz( final Path wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getY(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getY(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getY(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getY() };

        final ObservableList< PathElement > pathElements = wedge.getElements();
        pathElements.add( new MoveTo( xPts[ 0 ], yPts[ 0 ] ) );
        for ( int j = 1; j < xPts.length; j++ ) {
            pathElements.add( new LineTo( xPts[ j ], yPts[ j ] ) );
        }
        pathElements.add( new LineTo( xPts[ 0 ], yPts[ 0 ] ) );
    }

    protected void drawBottomViewHzAwt( final java.awt.geom.GeneralPath wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getY(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getY(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getY(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getY() };

        wedge.moveTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
        for ( int j = 1; j < xPts.length; j++ ) {
            wedge.lineTo( ( float ) xPts[ j ], ( float ) yPts[ j ] );
        }
        wedge.lineTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
    }

    // NOTE: This method unfortunately is currently redundant with the same
    // named method in RigPoints.java, but until we fully consolidate the
    // usage of EnclosureHash, we need them both (and it's too messy to special
    // case for now).
    // NOTE: It is additionally important to determine whether this belongs
    // here or in rigging, as it should probably correspond to the CRDM and not
    // the GC (verify whether this is currently the case).
    protected void drawCrosshairsAwt( final java.awt.geom.GeneralPath wedge ) {
        // Base the crosshairs dimension on the smallest used graphical
        // dimension
        // of the loudspeaker. This uses front height and front width.
        final float approximateDepth = ( float ) ( _cornerPointArray[ 1 ][ 1 ][ 1 ].getX()
                - _cornerPointArray[ 0 ][ 1 ][ 1 ].getX() );
        final float approximateWidth = ( float ) ( _cornerPointArray[ 1 ][ 1 ][ 1 ].getY()
                - _cornerPointArray[ 1 ][ 0 ][ 1 ].getY() );
        final float approximateHeight = ( float ) ( _cornerPointArray[ 1 ][ 1 ][ 1 ].getZ()
                - _cornerPointArray[ 1 ][ 1 ][ 0 ].getZ() );

        final float crosshairsDimension = 0.5f
                * FastMath.min( approximateHeight, FastMath.min( approximateDepth, approximateWidth ) );

        // Draw the cross-hair as two separate lines, to represent the geometric
        // center of the object.
        wedge.moveTo( -0.5f * crosshairsDimension, 0.0f );
        wedge.lineTo( 0.5f * crosshairsDimension, 0.0f );
        wedge.moveTo( 0.0f, -0.5f * crosshairsDimension );
        wedge.lineTo( 0.0f, 0.5f * crosshairsDimension );
    }

    protected void drawLeftViewVt( final Path wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getZ(),
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getZ() };

        final ObservableList< PathElement > pathElements = wedge.getElements();
        pathElements.add( new MoveTo( xPts[ 0 ], yPts[ 0 ] ) );
        for ( int j = 1; j < xPts.length; j++ ) {
            pathElements.add( new LineTo( xPts[ j ], yPts[ j ] ) );
        }
        pathElements.add( new LineTo( xPts[ 0 ], yPts[ 0 ] ) );
    }

    protected void drawLeftViewVtAwt( final java.awt.geom.GeneralPath wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 1 ][ 0 ].getZ(),
                                _cornerPointArray[ 1 ][ 1 ][ 0 ].getZ() };

        wedge.moveTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
        for ( int j = 1; j < xPts.length; j++ ) {
            wedge.lineTo( ( float ) xPts[ j ], ( float ) yPts[ j ] );
        }
        wedge.lineTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
    }

    protected void drawRightViewVt( final Path wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getZ(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getZ() };

        final ObservableList< PathElement > pathElements = wedge.getElements();
        pathElements.add( new MoveTo( xPts[ 0 ], yPts[ 0 ] ) );
        for ( int j = 1; j < xPts.length; j++ ) {
            pathElements.add( new LineTo( xPts[ j ], yPts[ j ] ) );
        }
        pathElements.add( new LineTo( xPts[ 0 ], yPts[ 0 ] ) );
    }

    protected void drawRightViewVtAwt( final java.awt.geom.GeneralPath wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getZ(),
                                _cornerPointArray[ 0 ][ 0 ][ 0 ].getZ(),
                                _cornerPointArray[ 1 ][ 0 ][ 0 ].getZ() };

        wedge.moveTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
        for ( int j = 1; j < xPts.length; j++ ) {
            wedge.lineTo( ( float ) xPts[ j ], ( float ) yPts[ j ] );
        }
        wedge.lineTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
    }

    protected void drawTopViewHz( final Path wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getY(),
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getY(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getY(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getY() };

        final ObservableList< PathElement > pathElements = wedge.getElements();
        pathElements.add( new MoveTo( xPts[ 0 ], yPts[ 0 ] ) );
        for ( int j = 1; j < xPts.length; j++ ) {
            pathElements.add( new LineTo( xPts[ j ], yPts[ j ] ) );
        }
        pathElements.add( new LineTo( xPts[ 0 ], yPts[ 0 ] ) );
    }

    protected void drawTopViewHzAwt( final java.awt.geom.GeneralPath wedge ) {
        final double xPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getX(),
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getX(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getX() };
        final double yPts[] = {
                                _cornerPointArray[ 1 ][ 1 ][ 1 ].getY(),
                                _cornerPointArray[ 1 ][ 0 ][ 1 ].getY(),
                                _cornerPointArray[ 0 ][ 0 ][ 1 ].getY(),
                                _cornerPointArray[ 0 ][ 1 ][ 1 ].getY() };

        wedge.moveTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
        for ( int j = 1; j < xPts.length; j++ ) {
            wedge.lineTo( ( float ) xPts[ j ], ( float ) yPts[ j ] );
        }
        wedge.lineTo( ( float ) xPts[ 0 ], ( float ) yPts[ 0 ] );
    }

    public Point3D get( final int ii, final int jj, final int kk ) {
        return _cornerPointArray[ ii ][ jj ][ kk ];
    }

    public Path getBottomSideVectorGraphics() {
        final Path wedge = new Path();
        drawBottomViewHz( wedge );
        return wedge;
    }

    public java.awt.geom.GeneralPath getBottomSideVectorGraphicsAwt() {
        final java.awt.geom.GeneralPath wedge =
                                              new java.awt.geom.GeneralPath( java.awt.geom.Path2D.WIND_EVEN_ODD );
        drawCrosshairsAwt( wedge );
        drawBottomViewHzAwt( wedge );
        return wedge;
    }

    public Point3D[][][] getCornerPointArray() {
        return _cornerPointArray;
    }

    public Path getLeftSideVectorGraphics() {
        final Path wedge = new Path();
        drawLeftViewVt( wedge );
        return wedge;
    }

    public java.awt.geom.GeneralPath getLeftSideVectorGraphicsAwt() {
        final java.awt.geom.GeneralPath wedge =
                                              new java.awt.geom.GeneralPath( java.awt.geom.Path2D.WIND_EVEN_ODD );
        drawCrosshairsAwt( wedge );
        drawLeftViewVtAwt( wedge );
        return wedge;
    }

    public final double getMaximumDepth() {
        final double leftSideDepth = _cornerPointArray[ 1 ][ 1 ][ 1 ].getX()
                - _cornerPointArray[ 0 ][ 1 ][ 1 ].getX();
        final double rightSideDepth = _cornerPointArray[ 1 ][ 1 ][ 1 ].getX()
                - _cornerPointArray[ 0 ][ 1 ][ 1 ].getX();
        final double maxDepth = FastMath.max( leftSideDepth, rightSideDepth );
        return maxDepth;
    }

    public final double getMaximumHeight() {
        final double rearHeight = _cornerPointArray[ 0 ][ 1 ][ 1 ].getZ()
                - _cornerPointArray[ 0 ][ 1 ][ 0 ].getZ();
        final double frontHeight = _cornerPointArray[ 1 ][ 1 ][ 1 ].getZ()
                - _cornerPointArray[ 1 ][ 1 ][ 0 ].getZ();
        final double maxHeight = FastMath.max( rearHeight, frontHeight );
        return maxHeight;
    }

    public final double getMaximumWidth() {
        final double rearWidth = _cornerPointArray[ 0 ][ 1 ][ 1 ].getY()
                - _cornerPointArray[ 0 ][ 0 ][ 1 ].getY();
        final double frontWidth = _cornerPointArray[ 1 ][ 1 ][ 1 ].getY()
                - _cornerPointArray[ 1 ][ 0 ][ 1 ].getY();
        final double maxWidth = FastMath.max( rearWidth, frontWidth );
        return maxWidth;
    }

    public Path getRightSideVectorGraphics() {
        final Path wedge = new Path();
        drawRightViewVt( wedge );
        return wedge;
    }

    public java.awt.geom.GeneralPath getRightSideVectorGraphicsAwt() {
        final java.awt.geom.GeneralPath wedge =
                                              new java.awt.geom.GeneralPath( java.awt.geom.Path2D.WIND_EVEN_ODD );
        drawCrosshairsAwt( wedge );
        drawRightViewVtAwt( wedge );
        return wedge;
    }

    public Path getTopSideVectorGraphics() {
        final Path wedge = new Path();
        drawTopViewHz( wedge );
        return wedge;
    }

    public java.awt.geom.GeneralPath getTopSideVectorGraphicsAwt() {
        final java.awt.geom.GeneralPath wedge =
                                              new java.awt.geom.GeneralPath( java.awt.geom.Path2D.WIND_EVEN_ODD );
        drawCrosshairsAwt( wedge );
        drawTopViewHzAwt( wedge );
        return wedge;
    }

    public final java.awt.geom.GeneralPath getVectorGraphicsAwt( final Orientation orientation,
                                                                 final boolean inverted ) {
        java.awt.geom.GeneralPath wedge;
        switch ( orientation ) {
        case VERTICAL:
            wedge = inverted ? getLeftSideVectorGraphicsAwt() : getRightSideVectorGraphicsAwt();
            break;
        case HORIZONTAL:
            wedge = inverted ? getBottomSideVectorGraphicsAwt() : getTopSideVectorGraphicsAwt();
            break;
        default:
            return null;
        }
        return wedge;
    }

    public void set( final Point3D[][][] cornerPointArray ) {
        _cornerPointArray = cornerPointArray;
    }

    public void setWedge( final Wedge wedge ) {
        _cornerPointArray = wedge.copyCornerPointArray();
    }

}// class Wedge
