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

import org.apache.commons.math3.util.FastMath;

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.commonstoolkit.util.SystemType;

import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

/**
 * Generalized manager for some of the more common gestures; especially ones
 * such as zoom that can also come from more traditional mouse events.
 */
public class GestureManager {

    protected static final double DEFAULT_SCROLL_DELTA = 1.3d;

    /** Flag for determining whether to support gestures. */
    private boolean gesturesEnabled;

    /** Keep track of the current Scrolling Sensitivity for the Mouse */
    protected ScrollingSensitivity scrollingSensitivity;

    protected double scrollDeltaY;
    protected double scrollScale;

    /** The class that will register and handle the gesture events. */
    public final GestureHandler gestureHandler;

    /** Cache the Client Properties (System Type, Locale, etc.). */
    public ClientProperties clientProperties;
        
    public GestureManager( final GestureHandler pGestureHandler,
                           final ClientProperties pClientProperties ) {
       gestureHandler = pGestureHandler;
       clientProperties = pClientProperties;

       gesturesEnabled = false;
       scrollingSensitivity = ScrollingSensitivity.defaultValue();

       scrollDeltaY = 0.0d;
       scrollScale = 1.0d;
       
       // Register all relevant mouse event handlers related to gestures.
       addMouseEventHandlers();
    }
    
    /**
     * Adds mouse event handlers for gestures such as zooming.
     */
    protected void addMouseEventHandlers() {
        // Add scroll-zoom and pinch-zoom handlers.
        final Node clickableNode = gestureHandler.getClickableContentNode();
        clickableNode.setOnScroll( ( event ) -> scrollZoom( 
                event, gestureHandler.getMouseMode() ) );
        clickableNode.setOnZoom( ( event ) -> pinchZoom( 
                event, gestureHandler.getMouseMode() ) );
   }
    
    public boolean isGesturesEnabled() {
        return gesturesEnabled;
    }

    public void setGesturesEnabled( final boolean pGesturesEnabled ) {
        gesturesEnabled = pGesturesEnabled;
    }

    /**
     * This is a standard getter method for the Scrolling Sensitivity setting.
     *
     * @return The current Scrolling Sensitivity setting
     */
    public ScrollingSensitivity getScrollingSensitivity() {
        return scrollingSensitivity;
    }

    /**
     * Set the new Scrolling Sensitivity for the Mouse Tools.
     *
     * @param pScrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity pScrollingSensitivity ) {
        // Cache the new Scrolling Sensitivity preference.
        scrollingSensitivity = pScrollingSensitivity;
    }

    // NOTE: This is a more traditional scroll wheel handler, but it can also
    //  cover gestures on a touch screen.
    public void scrollZoom( final ScrollEvent event,
                            final MouseToolMode mouseMode ) {
        // Ignore inertia events that happen past scrolling's end.
        if ( event.isInertia() ) {
            return;
        }

        // If Mouse Gestures are disabled, ignore this gesture event.
        if ( !isGesturesEnabled() ) {
            return;
        }

        // If Scrolling Sensitivity is off, then we are supposed to ignore
        // traditional mouse scroll wheel events.
        if ( ScrollingSensitivity.OFF.equals( scrollingSensitivity ) ) {
            return;
        }

        // Transitory Cut, Copy, Paste, should ignore Zoom due to side effects.
        //if ( MouseToolMode.COPY.equals( mouseMode ) 
        //        || MouseToolMode.PASTE.equals( mouseMode ) ) {
        if ( MouseToolMode.COPY.equals( mouseMode ) ) {
            return;
        }

        // Try for slightly coarser resolution (pixels), to improve performance.
        scrollDeltaY = event.getDeltaY();
        if ( FastMath.abs( scrollDeltaY ) < 3.0d ) {
            return;
        }

        // TODO: Finish this new algorithm, and make use of the User Preference
        //  for Scrolling Sensitivity.
        final double scrollDeltaY = DEFAULT_SCROLL_DELTA;
        final double oldScrollScale = scrollScale;
        double newScrollScale = oldScrollScale;
        if ( scrollDeltaY < 0.0d ) {
            newScrollScale /= scrollDeltaY;
        }
        else {
            newScrollScale *= scrollDeltaY;
        }
        double zoomFactor = newScrollScale - oldScrollScale;
        scrollScale = zoomFactor;

        // TODO: Delete this modified old one-line algorithm after finishing
        //  the new algorithm above.
        double zoomBasis = SystemType.MACOS.equals( clientProperties.systemType ) 
                ? 1.0002d 
                : 1.0003d;
        final double zoomDifferential = SystemType.MACOS.equals( clientProperties.systemType )
            ? 0.00015d
            : 0.0002d;
        switch ( scrollingSensitivity ) {
        case COARSE:
            zoomBasis += zoomDifferential;
            break;
        case MEDIUM:
            break;
        case FINE:
            zoomBasis -= zoomDifferential;
            break;
        case OFF:
            break;
        default:
            break;
        }

        // NOTE: The scroll direction convention on macOS tends to be inverted.
        final double zoomExponent = SystemType.MACOS.equals( clientProperties.systemType )
            ? -event.getDeltaY()
            : event.getDeltaY();
        zoomFactor = FastMath.pow( zoomBasis, zoomExponent );

        gestureHandler.zoom( zoomFactor, event.getSceneX(), event.getSceneY() );
    }

    // NOTE: This is a new gesture, not supported by some devices.
    public void pinchZoom( final ZoomEvent event,
                           final MouseToolMode mouseMode ) {
        // If Mouse Gestures are disabled, ignore this gesture event.
        if ( !isGesturesEnabled() ) {
            return;
        }

        // If Scrolling Sensitivity is off, then we are supposed to ignore
        // new zoom gestures (whether from a mouse or a trackpad).
        if ( ScrollingSensitivity.OFF.equals( scrollingSensitivity ) ) {
            return;
        }

        // Transitory Cut, Copy, Paste, should ignore Zoom due to side effects.
        //if ( MouseToolMode.COPY.equals( mouseMode ) 
        //        || MouseToolMode.PASTE.equals( mouseMode ) ) {
        if ( MouseToolMode.COPY.equals( mouseMode ) ) {
            return;
        }

        // Adjust the Zoom Factor based on User Preferences.
        // NOTE: The scroll direction convention on macOS tends to be inverted.
        double zoomFactor = SystemType.MACOS.equals( clientProperties.systemType )
            ? event.getZoomFactor()
            : -event.getZoomFactor();
        final double zoomMultiplier = SystemType.MACOS.equals( clientProperties.systemType )
            ? 3.0d
            : 4.0d;
        switch ( scrollingSensitivity ) {
        case COARSE:
            zoomFactor /= zoomMultiplier;
            break;
        case MEDIUM:
            break;
        case FINE:
            zoomFactor *= zoomMultiplier;
            break;
        case OFF:
            break;
        default:
            break;
        }

        gestureHandler.zoom( zoomFactor, event.getSceneX(), event.getSceneY() );
    }
}
