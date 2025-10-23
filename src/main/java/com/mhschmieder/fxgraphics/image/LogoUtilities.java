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
package com.mhschmieder.fxgraphics.image;

import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * {@code LogoUtilities} is a utility class for logo management in JavaFX.
 */
public final class LogoUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private LogoUtilities() {}

    // Switch black vs. white Logo based on Background Color hue analysis.
    public static ImageView getLogoImageView( final Color backColor,
                                              final String jarRelativeLightLogoUrl,
                                              final String jarRelativeDarkLogoUrl ) {
        final String jarRelativeLogoFilename = ColorUtilities.isColorDark( backColor )
            ? jarRelativeLightLogoUrl
            : jarRelativeDarkLogoUrl;

        // Load the Logo Image as a JAR-resident resource.
        // NOTE: Don't apply smoothing or caching, as it results in a fuzzy logo.
        return createLogo( jarRelativeLogoFilename );
    }

    /**
     * Create a Logo as an Image View, using a JAR-resident resource.
     *
     * @param jarRelativeLogoFilename
     *            The file name of the image icon file contained in this JAR
     * @return The Image View created from the supplied file name
     */
    public static ImageView createLogo( final String jarRelativeLogoFilename ) {
        // Background-load the Logo into an Image View container.
        return ImageUtilities.getImageView( jarRelativeLogoFilename, true );
    }
}
