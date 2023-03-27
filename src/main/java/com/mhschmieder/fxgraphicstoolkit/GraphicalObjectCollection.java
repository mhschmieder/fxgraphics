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
 * This file is part of the FxCommonsToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxCommonsToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcommonstoolkit
 */
package com.mhschmieder.fxgraphicstoolkit;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.mhschmieder.commonstoolkit.lang.LabeledObject;
import com.mhschmieder.commonstoolkit.text.TextUtilities;
import com.mhschmieder.fxgraphicstoolkit.geometry.GeometryUtilities;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerProperties;
import com.mhschmieder.fxgraphicstoolkit.layer.LayerUtilities;
import com.mhschmieder.fxgraphicstoolkit.shape.ShapeGroup;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The <code>GraphicalObjectCollection</code> class is the concrete
 * implementation base class for collections of Graphical Objects. It is a
 * "smart" collection which provides algorithms for selection set algorithms.
 */
public final class GraphicalObjectCollection< T extends GraphicalObject > {

    // Return the largest Bounding Box that encloses a collection.
    public static Bounds getBoundingBox( final Collection< ? extends GraphicalObject > collection ) {
        Bounds bbox = new BoundingBox( 0.0d, 0.0d, -1d, -1d );
        for ( final GraphicalObject graphicalObject : collection ) {
            // Keep enlarging the bounding box until it is a superset.
            final Bounds bbox2 = graphicalObject.getBoundingBox();
            bbox = GeometryUtilities.union( bbox, bbox2 );
        }

        return bbox;
    }

    // Return the Bounds that encloses all of collection's Reference Points.
    // This method ignores decorators and overall geometry and shapes.
    public static Bounds getTightContainment( final Collection< ? extends GraphicalObject > collection ) {
        Bounds bbox = new BoundingBox( 0.0d, 0.0d, -1d, -1d );
        for ( final GraphicalObject graphicalObject : collection ) {
            // Keep enlarging the bounding box until it is a superset.
            final Point2D point = graphicalObject.getReferencePoint2D();
            bbox = GeometryUtilities.updateBounds( bbox, point );
        }

        return bbox;
    }

    // Declare collections to hold graphical objects and selection sets.
    protected final Collection< T > _collection;
    protected final Collection< T > _selection;
    protected final Collection< T > _deselection;

    // Also declare a collection to serve as a clipboard for Cut/Copy/Paste.
    protected Collection< T >       _clipboard;

    // Default constructor, whose sole purpose is to avoid null pointers.
    public GraphicalObjectCollection() {
        // Use synchronous thread-safe unordered collections.
        _collection = new HashSet<>( 20 );
        _selection = new HashSet<>( 20 );
        _deselection = new HashSet<>( 20 );

        // The clipboard should be empty initially, until Cut/Copy.
        _clipboard = new HashSet<>( 0 );
    }

    public GraphicalObjectCollection( final Collection< T > collection,
                                      final Collection< T > selection,
                                      final Collection< T > deselection ) {
        // Construct the sets before assigning their contents.
        this();

        // Clone the contents of the sets from the source collections.
        set( collection, selection, deselection );
    }

    // NOTE: The copy constructor is needed for undo/redo!
    public GraphicalObjectCollection( final GraphicalObjectCollection< T > graphicalObjectCollection ) {
        // Construct the sets before assigning their contents.
        this();

        // Clone the contents of the sets from the source collections.
        set( graphicalObjectCollection );
    }

    // Add Graphical Object to the collection of Graphical Objects.
    public void addToCollection( final T graphicalObject ) {
        _collection.add( graphicalObject );
    }

    // Add Graphical Object to the deselection of Graphical Objects.
    public void addToDeselection( final T graphicalObject ) {
        _deselection.add( graphicalObject );
        graphicalObject.setSelected( false );
    }

    // Add Graphical Object to the selection of Graphical Objects.
    public void addToSelection( final T graphicalObject ) {
        _selection.add( graphicalObject );
        graphicalObject.setSelected( true );
    }

    // Toggle the selection set of Graphical Objects by a filter set.
    public void addToSelectionWithToggle( final Collection< T > filterSet ) {
        filterSet.forEach( filterObject -> addToSelectionWithToggle( filterObject ) );
    }

