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

import java.util.Locale;

/**
 * The <code>FacingDirection</code> class is an enumeration for facing direction
 * values for three dimensional objects in a two dimensional projection plane.
 *
 * NOTE: Other than for Presentation String, this is now redundant with JavaFX.
 */
public enum FacingDirection {
    RIGHT, LEFT;

    public static FacingDirection abbreviatedValueOf( final String abbreviatedFacingDirection ) {
        if ( "r".equalsIgnoreCase( abbreviatedFacingDirection ) ) { //$NON-NLS-1$
            return RIGHT;
        }
        else if ( "l".equalsIgnoreCase( abbreviatedFacingDirection ) ) { //$NON-NLS-1$
            return LEFT;
        }

        return defaultValue();
    }

    public static FacingDirection canonicalValueOf( final String canonicalFacingDirection ) {
        return ( canonicalFacingDirection != null )
            ? valueOf( canonicalFacingDirection.toUpperCase( Locale.ENGLISH ) )
            : defaultValue();
    }

    public static FacingDirection defaultValue() {
        return RIGHT;
    }

    public final String toAbbreviatedString() {
        String abbreviatedString = null;

        switch ( this ) {
        case RIGHT:
            abbreviatedString = "r"; //$NON-NLS-1$
            break;
        case LEFT:
            abbreviatedString = "l"; //$NON-NLS-1$
            break;
        default:
            final String errMessage = "Unexpected " //$NON-NLS-1$
                    + this.getClass().getSimpleName() + " " + this; //$NON-NLS-1$
            throw new IllegalArgumentException( errMessage );
        }

        return abbreviatedString;
    }

    public final String toCanonicalString() {
        return toString().toLowerCase( Locale.ENGLISH );
    }

    public final String toPresentationString() {
        String presentationString = null;

        switch ( this ) {
        case RIGHT:
            presentationString = "Right"; //$NON-NLS-1$
            break;
        case LEFT:
            presentationString = "Left"; //$NON-NLS-1$
            break;
        default:
            final String errMessage = "Unexpected " //$NON-NLS-1$
                    + this.getClass().getSimpleName() + " " + this; //$NON-NLS-1$
            throw new IllegalArgumentException( errMessage );
        }

        return presentationString;
    }

}
