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
package com.mhschmieder.fxgraphicstoolkit.io;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * This is the base class for options shared by all Graphics Export types.
 */
public class GraphicsExportOptions {

    protected static final boolean  EXPORT_ALL_DEFAULT       = true;
    protected static final boolean  EXPORT_CHART_DEFAULT     = false;
    protected static final boolean  EXPORT_AUXILIARY_DEFAULT = false;

    // Cached observable copy of most recent export options.
    protected final BooleanProperty exportAllData;
    protected final BooleanProperty exportChart;
    protected final BooleanProperty exportAuxiliary;

    // Default constructor when nothing is known.
    public GraphicsExportOptions() {
        this( EXPORT_ALL_DEFAULT, EXPORT_CHART_DEFAULT, EXPORT_AUXILIARY_DEFAULT );
    }

    // Fully specified constructor when everything is known.
    public GraphicsExportOptions( final boolean pExportAll,
                                  final boolean pExportChart,
                                  final boolean pExportAuxiliary ) {
        exportAllData = new SimpleBooleanProperty( pExportAll );
        exportChart = new SimpleBooleanProperty( pExportChart );
        exportAuxiliary = new SimpleBooleanProperty( pExportAuxiliary );
    }

    // Copy constructor.
    public GraphicsExportOptions( final GraphicsExportOptions pGraphicsExportOptions ) {
        this( pGraphicsExportOptions.isExportAll(),
              pGraphicsExportOptions.isExportChart(),
              pGraphicsExportOptions.isExportAuxiliary() );
    }

    public final BooleanProperty exportAllProperty() {
        return exportAllData;
    }

    public final BooleanProperty exportAuxiliaryProperty() {
        return exportAuxiliary;
    }

    public final BooleanProperty exportChartProperty() {
        return exportChart;
    }

    public final boolean isExportAll() {
        return exportAllProperty().get();
    }

    public final boolean isExportAuxiliary() {
        return exportAuxiliary.get();
    }

    public final boolean isExportChart() {
        return exportChart.get();
    }

    // Default pseudo-constructor.
    public void reset() {
        setExportAll( EXPORT_ALL_DEFAULT );
        setExportChart( EXPORT_CHART_DEFAULT );
        setExportAuxiliary( EXPORT_AUXILIARY_DEFAULT );
    }

    public final void setExportAll( final boolean pExportAll ) {
        exportAllData.set( pExportAll );
    }

    public final void setExportAuxiliary( final boolean pExportAuxiliary ) {
        exportAuxiliary.set( pExportAuxiliary );
    }

    public final void setExportChart( final boolean pExportChart ) {
        exportChart.set( pExportChart );
    }

    // Fully specified pseudo-constructor.
    public final void setGraphicsExportOptions( final boolean pExportAll,
                                                final boolean pExportChart,
                                                final boolean pExportAuxiliary ) {
        setExportAll( pExportAll );
        setExportChart( pExportChart );
        setExportAuxiliary( pExportAuxiliary );
    }

    // Pseudo-copy constructor.
    public final void setGraphicsExportOptions( final GraphicsExportOptions pGraphicsExportOptions ) {
        setGraphicsExportOptions( pGraphicsExportOptions.isExportAll(),
                                  pGraphicsExportOptions.isExportChart(),
                                  pGraphicsExportOptions.isExportAuxiliary() );
    }

}
