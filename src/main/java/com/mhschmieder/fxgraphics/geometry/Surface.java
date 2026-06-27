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

import com.mhschmieder.fxgraphics.layers.LayerManager;
import javafx.scene.shape.Line;

/**
 * The <code>Surface</code> class is the implementation class for a Surface as
 * used in some CAD apps. This class describes Surface Materials, ID, enabled
 * status and Surface End Points.
 */
public class Surface extends CartesianLine {

    // Declare an identifier number for this surface
    protected static final int SURFACE_NUMBER_DEFAULT = 1;

    // Surfaces are bypassed by default as they are only approximate.
    public static final boolean SURFACE_BYPASSED_DEFAULT = true;

    // Declare the default Surface Material.
    public static final SurfaceMaterial SURFACE_MATERIAL_DEFAULT
            = SurfaceMaterial.RIGID;

    protected boolean surfaceBypassed;
    protected int surfaceNumber;
    protected SurfaceMaterial surfaceMaterial;

    // This is the default constructor; it sets all instance variables to
    // default values.
    // NOTE: As this class declares additional fields to the parent class, we
    //  cannot just invoke the super-constructor from each constructor, so we
    //  need to invoke incrementally more complex local constructors instead.
    public Surface() {
        this( SURFACE_NUMBER_DEFAULT );
    }

    // This is the preferred default constructor using a unique Surface ID.
    public Surface( final int pSurfaceNumber ) {
        this(
                pSurfaceNumber,
                SURFACE_BYPASSED_DEFAULT,
                SURFACE_MATERIAL_DEFAULT,
                "Surface " + pSurfaceNumber );
    }

    // This is the partially qualified constructor, when all but extents are
    // known.
    public Surface( final int pSurfaceNumber,
                    final boolean pSurfaceBypassed,
                    final SurfaceMaterial pSurfaceMaterial,
                    final String pSurfaceLabel ) {
        this(
                pSurfaceNumber,
                pSurfaceBypassed,
                pSurfaceMaterial,
                CartesianLine.X1_DEFAULT,
                CartesianLine.Y1_DEFAULT,
                CartesianLine.X2_DEFAULT,
                CartesianLine.Y2_DEFAULT,
                pSurfaceLabel );
    }

    // This is the fully qualified constructor, using separate coordinates.
    // TODO: Pass in and use a unique Layer.
    public Surface( final int pSurfaceNumber,
                    final boolean pSurfaceBypassed,
                    final SurfaceMaterial pSurfaceMaterial,
                    final double x1,
                    final double y1,
                    final double x2,
                    final double y2,
                    final String pSurfaceLabel ) {
        super( x1,
                y1,
                x2,
                y2,
                pSurfaceLabel,
                LayerManager.makeDefaultLayer(),
                false,
                1 );

        setSurfaceNumber( pSurfaceNumber );
        setSurfaceBypassed( pSurfaceBypassed );
        setSurfaceMaterial( pSurfaceMaterial );
    }

    // This is the fully qualified constructor, using a Line.
    // TODO: Pass in and use a unique Surface Name and Layer.
    public Surface( final int pSurfaceNumber,
                    final boolean pSurfaceBypassed,
                    final SurfaceMaterial pSurfaceMaterial,
                    final Line pLine ) {
        super( pLine,
                "",
                LayerManager.makeDefaultLayer(),
                false,
                1 );

        setSurfaceNumber( pSurfaceNumber );
        setSurfaceBypassed( pSurfaceBypassed );
        setSurfaceMaterial( pSurfaceMaterial );
    }

    // NOTE: This is the copy constructor, and is offered in place of clone()
    //  to guarantee that the source object is never modified by the new target
    //  object created here.
    public Surface( final Surface pSurface ) {
        super();

        setSurface( pSurface );
    }

    // NOTE: Cloning is disabled as it is dangerous; use the copy constructor
    //  instead.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public int getSurfaceNumber() {
        return surfaceNumber;
    }

    public void setSurfaceNumber( final int pSurfaceNumber ) {
        surfaceNumber = pSurfaceNumber;
    }

    public boolean isSurfaceBypassed() {
        return surfaceBypassed;
    }

    public void setSurfaceBypassed( final boolean pSurfaceBypassed ) {
        surfaceBypassed = pSurfaceBypassed;
    }

    public SurfaceMaterial getSurfaceMaterial() {
        return surfaceMaterial;
    }

    public void setSurfaceMaterial( final SurfaceMaterial pSurfaceMaterial ) {
        surfaceMaterial = pSurfaceMaterial;
    }

    // Fully qualified pseudo-constructor
    // TODO: Pass in and use a unique surface name and layer.
    protected void setSurface( final Line pLine,
                               final int pSurfaceNumber,
                               final boolean pSurfaceBypassed,
                               final SurfaceMaterial pSurfaceMaterial ) {
        setCartesianLine(
                pLine,
                "",
                LayerManager.makeDefaultLayer(),
                false,
                1 );
        setSurfaceNumber( pSurfaceNumber );
        setSurfaceBypassed( pSurfaceBypassed );
        setSurfaceMaterial( pSurfaceMaterial );
    }

    // Pseudo-copy constructor.
    // TODO: Pass in and use a unique Surface Name.
    protected void setSurface( final Surface pSurface ) {
        setSurface(
                pSurface.getLine(),
                pSurface.getSurfaceNumber(),
                pSurface.isSurfaceBypassed(),
                pSurface.getSurfaceMaterial() );
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( !( obj instanceof Surface ) ) {
            return false;
        }

        // NOTE: We invoke getter methods vs. directly accessing data
        //  members, so that derived classes produce the correct results when
        //  comparing two objects.
        final Surface other = ( Surface ) obj;
        if ( !super.equals( obj ) 
                || ( getSurfaceNumber() != other.getSurfaceNumber() ) 
                || ( isSurfaceBypassed() != other.isSurfaceBypassed() )
                || ( getSurfaceMaterial() != other.getSurfaceMaterial() ) ) {
            return false;
        }

        // NOTE: The "label" and "layer" properties are exempt, as they are not
        //  implemented at this level. The Layer is handled nonetheless by the
        //  super-parent but will match when defaulted so is not an issue.
        return true;
    }

    @Override
    public int hashCode() {
        // TODO: Replace auto-generated method stub?
        return super.hashCode();
    }
}
