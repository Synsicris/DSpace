/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.matcher.ItemMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class ProjectConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConfigurationService configurationService;

    private Community projectsCommunity;
    private Community subprojectsCommunity;
    private Community subprojectAComm;
    private Community subprojectBComm;
    private Collection publicationsCollection;
    @SuppressWarnings("unused")
    private Collection collectionSubProjectA;
    @SuppressWarnings("unused")
    private Collection collectionSubProjectB;

    @Test
    public void projectConsumerCreateNewItemWithSharedProjectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withEmail("example2@email.com")
                .withPassword(password).build();

        EPerson ePerson3 = EPersonBuilder.createEPerson(context)
                .withEmail("example3@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "###GROUP_POLICY_PLACEHOLDER###");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null, "project");

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson3).build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        context.setCurrentUser(ePerson1);
        Item item = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Item Title")
                               .withSharedProject("project")
                               .withPolicyGroup("###GROUP_POLICY_PLACEHOLDER###")
                               .build();

        context.restoreAuthSystemState();

        String authToken = getAuthToken(ePerson1.getEmail(), password);
        getClient(authToken).perform(get("/api/core/items/" + item.getID()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", ItemMatcher.matchItemPolicyGroupAndSharedMetadata(
                                                     item, "Item Title", "project_"
                                                     + projectsCommunity.getID().toString()
                                                     + "_members_group", "project")));
    }

    @Test
    public void projectConsumerCreateNewItemWithSharedSubProjectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withEmail("example2@email.com")
                .withPassword(password).build();

        EPerson ePerson3 = EPersonBuilder.createEPerson(context)
                .withEmail("example3@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "###GROUP_POLICY_PLACEHOLDER###");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null, "project");

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        configurationService.setProperty("project.subproject-community-name", subprojectsCommunity.getName());

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson3).build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        context.setCurrentUser(ePerson2);
        Item item = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Item Title")
                               .withSharedProject("subproject")
                               .withPolicyGroup("###GROUP_POLICY_PLACEHOLDER###")
                               .build();

        context.restoreAuthSystemState();

        String authToken = getAuthToken(ePerson2.getEmail(), password);
        getClient(authToken).perform(get("/api/core/items/" + item.getID()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", ItemMatcher.matchItemPolicyGroupAndSharedMetadata(
                                                     item, "Item Title", "project_"
                                                     + subprojectAComm.getID().toString()
                                                     + "_members_group", "subproject")));
    }

    @Test
    public void subProjectMemberAreNotAllowed2createItemsInProjectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withEmail("example2@email.com")
                .withPassword(password).build();

        EPerson ePerson3 = EPersonBuilder.createEPerson(context)
                .withEmail("example3@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup = GroupBuilder.createGroup(context)
                     .withName("project_" + projectsCommunity.getID().toString() + "_members_group")
                     .addMember(ePerson1).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "###GROUP_POLICY_PLACEHOLDER###");
        itemService.addMetadata(context, templateItem, "cris", "workspace", "shared", null, "project");

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson3).build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();

        context.setCurrentUser(ePerson3);
        Item item = ItemBuilder.createItem(context, publicationsCollection)
                               .withTitle("Item Title")
                               .withSharedProject("project")
                               .withPolicyGroup("###GROUP_POLICY_PLACEHOLDER###")
                               .build();

        context.restoreAuthSystemState();

        String authToken = getAuthToken(ePerson1.getEmail(), password);
        getClient(authToken).perform(get("/api/core/items/" + item.getID()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", ItemMatcher.matchItemPolicyGroupAndSharedMetadata(
                                                     item, "Item Title", "###GROUP_POLICY_PLACEHOLDER###", "project")));
    }

}