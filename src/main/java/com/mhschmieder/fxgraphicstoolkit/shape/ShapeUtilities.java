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
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxgraphicstoolkit.shape;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.fxgraphicstoolkit.geometry.GeometryUtilities;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorConstants;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.util.Pair;

public final class ShapeUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ShapeUtilities() {}

    /**
     * Same as {@link #copyPath(Path, String)} but with the ID not specified.
     *
     * @see #copyPath(Path, String)
     *
     * @param oldPath
     *            The old path
     * @return A new path deep-copied from the old, with new ID
     */
    public static Path copyPath( final Path oldPath ) {
        return copyPath( oldPath, null );
    }

    /**
     * Copy all Shape and Path attributes from a Path into a new instance,
     * including its Path Elements. Node attributes are not copied.
     *
     * @param oldPath
     *            The old path
     * @param newId
     *            The ID of the new path (can be null or empty)
     * @return A new path deep-copied from the old, with new ID
     */
    public static Path copyPath( final Path oldPath, final String newId ) {
        final Path newPath = new Path();

        newPath.getElements().addAll( oldPath.getElements() );
        newPath.setSmooth( oldPath.isSmooth() );
        newPath.setStrokeType( oldPath.getStrokeType() );
        newPath.setStroke( oldPath.getStroke() );
        newPath.setStrokeWidth( oldPath.getStrokeWidth() );
        newPath.setStrokeDashOffset( oldPath.getStrokeDashOffset() );
        newPath.setStrokeLineCap( oldPath.getStrokeLineCap() );
        newPath.setStrokeLineJoin( oldPath.getStrokeLineJoin() );
        newPath.setStrokeMiterLimit( oldPath.getStrokeMiterLimit() );
        newPath.setFillRule( oldPath.getFillRule() );
        newPath.setFill( oldPath.getFill() );
        newPath.setAccessibleHelp( oldPath.getAccessibleHelp() );
        newPath.setAccessibleText( oldPath.getAccessibleText() );

        // Not querying isDisabled because that could be inherited from parents.
        newPath.setDisable( oldPath.isDisable() );

        // New path ID should be unique, valid, and not empty.
        if ( ( newId != null ) && !newId.isEmpty() ) {
            newPath.setId( newId );
        }

        return newPath;
    }

    /**
     * This method adds an arc to an existing list of shapes, positioned at
     * the center of the arc, with no axial rotation.
     *
     * @param arcGraphics
     *            The list of shapes to which we will add the arc graphics
     * @param arcCenterX
     *            The x-coordinate of the center of the arc
     * @param arcCenterY
     *            The y-coordinate of the center of the arc
     * @param arcWidth
     *            The width of the arc from its center to the left side of its
     *            bounding box
     * @param arcHeight
     *            The height of the arc from its center to the top of its
     *            bounding box
     * @param arcStartDeg
     *            The angle of a complete elliptical arc where we will start to
     *            draw this one
     * @param arcExtentDeg
     *            The radial extent of the arc beyond its start angle
     * @param arcType
     *            The type of Arc to draw (OPEN vs. CHORD vs. ROUND)
     * @param fill
     *            Flag for whether to fill the arc
     */
    public static void drawArc( final List< Shape > arcGraphics,
                                final double arcCenterX,
                                final double arcCenterY,
                                final double arcWidth,
                                final double arcHeight,
                                final double arcStartDeg,
                                final double arcExtentDeg,
                                final ArcType arcType,
                                final boolean fill ) {
        // Draw a single arc, at the specified distance from its center.
        final Arc arc = new Arc( arcCenterX,
                                 arcCenterY,
                                 arcWidth,
                                 arcHeight,
                                 arcStartDeg,
                                 arcExtentDeg );

        // NOTE: Oracle's documentation is weak for OPEN vs. CHORD vs. ROUND.
        arc.setType( arcType );

        // Make sure we only show the outline and not the interior.
        // NOTE: The arc may not show up if the fill is null, but is black by
        // default, so we'll have to live with setting to mid-gray for now.
        if ( fill ) {
            arc.setFill( ColorConstants.NEUTRAL_FILL );
        }
        else {
            arc.setFill( null );
        }

        // Add the arc graphics to the provided shape collection.
        arcGraphics.add( arc );
    }

    /**
     * This method adds an arc to an existing list of shapes, positioned at
     * the center of the arc, with no axial rotation, using a Path.
     *
     * @param arcGraphics
     *            The list of shapes to which we will add the arc graphics
     * @param left
     *            The x-coordinate of the left side of the arc's bounding box
     * @param top
     *            The y-coordinate of the top side of the arc's bounding box
     * @param arcRadiusX
     *            The radius of the arc from its center to the left side of its
     *            bounding box
     * @param arcRadiusY
     *            The radius of the arc from its center to the top of its
     *            bounding box
     * @param arcStartDegrees
     *            The angle of a complete elliptical arc where we will start to
     *            draw this one
     * @param arcExtentDegrees
     *            The radial extent of the arc beyond its start angle
     * @param largeArcFlag
     *            Large arg flag: determines which arc to use (large/small)
     * @param sweepFlag
     *            Sweep flag: determines which arc to use (direction)
     */
    public static void drawArcPath( final List< Shape > arcGraphics,
                                    final double left,
                                    final double top,
                                    final double arcRadiusX,
                                    final double arcRadiusY,
                                    final double arcStartDegrees,
                                    final double arcExtentDegrees,
                                    final boolean largeArcFlag,
                                    final boolean sweepFlag,
                                    final boolean closed ) {
        // Draw a single arc, at the specified distance from its bounding box.
        final Path arcPath = new Path();
        drawArcPath( arcPath,
                     left,
                     top,
                     arcRadiusX,
                     arcRadiusY,
                     arcStartDegrees,
                     arcExtentDegrees,
                     largeArcFlag,
                     sweepFlag,
                     closed );

        // Add the arc graphics to the provided shape collection.
        arcGraphics.add( arcPath );
    }

    /**
     * This method adds an arc to an existing list of Path Elements,
     * positioned at the center of the arc, with no axial rotation.
     *
     * @param pathElements
     *            The list of path elements to which we will add the arc
     *            graphics
     * @param left
     *            The x-coordinate of the left side of the arc's bounding box
     * @param top
     *            The y-coordinate of the top side of the arc's bounding box
     * @param arcRadiusX
     *            The radius of the arc from its center to the left side of its
     *            bounding box
     * @param arcRadiusY
     *            The radius of the arc from its center to the top of its
     *            bounding box
     * @param arcStartDegrees
     *            The angle of a complete elliptical arc where we will start to
     *            draw this one
     * @param arcExtentDegrees
     *            The radial extent of the arc beyond its start angle
     * @param largeArcFlag
     *            Large arg flag: determines which arc to use (large/small)
     * @param sweepFlag
     *            Sweep flag: determines which arc to use (direction)
     */
    public static void drawArcPath( final ObservableList< PathElement > pathElements,
                                    final double left,
                                    final double top,
                                    final double arcRadiusX,
                                    final double arcRadiusY,
                                    final double arcStartDegrees,
                                    final double arcExtentDegrees,
                                    final boolean largeArcFlag,
                                    final boolean sweepFlag,
                                    final boolean closed ) {
        // Draw a single arc, at the specified distance from its bounding box.
        // NOTE: We flip y-axis related values due to screen coordinates.
        final double beginAngleRadians = FastMath.toRadians( arcStartDegrees );
        final double cosBegin = FastMath.cos( beginAngleRadians );
        final double sinBegin = -FastMath.sin( beginAngleRadians );
        final double startX = left + ( arcRadiusX * ( 1.0d + cosBegin ) );
        final double startY = top + ( arcRadiusY * ( 1.0d + sinBegin ) );
        pathElements.add( new MoveTo( startX, startY ) );

        final double endAngleRadians = FastMath.toRadians( arcExtentDegrees ) + beginAngleRadians;
        final double cosEnd = FastMath.cos( endAngleRadians );
        final double sinEnd = -FastMath.sin( endAngleRadians );
        final double endX = left + ( arcRadiusX * ( 1.0d + cosEnd ) );
        final double endY = top + ( arcRadiusY * ( 1.0d + sinEnd ) );
        pathElements.add( new ArcTo( arcRadiusX,
                                     arcRadiusY,
                                     0.0d,
                                     endX,
                                     endY,
                                     largeArcFlag,
                                     sweepFlag ) );

        // Optionally connect the end of the arc back to the beginning.
        if ( closed ) {
            // Effectively make an arc of type Chord (simple closed type).
            pathElements.add( new LineTo( startX, startY ) );
        }
    }

    /**
     * This method adds an arc to an existing Path, positioned at the center
     * of the arc, with no axial rotation.
     *
     * @param path
     *            The path to which we will add the arc graphics
     * @param left
     *            The x-coordinate of the left side of the arc's bounding box
     * @param top
     *            The y-coordinate of the top side of the arc's bounding box
     * @param arcRadiusX
     *            The radius of the arc from its center to the left side of its
     *            bounding box
     * @param arcRadiusY
     *            The radius of the arc from its center to the top of its
     *            bounding box
     * @param arcStartDegrees
     *            The angle of a complete elliptical arc where we will start to
     *            draw this one
     * @param arcExtentDegrees
     *            The radial extent of the arc beyond its start angle
     * @param largeArcFlag
     *            Large arg flag: determines which arc to use (large/small)
     * @param sweepFlag
     *            Sweep flag: determines which arc to use (direction)
     */
    public static void drawArcPath( final Path path,
                                    final double left,
                                    final double top,
                                    final double arcRadiusX,
                                    final double arcRadiusY,
                                    final double arcStartDegrees,
                                    final double arcExtentDegrees,
                                    final boolean largeArcFlag,
                                    final boolean sweepFlag,
                                    final boolean closed ) {
        // Draw a single arc, at the specified distance from its bounding box.
        final ObservableList< PathElement > pathElements = path.getElements();
        drawArcPath( pathElements,
                     left,
                     top,
                     arcRadiusX,
                     arcRadiusY,
                     arcStartDegrees,
                     arcExtentDegrees,
                     largeArcFlag,
                     sweepFlag,
                     closed );
    }

    /**
     * This method adds a circle to an existing list of shapes, centered at
     * the origin, with no rotation. Generally this method should be applied
     * before any translational or rotational transforms on the full list of
     * shapes.
     *
     * @param circleGraphics
     *            The list of shapes to which we will add the circle graphics
     * @param diameter
     *            The diameter of the circle
     * @param fill
     *            Flag for whether to fill the ellipse
     */
    public static void drawCircle( final List< Shape > circleGraphics,
                                   final double diameter,
                                   final boolean fill ) {
        // Make a Circle at the origin, to be translated and rotated later.
        drawCircle( circleGraphics, 0.0d, 0.0d, diameter, fill );
    }

    /**
     * This method adds a circle to an existing list of shapes, positioned at
     * the upper left corner of the bounding box of the circle, with no
     * rotation. This method typically is invoked when back-to-back circles
     * are needed on the same path and thus applying transforms to move the
     * center of each circle from the origin is not an option. Generally this
     * method should be invoked before any translational or rotational
     * transforms are applied to the full list of shapes.
     *
     * @param circleGraphics
     *            The list of shapes to which we will add the circle graphics
     * @param centerX
     *            The x-axis offset of the center of the circle
     * @param centerY
     *            The y-axis offset of the center of the circle
     * @param diameter
     *            The diameter of the circle
     * @param fill
     *            Flag for whether to fill the ellipse
     */
    public static void drawCircle( final List< Shape > circleGraphics,
                                   final double centerX,
                                   final double centerY,
                                   final double diameter,
                                   final boolean fill ) {
        // Make the Circle as a basic Shape Node.
        final double arcRadius = 0.5 * diameter;
        final Circle circle = new Circle( centerX, centerY, arcRadius );

        // For simple shape graphics that do infer area closure, only the inside
        // stroke avoids effective doubling of intended stroke width.
        circle.setStrokeType( StrokeType.INSIDE );

        // Square end caps make it less likely that empty gaps will develop
        // between rendered segments of a graphics primitive that is supposed to
        // be closed, whether or not that graphic will be filled.
        circle.setStrokeLineCap( StrokeLineCap.SQUARE );

        // Make sure we only show the outline and not the interior.
        // NOTE: The circle may not show up if the fill is null, but is black
        // by default, so we'll have to live with setting to mid-gray for now.
        if ( fill ) {
            circle.setFill( ColorConstants.NEUTRAL_FILL );
        }
        else {
            circle.setFill( null );
        }

        // Add the circle graphics to the provided shape collection.
        circleGraphics.add( circle );
    }

    /**
     * This method adds a circle to an existing path, centered at the origin,
     * with no rotation. Generally this method should be applied before any
     * translational or rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the circle graphics
     * @param diameter
     *            The diameter of the circle
     */
    public static void drawCircle( final Path path, final double diameter ) {
        final double width = diameter;
        final double height = diameter;
        drawEllipse( path, width, height );
    }

    /**
     * This method adds a circle to an existing path, positioned at the upper
     * left corner of the bounding box of the circle, with no rotation. This
     * method typically is invoked when back-to-back circles are needed on the
     * same path and thus applying transforms to move the center of each circle
     * from the origin is not an option. Generally this method should be
     * invoked before any translational or rotational transforms are applied to
     * the overall path.
     *
     * @param path
     *            The path to which we will add the circle graphics
     * @param left
     *            The x-axis offset of the upper left corner of the bounding box
     *            for the circle
     * @param top
     *            The y-axis offset of the upper left corner of the bounding box
     *            for the circle
     * @param diameter
     *            The diameter of the circle
     */
    public static void drawCircle( final Path path,
                                   final double left,
                                   final double top,
                                   final double diameter ) {
        final double width = diameter;
        final double height = diameter;
        drawEllipse( path, left, top, width, height );
    }

    /**
     * This method adds a generic crosshair to an existing list of shapes,
     * centered at the origin. Generally this method should be applied before
     * any translational or rotational transforms on the full list of shapes.
     *
     * @param crosshairGraphics
     *            The list of shapes to which we will add the crosshair graphics
     * @param crosshairDimension
     *            The length of each of the two crossing lines that make up the
     *            crosshair; also the length of the side of its bounding box
     */
    public static void drawCrosshair( final List< Shape > crosshairGraphics,
                                      final double crosshairDimension ) {
        // Draw the crosshair as two separate orthogonal lines, like a cross.
        final double radialLength = 0.5d * crosshairDimension;
        drawLine( crosshairGraphics, -radialLength, 0.0d, radialLength, 0.0d );
        drawLine( crosshairGraphics, 0.0d, -radialLength, 0.0d, radialLength );
    }

    /**
     * This method adds a generic crosshair to an existing path, centered at
     * the origin. Generally this method should be applied before any
     * translational or rotational transforms on the overall path, adjusted by
     * an Affine Transform.
     *
     * @param pathElements
     *            The list of path elements to which we will add the crosshair
     *            graphics
     * @param affine
     *            The Affine Transform to use for adjusting the start and end
     *            points of the crosshair
     * @param crosshairDimension
     *            The length of each of the two crossing lines that make up the
     *            crosshair; also the length of the side of its bounding box
     */
    public static void drawCrosshair( final ObservableList< PathElement > pathElements,
                                      final Affine affine,
                                      final double crosshairDimension ) {
        // Draw the crosshair as two separate orthogonal lines, like a cross.
        final double radialLength = 0.5d * crosshairDimension;
        drawLine( pathElements, affine, -radialLength, 0.0d, radialLength, 0.0d );
        drawLine( pathElements, affine, 0.0d, -radialLength, 0.0d, radialLength );
    }

    /**
     * This method adds a generic crosshair to an existing path, centered at
     * the origin. Generally this method should be applied before any
     * translational or rotational transforms on the overall path.
     *
     * @param pathElements
     *            The list of path elements to which we will add the crosshair
     *            graphics
     * @param crosshairDimension
     *            The length of each of the two crossing lines that make up the
     *            crosshair; also the length of the side of its bounding box
     */
    public static void drawCrosshair( final ObservableList< PathElement > pathElements,
                                      final double crosshairDimension ) {
        // Draw the crosshair as two separate orthogonal lines, like a cross.
        final double radialLength = 0.5d * crosshairDimension;
        pathElements.add( new MoveTo( -radialLength, 0.0d ) );
        pathElements.add( new LineTo( radialLength, 0.0d ) );
        pathElements.add( new MoveTo( 0.0d, -radialLength ) );
        pathElements.add( new LineTo( 0.0d, radialLength ) );
    }

    /**
     * This method adds a generic crosshair to an existing path, centered at
     * the origin. Generally this method should be applied before any
     * translational or rotational transforms on the overall path, adjusted by
     * an Affine Transform.
     *
     * @param path
     *            The path to which we will add the crosshair graphics
     * @param affine
     *            The Affine Transform to use for adjusting the start and end
     *            points of the crosshair
     * @param crosshairDimension
     *            The length of each of the two crossing lines that make up the
     *            crosshair; also the length of the side of its bounding box
     */
    public static void drawCrosshair( final Path path,
                                      final Affine affine,
                                      final double crosshairDimension ) {
        // Draw the crosshair as two separate orthogonal lines, like a cross.
        final ObservableList< PathElement > pathElements = path.getElements();
        drawCrosshair( pathElements, affine, crosshairDimension );
    }

    /**
     * This method adds a generic crosshair to an existing path, centered at
     * the origin. Generally this method should be applied before any
     * translational or rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the crosshair graphics
     * @param crosshairDimension
     *            The length of each of the two crossing lines that make up the
     *            crosshair; also the length of the side of its bounding box
     */
    public static void drawCrosshair( final Path path, final double crosshairDimension ) {
        // Draw the crosshair as two separate orthogonal lines, like a cross.
        final ObservableList< PathElement > pathElements = path.getElements();
        drawCrosshair( pathElements, crosshairDimension );
    }

    /**
     * This method adds a crosshair with circle to an existing list of shapes,
     * centered at the origin. Generally this method should be applied before
     * any translational or rotational transforms on the full list of shapes.
     *
     * @param crosshairGraphics
     *            The list of shapes to which we will add the crosshair graphics
     * @param crosshairDiameter
     *            The diameter of the crosshair circle; also the length of the
     *            side of its bounding box
     */
    public static void drawCrosshairCircle( final List< Shape > crosshairGraphics,
                                            final double crosshairDiameter ) {
        // First draw a crosshair circle graphic using the given diameter.
        // NOTE: The circle should be empty vs. filled.
        drawCircle( crosshairGraphics, crosshairDiameter, false );

        // Now draw the generic crosshair cross graphic.
        final double crosshairDimension = crosshairDiameter * 1.5d;
        drawCrosshair( crosshairGraphics, crosshairDimension );
    }

    /**
     * This method adds a crosshair with circle to an existing path, centered
     * at the origin. Generally this method should be applied before any
     * translational or rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the crosshair graphics
     * @param crosshairDiameter
     *            The diameter of the crosshair circle; also the length of the
     *            side of its bounding box
     */
    public static void drawCrosshairCircle( final Path path, final double crosshairDiameter ) {
        // First draw a crosshair circle graphic using the given diameter.
        drawCircle( path, crosshairDiameter );

        // Now draw the generic crosshair cross graphic.
        final double crosshairDimension = crosshairDiameter * 1.5d;
        drawCrosshair( path, crosshairDimension );
    }

    /**
     * This method adds an ellipse to an existing list of shapes, centered at
     * the origin, with no rotation. Generally this method should be applied
     * before any translational or rotational transforms on the full list of
     * shapes.
     *
     * @param circleGraphics
     *            The list of shapes to which we will add the ellipse graphics
     * @param width
     *            The width of the bounding box for the ellipse
     * @param height
     *            The height of the bounding box for the ellipse
     * @param fill
     *            Flag for whether to fill the ellipse
     */
    public static void drawEllipse( final List< Shape > circleGraphics,
                                    final double width,
                                    final double height,
                                    final boolean fill ) {
        final double left = -0.5d * width;
        final double top = -0.5d * height;
        drawEllipse( circleGraphics, left, top, width, height, fill );
    }

    /**
     * This method adds an ellipse to an existing list of shapes, positioned
     * at the upper left corner of the bounding box of the ellipse, with no
     * rotation. This method typically is invoked when back-to-back ellipses
     * are needed on the same path and thus applying transforms to move the
     * center of each ellipse from the origin is not an option. Generally this
     * method should be applied before any translational or rotational
     * transforms on the full list of shapes.
     *
     * @param ellipseGraphics
     *            The list of shapes to which we will add the ellipse graphics
     * @param left
     *            The x-axis offset of the upper left corner of the bounding box
     *            for the ellipse
     * @param top
     *            The y-axis offset of the upper left corner of the bounding box
     *            for the ellipse
     * @param width
     *            The width of the bounding box for the ellipse
     * @param height
     *            The height of the bounding box for the ellipse
     * @param fill
     *            Flag for whether to fill the ellipse
     */
    public static void drawEllipse( final List< Shape > ellipseGraphics,
                                    final double left,
                                    final double top,
                                    final double width,
                                    final double height,
                                    final boolean fill ) {
        final double arcRadiusX = 0.5d * width;
        final double arcRadiusY = 0.5d * height;
        final double arcCenterX = left + arcRadiusX;
        final double arcCenterY = top + arcRadiusY;

        // Make sure we only show the outline and not the interior.
        // NOTE: The ellipse may not show up if the fill is null, but is black
        // by default, so we'll have to live with setting to mid-gray for now.
        final Ellipse ellipse = new Ellipse( arcCenterX, arcCenterY, arcRadiusX, arcRadiusY );
        if ( fill ) {
            ellipse.setFill( ColorConstants.NEUTRAL_FILL );
        }
        else {
            ellipse.setFill( null );
        }

        ellipseGraphics.add( ellipse );
    }

    /**
     * This method adds an ellipse to an existing path, centered at the
     * origin, with no rotation. Generally this method should be applied
     * before any translational or rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the ellipse graphics
     * @param width
     *            The width of the bounding box for the ellipse
     * @param height
     *            The height of the bounding box for the ellipse
     */
    public static void drawEllipse( final Path path, final double width, final double height ) {
        final double left = -0.5d * width;
        final double top = -0.5d * height;
        drawEllipse( path, left, top, width, height );
    }

    /**
     * This method adds an ellipse to an existing path, positioned at the
     * upper left corner of the bounding box of the ellipse, with no rotation.
     * This method typically is invoked when back-to-back ellipses are needed
     * on the same path and thus applying transforms to move the center of each
     * ellipse from the origin is not an option. Generally this method should
     * be applied before any translational or rotational transforms on the
     * overall path.
     *
     * @param path
     *            The path to which we will add the ellipse graphics
     * @param left
     *            The x-axis offset of the upper left corner of the bounding box
     *            for the ellipse
     * @param top
     *            The y-axis offset of the upper left corner of the bounding box
     *            for the ellipse
     * @param width
     *            The width of the bounding box for the ellipse
     * @param height
     *            The height of the bounding box for the ellipse
     */
    public static void drawEllipse( final Path path,
                                    final double left,
                                    final double top,
                                    final double width,
                                    final double height ) {
        final ObservableList< PathElement > pathElements = path.getElements();
        final double arcRadiusX = 0.5d * width;
        final double arcRadiusY = 0.5d * height;
        final double firstArcBeginX = left + arcRadiusX;
        final double firstArcBeginY = top;
        final double firstArcEndX = firstArcBeginX;
        final double firstArcEndY = firstArcBeginY + height;
        pathElements.add( new MoveTo( firstArcBeginX, firstArcBeginY ) );
        pathElements.add( new ArcTo( arcRadiusX,
                                     arcRadiusY,
                                     0.0d,
                                     firstArcEndX,
                                     firstArcEndY,
                                     true,
                                     true ) );
        pathElements.add( new ArcTo( arcRadiusX,
                                     arcRadiusY,
                                     0.0d,
                                     firstArcBeginX,
                                     firstArcBeginY,
                                     true,
                                     true ) );
    }

    /**
     * This method adds a generic line to an list of shapes, using the
     * specified origin and terminus.
     *
     * @param lineGraphics
     *            The list of shapes to which we will add the line graphics
     * @param startX
     *            The x-axis coordinate of the origin of the line
     * @param startY
     *            The y-axis coordinate of the origin of the line
     * @param endX
     *            The x-axis coordinate of the terminus of the line
     * @param endY
     *            The y-axis coordinate of the terminus of the line
     */
    public static void drawLine( final List< ? super Line > lineGraphics,
                                 final double startX,
                                 final double startY,
                                 final double endX,
                                 final double endY ) {
        // Make the Line as a basic Shape Node.
        final Line line = new Line( startX, startY, endX, endY );

        // For simple line graphics that do not infer area closure, only the
        // centered stroke avoids effective doubling of intended stroke width.
        line.setStrokeType( StrokeType.CENTERED );

        // Butt end caps improve perceived regularity of highlight dash patterns
        // and also make it less likely that an empty gap will be the final mark
        // for a graphic and thus cause confusion over its extrusion.
        line.setStrokeLineCap( StrokeLineCap.BUTT );

        // Add the line graphics to the provided shape collection.
        lineGraphics.add( line );
    }

    /**
     * This method adds a generic line to an existing path, using the
     * specified origin and terminus.
     *
     * @param pathElements
     *            The path elements to which we will add the line graphics
     * @param affine
     *            The Affine Transform to use for adjusting the start and end
     *            points of the line
     * @param startX
     *            The x-axis coordinate of the origin of the line
     * @param startY
     *            The y-axis coordinate of the origin of the line
     * @param endX
     *            The x-axis coordinate of the terminus of the line
     * @param endY
     *            The y-axis coordinate of the terminus of the line
     */
    public static void drawLine( final ObservableList< PathElement > pathElements,
                                 final Affine affine,
                                 final double startX,
                                 final double startY,
                                 final double endX,
                                 final double endY ) {
        // Draw the line as a transformed MoveTo/LineTo pair.
        final Point2D startPoint = affine.transform( startX, startY );
        pathElements.add( new MoveTo( startPoint.getX(), startPoint.getY() ) );

        final Point2D endPoint = affine.transform( endX, endY );
        pathElements.add( new LineTo( endPoint.getX(), endPoint.getY() ) );
    }

    /**
     * This method adds a generic line to an existing path, using the
     * specified origin and terminus.
     *
     * @param pathElements
     *            The path elements to which we will add the line graphics
     * @param startX
     *            The x-axis coordinate of the origin of the line
     * @param startY
     *            The y-axis coordinate of the origin of the line
     * @param endX
     *            The x-axis coordinate of the terminus of the line
     * @param endY
     *            The y-axis coordinate of the terminus of the line
     */
    public static void drawLine( final ObservableList< PathElement > pathElements,
                                 final double startX,
                                 final double startY,
                                 final double endX,
                                 final double endY ) {
        // Draw the line as a simple MoveTo/LineTo pair.
        pathElements.add( new MoveTo( startX, startY ) );
        pathElements.add( new LineTo( endX, endY ) );
    }

    /**
     * This method adds a generic line to an existing path, using the
     * specified origin and terminus, adjusted by an Affine Transform.
     *
     * @param path
     *            The path to which we will add the line graphics
     * @param affine
     *            The Affine Transform to use for adjusting the start and end
     *            points of the line
     * @param startX
     *            The x-axis coordinate of the origin of the line
     * @param startY
     *            The y-axis coordinate of the origin of the line
     * @param endX
     *            The x-axis coordinate of the terminus of the line
     * @param endY
     *            The y-axis coordinate of the terminus of the line
     */
    public static void drawLine( final Path path,
                                 final Affine affine,
                                 final double startX,
                                 final double startY,
                                 final double endX,
                                 final double endY ) {
        // Draw the line as a simple MoveTo/LineTo pair.
        final ObservableList< PathElement > pathElements = path.getElements();
        drawLine( pathElements, affine, startX, startY, endX, endY );
    }

    /**
     * This method adds a generic line to an existing path, using the
     * specified origin and terminus.
     *
     * @param path
     *            The path to which we will add the line graphics
     * @param startX
     *            The x-axis coordinate of the origin of the line
     * @param startY
     *            The y-axis coordinate of the origin of the line
     * @param endX
     *            The x-axis coordinate of the terminus of the line
     * @param endY
     *            The y-axis coordinate of the terminus of the line
     */
    public static void drawLine( final Path path,
                                 final double startX,
                                 final double startY,
                                 final double endX,
                                 final double endY ) {
        // Draw the line as a simple MoveTo/LineTo pair.
        final ObservableList< PathElement > pathElements = path.getElements();
        drawLine( pathElements, startX, startY, endX, endY );
    }

    public static void drawPolygon( final List< Shape > polygonGraphics,
                                    final double[] xPts,
                                    final double[] yPts ) {
        // NOTE: Substituting Polyline for now, as Polygon isn't showing up.
        final int numberOfVertices = FastMath.min( xPts.length, yPts.length );
        final List< Double > coordinates = new ArrayList<>( 2 * ( numberOfVertices + 1 ) );
        for ( int i = 0; i < numberOfVertices; i++ ) {
            coordinates.add( Double.valueOf( xPts[ i ] ) );
            coordinates.add( Double.valueOf( yPts[ i ] ) );
        }
        coordinates.add( Double.valueOf( xPts[ 0 ] ) );
        coordinates.add( Double.valueOf( yPts[ 0 ] ) );

        // NOTE: Substituting Polyline for now, as Polygon isn't showing up.
        // final Polygon polygon = new Polygon();
        final Polyline polygon = new Polyline();
        final ObservableList< Double > polygonCoordinates = polygon.getPoints();
        polygonCoordinates.setAll( coordinates );

        // By default, Polygons are filled, but we only want a wireframe.
        // NOTE: Not needed while we are doing this via a Polyline.
        // polygon.setFill( null );

        polygonGraphics.add( polygon );
    }

    public static void drawPolyline( final List< Shape > polylineGraphics,
                                     final double[] xPts,
                                     final double[] yPts ) {
        final int numberOfVertices = FastMath.min( xPts.length, yPts.length );
        final List< Double > coordinates = new ArrayList<>( 2 * numberOfVertices );
        for ( int i = 0; i < numberOfVertices; i++ ) {
            coordinates.add( Double.valueOf( xPts[ i ] ) );
            coordinates.add( Double.valueOf( yPts[ i ] ) );
        }

        final Polyline polyline = new Polyline();
        final ObservableList< Double > polylineCoordinates = polyline.getPoints();
        polylineCoordinates.setAll( coordinates );

        polylineGraphics.add( polyline );
    }

    /**
     * This method adds a generic x-axis ray to an existing list of shapes,
     * starting at the origin. It is hoped that eventually the JavaFX API will
     * be extended to support rays, but for now we must do this using a bounded
     * line. Generally this method should be applied before any translational
     * or rotational transforms on the full list of shapes.
     *
     * @param rayGraphics
     *            The list of shapes to which we will add the x-axis ray
     * @param rayLength
     *            The bounded length of the ray, as a positive offset
     */
    public static void drawXRay( final List< Shape > rayGraphics,
                                 final double startX,
                                 final double startY,
                                 final double rayLength ) {
        // Draw the x-axis ray as a simple line of given ray length.
        final double endX = startX + rayLength;
        final double endY = startY;
        drawLine( rayGraphics, startX, startY, endX, endY );
    }

    /**
     * This method adds a generic x-axis ray to an existing path, starting at
     * the origin. It is hoped that eventually the JavaFX API will be extended
     * to support rays, but for now we must do this using a bounded line.
     * Generally this method should be applied before any translational or
     * rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the x-axis ray
     * @param rayLength
     *            The bounded length of the ray, as a positive offset
     */
    public static void drawXRay( final Path path,
                                 final double startX,
                                 final double startY,
                                 final double rayLength ) {
        // Draw the x-axis ray as a simple line of given ray length.
        final double endX = startX + rayLength;
        final double endY = startY;
        drawLine( path, startX, startY, endX, endY );
    }

    /**
     * This method adds a generic y-axis ray to an existing list of shapes,
     * starting at the origin. It is hoped that eventually the JavaFX API will
     * be extended to support rays, but for now we must do this using a bounded
     * line. Generally this method should be applied before any translational
     * or rotational transforms on the list of shapes.
     *
     * @param rayGraphics
     *            The list of shapes to which we will add the y-axis ray
     * @param rayLength
     *            The bounded length of the ray, as a positive offset
     */
    public static void drawYRay( final List< Shape > rayGraphics,
                                 final double startX,
                                 final double startY,
                                 final double rayLength ) {
        // Draw the y-axis ray as a simple line of given ray length.
        final double endX = startX;
        final double endY = startY + rayLength;
        drawLine( rayGraphics, startX, startY, endX, endY );
    }

    /**
     * This method adds a generic y-axis ray to an existing path, starting at
     * the origin. It is hoped that eventually the JavaFX API will be extended
     * to support rays, but for now we must do this using a bounded line.
     * Generally this method should be applied before any translational or
     * rotational transforms on the overall path.
     *
     * @param path
     *            The path to which we will add the y-axis ray
     * @param rayLength
     *            The bounded length of the ray, as a positive offset
     */
    public static void drawYRay( final Path path,
                                 final double startX,
                                 final double startY,
                                 final double rayLength ) {
        // Draw the y-axis ray as a simple line of given ray length.
        final double endX = startX;
        final double endY = startY + rayLength;
        drawLine( path, startX, startY, endX, endY );
    }

    // Get the generic Crosshair graphics at the origin.
    public static List< Shape > getCrosshairGraphics( final double crosshairDimension ) {
        // We need to use a list of shapes with multiple visual elements, as we
        // have several categories of graphics that make up the Crosshair.
        final List< Shape > crosshairGraphics = new ArrayList<>();

        // Draw the standard Crosshair circle with a cross.
        drawCrosshairCircle( crosshairGraphics, crosshairDimension );

        return crosshairGraphics;
    }

    // Get generic Crosshair graphics, at a specific reference point location.
    public static List< Shape > getCrosshairGraphics( final Pair< Double, Double > referencePoint,
                                                      final double crosshairDimension ) {
        // Draw the Crosshair graphics at the origin.
        final List< Shape > crosshairGraphics = getCrosshairGraphics( crosshairDimension );

        // Transform the Crosshair from the origin to a specific location.
        crosshairGraphics
                .forEach( crosshair -> moveShapeToReferencePoint( crosshair, referencePoint ) );

        // Set all Crosshair graphics line strokes to Magenta.
        crosshairGraphics.forEach( crosshair -> crosshair.setStroke( Color.MAGENTA ) );

        return crosshairGraphics;
    }

    // Get generic Crosshair graphics, at a specific reference point location.
    public static List< Shape > getCrosshairGraphics( final Vector2D referencePoint,
                                                      final double crosshairDimension ) {
        // Draw the Crosshair graphics at the origin.
        final List< Shape > crosshairGraphics = getCrosshairGraphics( crosshairDimension );

        // Transform the Crosshair from the origin to a specific location.
        crosshairGraphics
                .forEach( crosshair -> moveShapeToReferencePoint( crosshair, referencePoint ) );

        // Set all Crosshair graphics line strokes to Magenta.
        crosshairGraphics.forEach( crosshair -> crosshair.setStroke( Color.MAGENTA ) );

        return crosshairGraphics;
    }

    /**
     *
     * @param affineTransform
     *            a transform to apply to the crosshairs before returning
     * @param diameter
     *            the diameter of the crosshairs
     * @return a crosshairs graphic
     * @see #getCrosshairs2D(double, double, double)
     */
    public static Shape getCrosshairs2D( final Affine affineTransform, final double diameter ) {
        // Define a crosshairs graphic in entity space and immediately
        // translate each sub-path from entity space to model space.
        // NOTE: Since we are in model space, we need to think of the ellipse
        // origin as the lower left vs. upper left "corner" of the bounding box!
        final Circle circle = new Circle( 0.5d * diameter );
        circle.getTransforms().add( affineTransform );

        final double targetDimension = diameter * 1.5;
        final Line targetX =
                           new Line( -0.5d * targetDimension, 0.0d, 0.5d * targetDimension, 0.0d );
        targetX.getTransforms().add( affineTransform );

        // NOTE: At this time (20151020) it is unknown whether union will
        // produce the correct graphics.
        Shape result = Shape.union( circle, targetX );

        final Line targetY =
                           new Line( 0.0d, -0.5d * targetDimension, 0.0d, 0.5d * targetDimension );
        targetY.getTransforms().add( affineTransform );
        result = Shape.union( result, targetY );

        return result;
    }

    /**
     *
     * @param x
     *            x translation
     * @param y
     *            y translation
     * @param diameter
     *            the diameter of the crosshairs
     * @return a crosshairs graphic
     * @see #getCrosshairs2D
     */
    public static Shape getCrosshairs2D( final double x, final double y, final double diameter ) {
        // Get a translational affine transformation matrix for translating from
        // entity space to model space.
        final Affine affineTransform = new Affine();
        affineTransform.appendTranslation( x, y );

        // Return the crosshairs defined at the specified location.
        return getCrosshairs2D( affineTransform, diameter );
    }

    /**
     * Translate a shape from the origin to a specified reference point.
     *
     * @param shape
     *            The untransformed shape in object space
     */
    public static void moveShapeToReferencePoint( final Shape shape,
                                                  final Pair< Double, Double > referencePoint ) {
        final Transform affineTransform = GeometryUtilities
                .getReferencePointTransform( referencePoint );
        shape.getTransforms().add( affineTransform );
    }

    /**
     * Translate a shape from the origin to a specified reference point.
     *
     * @param shape
     *            The untransformed shape in object space
     */
    public static void moveShapeToReferencePoint( final Shape shape,
                                                  final Vector2D referencePoint ) {
        final Transform affineTransform = GeometryUtilities
                .getReferencePointTransform( referencePoint );
        shape.getTransforms().add( affineTransform );
    }

}
