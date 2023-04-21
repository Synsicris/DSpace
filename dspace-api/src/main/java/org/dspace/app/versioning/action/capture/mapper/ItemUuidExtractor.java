/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.util.Optional;
import java.util.UUID;

import org.dspace.content.Item;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class ItemUuidExtractor implements ItemValueExtractor<String> {

    @Override
    public String apply(Item i) {
        return Optional.ofNullable(i)
                    .map(Item::getID)
                    .map(UUID::toString)
                    .orElse("");
    }

}