    // Toggle the selection status of a Graphical Object within its set.
    public void addToSelectionWithToggle( final T filterObject ) {
        // Detect whether this Graphical Object is already present.
        final boolean redundantObject = _selection.stream()
                .anyMatch( graphicalObject -> graphicalObject.equals( filterObject ) );

        // Conditionally add this Graphical Object to the selection set, if not
        // already present, or remove it, if already present.
        if ( redundantObject ) {
            _selection.remove( filterObject );
        }
        else {
            _selection.add( filterObject );
        }

        // Select the filtered object if not redundant.
        filterObject.setSelected( !redundantObject );
    }

    // Reassign the Layer in all selected objects, and then update the displayed
    // color to match, accounting for Layer Lock status and current background
    // color masking.
    public void applyLayerToSelectedObjects( final LayerProperties layer,
                                             final Color backColor,
                                             final Color defaultColor ) {
        _selection.forEach( graphicalObject -> {
            graphicalObject.setLayer( layer );
            graphicalObject.updateLockedStatus( backColor, defaultColor );
        } );
    }

    // Clear the selection set of Graphical Objects for the next selection, and
    // move it into the deselection set for later recall if necessary.
    public void clearSelection() {
        // If the selection set is empty, avoid side effects by exiting.
        if ( _selection.size() == 0 ) {
            return;
        }

        // Clear the current deselection before replacing with the selection.
        _deselection.clear();

        // NOTE: It is safer to avoid parallel streams right after clearing one
        // of the collections as a bulk action.
        _selection.stream().forEach( graphicalObject -> addToDeselection( graphicalObject ) );

        // Clear the current selection set for the next user action.
        _selection.clear();
    }

    public void deepCloneSelectedObjects() {
        // Deep-clone the selected Graphical Objects before modifying them, so
        // that their pre-edit and post-edit states are distinct objects (vs.
        // reference counting on the same underlying object).
        // TODO: Verify there are no memory leaks with the pre-cloned objects.
        final Collection< T > graphicalObjectClones = getDeepClonedSelection();
        replaceReferences( graphicalObjectClones );
    }

    // Delete all of the Graphical Objects from the collection.
    public void deleteAll() {
        // Delete all of the Graphical Objects from the collection.
        _collection.clear();

        // Clear the selection set of Graphical Objects for the next selection.
        _selection.clear();

        // Clear the deselection set of Graphical Objects for the next
        // deselection.
        _deselection.clear();
    }

    // Delete the selection set of Graphical Objects from the collection.
    public void deleteSelectedObjects() {
        // If the selection set is empty, avoid side effects by exiting.
        if ( _selection.size() == 0 ) {
            return;
        }

        // Filter the selection for Graphical Objects that are not editable.
        final Set< T > deletableObjects = _selection.stream().filter( T::isEditable )
                .collect( Collectors.toSet() );

        // Delete the selection set of Graphical Objects from the collection.
        _collection.removeAll( deletableObjects );

        // Clear the selection set of Graphical Objects for the next selection,
        // and mark all selected objects as unselected (in case any were
        // filtered due to being on a locked layer).
        clearSelection();

        // Clear the deselection set of Graphical Objects for the next
        // deselection.
        _deselection.clear();
    }

    // Deselect any selected objects that are now on a Locked or Hidden Layer.
    public void deselectLockedAndHiddenObjects() {
        // Filter the selection for objects that are locked or hidden.
        // NOTE: The context of invocation isn't thread-safe and is highly
        // re-entrant, so avoid parallel streams here to avoid freeze-ups.
        final Set< T > lockedAndHiddenObjects = _selection.stream()
                .filter( graphicalObject -> !graphicalObject.isEditable() )
                .collect( Collectors.toSet() );

        // NOTE: Do not combine with the above, as this action modifies the
        // source of the stream filter, which introduces concurrency issues.
        lockedAndHiddenObjects.forEach( graphicalObject -> removeFromSelection( graphicalObject ) );
    }

    /**
     * Drag the entire selection set by the indicated amounts (in meters).
     *
     * @param deltaX
     *            The amount to drag along the x-axis, in meters
     * @param deltaY
     *            The amount to drag along the y-axis, in meters
     */
    public void dragSelection( final double deltaX, final double deltaY ) {
        _selection.stream().filter( T::isEditable )
                .forEach( graphicalObject -> graphicalObject.drag( deltaX, deltaY ) );
    }

