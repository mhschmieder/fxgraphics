/**
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
package com.mhschmieder.fxgraphicstoolkit.shape;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;

/**
 * Utilities for working with SVG Shapes and Groups.
 */
public class SvgUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SvgUtilities() {}


    // Load the entire SVG file into a Group of JavaFX SVGPath nodes.
    // NOTE: The SVG is delivered as an HTML-based document.
    public static void makeSvgPathGroup( final Document doc,
                                         final Group svgGroup ) {
        // Get the SVG Path Elements from the SVG Document.
        final Elements pathElements = doc.getElementsByTag( "path" );

        // Load each SVG Path from the Path Elements and add to the Group
        // Layout.
        final List< Node > svgPaths = new ArrayList<>();
        for ( final Element element : pathElements ) {
            // Load the actual SVG Path from the SVG Path Element.
            final String path = element.attr( "d" );

            // TODO: See if we need to use the SVG path Attribute.
            // final String usage = element.attr( "id" );

            // Create a JavaFX SVG Path Node and set its content from the SVG
            // Document, along with preferred attributes.
            final SVGPath svgPath = new SVGPath();
            svgPath.setContent( path );
            svgPath.setStrokeWidth( 1.0d );
            svgPath.setFillRule( FillRule.NON_ZERO );
            svgPath.setFill( Color.TRANSPARENT );
            svgPath.setStroke( Color.BLACK );

            // Add this SVG Path to the overall collection of SVG Paths.
            svgPaths.add( svgPath );
        }

        // Replace the SVG group layers with the new SVG path Collection.
        svgGroup.getChildren().setAll( svgPaths );
    }
}
