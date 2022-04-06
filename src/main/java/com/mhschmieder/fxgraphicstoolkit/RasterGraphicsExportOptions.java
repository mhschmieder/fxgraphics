/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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
package com.mhschmieder.fxgraphicstoolkit;

import com.mhschmieder.fxgraphicstoolkit.image.ImageSize;

/**
 * These are the Export Options for Raster Graphics Export. By default,
 * everything is exported, and this also covers under-specified cases. All
 * options are mutually exclusive as otherwise an empty export could result.
 */
public final class RasterGraphicsExportOptions extends GraphicsExportOptions {

    // Maintain an observable reference to the Image Size.
    private final ImageSize imageSize;

    // Default constructor when nothing is known.
    public RasterGraphicsExportOptions() {
        this( EXPORT_ALL_DEFAULT, EXPORT_CHART_DEFAULT, EXPORT_AUXILIARY_DEFAULT );
    }

    // Partially specified constructor when everything but Image Size is known.
    public RasterGraphicsExportOptions( final boolean pExportAll,
                                        final boolean pExportChart,
                                        final boolean pExportAuxiliary ) {
        this( pExportAll, pExportChart, pExportAuxiliary, null );
    }

    // Fully specified constructor when everything is known.
    public RasterGraphicsExportOptions( final boolean pExportAll,
                                        final boolean pExportChart,
                                        final boolean pExportAuxiliary,
                                        final ImageSize pImageSize ) {
        super( pExportAll, pExportChart, pExportAuxiliary );

        imageSize = ( pImageSize != null ) ? new ImageSize( pImageSize ) : new ImageSize();
    }

    // Copy constructor.
    public RasterGraphicsExportOptions( final RasterGraphicsExportOptions pImageGraphicsExportOptions ) {
        this( pImageGraphicsExportOptions.isExportAll(),
              pImageGraphicsExportOptions.isExportChart(),
              pImageGraphicsExportOptions.isExportAuxiliary(),
              pImageGraphicsExportOptions.getImageSize() );
    }

    public ImageSize getImageSize() {
        return imageSize;
    }

    public double getPixelHeight() {
        return imageSize.getPixelHeight();
    }

    public double getPixelWidth() {
        return imageSize.getPixelWidth();
    }

    public boolean isUseOnScreenImageSize() {
        return imageSize.isAutoSize();
    }

    // Default pseudo-constructor.
    @Override
    public void reset() {
        super.reset();

        imageSize.reset();
    }

    // Fully specified pseudo-constructor.
    public void setRasterGraphicsExportOptions( final boolean pExportAll,
                                                final boolean pExportChart,
                                                final boolean pExportAuxiliary,
                                                final ImageSize pImageSize ) {
        setGraphicsExportOptions( pExportAll, pExportChart, pExportAuxiliary );

        imageSize.setImageSize( pImageSize );
    }

    // Pseudo-copy constructor.
    public void setRasterGraphicsExportOptions( final RasterGraphicsExportOptions pRasterGraphicsExportOptions ) {
        setRasterGraphicsExportOptions( pRasterGraphicsExportOptions.isExportAll(),
                                        pRasterGraphicsExportOptions.isExportChart(),
                                        pRasterGraphicsExportOptions.isExportAuxiliary(),
                                        pRasterGraphicsExportOptions.getImageSize() );
    }

    public void setImageSize( final ImageSize pImageSize ) {
        imageSize.setImageSize( pImageSize );
    }

    public void setPixelDimensions( final double pPixelWidth, final double pPixelHeight ) {
        imageSize.setPixelDimensions( pPixelWidth, pPixelHeight );
    }

    public void setUseOnScreenImageSize( final boolean useOnScreenImageSize ) {
        imageSize.setAutoSize( useOnScreenImageSize );
    }

}