    // Fill the selection set of Graphical Objects.
    // NOTE: We exclude objects from auto-selection that cannot be edited, but
    // these can still be selected individually.
    public void fillSelection() {
        _selection.clear();
        _deselection.clear();

        // NOTE: It is safer to avoid parallel streams right after clearing one
        // of the collections as a bulk action.
        _collection.stream().filter( T::isEditable )
                .forEach( graphicalObject -> {
                    addToSelection( graphicalObject );
                    _deselection.add( graphicalObject );
                } );
    }

    /**
     * Filter the selection set of Graphical Objects to just those that are
     * within the specified area (and are editable).
     */
    public Collection< T > filterByArea( final Bounds filterArea, final boolean allowUneditable ) {
        // Build up a collection of candidates from all that are within the
        // specified area.
        // NOTE: Parallel streams are dangerous here, as we may be running from
        // a Prediction Service based thread.
        final Set< T > candidates = _selection.stream()
                .filter( graphicalObject -> graphicalObject.isFilterableByArea( filterArea,
                                                                                allowUneditable ) )
                .collect( Collectors.toSet() );
        return candidates;
    }

    // Filter a selection set for a specific Graphical Object.
    public void filterSelection( final T filterObject ) {
        // Clear the selection set of Graphical Objects for the current
        // selection, and move its objects into the deselection set for later
        // recall if necessary; filtering for the given Graphical Object to
        // avoid redundancy/inconsistency.
        // NOTE: Parallel streams could be non-deterministic here in terms of
        // invertibility of selection, deselection, and reselection.
        // TODO: Determine if this is the correct behavior for deselection.
        _deselection.clear();
        _selection.stream().filter( graphicalObject -> !graphicalObject.equals( filterObject ) )
                .forEach( graphicalObject -> addToDeselection( graphicalObject ) );
        _selection.clear();

        // If the filter is selected, reinsert it in the selection set.
        if ( ( filterObject != null ) && filterObject.isSelected() ) {
            _selection.add( filterObject );
        }
    }

    /**
     * Filter the selection set of Graphical Objects to just those that are
     * assigned to Visible Layers.
     */
    public Collection< T > filterVisible() {
        // Build up a collection of visible objects.
        // NOTE: Parallel streams are dangerous here, as we may be running from
        // a Prediction Service based thread.
        final Set< T > visibleObjects = _collection.stream().filter( GraphicalObject::isVisible )
                .collect( Collectors.toSet() );
        return visibleObjects;
    }

    // Return the largest Bounding Box that encloses the full collection.
    // TODO: Write a similar method for the bounding box of the selection?
    public Bounds getBoundingBox() {
        final Bounds bbox = getBoundingBox( _collection );
        return bbox;
    }

    public Collection< T > getClipboard() {
        return _clipboard;
    }

    public Collection< T > getCollection() {
        return _collection;
    }

    public String getCurrentLayerNameFromSelection( final String currentLayerName ) {
        String currentLayerNameCandidate = currentLayerName;
        if ( LayerUtilities.VARIOUS_LAYER_NAME.equals( currentLayerNameCandidate ) ) {
            return currentLayerNameCandidate;
        }

        for ( final T graphicalObject : _selection ) {
            final LayerProperties layer = graphicalObject.getLayer();
            final String layerName = layer.getLayerName();
            if ( currentLayerNameCandidate.isEmpty() ) {
                currentLayerNameCandidate = layerName;
            }
            else if ( !layerName.equals( currentLayerNameCandidate ) ) {
                currentLayerNameCandidate = LayerUtilities.VARIOUS_LAYER_NAME;
                break;
            }
        }

        return currentLayerNameCandidate;
    }

    @SuppressWarnings("unchecked")
    public Collection< T > getDeepClonedClipboard( final LayerProperties layer ) {
        // Deep-clone the Graphical Objects on the clipboard before modifying
        // them, so that their pre-edit and post-edit states are distinct
        // objects (vs. reference counting on the same underlying object).
        // TODO: Verify there are no memory leaks with the pre-cloned objects.
        final Collection< T > graphicalObjectClones = new HashSet<>( _clipboard.size() );

        // NOTE: This is called from Swing's Undo/Redo mechanism, so it isn't
        // safe to use Streams here (we got application freeze when we tried).
        _clipboard.forEach( graphicalObject -> graphicalObject
                .deepCloneToCollection( ( Collection< GraphicalObject > ) graphicalObjectClones ) );

        // Conditionally reassign all cloned objects to the specified Layer vs.
        // the Layer of the original objects, to avoid workflow edge cases.
        if ( layer != null ) {
            graphicalObjectClones.forEach( graphicalObject -> graphicalObject.setLayer( layer ) );
        }

        return graphicalObjectClones;
    }

