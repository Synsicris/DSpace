/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.dspace.app.rest.authorization.impl.IsItemEditable;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test of Is Item Editable Feature implementation.
 *
 * @author Mohamed Eskander (mohamed.eskander at 4science.it)
 */
public class IsItemEditableIT extends AbstractControllerIntegrationTest {

    private Item itemA;

    private Collection collection;

    private AuthorizationFeature isItemEditable;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ItemConverter itemConverter;

    @Autowired
    private Utils utils;

    @Autowired
    private AuthorizationFeatureService authorizationFeatureService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Community")
                                          .build();

        collection = CollectionBuilder.createCollection(context, parentCommunity)
                                      .withEntityType("publication")
                                      .withName("collection")
                                      .build();

        itemA = ItemBuilder.createItem(context, collection)
                           .build();

        context.restoreAuthSystemState();

        isItemEditable = authorizationFeatureService.find(IsItemEditable.NAME);

    }

    @Test
    public void testIsItemEditable() throws Exception {

        context.setCurrentUser(admin);

        configurationService.setProperty("project.entity.edit-mode", "MODE1");

        ItemRest itemRest = itemConverter.convert(itemA, Projection.DEFAULT);

        boolean isEditable = isItemEditable.isAuthorized(context, itemRest);

        assertThat(isEditable, is(true));

    }

    @Test
    public void testIsItemNotEditable() throws Exception {

        context.setCurrentUser(eperson);

        ItemRest itemRest = itemConverter.convert(itemA, Projection.DEFAULT);

        boolean isEditable = isItemEditable.isAuthorized(context, itemRest);

        assertThat(isEditable, is(false));

    }

    @Test
    public void testIsItemEditableWithWrongPropertyValue() throws Exception {

        configurationService.setProperty("project.entity.edit-mode", "CUSTOM");

        ItemRest itemRest = itemConverter.convert(itemA, Projection.DEFAULT);

        boolean isEditable = isItemEditable.isAuthorized(context, itemRest);

        assertThat(isEditable, is(false));

    }

}
