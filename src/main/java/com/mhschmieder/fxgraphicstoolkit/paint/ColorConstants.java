/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
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
 * This file is part of the FxGuiToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * GuiToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxguitoolkit
 */
package com.mhschmieder.fxgraphicstoolkit.paint;

import javafx.scene.paint.Color;

/**
 * {@code ColorConstants} is a container for constants related to JavaFX color
 * handling, such as lookup indices for individual color components of HSB.
 * <p>
 * TODO: Bring these more in line with the better-documented color constants
 * done for the GraphicsToolkit library for the Graphics2D API in Java.
 *
 * @version 0.1
 *
 * @author Mark Schmieder
 */
public class ColorConstants {

    /**
     * The default constructor is disabled, as this is a static constants class.
     */
    private ColorConstants() {}

    // NOTE: This is a hack used for filling closed shapes, until we can figure
    // out why those don't paint their outside strokes in some contexts and thus
    // show up blank if their fill is set to null. This semi-transparent
    // mid-gray is meant to strike a neutral balance that we can live with.
    public static final Color NEUTRAL_FILL                       =
                                           new Color( 0.5d, 0.5d, 0.5d, 0.5d );

    // NOTE: These colors are meant to fill gaps in the official CSS palette.
    // NOTE: Gray 80% is rounded down so that it is closer to being in between
    // Gray 75% and Gainsboro (which is roughly between Gray 82% and Gray 83%).
    public static final Color BLUEGRAY                           = Color.rgb( 169, 176, 190 );
    public static final Color BRIGHTBLUE                         = Color.rgb( 10, 10, 255 );
    public static final Color BRIGHTCYAN                         = Color.rgb( 10, 255, 255 );
    public static final Color BRIGHTGREEN                        = Color.rgb( 10, 255, 10 );
    public static final Color BRIGHTMAGENTA                      = Color.rgb( 255, 10, 255 );
    public static final Color BRIGHTORANGE                       = Color.rgb( 255, 128, 10 );
    public static final Color BRIGHTRED                          = Color.rgb( 255, 10, 10 );
    public static final Color BRIGHTYELLOW                       = Color.rgb( 255, 255, 10 );
    public static final Color BURGUNDY                           = Color.rgb( 62, 16, 16 );
    public static final Color CHARCOAL                           = Color.rgb( 54, 69, 79 );
    public static final Color DARKBLUEGRAY                       = Color.rgb( 126, 136, 156 );
    public static final Color DARKDODGERBLUE                     = Color.rgb( 17, 87, 225 );
    public static final Color DARKLEMON                          = Color.rgb( 96, 96, 32 );
    public static final Color DARKROYALBLUE                      = Color.rgb( 62, 86, 151 );
    public static final Color DARKSKYBLUE                        = Color.rgb( 33, 111, 225 );
    public static final Color DARKSPRINGGREEN                    = Color.rgb( 00, 163, 33 );
    public static final Color DIMBURGUNDY                        = Color.rgb( 98, 24, 24 );
    public static final Color DIMCYAN                            = Color.rgb( 10, 128, 128 );
    public static final Color DIMOLIVE                           = Color.rgb( 128, 128, 10 );
    public static final Color DIMORANGE                          = Color.rgb( 128, 70, 10 );
    public static final Color DIMPURPLE                          = Color.rgb( 128, 10, 128 );
    public static final Color GRAY05                             = Color.rgb( 13, 13, 13 ); // Night Mode, Heavy Gray
    public static final Color GRAY10                             = Color.rgb( 26, 26, 26 ); // US Navy Background Gray
    public static final Color GRAY15_5                           = Color.rgb( 39, 39, 39 ); // 15.5% Gray
    public static final Color GRAY17                             = Color.rgb( 43, 43, 43 ); // US Navy Very Dark Gray
    public static final Color GRAY19_5                           = Color.rgb( 49, 49, 49 ); // 19.5% Gray
    public static final Color GRAY20                             = Color.rgb( 51, 51, 51 ); // Dark Charcoal
    public static final Color GRAY20_5                           = Color.rgb( 52, 52, 52 ); // 20.5% Gray, Jet
    public static final Color GRAY21_5                           = Color.rgb( 55, 55, 55 ); // 21.5% Gray, Eclipse
    public static final Color GRAY23_5                           = Color.rgb( 60, 60, 60 ); // Onyx
    public static final Color GRAY25                             = Color.rgb( 64, 64, 64 );
    public static final Color GRAY30                             = Color.rgb( 77, 77, 77 );
    public static final Color GRAY32_5                           = Color.rgb( 83, 83, 83 ); // 32.5% Gray
    public static final Color GRAY33_3                           = Color.rgb( 85, 85, 85 ); // 1/3 Gray, Davy's Gray
    public static final Color GRAY37                             = Color.rgb( 94, 94, 94 ); // Scorpion
    public static final Color GRAY37_5                           = Color.rgb( 96, 96, 96 ); // 37.5% Gray, Shades On
    public static final Color GRAY40                             = Color.rgb( 102, 102, 102 );
    public static final Color GRAY40_5                           = Color.rgb( 103, 103, 103 ); // 40.5% Gray, Granite
    public static final Color GRAY41                             = Color.DIMGRAY; // Color.rgb( 105, 105, 105 )
    public static final Color GRAY42                             = Color.rgb( 107, 107, 107 ); // Anvil Gray
    public static final Color GRAY45                             = Color.rgb( 115, 115, 115 );
    public static final Color GRAY46                             = Color.rgb( 117, 117, 117 ); // Sonic Gray/Silver
    public static final Color GRAY50                             = Color.GRAY; // Color.rgb( 128, 128, 128 )
    public static final Color GRAY55                             = Color.rgb( 140, 140, 140 );
    public static final Color GRAY59                             = Color.rgb( 151, 151, 151 ); // Solid Gray
    public static final Color GRAY59_5                           = Color.rgb( 152, 152, 152 ); // 59.5% Gray, Spanish Gray
    public static final Color GRAY60                             = Color.rgb( 153, 153, 153 );
    public static final Color GRAY60_5                           = Color.rgb( 155, 155, 155 ); // 60.5% Gray
    public static final Color GRAY63                             = Color.rgb( 161, 161, 161 ); // Perfect Gray
    public static final Color GRAY65                             = Color.rgb( 166, 166, 166 ); // Quick Silver
    public static final Color GRAY66                             = Color.DARKGRAY; // Color.rgb( 169, 169, 169 )
    public static final Color GRAY66_6                           = Color.rgb( 170, 170, 170 ); // 2/3 Gray
    public static final Color GRAY67                             = Color.rgb( 172, 172, 172 ); // Silver Chalice
    public static final Color GRAY69_5                           = Color.rgb( 177, 177, 177 ); // 69.5% Gray
    public static final Color GRAY74_5                           = Color.rgb( 190, 190, 190 ); // 74.5% Gray, Medium Gray
    public static final Color GRAY75_5                           = Color.SILVER; // Color.rgb( 192, 192, 192 )
    public static final Color GRAY79_5                           = Color.rgb( 203, 203, 203 ); // 79.5% Gray
    public static final Color GRAY82_5                           = Color.LIGHTGRAY; // Color.rgb( 211, 211, 211 )
    public static final Color GRAY84_5                           = Color.rgb( 216, 216, 216 ); // 84.5% Gray
    public static final Color GRAY85_5                           = Color.rgb( 218, 218, 218 ); // 85.5% Gray, Gentle Gray
    public static final Color GRAY86                             = Color.GAINSBORO; // Color.rgb( 220, 220, 220 )
    public static final Color GRAY90_5                           = Color.rgb( 231, 231, 231 ); // 90.5% Gray, Day Mode
    public static final Color GRAY92                             = Color.rgb( 236, 236, 236 ); // Smoky White
    public static final Color GRAY94                             = Color.rgb( 240, 240, 240 ); // Smoky White
    public static final Color GRAY96                             = Color.WHITESMOKE; // Color.rgb( 245, 245, 245 )
    public static final Color LEMON                              = Color.rgb( 202, 202, 70 );
    public static final Color LIGHTBLUEGRAY                      = Color.rgb( 212, 216, 224 );
    public static final Color LIGHTBURGUNDY                      = Color.rgb( 124, 32, 32 );
    public static final Color LIGHTLEMON                         = Color.rgb( 255, 255, 86 );
    public static final Color PALEBURGUNDY                       = Color.rgb( 156, 40, 40 );
    public static final Color PALESTEELBLUE                      = Color.rgb( 202, 225, 255 );

