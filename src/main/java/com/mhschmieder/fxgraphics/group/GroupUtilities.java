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
package com.mhschmieder.fxgraphics.group;

import javafx.scene.Group;

public final class GroupUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private GroupUtilities() {}

    /**
     * This method initializes the persistent shared attributes of decorator
     * node groups, which generally are application managed and non-interactive.
     *
     * @param decoratorNodeGroup
     *            The decorator node group whose persistent shared attributes
     *            are to be set at initialization time
     */
    public static void initDecoratorNodeGroup( final Group decoratorNodeGroup ) {
        // Mark the decorator node group as unmanaged, as its preferred size
        // changes should not affect our layout, and as otherwise changes to
        // Distance Unit can create interim states that we never recover from
        // due to JavaFX making layout decisions for managed nodes/groups.
        decoratorNodeGroup.setManaged( false );

        // Do not auto-size decorator node group children, as we are managing
        // the nodes ourselves, and as otherwise changes to Distance Unit can
        // create interim states that we never recover from due to JavaFX making
        // layout decisions for auto-sized children.
        decoratorNodeGroup.setAutoSizeChildren( false );

        // For now, we do not allow mouse-picking of decorator node groups.
        decoratorNodeGroup.setMouseTransparent( true );
        decoratorNodeGroup.setPickOnBounds( false );
    }
}
