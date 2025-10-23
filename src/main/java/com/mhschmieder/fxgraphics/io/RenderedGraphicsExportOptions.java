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
package com.mhschmieder.fxgraphics.io;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class contains the options for Rendered Graphics Export actions,
 * which refer to standard Vector Graphics Exports done via Java 2D Rendering.
 */
public class RenderedGraphicsExportOptions {

    private static final boolean    EXPORT_AUXILIARY_PANEL_DEFAULT    = true;
    private static final boolean    EXPORT_INFORMATION_TABLES_DEFAULT = true;
    private static final boolean    EXPORT_OPTIONAL_ITEM_DEFAULT      = true;

    // Cached observable copy of most recent export options.
    protected final StringProperty  title;
    protected final BooleanProperty exportAuxiliaryPanel;
    protected final BooleanProperty exportInformationTables;
    protected final BooleanProperty exportOptionalItem;

    // Default constructor when nothing is known.
    @SuppressWarnings("nls")
    public RenderedGraphicsExportOptions() {
        this( "",
              EXPORT_AUXILIARY_PANEL_DEFAULT,
              EXPORT_INFORMATION_TABLES_DEFAULT,
              EXPORT_OPTIONAL_ITEM_DEFAULT );
    }

    // Copy constructor.
    public RenderedGraphicsExportOptions( final RenderedGraphicsExportOptions pRenderedGraphicsExportOptions ) {
        this( pRenderedGraphicsExportOptions.getTitle(),
              pRenderedGraphicsExportOptions.isExportAuxiliaryPanel(),
              pRenderedGraphicsExportOptions.isExportInformationTables(),
              pRenderedGraphicsExportOptions.isExportOptionalItem() );
    }

    // Fully specified constructor when everything is known.
    public RenderedGraphicsExportOptions( final String pTitle,
                                          final boolean pExportAuxiliary,
                                          final boolean pExportInformationTables,
                                          final boolean pExportOptionalItem ) {
        title = new SimpleStringProperty( pTitle );

        exportAuxiliaryPanel = new SimpleBooleanProperty( pExportAuxiliary );
        exportInformationTables = new SimpleBooleanProperty( pExportInformationTables );
        exportOptionalItem = new SimpleBooleanProperty( pExportOptionalItem );
    }

    public final BooleanProperty exportAuxiliaryPanelProperty() {
        return exportAuxiliaryPanel;
    }

    public final BooleanProperty exportInformationTablesProperty() {
        return exportInformationTables;
    }

    public final BooleanProperty exportOptionalItemProperty() {
        return exportOptionalItem;
    }

    public final String getTitle() {
        return title.get();
    }

    public final boolean isExportAuxiliaryPanel() {
        return exportAuxiliaryPanel.get();
    }

    public final boolean isExportInformationTables() {
        return exportInformationTables.get();
    }

    public final boolean isExportOptionalItem() {
        return exportOptionalItem.get();
    }

    // Default pseudo-constructor.
    public final void reset() {
        setTitle( "" ); //$NON-NLS-1$

        setExportAuxiliaryPanel( EXPORT_AUXILIARY_PANEL_DEFAULT );
        setExportInformationTables( EXPORT_INFORMATION_TABLES_DEFAULT );
        setExportOptionalItem( EXPORT_OPTIONAL_ITEM_DEFAULT );
    }

    public final void setExportAuxiliaryPanel( final boolean pExportAuxiliaryPanel ) {
        exportAuxiliaryPanel.set( pExportAuxiliaryPanel );
    }

    public final void setExportInformationTables( final boolean pExportInformationTables ) {
        exportInformationTables.set( pExportInformationTables );
    }

    public final void setExportOptionalItem( final boolean pExportOptionalItem ) {
        exportOptionalItem.set( pExportOptionalItem );
    }

    // Pseudo-copy constructor.
    public final void setRenderedGraphicsExportOptions( final RenderedGraphicsExportOptions pRenderedGraphicsExportOptions ) {
        setRenderedGraphicsExportOptions( pRenderedGraphicsExportOptions.getTitle(),
                                          pRenderedGraphicsExportOptions.isExportAuxiliaryPanel(),
                                          pRenderedGraphicsExportOptions
                                                  .isExportInformationTables(),
                                          pRenderedGraphicsExportOptions.isExportOptionalItem() );
    }

    // Fully specified pseudo-constructor.
    public final void setRenderedGraphicsExportOptions( final String pTitle,
                                                        final boolean pExportAuxiliaryPanel,
                                                        final boolean pExportInformationTables,
                                                        final boolean pExportOptionalItem ) {
        setTitle( pTitle );

        setExportAuxiliaryPanel( pExportAuxiliaryPanel );
        setExportInformationTables( pExportInformationTables );
        setExportOptionalItem( pExportOptionalItem );
    }

    public final void setTitle( final String pTitle ) {
        title.set( pTitle );
    }

    public final StringProperty titleProperty() {
        return title;
    }
}
