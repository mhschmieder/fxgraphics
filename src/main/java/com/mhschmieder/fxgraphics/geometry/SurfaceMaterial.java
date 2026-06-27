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
 * This file is part of the fxgraphics Library
 *
 * You should have received a copy of the MIT License along with the fxgraphics
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxgraphics
 */
package com.mhschmieder.fxgraphics.geometry;

import com.mhschmieder.jcommons.lang.Abbreviated;
import com.mhschmieder.jcommons.lang.EnumUtilities;
import com.mhschmieder.jcommons.lang.Labeled;

/**
 * An enumeration of standard surface materials as specified in Harry Ferdinand, 
 * Olson's Acoustical Engineering textbook from 1957, designed to cover the
 * basic  range of acoustic absorption properties and other attributes, and
 * including  named materials that are composites of several layers.
 * <p>
 * Although frequently used in the context of physics for material properties,
 * or acoustics for absorption and other characteristics, surface materials are
 * also important to general CAD modeling, for visual representation/lighting.
 * <p>
 * TODO: Switch to more human-readable descriptions for the regular labels.
 */
public enum SurfaceMaterial implements Labeled< SurfaceMaterial >,
        Abbreviated< SurfaceMaterial > {
    ACOUSTIC_TILE_ON_RIGID_SURF_KF(
            "AcousticTileOnRigidSurfKF",
            "AcousticTileOnRigidSurfKF" ),
    BRICK_WALL_PAINTED_LB(
            "BrickWallPaintedLB",
            "BrickWallPaintedLB" ),
    BRICK_WALL_UNPAINTED_LB(
            "BrickWallUnpaintedLB",
            "BrickWallUnpaintedLB" ),
    CARPET_HEAVY_ON_CONCRETE_CH(
            "CarpetHeavyOnConcreteCH",
            "CarpetHeavyOnConcreteCH" ),
    CONCRETE_BLOCK_PAINTED_CH(
            "ConcreteBlockPaintedCH",
            "ConcreteBlockPaintedCH" ),
    CONCRETE_BLOCK_UNPAINTED_CH(
            "ConcreteBlockUnpaintedCH",
            "ConcreteBlockUnpaintedCH" ),
    PLASTER_ON_LATHE_CH(
            "PlasterOnLathCH",
            "PlasterOnLathCH" ),
    POURED_CONCRETE_PAINTED_LB(
            "PouredConcretePaintedLB",
            "PouredConcretePaintedLB" ),
    POURED_CONCRETE_UNPAINTED_LB(
            "PouredConcreteUnpaintedLB",
            "PouredConcreteUnpaintedLB" ),
    RIGID( "Rigid", "Rigid" ),
    VELOUR_TEN_OZ_PER_YARD_SQR_TOUCHING_WALL_CH(
            "VelourTenOzPerYardSqrTouchingWallCH",
            "VelourTenOzPerYardSqrTouchingWallCH" );

    private final String label;
    private final String abbreviation;

    SurfaceMaterial( final String pLabel,
                     final String pAbbreviation ) {
        label = pLabel;
        abbreviation = pAbbreviation;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public SurfaceMaterial valueOfLabel( final String text ) {
        return ( SurfaceMaterial ) EnumUtilities.getLabeledEnumFromLabel(
                text, values() );
    }

    @Override
    public String abbreviation() {
        return abbreviation;
    }

    @Override
    public SurfaceMaterial valueOfAbbreviation( final String abbreviatedText ) {
        return ( SurfaceMaterial ) EnumUtilities
                .getAbbreviatedEnumFromAbbreviation(
                        abbreviatedText, values() );
    }

    @Override
    public String toString() {
        // NOTE: This override takes care of displaying the current choice in
        //  its custom label form when a Combo Box is hosted by a Table Cell. It
        //  also addresses an issue with the Jackson parser if in a JSON file.
        return label();
    }

    public static SurfaceMaterial defaultValue() {
        return RIGID;
    }
}
