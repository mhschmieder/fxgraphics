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

// NOTE: This is a placeholder class to deal with Catch-22 situations where we
//  would otherwise be assigning variables back and forth between AWT and JavaFX
//  on the same line of code with no possible way of properly thread-wrapping
//  each half of the assignment statement.
public class GraphicsImportDrawingLimits {

    // Declare default constants, where appropriate, for all fields.
    public static final double  X_DEFAULT         = 0.0d;
    public static final double  Y_DEFAULT         = 0.0d;
    public static final double  WIDTH_DEFAULT     = 40d;
    public static final double  HEIGHT_DEFAULT    = 20.0d;

    // Declare minimum and maximum allowed dimensions (same for x and y).
    public static final double  SIZE_MIN          = 3.0d;
    public static final double  SIZE_MAX          = 1000d;

    public static final boolean AUTO_SYNC_DEFAULT = false;

    // Cached copy of most recent auto-sync setting.
    private boolean             _autoSync         = AUTO_SYNC_DEFAULT;

    // Origin and dimensions, done like AWT's Rectangle2D class.
    private double              _x                = X_DEFAULT;
    private double              _y                = Y_DEFAULT;
    private double              _width            = WIDTH_DEFAULT;
    private double              _height           = HEIGHT_DEFAULT;

    // Default constructor when nothing is known.
    public GraphicsImportDrawingLimits() {
        this( AUTO_SYNC_DEFAULT, X_DEFAULT, Y_DEFAULT, WIDTH_DEFAULT, HEIGHT_DEFAULT );
    }

    // Fully qualified constructor when all dimensions are known.
    public GraphicsImportDrawingLimits( final boolean autoSync,
                                        final double drawingLimitsX,
                                        final double drawingLimitsY,
                                        final double drawingLimitsWidth,
                                        final double drawingLimitsHeight ) {
        super();

        setGraphicsImportDrawingLimits( autoSync,
                                        drawingLimitsX,
                                        drawingLimitsY,
                                        drawingLimitsWidth,
                                        drawingLimitsHeight );
    }

    // Copy constructor.
    public GraphicsImportDrawingLimits( final GraphicsImportDrawingLimits drawingLimits ) {
        super();

        setGraphicsImportDrawingLimits( drawingLimits );
    }

    public final double getHeight() {
        return _height;
    }

    public final double getWidth() {
        return _width;
    }

    public final double getX() {
        return _x;
    }

    public final double getY() {
        return _y;
    }

    public final boolean isAutoSync() {
        return _autoSync;
    }

    // Default pseudo-constructor.
    public final void reset() {
        setGraphicsImportDrawingLimits( AUTO_SYNC_DEFAULT,
                                        X_DEFAULT,
                                        Y_DEFAULT,
                                        WIDTH_DEFAULT,
                                        HEIGHT_DEFAULT );
    }

    public final void setAutoSync( final boolean autoSync ) {
        _autoSync = autoSync;
    }

    // Pseudo-constructor
    public final void setGraphicsImportDrawingLimits( final boolean autoSync,
                                                      final double drawingLimitsX,
                                                      final double drawingLimitsY,
                                                      final double drawingLimitsWidth,
                                                      final double drawingLimitsHeight ) {
        _autoSync = autoSync;

        setRect( drawingLimitsX, drawingLimitsY, drawingLimitsWidth, drawingLimitsHeight );
    }

    // Pseudo-copy constructor
    public final void setGraphicsImportDrawingLimits( final GraphicsImportDrawingLimits drawingLimits ) {
        setGraphicsImportDrawingLimits( drawingLimits.isAutoSync(),
                                        drawingLimits.getX(),
                                        drawingLimits.getY(),
                                        drawingLimits.getWidth(),
                                        drawingLimits.getHeight() );
    }

    public final void setHeight( final double drawingLimitsHeight ) {
        _height = drawingLimitsHeight;
    }

    public final void setRect( final double x,
                               final double y,
                               final double width,
                               final double height ) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
    }

    public final void setWidth( final double drawingLimitsWidth ) {
        _width = drawingLimitsWidth;
    }

    public final void setX( final double drawingLimitsX ) {
        _x = drawingLimitsX;
    }

    public final void setY( final double drawingLimitsY ) {
        _y = drawingLimitsY;
    }
}
