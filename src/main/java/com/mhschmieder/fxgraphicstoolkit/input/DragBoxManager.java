/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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
package com.mhschmieder.fxgraphicstoolkit.input;

import java.util.List;

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.fxgraphicstoolkit.render.HighlightUtilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * A stateful manager for drag-box handling via the mouse.
 * <p>
 * As not all applications are in Cartesian Space (e.g., some are in world
 * coordinates, whose distances change with latitude), this class works
 * strictly in pixels, for now. 
 * <p>
 * If I can find a good way to abstract the handling of application-local 
 * units via an interface and/or method overrides, I will do so.
 */
public class DragBoxManager {

    /**
     * Declare a flag to keep track of whether we need to pre-process for a drag
     * operation.
     */
    public boolean _dragPreprocess;

    /** Declare a flag to keep track of whether to show the Drag Box. */
    public boolean _dragBoxActive;


    /**
     * Declare a variable to keep track of the Drag Box origin (initial click).
     */
    public Point2D _dragBoxOriginPixels;

    /**
     * Declare a Bounds for the Drag Box, for efficient containment tests.
     */
    public Bounds _dragBoxPixels;

    /**
     * Declare a visual element (and associated group container), to represent
     * the Drag Box.
     */
    public Rectangle _dragBoxElement;
    public Group _dragBoxGroup;

    public DragBoxManager() {
        _dragPreprocess = true;
        _dragBoxActive = false;

        _dragBoxOriginPixels = new Point2D( 0.0d, 0.0d );
        _dragBoxPixels = new BoundingBox( 0.0d, 0.0d, 0.0d, 0.0d );
    }

    // TODO: Switch to JavaFX gesture-based Drag Boxes from this old AWT way?
    public void initDragBox( final ClickLocation clickPointPixels ) {
        _dragBoxActive = true;

        final double xPixels = clickPointPixels.x;
        final double yPixels = clickPointPixels.y;

        _dragBoxOriginPixels = new Point2D( xPixels, yPixels );
        _dragBoxPixels = new BoundingBox( xPixels, yPixels, 0.0d, 0.0d );

        // Show the Drag Box in the Scene Graph.
        _dragBoxGroup.setVisible( true );
    }

    public void makeDragBoxGraphics( final Color mouseToolColor ) {
        // Make the Rectangle associated with the Drag Box.
        final double x = _dragBoxPixels.getMinX();
        final double y = _dragBoxPixels.getMinY();
        final double width = FastMath.abs( _dragBoxPixels.getMaxX() - _dragBoxPixels.getMinX() );
        final double height = FastMath.abs( _dragBoxPixels.getMaxY() - _dragBoxPixels.getMinY() );
        _dragBoxElement = new Rectangle( x, y, width, height );

        // Set all Mouse Tool graphics line strokes to an appropriate hue.
        _dragBoxElement.setStroke( mouseToolColor );

        // Set to 15% opacity fill for the interior, so that the user can more
        // easily see what is selected by the Drag Box.
        final Color mouseToolFill = new Color( mouseToolColor.getRed(),
                                               mouseToolColor.getGreen(),
                                               mouseToolColor.getBlue(),
                                               0.15d );
        _dragBoxElement.setFill( mouseToolFill );
        _dragBoxElement.setStrokeWidth( 1.5d );

        // For simple line graphics that do not infer area closure, only the
        // centered stroke avoids effective doubling of intended stroke width.
        _dragBoxElement.setStrokeType( StrokeType.CENTERED );

        // Butt end caps improve perceived regularity of the highlight dash
        // pattern and also make it less likely that an empty gap will be the
        // final mark for a graphic and thus cause confusion over its extrusion.
        _dragBoxElement.setStrokeLineCap( StrokeLineCap.BUTT );

        // Grab the dash pattern to use for highlighting.
        final List< Double > highlightDashPattern = HighlightUtilities
                .getHighlightDashPattern( 1.25d );

        // Apply the highlighting pattern, as the Drag Box is always selected.
        HighlightUtilities.applyHighlight( _dragBoxElement, true, highlightDashPattern );

        // Hide the Drag Box Group until it is needed.
        _dragBoxGroup = new Group( _dragBoxElement );
        _dragBoxGroup.setVisible( false );
    }

    public void updateDragBoxSize( final ClickLocation mouseLocationPixels ) {
        // Update the Drag Box size based on the deltas in all dimensions.
        // NOTE: Due to the possibility of dynamically swapping the vertices
        //  based on drag direction, we have to take both min and max to be
        //  quadrant sensitive, but stay aware of the API's upper left bias.
        final double ulxPixels = FastMath.min( _dragBoxOriginPixels.getX(), 
                                               mouseLocationPixels.x );
        final double ulyPixels = FastMath.min( _dragBoxOriginPixels.getY(), 
                                               mouseLocationPixels.y );
        final double lrxPixels = FastMath.max( _dragBoxOriginPixels.getX(), 
                                               mouseLocationPixels.x );
        final double lryPixels = FastMath.max( _dragBoxOriginPixels.getY(), 
                                               mouseLocationPixels.y );

        final double widthPixels = FastMath.abs( ulxPixels - lrxPixels );
        final double heightPixels = FastMath.abs( ulyPixels - lryPixels );

        _dragBoxPixels = new BoundingBox( ulxPixels, ulyPixels, widthPixels, heightPixels );

        // Now update the visual element associated with the Drag Box.
        updateDragBoxGraphics();
    }

    public void updateDragBoxGraphics() {
        // Update the dimensions of the Rectangle associated with the Drag Box.
        final double x = _dragBoxPixels.getMinX();
        final double y = _dragBoxPixels.getMinY();
        final double width = FastMath.abs( _dragBoxPixels.getMaxX() - _dragBoxPixels.getMinX() );
        final double height = FastMath.abs( _dragBoxPixels.getMaxY() - _dragBoxPixels.getMinY() );

        _dragBoxElement.setX( x );
        _dragBoxElement.setY( y );
        _dragBoxElement.setWidth( width );
        _dragBoxElement.setHeight( height );
    }
}
