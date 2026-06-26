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
 * This file is part of the FxCadGraphics Library.
 *
 * You should have received a copy of the MIT License along with the
 * FxCadGraphics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgraphics
 */
package com.mhschmieder.fxgraphics.geometry;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

/**
 * The <code>DrawingLimits</code> class is the implementation class for the
 * inclusive bounds of a CAD drawing, such as venues used in CAD apps. It
 * currently contains a rectangle describing the boundary of the CAD space, as
 * well as a flag for whether to auto-sync to another boundary (usually a
 * Region2D, such as one that is used as a Prediction Plane in CAD apps).
 * <p>
 * This class is generally for 2D CAD, but is deliberately flexible towards
 * referring either to screen space (which is always 2D currently), or to
 * whatever projective two-dimensional axes are currently in use.
 */
public final class DrawingLimits extends Extents2D {

    // Declare default constants, where appropriate, for all fields.
    public static final boolean     AUTO_SYNC_DEFAULT    = true;

    /**
     * Declare an invalid Bounding Box for convenience. According to the JavaFX
     * API docs, invalid Bounding Boxes are flagged by setting their width and
     * height to "-1". Default constructors might do that, but this is clearer.
     */
    public static final BoundingBox INVALID_BOUNDING_BOX = new BoundingBox(
            0.0d, 0.0d, -1d, -1d );

    /**
     * Cached copy of most recent auto-sync setting.
     */
    private boolean         autoSync;

    /**
     * Default constructor when nothing is known. Sets default extents.
     */
    public DrawingLimits() {
        this( AUTO_SYNC_DEFAULT );
    }

