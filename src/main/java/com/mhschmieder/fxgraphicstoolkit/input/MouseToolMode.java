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
package com.mhschmieder.fxgraphicstoolkit.input;

import java.util.Locale;

/**
 * The <code>MouseToolMode</code> enum is an enumeration of typical mouse modes.
 */
public enum MouseToolMode {
    SELECT, MOVE, ROTATE, ZOOM, PAN, DRAW, MEASURE, COPY, PASTE;

    @SuppressWarnings("nls")
    public static MouseToolMode canonicalValueOf( final String mouseMode ) {
        if ( mouseMode == null ) {
            return defaultValue();
        }
        else if ( "select".equalsIgnoreCase( mouseMode ) ) {
            return SELECT;
        }
        else if ( "move".equalsIgnoreCase( mouseMode ) ) {
            return MOVE;
        }
        else if ( "rotate".equalsIgnoreCase( mouseMode ) ) {
            return ROTATE;
        }
        else if ( "zoom".equalsIgnoreCase( mouseMode ) ) {
            return ZOOM;
        }
        else if ( "pan".equalsIgnoreCase( mouseMode ) ) {
            return PAN;
        }
        else if ( "draw".equalsIgnoreCase( mouseMode ) ) {
            return DRAW;
        }
        else if ( "measure".equalsIgnoreCase( mouseMode ) ) {
            return MEASURE;
        }
        else if ( "copy".equalsIgnoreCase( mouseMode ) ) {
            return COPY;
        }
        else if ( "paste".equalsIgnoreCase( mouseMode ) ) {
            return PASTE;
        }
        else {
            return valueOf( mouseMode.toUpperCase( Locale.ENGLISH ) );
        }
    }

    public static MouseToolMode defaultValue() {
        return SELECT;
    }

    @SuppressWarnings("nls")
    public final String toLabel() {
        String label = "select";
        
        switch ( this ) {
        case SELECT:
            label = "select";
            break;
        case MOVE:
            label = "move";
            break;
        case ROTATE:
            label = "rotate";
            break;
        case ZOOM:
            label = "zoom";
            break;
        case PAN:
            label = "pan";
            break;
        case DRAW:
            label = "draw";
            break;
        case MEASURE:
            label = "measure";
            break;
        case COPY:
            label = "copy";
            break;
        case PASTE:
            label = "paste";
            break;
        default:
            break;
        }
        
        return label;
    }

}