    @SuppressWarnings("unchecked")
    public Collection< T > getDeepClonedSelection() {
        // Deep-clone the selected Graphical Objects before modifying them, so
        // that their pre-edit and post-edit states are distinct objects (vs.
        // reference counting on the same underlying object).
        // TODO: Verify there are no memory leaks with the pre-cloned objects.
        final Collection< T > graphicalObjectClones = new HashSet<>( _selection.size() );

        // NOTE: This is called from Swing's Undo/Redo mechanism, so it isn't
        // safe to use Streams here (we got application freeze when we tried).
        _selection.forEach( graphicalObject -> graphicalObject
                .deepCloneToCollection( ( Collection< GraphicalObject > ) graphicalObjectClones ) );

        return graphicalObjectClones;
    }

    public Collection< T > getDeselection() {
        return _deselection;
    }

    // Get the first available Graphical Object label from the base number.
    // NOTE: This method should only be invoked on labeled objects.
    public String getFirstAvailableLabel( final String graphicalObjectLabelDefault ) {
        return getNextAvailableLabel( graphicalObjectLabelDefault, 1 );
    }

    // Get the corrected label for a new Graphical Object in the collection.
    // NOTE: This method should only be invoked on labeled objects.
    public String getNewLabel( final String graphicalObjectLabelCandidate,
                               final String graphicalObjectLabelDefault ) {
        final String newGraphicalObjectLabelDefault = ( ( graphicalObjectLabelCandidate == null )
                || graphicalObjectLabelCandidate.trim().isEmpty() )
                    ? getNewLabelDefault( graphicalObjectLabelDefault )
                    : graphicalObjectLabelCandidate;

        return newGraphicalObjectLabelDefault;
    }

    // Get the default label for a new Graphical Object in the collection.
    // NOTE: This method should only be invoked on labeled objects.
    public String getNewLabelDefault( final String graphicalObjectLabelDefault ) {
        // Bump beyond the current count, as the new Graphical Object hasn't
        // been added to the collection yet.
        final int newGraphicalObjectNumber = _collection.size() + 1;
        final String newGraphicalObjectLabelDefault =
                                                    getNextAvailableLabel( graphicalObjectLabelDefault,
                                                                           newGraphicalObjectNumber );
        return newGraphicalObjectLabelDefault;
    }

    // Get the next available Graphical Object Label from the current number.
    // NOTE: This method should only be invoked on labeled objects.
    public String getNextAvailableLabel( final String graphicalObjectLabelDefault,
                                         final int graphicalObjectNumber ) {
        // Recursively search for (and enforce) name-uniqueness of the next
        // Graphical Object Label using the current number as the basis.
        String nextAvailableLabel = graphicalObjectLabelDefault + " " //$NON-NLS-1$
                + Integer.toString( graphicalObjectNumber );
        for ( final T graphicalObject : _collection ) {
            final LabeledObject labeledObject = ( LabeledObject ) graphicalObject;
            final String objectLabel = labeledObject.getLabel();
            if ( nextAvailableLabel.equals( objectLabel ) ) {
                // If the proposed label is not unique in the collection, bump
                // the Graphical Object Number recursively until unique.
                nextAvailableLabel = getNextAvailableLabel( graphicalObjectLabelDefault,
                                                            graphicalObjectNumber + 1 );
                break;
            }
        }

        return nextAvailableLabel;
    }

    // NOTE: This method treats multi-select as no selection, as otherwise an
    // ambiguous or arbitrary choice is made, or one that depends on the type of
    // Collection used to implement the Selection Set (which might change).
    public T getSelectedGraphicalObject() {
        final Collection< T > selection = getSelection();
        final T selectedGraphicalObject = ( selection != null ) && ( selection.size() == 1 )
            ? selection.iterator().next()
            : null;
        return selectedGraphicalObject;
    }

