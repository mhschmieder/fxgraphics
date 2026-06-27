/*
 * MIT License
 *
 * Copyright (c) 2024, 2026 Mark Schmieder. All rights reserved.
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
 * This file is part of the fxgraphics Library
 *
 * You should have received a copy of the MIT License along with the fxgraphics
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgraphics
 */
package com.mhschmieder.fxgraphics.image;

import com.mhschmieder.jmath.geometry.euclidean.LightSourceDirection;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

public final class BumpMapUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private BumpMapUtilities() {}

    public static void updateBumpMap(
            final PixelWriter writer,
            final int x,
            final int y,
            final boolean shadingActive,
            final double value,
            final double minValue,
            final double maxValue ) {
        if ( shadingActive ) {
            double norm = ( value - minValue ) / ( maxValue - minValue );
            norm = FastMath.min( 1.0d, FastMath.max( 0.0d, norm ) );
            writer.setColor( x, y, new Color( norm, norm, norm, 1.0d ) );
        }
    }

    public static void addBumpMapToImage(
            final boolean shadingActive,
            final WritableImage image,
            final WritableImage greyImage,
            final LightSourceDirection lightSourceDirection,
            final int heightScale) {
        if ( !shadingActive ) {
            return;
        }

        final Light.Distant light = new Light.Distant();
        double azimuth = 315.0d;
        switch ( lightSourceDirection ) {
            case NORTHEAST -> azimuth = 45.0d;
            case SOUTHEAST -> azimuth = 135.0d;
            case SOUTHWEST -> azimuth = 225.0d;
            case NORTHWEST -> azimuth = 315.0d;
        }
        light.setAzimuth( azimuth );
        light.setElevation( 45.0d );

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(heightScale);
        lighting.setBumpInput(new ImageInput( image ) );

        ImageView iv = new ImageView( image );
        iv.setEffect(lighting);

        final WritableImage result = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight()
        );
        iv.snapshot( new SnapshotParameters(), result );

        final PixelReader reader = result.getPixelReader();
        final PixelWriter writer = image.getPixelWriter();

        writer.setPixels( 0,
                0,
                (int) image.getWidth(),
                (int) image.getHeight(),
                reader,
                0,
                0);
    }
}
