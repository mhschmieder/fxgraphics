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
 * This file is part of the fxcadgraphics Library.
 *
 * You should have received a copy of the MIT License along with the
 * fxcadgraphics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgraphics
 */
package com.mhschmieder.fxgraphics.geometry;

import com.mhschmieder.fxcadgraphics.util.SurfaceNameManager;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Region2D</code> class is the implementation class for a Region as
 * used in some CAD apps. It currently contains a rectangle describing the
 * dimensions of a subspace of interest, along with surfaces and their
 * status/materials. As such, it isn't quite the same as a Reference Plane.
 * <p>
 * This class is loosely based on the Region object from AutoCAD, which is a
 * two-dimensional enclosed area that optionally has mass properties. In our
 * case, we don't compute the centroid but do model the surface properties.
 * <p>
 * This class is strictly for 2D CAD; the AutoCAD 3DFace object is a good model
 * for extending to 3D CAD later on.
 */
public final class Region2D extends Extents2D {

    // For now, we are limited to four orthogonal surfaces.
    public static final int NUMBER_OF_SURFACES = 4;

    // Declare minimum and maximum allowed dimensions (same for x and y).
    public static final double SIZE_METERS_MINIMUM = 3.0d;
    public static final double SIZE_METERS_MAXIMUM = 1000.0d;

    /** A list of Surfaces. */
    private final List< Surface > surfaceList;

    /*
     * Default constructor when nothing is known.
     */
    public Region2D() {
        this( X_METERS_DEFAULT,
                Y_METERS_DEFAULT,
                WIDTH_METERS_DEFAULT,
                HEIGHT_METERS_DEFAULT );
    }

    /*
     * Default constructor when surfaces are disabled.
     */
    private Region2D( final double pBoundaryX,
                      final double pBoundaryY,
                      final double pBoundaryWidth,
                      final double pBoundaryHeight ) {
        this(
                pBoundaryX,
                pBoundaryY,
                pBoundaryWidth,
                pBoundaryHeight,
                SurfaceNameManager.getSurfaceNameDefault( 1 ),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                SurfaceNameManager.getSurfaceNameDefault( 2 ),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                SurfaceNameManager.getSurfaceNameDefault( 3 ),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                SurfaceNameManager.getSurfaceNameDefault( 4 ),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT);
    }

    /*
     * Default constructor when surfaces are selectively enabled.
     */
    public Region2D( final double pBoundaryX,
                     final double pBoundaryY,
                     final double pBoundaryWidth,
                     final double pBoundaryHeight,
                     final List< Surface > pSurfaces ) {
        this( pBoundaryX,
                pBoundaryY,
                pBoundaryWidth,
                pBoundaryHeight,
                pSurfaces.get( 0 ).getLabel(),
                pSurfaces.get( 0 ).isSurfaceBypassed(),
                pSurfaces.get( 0 ).getSurfaceMaterial(),
                pSurfaces.get( 1 ).getLabel(),
                pSurfaces.get( 1 ).isSurfaceBypassed(),
                pSurfaces.get( 1 ).getSurfaceMaterial(),
                pSurfaces.get( 2 ).getLabel(),
                pSurfaces.get( 2 ).isSurfaceBypassed(),
                pSurfaces.get( 2 ).getSurfaceMaterial(),
                pSurfaces.get( 3 ).getLabel(),
                pSurfaces.get( 3 ).isSurfaceBypassed(),
                pSurfaces.get( 3 ).getSurfaceMaterial() );
    }

    /*
     * Default constructor when surfaces are selectively enabled.
     */
    public Region2D( final double pBoundaryX,
                     final double pBoundaryY,
                     final double pBoundaryWidth,
                     final double pBoundaryHeight,
                     final String pSurface1Name,
                     final boolean pSurface1Bypassed,
                     final SurfaceMaterial pSurface1Material,
                     final String pSurface2Name,
                     final boolean pSurface2Bypassed,
                     final SurfaceMaterial pSurface2Material,
                     final String pSurface3Name,
                     final boolean pSurface3Bypassed,
                     final SurfaceMaterial pSurface3Material,
                     final String pSurface4Name,
                     final boolean pSurface4Bypassed,
                     final SurfaceMaterial pSurface4Material ) {
        // Always call the super-constructor first!
        super( pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );

        surfaceList = new ArrayList<>();

        final Surface surface1Properties = new Surface(
                1,
                pSurface1Bypassed,
                pSurface1Material,
                pSurface1Name );
        surfaceList.add( surface1Properties );

        final Surface surface2Properties = new Surface(
                2,
                pSurface2Bypassed,
                pSurface2Material,
                pSurface2Name );
        surfaceList.add( surface2Properties );

        final Surface surface3Properties = new Surface(
                3,
                pSurface3Bypassed,
                pSurface3Material,
                pSurface3Name );
        surfaceList.add( surface3Properties );

        final Surface surface4Properties = new Surface(
                4,
                pSurface4Bypassed,
                pSurface4Material,
                pSurface4Name );
        surfaceList.add( surface4Properties );
    }

    /*
     * Default constructor when surfaces are selectively enabled.
     */
    public Region2D( final Rectangle pBoundary,
                     final List< Surface > pSurfaceProperties ) {
        this( pBoundary.getX(),
                pBoundary.getY(),
                pBoundary.getWidth(),
                pBoundary.getHeight(),
                pSurfaceProperties );
    }

