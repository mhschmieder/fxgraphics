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
package com.mhschmieder.fxgraphicstoolkit.geometry;

import java.util.Objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

/**
 * This is a simple class for describing extents in 2D Cartesian Space.
 */
public class Extents2D {

    public static final double     X_METERS_DEFAULT      = 0.0d;
    public static final double     Y_METERS_DEFAULT      = 0.0d;
    public static final double     WIDTH_METERS_DEFAULT  = 40d;
    public static final double     HEIGHT_METERS_DEFAULT = 20.0d;

    // NOTE: These fields must follow JavaFX Property Bean naming conventions.
    protected final DoubleProperty x;
    protected final DoubleProperty y;
    protected final DoubleProperty width;
    protected final DoubleProperty height;

    /**
     * Default constructor, which zeroes everything.
     */
    public Extents2D() {
        this( 0.0d, 0.0d, 0.0d, 0.0d );
    }

    /**
     * Fully qualified constructor.
     *
     * @param pX
     *            The x-origin to use for the new {@link Extents2D}
     * @param pY
     *            The y-origin to use for the new {@link Extents2D}
     * @param pWidth
     *            The width to use for the new {@link Extents2D}
     * @param pHeight
     *            The height to use for the new {@link Extents2D}
     */
    public Extents2D( final double pX,
                      final double pY,
                      final double pWidth,
                      final double pHeight ) {
        x = new SimpleDoubleProperty( pX );
        y = new SimpleDoubleProperty( pY );
        width = new SimpleDoubleProperty( pWidth );
        height = new SimpleDoubleProperty( pHeight );
    }

    /**
     * Copy Constructor.
     *
     * @param pExtents
     *            The {@link Extents2D} to use for setting the fields
     */
    public Extents2D( final Extents2D pExtents ) {
        this( pExtents.getX(), pExtents.getY(), pExtents.getWidth(), pExtents.getHeight() );
    }

    /**
     * Cross-constructor from {@link Rectangle} to {@link Extents2D}.
     *
     * @param pBoundary
     *            The {@link Rectangle} to use for setting the fields
     */
    public Extents2D( final Rectangle pBoundary ) {
        this( pBoundary.getX(), pBoundary.getY(), pBoundary.getWidth(), pBoundary.getHeight() );
    }

    /**
     * Cross-constructor from {@link Rectangle2D} to {@link Extents2D}.
     *
     * @param pBounds
     *            The {@link Rectangle2D} to use for setting the fields
     */
    public Extents2D( final Rectangle2D pBounds ) {
        this( pBounds.getMinX(), pBounds.getMinY(), pBounds.getWidth(), pBounds.getHeight() );
    }

    @Override
    public boolean equals( final Object other ) {
        if ( this == other ) {
            return true;
        }
        if ( ( other == null ) || ( getClass() != other.getClass() ) ) {
            return false;
        }
        final Extents2D otherExtents2D = ( Extents2D ) other;
        return Objects.equals( x, otherExtents2D.x ) && Objects.equals( y, otherExtents2D.y )
                && Objects.equals( width, otherExtents2D.width )
                && Objects.equals( height, otherExtents2D.height );
    }

    @Override
    public int hashCode() {
        return Objects.hash( x, y, width, height );
    }

    public final double getHeight() {
        return height.get();
    }

    public final Point2D getMaximumPoint() {
        final Point2D maximumPoint = new Point2D( getX() + getWidth(), getY() + getHeight() );
        return maximumPoint;
    }

    public final Point2D getMinimumPoint() {
        final Point2D minimumPoint = new Point2D( getX(), getY() );
        return minimumPoint;
    }

    public final double getWidth() {
        return width.get();
    }

    public final double getX() {
        return x.get();
    }

    public final double getY() {
        return y.get();
    }

    public final DoubleProperty heightProperty() {
        return height;
    }

    /*
     * Partially qualified copy pseudo-constructor.
     */
    public final void setExtents( final Bounds pBounds ) {
        setExtents( pBounds.getMinX(), pBounds.getMinY(), pBounds.getWidth(), pBounds.getHeight() );
    }

    /* Partially qualified pseudo-constructor. */
    public final void setExtents( final double pX,
                                  final double pY,
                                  final double pWidth,
                                  final double pHeight ) {
        setX( pX );
        setY( pY );
        setWidth( pWidth );
        setHeight( pHeight );
    }

    /*
     * Partially qualified copy pseudo-constructor.
     */
    public final void setExtents( final Extents2D extents ) {
        setExtents( extents.getX(), extents.getY(), extents.getWidth(), extents.getHeight() );
    }

    /*
     * Partially qualified copy pseudo-constructor.
     * <p>
     * NOTE: Unless there is already a Rectangle Node lying around, it is
     * probably better to use {@link #setExtents(Bounds)}.
     */
    public final void setExtents( final Rectangle pRectangle ) {
        setExtents( pRectangle.getX(),
                    pRectangle.getY(),
                    pRectangle.getWidth(),
                    pRectangle.getHeight() );
    }

    /*
     * Partially qualified copy pseudo-constructor.
     */
    public final void setExtents( final Rectangle2D pRectangle ) {
        setExtents( pRectangle.getMinX(),
                    pRectangle.getMinY(),
                    pRectangle.getWidth(),
                    pRectangle.getHeight() );
    }

    public final void setHeight( final double pHeight ) {
        height.set( pHeight );
    }

    public final void setWidth( final double pWidth ) {
        width.set( pWidth );
    }

    public final void setX( final double pX ) {
        x.set( pX );
    }

    public final void setY( final double pY ) {
        y.set( pY );
    }

    public final DoubleProperty widthProperty() {
        return width;
    }

    public final DoubleProperty xProperty() {
        return x;
    }

    public final DoubleProperty yProperty() {
        return y;
    }

}
