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
package com.mhschmieder.fxgraphics.input;

/**
 * This class stores contextual information and settings for Mouse Drag and
 * related handling. All values are stored in pixels, for mouse interaction.
 * <p>
 * TODO: Add the ALT key detection etc., and change to more general name?
 *  Possibly add the drag box and its handling as well?
 */
public class MouseDragContext {

    public double  _dragOriginX;
    public double  _dragOriginY;

    public double  _dragDestinationX;
    public double  _dragDestinationY;

    public double  _dragDeltaX;
    public double  _dragDeltaY;

    public boolean _valid;

    public MouseDragContext() {
        _dragOriginX = 0.0d;
        _dragOriginY = 0.0d;
        
        _dragDestinationX = 0.0d;
        _dragDestinationY = 0.0d;
        
        _dragDeltaX = 0.0d;
        _dragDeltaY = 0.0d;
        
        _valid = false;
    }

    // This is the initializer when interacting with Drag events.
    // NOTE: The first Drag Event is thrown out, so we zero the destination.
    //  This ensures that the deltas stay scaled properly on each succession.
    public void initializeDrag( final double firstX, final double firstY ) {
        _dragOriginX = firstX;
        _dragOriginY = firstY;

        _dragDestinationX = 0.0d;
        _dragDestinationY = 0.0d;

        _valid = true;
    }
    
    /**
     * Drags the contextual origin and destination by a computed delta and
     * caches that delta for the caller to use when converting to meters etc.
     * 
     * @param cursorCoordinatesPixels
     *            The current coordinates of the cursor, in pixels
     */
    public void dragTo( final ClickLocation cursorCoordinatesPixels ) {
        // Update the x and y deltas for the drag and drop operation by 
        // computing the x and y differentials between the current mouse 
        // location and the previous mouse location, in pixels.
        _dragDeltaX = cursorCoordinatesPixels.x - _dragOriginX;
        _dragDeltaY = cursorCoordinatesPixels.y - _dragOriginY;

        // Try for a coarser resolution, to improve performance.
        // NOTE: This causes a perception of lag or jumpiness, vs. smooth and
        //  high resolution dragging, and the performance issue no longer seems
        //  present even with large selection sets being dragged, as other
        //  improvements were made since this performance fix was first
        //  implemented. If we re-enable, perhaps use a smaller threshold.
        // if ( ( FastMath.abs( _dragDeltaX ) <= 4.0d )
        //         && ( FastMath.abs( _dragDeltaY ) <= 4.0d ) ) {
        //     return;
        // }

        // Replace the Drag Origin with the current cursor coordinates for the
        // next mouse drag/move, as otherwise we accelerate over time.
        _dragOriginX = cursorCoordinatesPixels.x;
        _dragOriginY = cursorCoordinatesPixels.y;

        // Keep track of the cumulative drag deltas calculated from the current
        // mouse cursor movement, to use for node placement.
        _dragDestinationX += _dragDeltaX;
        _dragDestinationY += _dragDeltaY;       
    }

    // This is the initializer when interacting with Move events.
    // NOTE: It is most likely the source will be set to the destination in
    //  such cases, as the first Move Event is not thrown out as with Drag Events
    //  and otherwise would result in an overly large initial delta computation.
    public void initializeMove( final double firstX,
                                final double firstY,
                                final double lastX,
                                final double lastY ) {
        _dragOriginX = firstX;
        _dragOriginY = firstY;

        _dragDestinationX = lastX;
        _dragDestinationY = lastY;

        _valid = true;
    }

    public void invalidate() {
        _valid = false;
    }

    @Override
    public String toString() {
        return "MouseDragContext[lastX:" + _dragDestinationX 
                + ", lastY:" + _dragDestinationY
                + ",\n\tfirstX:" + _dragOriginX 
                + ", firstY:" + _dragOriginY
                + ", valid:" + _valid + "]";
    }
}
