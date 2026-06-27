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
package com.mhschmieder.fxgraphics.input;

import com.mhschmieder.fxgraphics.render.HighlightUtilities;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import org.apache.commons.math3.util.FastMath;

import java.util.List;

/**
 * A stateful manager for drag-box handling via the mouse, now supporting both
 * rectangular and circular shapes. The origin (initial click) remains fixed
 * when resizing, and for circles it acts as the center.
 * <p>
 * As not all applications are in Cartesian Space (e.g., some are in world
 * coordinates, whose distances change with latitude), this class works
 * strictly in pixels, for now. 
 * <p>
 * If we can find a good way to abstract the handling of application-local
 * units via an interface and/or method overrides, we will do so.
 */
public class DragBoxManager {

    /**
     * Flag to keep track of whether we need to pre-process for drag operations.
     */
    public boolean dragPreprocess;

    /** Flag to keep track of whether to show the Drag Box (i.e., is active). */
    public boolean dragBoxActive;

    /**
     * The origin of the Drag Box (initial click point).
     */
    public Point2D dragBoxOriginPixels;

    /**
     * Current Bounds (pixels) of the Drag Box, for efficient containment tests.
     */
    public Bounds dragBoxPixels;

    /**
     * Current Drag Shape type to use for representing the Drag Box.
     */
    private DragShape dragShape;

    /**
     * Visual representation of the Drag Box.
     */
    public Shape dragBoxShape;

    /**
     * Associated Group container for the Drag Box shape.
     */
    public Group dragBoxGroup;

    public DragBoxManager() {
        dragPreprocess = true;
        dragBoxActive = false;

        dragBoxOriginPixels = new Point2D( 0.0d, 0.0d );
        dragBoxPixels = new BoundingBox( 0.0d, 0.0d, 0.0d, 0.0d );

        dragShape = DragShape.RECTANGLE;

        dragBoxGroup = new Group();
        dragBoxGroup.setVisible( false );
    }

    /**
     * Change the drag shape type and rebuild the graphics.
     * @param shape target DragShape
     * @param mouseToolColor color for the outline/fill
     */
    public void setDragShape( final DragShape shape,
                              final Color mouseToolColor ) {
        dragShape = shape;
        makeDragBoxGraphics( mouseToolColor );
    }

    // TODO: Switch to JavaFX gesture-based Drag Boxes from this old AWT way?
    public void initDragBox( final ClickLocation clickPointPixels ) {
        dragBoxActive = true;

        final double xPixels = clickPointPixels.x;
        final double yPixels = clickPointPixels.y;

        dragBoxOriginPixels = new Point2D( xPixels, yPixels );
        dragBoxPixels = new BoundingBox( xPixels, yPixels, 0.0d, 0.0d );

        // Build initial graphics at the origin.
        if ( dragBoxShape != null ) {
            makeDragBoxGraphics( ( Color ) dragBoxShape.getStroke() );
        }

        // Show the Drag Box in the Scene Graph.
        dragBoxGroup.setVisible( true );
    }

    /**
     * Create or recreate the visual shape at the drag origin.
     * @param mouseToolColor outline and fill color
     */
    public void makeDragBoxGraphics( final Color mouseToolColor ) {
        dragBoxGroup.getChildren().clear();

        switch ( dragShape ) {
            case RECTANGLE -> dragBoxShape = new Rectangle(
                    dragBoxOriginPixels.getX(),
                    dragBoxOriginPixels.getY(),
                    0.0d,
                    0.0d );
            case CIRCLE -> dragBoxShape = new Circle(
                    dragBoxOriginPixels.getX(),
                    dragBoxOriginPixels.getY(),
                    0.0d );
        }

        // Make the Rectangle associated with the Drag Box.
        // NOTE: This was the old way, before we supported two drag shapes.
        /*
        final double x = dragBoxPixels.getMinX();
        final double y = dragBoxPixels.getMinY();
        final double width = FastMath.abs(
                dragBoxPixels.getMaxX() - dragBoxPixels.getMinX() );
        final double height = FastMath.abs(
                dragBoxPixels.getMaxY() - dragBoxPixels.getMinY() );
        dragBoxShape = new Rectangle( x, y, width, height );
        */

        // Set all Mouse Tool graphics line strokes to an appropriate hue.
        dragBoxShape.setStroke( mouseToolColor );

        // Set to 15% opacity fill for the interior, so that the user can more
        // easily see what is selected by the Drag Box.
        final Color mouseToolFill = new Color( mouseToolColor.getRed(),
                                               mouseToolColor.getGreen(),
                                               mouseToolColor.getBlue(),
                                               0.15d );
        dragBoxShape.setFill( mouseToolFill );
        dragBoxShape.setStrokeWidth( 1.5d );

        // For simple line graphics that do not infer area closure, only the
        // centered stroke avoids effective doubling of intended stroke width.
        dragBoxShape.setStrokeType( StrokeType.CENTERED );

        // Butt-end caps improve perceived regularity of the highlight dash
        // pattern and also make it less likely that an empty gap will be the
        // final mark for a graphic and thus cause confusion over its extrusion.
        dragBoxShape.setStrokeLineCap( StrokeLineCap.BUTT );

        // Grab the dash pattern to use for highlighting.
        final List< Double > highlightDashPattern = HighlightUtilities
                .getHighlightDashPattern( 1.25d );

        // Apply the highlighting pattern, as the Drag Box is always selected.
        HighlightUtilities.applyHighlight(
                dragBoxShape,
                true,
                highlightDashPattern );

        dragBoxGroup.getChildren().add( dragBoxShape );

        // Hide the Drag Box Group until it is needed.
        // TODO: Verify that we should still wait to show the Drag Box.
        dragBoxGroup.setVisible( false );
    }