    // Predefine the table header colors.
    public static final Color TABLE_HEADER_BACKGROUND_COLOR      = DARKROYALBLUE;
    public static final Color TABLE_HEADER_FOREGROUND_COLOR      = Color.WHITE;

    // Predefine the table cell colors.
    public static final Color TABLE_CELL_BACKGROUND_COLOR        = Color.WHITE;
    public static final Color TABLE_CELL_FOREGROUND_COLOR        = Color.BLACK;

    // Predefine the notes/notices colors.
    public static final Color NOTES_BACKGROUND_COLOR             = Color.FLORALWHITE;
    public static final Color NOTES_FOREGROUND_COLOR             = Color.BLACK;

    // Predefine the polarity "reversed" and "normal" toggle button colors.
    public static final Color POLARITY_NORMAL_BACKGROUND_COLOR   = GRAY15_5;
    public static final Color POLARITY_NORMAL_FOREGROUND_COLOR   = Color.WHITE;

    public static final Color POLARITY_REVERSED_BACKGROUND_COLOR = GRAY84_5;
    public static final Color POLARITY_REVERSED_FOREGROUND_COLOR = Color.BLACK;

    // Predefine the mute switch "muted" and "unmuted" toggle button colors.
    public static final Color UNMUTED_BACKGROUND_COLOR           = DIMBURGUNDY;
    public static final Color UNMUTED_FOREGROUND_COLOR           = Color.WHITE;

