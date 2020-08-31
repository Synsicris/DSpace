/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.filler;

import java.sql.SQLException;

import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;

/**
 * Interface for classes that enrich the item related entities created during an
 * item submission using additional logic.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public interface AuthorityImportFiller {

    /**
     * Returns true if the given item could be updated using the source metadata.
     *
     * @param context        the context
     * @param sourceMetadata the metadata related to the item to fill
     * @param itemToFill     the item to fill
     * @return true if the given item could be filled, false otherwise
     */
    boolean allowsUpdate(Context context, MetadataValue sourceMetadata, Item itemToFill);

    /**
     * Fill the given item using the source metadata.
     *
     * @param context        the context
     * @param sourceMetadata the metadata related to the item to fill
     * @param itemToFill     the item to fill
     * @throws SQLException if an error occurs during the metadata addition
     */
    void fillItem(Context context, MetadataValue sourceMetadata, Item itemToFill) throws SQLException;
}
