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

import com.mhschmieder.fxgraphics.shape.ShapeUtilities;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * A stateful manager for rotation handling via the mouse.
 * <p>
 * As not all applications are in Cartesian Space (e.g., some are in world
 * coordinates, whose distances change with latitude), this class works
 * strictly in pixels, but also stores angles in radians for universality. 
 * <p>
 * If I can find a good way to abstract the handling of application-local 
 * units via an interface and/or method overrides, I will do so.
 */
public class RotationManager {

    /**
     * Declare a flag to keep track of whether to show the angle of rotation.
     */
    public boolean _angleOfRotationActive;

    /**
     * Declare variables to keep track of the rotation center point location.
     * <p>
     * NOTE: Angles are stored in computational units (radians).
     */
    public double _rotateReference;
    public double _rotateTheta;
    public double _rotateThetaRelative;

    /**
     * Declare a visual element (and associated group container), to represent
     * the Center of Rotation.
     */
    public Path _centerOfRotationElement;
    public Group _centerOfRotationGroup;

    public RotationManager() {
        _angleOfRotationActive = false;

        _rotateReference = 0.0d;
        _rotateTheta = 0.0d;
        _rotateThetaRelative = 0.0d;
    }
    
    /**
     * Initialize the Rotation parameters, using a passed-in calculated angle.
     * <p>
     * NOTE: As the angle must be pre-calculated using the local units of
     *  the application, it must be passed in rather than derived here.
     * 
     * @param rotateTheta The calculated angle for rotation, in radians
     */
    public void initRotation( final double rotateTheta ) {
        // Trigger the feedback window for angle of rotation (updated
        // during repaint()).
        _angleOfRotationActive = true;

        _rotateTheta = rotateTheta;
        _rotateReference = _rotateTheta;
    }
    
    /**
     * Update the Rotation parameters, using a new passed-in calculated angle.
     * <p>
     * NOTE: As the angle must be pre-calculated using the local units of
     *  the application, it must be passed in rather than derived here.
     * 
     * @param rotateThetaNew The calculated angle for rotation, in radians
     */
    public void updateRotation( final double rotateThetaNew ) {
        _rotateThetaRelative = _rotateTheta - rotateThetaNew;
        _rotateTheta = rotateThetaNew;
    }

    public void makeCenterOfRotationGraphics( final Color mouseToolColor ) {
        // Make the Center of Rotation graphics, as a crosshair.
        _centerOfRotationElement = new Path();

        // TODO: Determine if this needs to be resolution.sensitive.
        final double crosshairDiameter = 16d;
        ShapeUtilities.drawCrosshairCircle( _centerOfRotationElement, crosshairDiameter );

        // Set all Mouse Tool graphics line strokes to an appropriate hue.
        _centerOfRotationElement.setStroke( mouseToolColor );

        _centerOfRotationElement.setFill( null );
        _centerOfRotationElement.setStrokeWidth( 1.5d );

        // For simple line graphics that do not infer area closure, only the
        // centered stroke avoids effective doubling of intended stroke width.
        _centerOfRotationElement.setStrokeType( StrokeType.CENTERED );

        // Butt end caps improve perceived regularity of the highlight dash
        // pattern and also make it less likely that an empty gap will be the
        // final mark for a graphic and thus cause confusion over its extrusion.
        _centerOfRotationElement.setStrokeLineCap( StrokeLineCap.BUTT );

        // Hide the Center of Rotation Group until it is needed.
        _centerOfRotationGroup = new Group( _centerOfRotationElement );
        _centerOfRotationGroup.setVisible( false );
    }

    /**
     * Update the Center of Rotation Node's graphics content, and re-scale for
     * the current Distance Unit.
     */
    public void updateCenterOfRotationGraphics( final double centerOfRotationXPx,
                                                final double centerOfRotationYPx) {
        // Position the Center of Rotation at the mouse click.
        _centerOfRotationGroup.setTranslateX( centerOfRotationXPx );
        _centerOfRotationGroup.setTranslateY( centerOfRotationYPx );
    }
}