    public static final Color MUTED_BACKGROUND_COLOR             = Color.RED;
    public static final Color MUTED_FOREGROUND_COLOR             = Color.WHITE;

    // Predefine the status "active" and "inactive" toggle button colors.
    public static final Color INACTIVE_BACKGROUND_COLOR          = GRAY30;
    public static final Color INACTIVE_FOREGROUND_COLOR          = Color.WHITE;

    public static final Color ACTIVE_BACKGROUND_COLOR            = BRIGHTGREEN;
    public static final Color ACTIVE_FOREGROUND_COLOR            = Color.BLUE;

    // Predefine the display "visible" and "hidden" toggle button colors.
    public static final Color VISIBLE_BACKGROUND_COLOR           = BRIGHTYELLOW;
    public static final Color VISIBLE_FOREGROUND_COLOR           = Color.BLUE;

    public static final Color HIDDEN_BACKGROUND_COLOR            = Color.BLUE;
    public static final Color HIDDEN_FOREGROUND_COLOR            = BRIGHTYELLOW;

    // Predefine the lock "locked" and "unlocked" toggle button colors.
    public static final Color UNLOCKED_BACKGROUND_COLOR          = BRIGHTYELLOW;
    public static final Color UNLOCKED_FOREGROUND_COLOR          = BRIGHTGREEN;

    public static final Color LOCKED_BACKGROUND_COLOR            = BRIGHTRED;
    public static final Color LOCKED_FOREGROUND_COLOR            = BRIGHTYELLOW;

    // Predefine the status "enabled" and "bypassed" toggle button colors.
    public static final Color ENABLED_BACKGROUND_COLOR           = Color.OLIVE;
    public static final Color ENABLED_FOREGROUND_COLOR           = Color.WHITE;

    public static final Color BYPASSED_BACKGROUND_COLOR          = BRIGHTYELLOW;
    public static final Color BYPASSED_FOREGROUND_COLOR          = Color.BLACK;

    // Predefine object settings oriented action button background and
    // foreground colors.
    public static final Color SAVE_ACTION_BACKGROUND_COLOR       = Color.LIGHTSEAGREEN;
    public static final Color SAVE_ACTION_FOREGROUND_COLOR       = Color.WHITE;

