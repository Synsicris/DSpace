/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.dspace.project.util.ProjectConstants.MD_FUNDER_POLICY_GROUP;
import static org.dspace.project.util.ProjectConstants.MD_RELATION_COMMENT_PROJECT;
import static org.dspace.project.util.ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;

import org.dspace.app.matcher.MetadataValueMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.CommentPolicyConsumer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * test against {@link CommentPolicyConsumer}.
 *
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CommentPolicyConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

    private Collection collection;
    private Collection projectCollection;
    private Community sharedCommunity;
    private Community projectACommunity;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        context.turnOffAuthorisationSystem();

        // create projects community
        parentCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("Projects")
                .build();
        // crate project community
        projectACommunity =
            CommunityBuilder.createSubCommunity(context, parentCommunity)
            .withName("Project a")
            .build();
        // create project collection
        projectCollection =
            CollectionBuilder.createCollection(context, projectACommunity)
                .withName("Consortia")
                .withEntityType(ProjectConstants.PROJECT_ENTITY)
                .build();

        // create shared community
        sharedCommunity =
            CommunityBuilder.createCommunity(context)
                .withName("Shared")
                .build();
        // create comments collection
        collection =
            CollectionBuilder.createCollection(context, sharedCommunity)
            .withName("Comments")
            .withEntityType(ProjectConstants.COMMENT_ENTITY)
            .build();

        context.restoreAuthSystemState();

    }

    @Test
    public void testCreateCommentItem() throws Exception {

        context.turnOffAuthorisationSystem();

        Item commentItem =
            ItemBuilder.createItem(context, collection)
                .withTitle("New Comment")
                .build();

        context.restoreAuthSystemState();

        assertNotNull(commentItem);
    }

    @Test
    public void testCreateCommentProjectItem() throws Exception {

        context.turnOffAuthorisationSystem();

        Group fundersGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        PROJECT_FUNDERS_GROUP_TEMPLATE,
                        projectACommunity.getID().toString()
                    )
                )
                .build();

        Item project =
            ItemBuilder.createItem(context, projectCollection)
                .withTitle("Project a")
                .build();

        Item commentItem =
            ItemBuilder.createItem(context, collection)
                .withTitle("New Comment")
                .withRelationCommentProject(project.getName(), project.getID().toString())
                .build();

        context.restoreAuthSystemState();

        assertNotNull(project);
        assertNotNull(commentItem);
        assertThat(
            this.itemService.getMetadata(
                commentItem, MD_RELATION_COMMENT_PROJECT.schema, MD_RELATION_COMMENT_PROJECT.element,
                MD_RELATION_COMMENT_PROJECT.qualifier, null
            ),
            hasItem(
                MetadataValueMatcher
                    .with(
                        MD_RELATION_COMMENT_PROJECT.toString(), project.getName(), project.getID().toString(),
                        Choices.CF_ACCEPTED
                    )
            )
        );
        assertThat(
            this.itemService.getMetadata(
                commentItem, MD_FUNDER_POLICY_GROUP.schema, MD_FUNDER_POLICY_GROUP.element,
                MD_FUNDER_POLICY_GROUP.qualifier, null
            ),
            hasItem(
                MetadataValueMatcher
                    .with(
                        MD_FUNDER_POLICY_GROUP.toString(),
                        fundersGroup.getName(),
                        fundersGroup.getID().toString(),
                        Choices.CF_ACCEPTED
                    )
            )
        );
    }
}
