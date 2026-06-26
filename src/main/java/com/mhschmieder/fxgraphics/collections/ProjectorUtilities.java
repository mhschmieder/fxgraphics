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
 * This file is part of the FxCadGraphics Library
 *
 * You should have received a copy of the MIT License along with the
 * FxCadGraphics Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgraphics
 */
package com.mhschmieder.fxgraphics.collections;

import com.mhschmieder.fxgraphics.geometry.CartesianLine;
import com.mhschmieder.fxgraphics.geometry.GraphicalObject;
import com.mhschmieder.fxgraphics.geometry.PolarLine;

import java.util.Collection;
import java.util.HashSet;

/**
 * This is a utility class for Projectors -- especially actions on collections
 * that otherwise would require class derivation of GraphicalObjectCollection.
 * <p>
 * Projectors are specialized versions of Linear Objects and are marked as such.
 */
public final class ProjectorUtilities {

    public static Collection< CartesianLine > getSelectedCartesianProjectors(
            final GraphicalObjectCollection< CartesianLine > cartesianLineCollection ) {
        // Get all of the selected Cartesian Lines that are marked as
        // Projectors.
        final Collection< CartesianLine > selectedCartesianProjectors = new HashSet<>( 20 );
        final Collection< CartesianLine > selectedCartesianLines = cartesianLineCollection
                .getSelection();
        selectedCartesianLines.forEach( cartesianLine -> {
            if ( cartesianLine.isUseAsProjector() ) {
                selectedCartesianProjectors.add( cartesianLine );
            }
        } );
        return selectedCartesianProjectors;
    }

    public static Collection< PolarLine > getSelectedPolarProjectors(
            final GraphicalObjectCollection< PolarLine > polarLineCollection ) {
        // Get all of the selected Polar Lines that are marked as Projectors.
        final Collection< PolarLine > selectedPolarProjectors = new HashSet<>( 20 );
        final Collection< PolarLine > selectedPolarLines = polarLineCollection.getSelection();
        selectedPolarLines.forEach( polarLine -> {
            if ( polarLine.isUseAsProjector() ) {
                selectedPolarProjectors.add( polarLine );
            }
        } );
        return selectedPolarProjectors;
    }

    // TODO: Continue to try to genericize to just one combined method for all
    //  types of Linear Objects. So far every effort fails to compile.
    public static void selectAllCartesianProjectors( final GraphicalObjectCollection< CartesianLine > cartesianLineCollection ) {
        // Fill the Linear Objects selection set with all of the Linear Objects
        // that are marked as Projectors.
        final Collection< CartesianLine > collection = cartesianLineCollection.getCollection();
        final Collection< CartesianLine > deselection = cartesianLineCollection.getDeselection();

        // Fill the Linear Objects selection set with all of the Projectors.
        // NOTE: It is safer to avoid parallel streams right after clearing one
        //  of the collections as a bulk action.
        collection.stream().filter( GraphicalObject::isEditable ).forEach(cartesianLine -> {
            if ( cartesianLine.isEditable() && cartesianLine.isUseAsProjector() ) {
                cartesianLineCollection.addToSelection( cartesianLine );
                deselection.add( cartesianLine );
            }
        } );
    }

    // TODO: Continue to try to genericize to just one combined method for all
    //  types of Linear Objects. So far every effort fails to compile.
    public static void selectAllPolarProjectors( final GraphicalObjectCollection< PolarLine > polarLineCollection ) {
        // Fill the Linear Objects selection set with all of the Linear Objects
        // that are marked as Projectors.
        final Collection< PolarLine > collection = polarLineCollection.getCollection();
        final Collection< PolarLine > deselection = polarLineCollection.getDeselection();

        // Fill the Linear Objects selection set with all of the Projectors.
        // NOTE: It is safer to avoid parallel streams right after clearing one
        //  of the collections as a bulk action.
        collection.stream().filter( GraphicalObject::isEditable ).forEach( polarLine -> {
            if ( polarLine.isEditable() && polarLine.isUseAsProjector() ) {
                polarLineCollection.addToSelection( polarLine );
                deselection.add( polarLine );
            }
        } );
    }
}