    public static final Color LOAD_ACTION_BACKGROUND_COLOR       = Color.MEDIUMORCHID;
    public static final Color LOAD_ACTION_FOREGROUND_COLOR       = Color.WHITE;

    public static final Color RESET_ACTION_BACKGROUND_COLOR      = Color.DARKRED;
    public static final Color RESET_ACTION_FOREGROUND_COLOR      = Color.WHITE;

    public static final Color RECALL_ACTION_BACKGROUND_COLOR     = Color.ORANGE;
    public static final Color RECALL_ACTION_FOREGROUND_COLOR     = Color.WHITE;

    public static final Color CREATE_ACTION_BACKGROUND_COLOR     = DARKDODGERBLUE;
    public static final Color CREATE_ACTION_FOREGROUND_COLOR     = Color.WHITE;

    public static final Color STORE_ACTION_BACKGROUND_COLOR      = DARKDODGERBLUE;
    public static final Color STORE_ACTION_FOREGROUND_COLOR      = Color.WHITE;

    public static final Color INSERT_ACTION_BACKGROUND_COLOR     = CREATE_ACTION_BACKGROUND_COLOR;
    public static final Color INSERT_ACTION_FOREGROUND_COLOR     = CREATE_ACTION_FOREGROUND_COLOR;

    public static final Color DELETE_ACTION_BACKGROUND_COLOR     = Color.CRIMSON;
    public static final Color DELETE_ACTION_FOREGROUND_COLOR     = Color.WHITE;

    public static final Color EDIT_ACTION_BACKGROUND_COLOR       = RECALL_ACTION_BACKGROUND_COLOR;
    public static final Color EDIT_ACTION_FOREGROUND_COLOR       = RECALL_ACTION_FOREGROUND_COLOR;

    public static final Color APPLY_ACTION_BACKGROUND_COLOR      = SAVE_ACTION_BACKGROUND_COLOR;
    public static final Color APPLY_ACTION_FOREGROUND_COLOR      = SAVE_ACTION_FOREGROUND_COLOR;

    public static final Color DONE_ACTION_BACKGROUND_COLOR       = STORE_ACTION_BACKGROUND_COLOR;
    public static final Color DONE_ACTION_FOREGROUND_COLOR       = STORE_ACTION_FOREGROUND_COLOR;

    public static final Color REVERT_ACTION_BACKGROUND_COLOR     = DARKSPRINGGREEN;
    public static final Color REVERT_ACTION_FOREGROUND_COLOR     = Color.WHITE;

    public static final Color OK_ACTION_BACKGROUND_COLOR         = CREATE_ACTION_BACKGROUND_COLOR;
    public static final Color OK_ACTION_FOREGROUND_COLOR         = CREATE_ACTION_FOREGROUND_COLOR;

    public static final Color CANCEL_ACTION_BACKGROUND_COLOR     = DELETE_ACTION_BACKGROUND_COLOR;
    public static final Color CANCEL_ACTION_FOREGROUND_COLOR     = DELETE_ACTION_FOREGROUND_COLOR;

    // Predefine table row oriented action button background and foreground
    // colors.
    public static final Color INSERT_TABLE_ROW_BACKGROUND_COLOR  = INSERT_ACTION_BACKGROUND_COLOR;
    public static final Color INSERT_TABLE_ROW_FOREGROUND_COLOR  = INSERT_ACTION_FOREGROUND_COLOR;

    public static final Color DELETE_TABLE_ROW_BACKGROUND_COLOR  = DELETE_ACTION_BACKGROUND_COLOR;
    public static final Color DELETE_TABLE_ROW_FOREGROUND_COLOR  = DELETE_ACTION_FOREGROUND_COLOR;

    // NOTE: This is sort of an orphan that might apply more generically to
    // any targeted object of interest against a color-dense background.
    public static final Color TARGET_HIGHLIGHTED_COLOR           = Color.MAGENTA;

    // NOTE: This is from an old request for indicating locked objects.
    public static final Color OBJECT_LOCKED_COLOR                = Color.rgb( 191, 64, 64 );