    public Collection< T > getSelection() {
        return _selection;
    }

    // NOTE: This method should only be invoked on labeled objects.
    public String getUniqueLabel( final String graphicalObjectLabelCandidate,
                                  final String graphicalObjectLabelToExclude,
                                  final int uniquefierNumber,
                                  final NumberFormat uniquefierNumberFormat ) {
        // Recursively search for (and enforce) uniqueness of the supplied
        // Graphical Object Label candidate and uniquefier number.
        final String uniquefierAppendix = TextUtilities
                .getUniquefierAppendix( uniquefierNumber, uniquefierNumberFormat );
        String uniqueGraphicalObjectLabel = graphicalObjectLabelCandidate + uniquefierAppendix;
        for ( final T graphicalObject : _collection ) {
            final LabeledObject labeledObject = ( LabeledObject ) graphicalObject;
            final String graphicalObjectLabel = labeledObject.getLabel();
            if ( !graphicalObjectLabel.equals( graphicalObjectLabelToExclude )
                    && graphicalObjectLabel.equals( uniqueGraphicalObjectLabel ) ) {
                // Recursively guarantee the appendix-adjusted label is also
                // unique, using a hopefully-unique number as the appendix.
                uniqueGraphicalObjectLabel = getUniqueLabel( graphicalObjectLabelCandidate,
                                                             graphicalObjectLabelToExclude,
                                                             uniquefierNumber + 1,
                                                             uniquefierNumberFormat );
                break;
            }
        }

        return uniqueGraphicalObjectLabel;
    }

    // NOTE: This method should only be invoked on labeled objects.
    public String getUniqueLabel( final String graphicalObjectLabelCandidate,
                                  final String graphicalObjectLabelToExclude,
                                  final NumberFormat uniquefierNumberFormat ) {
        // Only adorn the Graphical Object Label candidate if it is non-unique.
        final int uniquefierNumber = 0;
        final String uniqueGraphicalObjectLabel = getUniqueLabel( graphicalObjectLabelCandidate,
                                                                  graphicalObjectLabelToExclude,
                                                                  uniquefierNumber,
                                                                  uniquefierNumberFormat );

        return uniqueGraphicalObjectLabel;
    }

    // Get a unique Graphical Object Label from the candidate label.
    // NOTE: This method should only be invoked on labeled objects.
    public String getUniqueLabel( final String graphicalObjectLabelCandidate,
                                  final String graphicalObjectLabelDefault,
                                  final String graphicalObjectLabelToExclude,
                                  final NumberFormat uniquefierNumberFormat ) {
        // Recursively search for (and enforce) name-uniqueness of the Graphical
        // Object Label candidate, leaving unadorned if possible. If no label
        // candidate exists, start with a default label.
        final String uniqueGraphicalObjectLabel = ( ( graphicalObjectLabelCandidate == null )
                || graphicalObjectLabelCandidate.trim().isEmpty() )
                    ? getUniqueLabel( graphicalObjectLabelDefault,
                                      graphicalObjectLabelToExclude,
                                      uniquefierNumberFormat )
                    : getUniqueLabel( graphicalObjectLabelCandidate,
                                      graphicalObjectLabelToExclude,
                                      uniquefierNumberFormat );

        return uniqueGraphicalObjectLabel;
    }

    // Return the intersection of the selection set with another list of
    // Graphical Objects.
    public Collection< T > intersectSelection( final Collection< T > filterObjects ) {
        final Set< T > intersection = filterObjects.stream()
                .filter( graphicalObject -> _selection.contains( graphicalObject ) )
                .collect( Collectors.toSet() );

        return intersection;
    }

    /**
     * Given a proposed delta offset for each dimension, calculate the resulting
     * location of each Graphical Object in this collection and make sure that
     * none of them would end up being outside the supplied bounds.
     * <p>
     * This is done as a simple combined test of "too far left", "too far up",
     * "too far right", and "too far down", based on a single reference point.
     *
     * @param deltaX
     *            The offset along the x-axis of the proposed new location
     * @param deltaY
     *            The offset along the y-axis of the proposed new location
     * @param bounds
     *            The bounds that must contain the proposed new location
     * @return Whether or not the proposed new location falls within the
     *         supplied bounds
     */
    public boolean isDragTargetWithinBounds( final double deltaX,
                                             final double deltaY,
                                             final Bounds bounds ) {
        final boolean dragTargetOutsideBounds = _selection.stream()
                .anyMatch( graphicalObject -> !graphicalObject
                        .isDragTargetWithinBounds( deltaX, deltaY, bounds ) );

        return !dragTargetOutsideBounds;
    }

