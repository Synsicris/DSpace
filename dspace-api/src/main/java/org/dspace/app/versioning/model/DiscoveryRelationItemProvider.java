/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.model;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.discovery.configuration.DiscoveryConfigurationUtilsService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class DiscoveryRelationItemProvider implements ItemProvider {

    @Autowired
    private DiscoveryConfigurationUtilsService searchConfigurationUtilsService;

    protected final String relationName;

    public DiscoveryRelationItemProvider(String relationName) {
        super();
        this.relationName = relationName;
    }

    @Override
    public Stream<Item> retrieve(Context c, Item i) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                this.searchConfigurationUtilsService.findByRelation(c, i, relationName),
                Spliterator.ORDERED
            ),
            false
        );
    }

}
