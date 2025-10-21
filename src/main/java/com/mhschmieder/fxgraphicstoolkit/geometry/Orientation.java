/*
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
package com.mhschmieder.fxgraphicstoolkit.geometry;

import com.mhschmieder.jcommons.lang.Abbreviated;
import com.mhschmieder.jcommons.lang.EnumUtilities;
import com.mhschmieder.jcommons.lang.Labeled;

import java.util.Locale;

/**
 * The <code>Orientation</code> enum is an enumeration for conventional
 * orientation values for graphical objects. For example, it may be relative to
 * a plane cutting through an object's geometric center or COG.
 *
 * NOTE: Other than for string conversions, this is now redundant with JavaFX.
 */
public enum Orientation implements Labeled< Orientation >,
        Abbreviated< Orientation > {
    HORIZONTAL( "Horizontal", "hz" ),
    VERTICAL( "Vertical", "vt" );

    private String label;
    private String abbreviation;

    Orientation( final String pLabel,
                       final String pAbbreviation ) {
        label = pLabel;
        abbreviation = pAbbreviation;
    }

    @Override
    public final String label() {
        return label;
    }

    @Override
    public Orientation valueOfLabel( final String text ) {
        return ( Orientation ) EnumUtilities.getLabeledEnumFromLabel(
                text, values() );
    }

    @Override
    public final String abbreviation() {
        return abbreviation;
    }

    @Override
    public Orientation valueOfAbbreviation(
            final String abbreviatedText ) {
        return ( Orientation ) EnumUtilities
                .getAbbreviatedEnumFromAbbreviation(
                        abbreviatedText, values() );
    }

    public static Orientation defaultValue() {
        return HORIZONTAL;
    }

    public static Orientation canonicalValueOf( final String canonicalOrientation ) {
        return ( canonicalOrientation != null )
            ? valueOf( canonicalOrientation.toUpperCase( Locale.ENGLISH ) )
            : defaultValue();
    }

    public final String toCanonicalString() {
        return toString().toLowerCase( Locale.ENGLISH );
    }
}