    public boolean isEmpty() {
        return _collection.isEmpty();
    }

    public boolean isGraphicalObjectSelected( final T graphicalObject ) {
        final Collection< T > selection = getSelection();
        final boolean graphicalObjectSelected = selection.contains( graphicalObject );
        return graphicalObjectSelected;
    }

    // NOTE: This method should only be invoked on labeled objects.
    public boolean isLabelUnique( final String graphicalObjectLabelCandidate ) {
        // Check whether the supplied Graphical Object Label candidate is unique
        // within the context of its type-specific collection.
        // NOTE: The context of invocation isn't thread-safe and is highly
        // re-entrant, so avoid parallel streams here to avoid freeze-ups.
        final boolean labelNotUnique = _collection.stream().anyMatch( graphicalObject -> {
            final LabeledObject labeledObject = ( LabeledObject ) graphicalObject;
            final String graphicalObjectLabel = labeledObject.getLabel();
            return ( graphicalObjectLabel.equals( graphicalObjectLabelCandidate ) );
        } );

        return !labelNotUnique;
    }

    /**
     * Reassign any Graphical Objects that are now on Deleted Layers.
     *
     * @param activeLayer
     *            The properties reference to the current Active Layer
     * @param layerCollection
     *            The full list of current Layers, for checking whether a
     *            Graphical Object's current assignment is to a Deleted Layer
     */
    public void reassignObjectsOnDeletedLayers( final LayerProperties activeLayer,
                                                final ObservableList< LayerProperties > layerCollection ) {
        // NOTE: The context of invocation isn't thread-safe and is highly
        // re-entrant, so avoid parallel streams here to avoid freeze-ups.
        _collection.stream().forEach( graphicalObject -> LayerUtilities
                .reassignObjectOnDeletedLayer( graphicalObject, layerCollection, activeLayer ) );
    }

    /**
     * Refill the selection set of Graphical Objects from the deselected set.
     */
    public void refillSelection() {
        // If the deselection set is empty, avoid side effects by exiting.
        if ( _deselection.size() == 0 ) {
            return;
        }

        // Clear the current selection before replacing with the deselection.
        _selection.clear();

        // NOTE: It is safer to avoid parallel streams right after clearing one
        // of the collections as a bulk action.
        _deselection.stream().forEach( graphicalObject -> addToSelection( graphicalObject ) );
    }

    /**
     * Remove a Graphical Object from the collection of Graphical Objects.
     *
     * @param graphicalObject
     *            The Graphical Object to be removed from the collection
     */
    public void removeFromCollection( final T graphicalObject ) {
        _collection.remove( graphicalObject );
        _selection.remove( graphicalObject );
        _deselection.remove( graphicalObject );
    }

    /**
     * Remove a Graphical Object from the selection of Graphical Objects.
     *
     * @param graphicalObject
     *            The Graphical Object to be removed from the selection
     */
    public void removeFromSelection( final T graphicalObject ) {
        _selection.remove( graphicalObject );
        graphicalObject.setSelected( false );
    }

    /**
     * This method removes all selected objects in the collection and prepares
     * to replace them with new references that then serve also as the new
     * selection. This is generally used in a deep-clone Undo/Redo context.
     *
     * @param graphicalObjects
     *            The list of new Graphical Object references to replace in the
     *            collection (generally these are clones, for Undo/Redo)
     */
    public void replaceReferences( final Collection< T > graphicalObjects ) {
        _collection.removeAll( _selection );
        _collection.addAll( graphicalObjects );
        setSelection( graphicalObjects );
    }

    // Clear all of the Graphical Objects from all of the collections.
    // TODO: Determine whether we also need to delete the objects.
    public void reset() {
        // Delete all of the Graphical Objects from the collection.
        _collection.clear();

        // Delete all of the Graphical Objects from the selection.
        _selection.clear();

        // Delete all of the Graphical Objects from the deselection.
        _deselection.clear();
    }