    /**
     * Update the drag shape's size (and position for rectangle) as the mouse moves.
     * @param mouseLocationPixels current mouse coordinates
     */
    public void updateDragBoxSize( final ClickLocation mouseLocationPixels ) {
        double dx = mouseLocationPixels.x - dragBoxOriginPixels.getX();
        double dy = mouseLocationPixels.y - dragBoxOriginPixels.getY();

        switch ( dragShape ) {
            case RECTANGLE -> {
                double width = FastMath.abs( dx );
                double height = FastMath.abs( dy );
                double x = ( dx >= 0.0d )
                        ? dragBoxOriginPixels.getX()
                        : dragBoxOriginPixels.getX() - width;
                double y = ( dy >= 0.0d )
                        ? dragBoxOriginPixels.getY()
                        : dragBoxOriginPixels.getY() - height;
                dragBoxPixels = new BoundingBox( x, y, width, height );
            }
            case CIRCLE -> {
                double radius = FastMath.hypot( dx, dy );
                double diameter = radius * 2.0d;
                double x = dragBoxOriginPixels.getX() - radius;
                double y = dragBoxOriginPixels.getY() - radius;
                dragBoxPixels = new BoundingBox( x, y, diameter, diameter );
            }
        }

        // Update the Drag Box size based on the deltas in all dimensions.
        // NOTE: Due to the possibility of dynamically swapping the vertices
        //  based on drag direction, we have to take both min and max to be
        //  quadrant sensitive, but stay aware of the API's upper left bias.
        // NOTE: This was the old way, before we supported two drag shapes. We
        //  may need to consult this logic if the new way doesn't enforce the
        //  upper right corner or otherwise has new edge case issues.
        /*
        final double ulxPixels = FastMath.min( dragBoxOriginPixels.getX(),
                                               mouseLocationPixels.x );
        final double ulyPixels = FastMath.min( dragBoxOriginPixels.getY(),
                                               mouseLocationPixels.y );
        final double lrxPixels = FastMath.max( dragBoxOriginPixels.getX(),
                                               mouseLocationPixels.x );
        final double lryPixels = FastMath.max( dragBoxOriginPixels.getY(),
                                               mouseLocationPixels.y );

        final double widthPixels = FastMath.abs( ulxPixels - lrxPixels );
        final double heightPixels = FastMath.abs( ulyPixels - lryPixels );

        dragBoxPixels = new BoundingBox(
                ulxPixels, ulyPixels, widthPixels, heightPixels );
        */

        // Now update the visual element associated with the Drag Box.
        updateDragBoxGraphics();
    }

    /**
     * Refresh the visual shape based on the current bounds or radius.
     */
    public void updateDragBoxGraphics() {
        // Update the dimensions of the Rectangle associated with the Drag Box.
        switch ( dragBoxShape ) {
            case Rectangle rectangle -> {
                rectangle.setX( dragBoxPixels.getMinX() );
                rectangle.setY( dragBoxPixels.getMinY() );
                rectangle.setWidth( dragBoxPixels.getWidth() );
                rectangle.setHeight( dragBoxPixels.getHeight() );
            }
            case Circle circle -> {
                circle.setCenterX( dragBoxOriginPixels.getX() );
                circle.setCenterY( dragBoxOriginPixels.getY() );
                circle.setRadius( 0.5d * dragBoxPixels.getWidth() );
            }
            default -> throw new IllegalStateException(
                    "Unexpected Drag Box Shape: " + dragBoxShape );
        }
    }

    /**
     * Stop the drag and hide the shape.
     */
    public void clearDragBox() {
        dragBoxActive = false;
        dragBoxShape.setVisible( false );
    }
}