    /*
     * Copy constructor.
     */
    public Region2D( final Region2D pRegion2D ) {
        this( pRegion2D.getX(),
                pRegion2D.getY(),
                pRegion2D.getWidth(),
                pRegion2D.getHeight(),
                pRegion2D.getSurfaces() );
    }

    // NOTE: Cloning is disabled as it is dangerous; use the copy constructor
    // instead.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public List< Surface > getSurfaces() {
        return surfaceList;
    }

    /*
     * Default pseudo-constructor.
     */
    public void reset() {
        // NOTE: Do not reset the Surface Names.
        setRegion2D(
                X_METERS_DEFAULT,
                Y_METERS_DEFAULT,
                WIDTH_METERS_DEFAULT,
                HEIGHT_METERS_DEFAULT,
                surfaceList.get( 0 ).getLabel(),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                surfaceList.get( 1 ).getLabel(),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                surfaceList.get( 2 ).getLabel(),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT,
                surfaceList.get( 3 ).getLabel(),
                Surface.SURFACE_BYPASSED_DEFAULT,
                Surface.SURFACE_MATERIAL_DEFAULT);
    }

    /*
     * Pseudo-constructor. Private, so does not notify listeners.
     */
    public void setRegion2D( final double pBoundaryX,
                             final double pBoundaryY,
                             final double pBoundaryWidth,
                             final double pBoundaryHeight,
                             final List< Surface > pSurfaceProperties ) {
        setExtents( pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );

        setSurfaces( pSurfaceProperties );
    }

    /*
     * Fully qualified pseudo-constructor.
     */
    public void setRegion2D( final double pBoundaryX,
                             final double pBoundaryY,
                             final double pBoundaryWidth,
                             final double pBoundaryHeight,
                             final String pSurface1Name,
                             final boolean pSurface1Bypassed,
                             final SurfaceMaterial pSurface1Material,
                             final String pSurface2Name,
                             final boolean pSurface2Bypassed,
                             final SurfaceMaterial pSurface2Material,
                             final String pSurface3Name,
                             final boolean pSurface3Bypassed,
                             final SurfaceMaterial pSurface3Material,
                             final String pSurface4Name,
                             final boolean pSurface4Bypassed,
                             final SurfaceMaterial pSurface4Material ) {
        setExtents( pBoundaryX, pBoundaryY, pBoundaryWidth, pBoundaryHeight );

        setSurfaces( pSurface1Name,
                pSurface1Bypassed,
                pSurface1Material,
                pSurface2Name,
                pSurface2Bypassed,
                pSurface2Material,
                pSurface3Name,
                pSurface3Bypassed,
                pSurface3Material,
                pSurface4Name,
                pSurface4Bypassed,
                pSurface4Material );
    }

    /*
     * Pseudo-constructor. Private, so does not notify listeners.
     */
    public void setRegion2D( final Rectangle pBoundary,
                             final List< Surface > pSurfaceProperties ) {
        setRegion2D( pBoundary.getX(),
                pBoundary.getY(),
                pBoundary.getWidth(),
                pBoundary.getHeight(),
                pSurfaceProperties );
    }

    /*
     * Copy pseudo-constructor.
     */
    public void setRegion2D( final Region2D pRegion2D ) {
        setRegion2D(
                pRegion2D.getX(),
                pRegion2D.getY(),
                pRegion2D.getWidth(),
                pRegion2D.getHeight(),
                pRegion2D.getSurfaces() );
    }

    public void setSurfaces(final int pSurfaceIndex,
                            final String pSurfaceName,
                            final boolean pSurfaceBypassed,
                            final SurfaceMaterial pSurfaceMaterial ) {
        final Surface surfaceProperties = surfaceList.get(
                pSurfaceIndex );
        surfaceProperties.setSurfaceNumber( pSurfaceIndex + 1 );
        surfaceProperties.setLabel( pSurfaceName );
        surfaceProperties.setSurfaceBypassed( pSurfaceBypassed );
        surfaceProperties.setSurfaceMaterial( pSurfaceMaterial );
    }

    private void setSurfaces( final List< Surface > surfaces ) {
        for ( int surfaceIndex = 0;
              surfaceIndex < NUMBER_OF_SURFACES;
              surfaceIndex++ ) {
            final Surface surface = surfaces.get( surfaceIndex );
            setSurfaces(
                    surfaceIndex,
                    surface.getLabel(),
                    surface.isSurfaceBypassed(),
                    surface.getSurfaceMaterial() );
        }
    }

    public void setSurfaces( final String pSurface1Name,
                             final boolean pSurface1Bypassed,
                             final SurfaceMaterial pSurface1Material,
                             final String pSurface2Name,
                             final boolean pSurface2Bypassed,
                             final SurfaceMaterial pSurface2Material,
                             final String pSurface3Name,
                             final boolean pSurface3Bypassed,
                             final SurfaceMaterial pSurface3Material,
                             final String pSurface4Name,
                             final boolean pSurface4Bypassed,
                             final SurfaceMaterial pSurface4Material ) {
        setSurfaces(
                0,
                pSurface1Name,
                pSurface1Bypassed,
                pSurface1Material );
        setSurfaces(
                1,
                pSurface2Name,
                pSurface2Bypassed,
                pSurface2Material );
        setSurfaces(
                2,
                pSurface3Name,
                pSurface3Bypassed,
                pSurface3Material );
        setSurfaces(
                3,
                pSurface4Name,
                pSurface4Bypassed,
                pSurface4Material );
    }
}
