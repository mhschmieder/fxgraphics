/**
 * MIT License
 *
 * Copyright (c) 2025 Mark Schmieder
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
package com.mhschmieder.fxgraphicstoolkit.beans;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;

/**
 * Factory for Observable Properties in the JavaFX Beans sub-package.
 */
public final class BeanFactory {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private BeanFactory() {}

    /**
     * Returns a {@link BooleanBinding} object with its binding dependencies set.
     * <p>
     * This starts observing the dependencies for changes. If the value of one of
     * the supplied dependencies changes, the binding is marked as invalid.
     * <p>
     * Call this method in the owning class for the returned {@link BooleanBinding}
     * instead of directly invoking the {@link BooleanBinding} constructor, to 
     * avoid otherwise identical boilerplate code that is verbose and that 
     * increases the distance in code between the observable properties being
     * constructed and their being passed as binding dependencies.
     *
     * @param dependencies
     *            the dependencies to observe on the {@link BooleanBinding}
     * @return a {@link BooleanBinding} object with its binding dependencies set
     */
   public static BooleanBinding makeBooleanBinding( final Observable... dependencies ) {
        // Establish the dirty flag criteria as a change to any listed dependency.
        return new BooleanBinding() {
            {
                // When any of these assignable values change, the Boolean Binding
                // is invalidated and notifies its listeners.
                super.bind( dependencies );
            }

            /**
             * Auto-clears the invalidation by overriding with a status that is
             * affirmative of a value change having triggered this call.
             *
             * @return true
             */
            @Override
            protected boolean computeValue() {
                return true;
            }
        };
    }
    
}