    /**
     * Rotate the entire selection set by the indicated amounts.
     *
     * @param deltaX
     *            The amount to drag along the x-axis, in meters
     * @param deltaY
     *            The amount to drag along the y-axis, in meters
     */
    public void rotateSelection( final double rotateX,
                                 final double rotateY,
                                 final double rotateThetaRelativeDegrees,
                                 final double deltaX,
                                 final double deltaY,
                                 final double cosTheta,
                                 final double sinTheta ) {
        _selection.stream().filter( GraphicalObject::isEditable )
                .forEach( graphicalObject -> graphicalObject.rotate( rotateX,
                                                                     rotateY,
                                                                     rotateThetaRelativeDegrees,
                                                                     deltaX,
                                                                     deltaY,
                                                                     cosTheta,
                                                                     sinTheta ) );
    }

    public void saveSelectionToClipboard() {
        _clipboard = getDeepClonedSelection();
    }

    /**
     * Select all Graphical Objects in the collection that are within the
     * specified area.
     */
    public Collection< T > selectByArea( final Bounds dragBoxInModelCoordinates,
                                         final boolean allowUneditable ) {
        // Build up a collection of candidates from all that are within the
        // specified area.
        // NOTE: Using parallel streams here can result in non-invertibility.
        final Set< T > candidates = _collection.stream()
                .filter( graphicalObject -> graphicalObject
                        .isFilterableByArea( dragBoxInModelCoordinates, allowUneditable ) )
                .collect( Collectors.toSet() );

        return candidates;
    }

    /**
     * Select all Graphical Objects in the collection that are within the
     * specified area.
     */
    public Collection< T > selectByArea( final Rectangle dragBoxInModelCoordinates,
                                         final boolean allowUneditable ) {
        // Build up a collection of candidates from all that are within the
        // specified area.
        // NOTE: Using parallel streams here can result in non-invertibility.
        final Set< T > candidates = _collection.stream()
                .filter( graphicalObject -> graphicalObject
                        .isFilterableByArea( dragBoxInModelCoordinates, allowUneditable ) )
                .collect( Collectors.toSet() );

        return candidates;
    }

    /**
     * Select all Graphical Objects in the collection that are within the
     * specified area.
     */
    public Collection< T > selectByArea( final Rectangle2D dragBoxInModelCoordinates,
                                         final boolean allowUneditable ) {
        // Build up a collection of candidates from all that are within the
        // specified area.
        // NOTE: Using parallel streams here can result in non-invertibility.
        final Set< T > candidates = _collection.stream()
                .filter( graphicalObject -> graphicalObject
                        .isFilterableByArea( dragBoxInModelCoordinates, allowUneditable ) )
                .collect( Collectors.toSet() );

        return candidates;
    }

    public void selectByAreaWithToggle( final Bounds dragBoxInModelCoordinates ) {
        // Build up a collection of editable candidates from all that are within
        // the specified area, and then toggle their selection status.
        // NOTE: Using parallel streams here can result in non-invertibility.
        _collection.stream()
                .filter( graphicalObject -> graphicalObject
                        .isFilterableByArea( dragBoxInModelCoordinates, false ) )
                .forEach( filterObject -> addToSelectionWithToggle( filterObject ) );
    }

    /**
     * Select all Graphical Objects in the collection that contain the point of
     * mouse click.
     */
    public Collection< T > selectByPoint( final Point2D clickPointMeters,
                                          final Bounds contextBounds,
                                          final boolean allowTightFitContainment ) {
        // Build up a collection of candidates from all that are within picking
        // distance.
        // NOTE: Using parallel streams here can result in non-invertibility.
        final Set< T > candidates = _collection.stream().filter( graphicalObject -> graphicalObject
                .isFilterableByPoint( clickPointMeters, contextBounds, allowTightFitContainment ) )
                .collect( Collectors.toSet() );

        return candidates;
    }

    /**
     * Preselect all Graphical Objects in the collection that contain the point
     * of mouse click, and then select the one that is closest.
     */
    public T selectClosestByPoint( final Point2D clickPointMeters,
                                   final Bounds contextBounds,
                                   final boolean allowTightFitContainment ) {
        // Build up a collection of candidates from all that are within picking
        // distance.
        final Collection< T > candidates = selectByPoint( clickPointMeters,
                                                          contextBounds,
                                                          allowTightFitContainment );

        // Search for the closest Graphical Object amongst all the candidates.
        T selection = null;
        for ( final T graphicalObject : candidates ) {
            // If the current object is closer, set it as the current selection.
            if ( graphicalObject.isCloserThan( selection, clickPointMeters ) ) {
                selection = graphicalObject;
            }
        }

        return selection;
    }