    /**
     * Default constructor when only auto-sync is known. Sets default bounds.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     */
    public DrawingLimits( final boolean pAutoSync ) {
        super();

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Cross-constructor from {@link Rectangle} to {@link DrawingLimits}.
     *
     * @param pBoundary
     *            The {@link Rectangle} to use for setting the fields
     */
    public DrawingLimits( final Rectangle pBoundary ) {
        this( AUTO_SYNC_DEFAULT, pBoundary );
    }

    /**
     * Cross-constructor from {@link Rectangle} to {@link DrawingLimits}.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     * @param pBoundary
     *            The {@link Rectangle} to use for setting the fields
     */
    public DrawingLimits( final boolean pAutoSync,
                          final Rectangle pBoundary ) {
        // Always call the super-constructor first!
        super( pBoundary );

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Cross-constructor from {@link Rectangle2D} to {@link DrawingLimits}.
     *
     * @param pBounds
     *            The {@link Rectangle2D} to use for setting the fields
     */
    public DrawingLimits( final Rectangle2D pBounds ) {
        this( AUTO_SYNC_DEFAULT, pBounds );
    }

    /**
     * Cross-constructor from {@link Rectangle2D} to {@link DrawingLimits}.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     * @param pBounds
     *            The {@link Rectangle2D} to use for setting the fields
     */
    public DrawingLimits( final boolean pAutoSync,
                          final Rectangle2D pBounds ) {
        // Always call the super-constructor first!
        super( pBounds );

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Cross-constructor from {@link Bounds} to {@link DrawingLimits}.
     *
     * @param computedBounds
     *            The {@link Bounds} to use for setting the fields
     */
    public DrawingLimits(final Bounds computedBounds ) {
        this( AUTO_SYNC_DEFAULT, computedBounds );
    }

    /**
     * Cross-constructor from {@link Bounds} to {@link DrawingLimits}.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     * @param computedBounds
     *            The {@link Bounds} to use for setting the fields
     */
    public DrawingLimits( final boolean pAutoSync,
                          final Bounds computedBounds ) {
        super( computedBounds );

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Cross-constructor from {@link Extents2D} to {@link DrawingLimits}.
     *
     * @param pExtents
     *            The {@link Extents2D} to use for setting the fields
     */
    public DrawingLimits(final Extents2D pExtents ) {
        this( AUTO_SYNC_DEFAULT, pExtents );
    }

    /**
     * Cross-constructor from {@link Extents2D} to {@link DrawingLimits}.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     * @param pExtents
     *            The {@link Extents2D} to use for setting the fields
     */
    public DrawingLimits(final boolean pAutoSync, final Extents2D pExtents ) {
        // Always call the super-constructor first!
        super( pExtents );

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Partially qualified constructor. Turns auto-sync off by default.
     *
     * @param pBoundaryX
     *            The x-origin to use for the new {@link DrawingLimits}
     * @param pBoundaryY
     *            The y-origin to use for the new {@link DrawingLimits}
     * @param pBoundaryWidth
     *            The width to use for the new {@link DrawingLimits}
     * @param pBoundaryHeight
     *            The height to use for the new {@link DrawingLimits}
     */
    public DrawingLimits( final double pBoundaryX,
                          final double pBoundaryY,
                          final double pBoundaryWidth,
                          final double pBoundaryHeight ) {
        this( AUTO_SYNC_DEFAULT, pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );
    }

    /**
     * Fully qualified constructor.
     *
     * @param pAutoSync
     *            {@code true} if auto-sync to other extents
     * @param pBoundaryX
     *            The x-origin to use for the new {@link DrawingLimits}
     * @param pBoundaryY
     *            The y-origin to use for the new {@link DrawingLimits}
     * @param pBoundaryWidth
     *            The width to use for the new {@link DrawingLimits}
     * @param pBoundaryHeight
     *            The height to use for the new {@link DrawingLimits}
     */
    public DrawingLimits( final boolean pAutoSync,
                          final double pBoundaryX,
                          final double pBoundaryY,
                          final double pBoundaryWidth,
                          final double pBoundaryHeight ) {
        super( pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );

        // Initialize the fields that are unique to DrawingLimits vs.Extents2D.
        initDrawingLimits( pAutoSync );
    }

    /**
     * Copy Constructor.
     *
     * @param pDrawingLimits
     *            The {@link DrawingLimits} to use for setting the fields
     */
    public DrawingLimits( final DrawingLimits pDrawingLimits ) {
        this( pDrawingLimits.isAutoSync(),
                pDrawingLimits.getX(),
                pDrawingLimits.getY(),
                pDrawingLimits.getWidth(),
                pDrawingLimits.getHeight() );
    }

    // NOTE: Cloning is disabled as it is dangerous; use the copy constructor
    // instead.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync( final boolean pAutoSync ) {
        autoSync = pAutoSync;
    }

    /*
     * Initialize the fields that are unique to {@link DrawingLimits}.
     * Generally called by constructors after setting Extents2D fields.
     */
    private void initDrawingLimits( final boolean pAutoSync ) {
        autoSync = pAutoSync;
    }

    /** Default pseudo-constructor. */
    public void reset() {
        setDrawingLimits( AUTO_SYNC_DEFAULT,
                X_METERS_DEFAULT,
                Y_METERS_DEFAULT,
                WIDTH_METERS_DEFAULT,
                HEIGHT_METERS_DEFAULT );
    }

    /*
     * Fully qualified pseudo-constructor.
     */
    public void setDrawingLimits( final boolean pAutoSync,
                                  final double pBoundaryX,
                                  final double pBoundaryY,
                                  final double pBoundaryWidth,
                                  final double pBoundaryHeight ) {
        setAutoSync( pAutoSync );

        setExtents( pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );
    }

    /*
     * Fully qualified pseudo-constructor.
     */
    public void setDrawingLimits( final boolean pAutoSync,
                                  final Extents2D pExtents ) {
        setAutoSync( pAutoSync );

        setExtents( pExtents );
    }

    /*
     * Fully qualified pseudo-constructor.
     */
    public void setDrawingLimits( final boolean pAutoSync,
                                  final Rectangle2D pRectangle ) {
        setAutoSync( pAutoSync );

        setExtents( pRectangle );
    }

    /*
     * Copy pseudo-constructor.
     */
    public void setDrawingLimits( final DrawingLimits pDrawingLimits ) {
        setDrawingLimits(
                pDrawingLimits.isAutoSync(),
                pDrawingLimits.getX(),
                pDrawingLimits.getY(),
                pDrawingLimits.getWidth(),
                pDrawingLimits.getHeight() );
    }
}