    // These are approximate to commonly set Day and Night contrast mode colors.
    public static final Color DAY_MODE                           = GRAY90_5;
    public static final Color NIGHT_MODE                         = GRAY05;
    
    // Although these are common named grays, they aren't included in the CSS3 spec.
    public static final Color DARK_CHARCOAL                      = GRAY20;
    public static final Color DAVYS_GRAY                         = GRAY33_3;
    public static final Color SPANISH_GRAY                       = GRAY59_5;
    public static final Color MEDIUM_GRAY                        = GRAY74_5;
   
    // This is a common dark background color when black is too intense. 
    // It is 10% Gray. This is the background for the US Navy style guidelines,
    // which are a good reference as they canvassed everything out there.
    public static final Color NAVY_BACKGROUND_GRAY               = GRAY10;
    
    // This is another gray variant that is part of the US Navy standard but
    // isn't part of most official named gray lists as those focus more on the
    // lighter end of the gray spectrum overall.
    public static final Color NAVY_VERY_DARK_GRAY                = GRAY17;

    // NOTE: This is meant to be used as the default layout pane background in
    // windows that support custom colors per user, until a new color is chosen.
    public static final Color DEFAULT_BACKGROUND_COLOR           = Color.WHITE;
    public static final Color DEFAULT_FOREGROUND_COLOR           = Color.BLACK;

    // NOTE: This is meant to be used as the layout pane background in windows
    // that don't support custom colors per user. It is designed to be a fairly
    // close match to Day Mode in modern apps, adjusted for ideal contrast.
    public static final Color WINDOW_BACKGROUND_COLOR            = Color.GAINSBORO;
    public static final Color WINDOW_FOREGROUND_COLOR            = Color.BLACK;

    public static final Color PREDICT_BACKGROUND_COLOR           = Color.rgb( 182, 231, 201 );
    public static final Color CANCEL_BACKGROUND_COLOR            = Color.rgb( 231, 182, 201 );
    public static final Color CLEAR_BACKGROUND_COLOR             = Color.rgb( 231, 231, 201 );

    public static final Color EDIT_NOTES_BACKGROUND_COLOR        = NOTES_BACKGROUND_COLOR;
    public static final Color EDIT_NOTES_FOREGROUND_COLOR        = NOTES_FOREGROUND_COLOR;

    public static final Color GRAPHICS_EXPORT_BACKGROUND_COLOR   = SAVE_ACTION_BACKGROUND_COLOR;
    public static final Color GRAPHICS_EXPORT_FOREGROUND_COLOR   = SAVE_ACTION_FOREGROUND_COLOR;

    public static final Color CANCEL_EXPORT_BACKGROUND_COLOR     = CANCEL_ACTION_BACKGROUND_COLOR;
    public static final Color CANCEL_EXPORT_FOREGROUND_COLOR     = CANCEL_ACTION_FOREGROUND_COLOR;

    public static final Color GRAPHICS_IMPORT_BACKGROUND_COLOR   = LOAD_ACTION_BACKGROUND_COLOR;
    public static final Color GRAPHICS_IMPORT_FOREGROUND_COLOR   = LOAD_ACTION_FOREGROUND_COLOR;

    public static final Color CANCEL_IMPORT_BACKGROUND_COLOR     = CANCEL_ACTION_BACKGROUND_COLOR;
    public static final Color CANCEL_IMPORT_FOREGROUND_COLOR     = CANCEL_ACTION_FOREGROUND_COLOR;

    public static final Color GENERATE_REPORT_BACKGROUND_COLOR   = SAVE_ACTION_BACKGROUND_COLOR;
    public static final Color GENERATE_REPORT_FOREGROUND_COLOR   = SAVE_ACTION_FOREGROUND_COLOR;

    public static final Color CANCEL_REPORT_BACKGROUND_COLOR     = CANCEL_ACTION_BACKGROUND_COLOR;
    public static final Color CANCEL_REPORT_FOREGROUND_COLOR     = CANCEL_ACTION_FOREGROUND_COLOR;

}
