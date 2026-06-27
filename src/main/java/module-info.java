/*
 * MIT License
 *
 * Copyright (c) 2026 Mark Schmieder. All rights reserved.
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
module fxgraphics {
    exports com.mhschmieder.fxgraphics;
    exports com.mhschmieder.fxgraphics.beans;
    exports com.mhschmieder.fxgraphics.canvas;
    exports com.mhschmieder.fxgraphics.collections;
    exports com.mhschmieder.fxgraphics.geometry;
    exports com.mhschmieder.fxgraphics.group;
    exports com.mhschmieder.fxgraphics.image;
    exports com.mhschmieder.fxgraphics.input;
    exports com.mhschmieder.fxgraphics.io;
    exports com.mhschmieder.fxgraphics.layers;
    exports com.mhschmieder.fxgraphics.paint;
    exports com.mhschmieder.fxgraphics.render;
    exports com.mhschmieder.fxgraphics.shape;
    exports com.mhschmieder.fxgraphics.svg;
    requires commons.math3;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.swing;
    requires jcommons;
    requires jgraphics;
    requires jmath;
    requires jphysics;
    requires org.jsoup;
}