    /**
     * Select the first Graphical Object in the collection that contain the
     * point of mouse click.
     */
    public T selectFirstByPoint( final Point2D clickPointMeters, final Bounds contextBounds ) {
        // Search for the first Graphical Object amongst all the candidates,
        // that contains the point of mouse click.
        final T selection = _collection.stream()
                .filter( graphicalObject -> graphicalObject.contains( clickPointMeters ) )
                .findFirst().orElse( null );

        return selection;
    }

    public void set( final Collection< T > collection,
                     final Collection< T > selection,
                     final Collection< T > deselection ) {
        setCollection( collection );
        setDeselection( selection );
        setSelection( deselection );
    }

    // NOTE: The global set method is needed for Undo/Redo!
    public void set( final GraphicalObjectCollection< T > graphicalObjectCollection ) {
        if ( graphicalObjectCollection != null ) {
            set( graphicalObjectCollection.getCollection(),
                 graphicalObjectCollection.getDeselection(),
                 graphicalObjectCollection.getSelection() );
        }
    }

    public void setCollection( final Collection< T > collection ) {
        _collection.clear();
        if ( ( collection != null ) ) {
            // This is used by Undo/Redo, so must respect the encounter order.
            _collection.addAll( collection );
        }
    }

    public void setDeselection( final Collection< T > deselection ) {
        _deselection.clear();
        if ( ( deselection != null ) ) {
            // This is used by Undo/Redo, so must respect the encounter order.
            _deselection.addAll( deselection );
        }
    }

    public void setSelection( final Collection< T > selection ) {
        _selection.clear();
        if ( ( selection != null ) ) {
            // This is used by Undo/Redo, so must respect the encounter order.
            _selection.addAll( selection );
        }
    }

    // Conditionally add or toggle a Graphical Object's selection status.
    public void toggleSelection( final T graphicalObject, final boolean toggleIfSelected ) {
        // It is legitimate to indirectly pass in a null pointer reference.
        if ( graphicalObject == null ) {
            return;
        }

        // Conditionally add or toggle an object's selection status. If it is
        // already selected, conditionally toggle it -- otherwise, select it as
        // a side effect of adding it to the selection set.
        if ( graphicalObject.isSelected() ) {
            if ( toggleIfSelected ) {
                removeFromSelection( graphicalObject );
            }
        }
        else {
            addToSelection( graphicalObject );
        }
    }

    public void updateAllStrokeWidths( final double graphicalObjectStrokeWidth,
                                       final double markerStrokeWidth ) {
        _collection.forEach( graphicalObject -> {
            final ShapeGroup graphicalNode = graphicalObject.getCachedGraphicalNode();
            if ( graphicalNode != null ) {
                graphicalNode.updateStrokeWidth( graphicalObjectStrokeWidth );
            }

            final ShapeGroup markerNode = graphicalObject.getCachedMarkerNode();
            if ( markerNode != null ) {
                markerNode.updateStrokeWidth( markerStrokeWidth );
            }

            // Regenerate the highlighting, as it is scale factor sensitive.
            graphicalObject.updateHighlighting();
        } );
    }

    /**
     * Loop through each collection and set the color of the associated
     * graphical nodes based on the locked status of the assigned Layer for each
     * Graphical Object.
     */
    public void updateLayerObjectLockedStatus( final Color backColor, final Color defaultColor ) {
        // NOTE: The context of invocation isn't thread-safe and is highly
        // re-entrant, so avoid parallel streams here to avoid freeze-ups.
        _collection.stream().forEach( graphicalObject -> graphicalObject
                .updateLockedStatus( backColor, defaultColor ) );
    }

    /**
     * Loop through the entire collection and set visibility of the associated
     * graphical nodes and markers based on the visibility of the assigned Layer
     * for each Graphical Object.
     */
    public void updateLayerObjectVisibility() {
        // NOTE: The context of invocation isn't thread-safe and is highly
        // re-entrant, so avoid parallel streams here to avoid freeze-ups.
        _collection.stream().forEach( GraphicalObject::updateVisibility );
    }

}
