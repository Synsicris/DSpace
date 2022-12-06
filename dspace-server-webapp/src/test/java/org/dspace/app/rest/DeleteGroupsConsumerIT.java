/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.project.util.ProjectConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public class DeleteGroupsConsumerIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ItemService itemService;

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
    public void createWorkspaceItemWithSubmitterUsing_projectTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Mykhaylo", "Boychuk")
                .withEmail("mykhaylo.boychuk@email.com")
                .withPassword(password).build();

        EPerson ePerson2 = EPersonBuilder.createEPerson(context)
                .withNameInMetadata("Viktor", "Beketov")
                .withEmail("example2@email.com")
                .withPassword(password).build();

        projectsCommunity = CommunityBuilder.createCommunity(context)
                                            .withName("Projects Community").build();

        Group projectsCommunityGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_MEMBERS_GROUP_TEMPLATE,
                        projectsCommunity.getID().toString()
                    )
                )
                .addMember(ePerson1)
                .addMember(ePerson2).build();

        Group projectsCommunityCoordinatorsGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_COORDINATORS_GROUP_TEMPLATE,
                        projectsCommunity.getID().toString()
                    )
                )
                .addMember(ePerson1)
                .build();

        Group projectsCommunityFunderGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_FUNDERS_GROUP_TEMPLATE,
                        projectsCommunity.getID().toString()
                    )
                )
                .addMember(ePerson1).build();

        Group projectsCommunityReaderGroup =
            GroupBuilder.createGroup(context)
                .withName(
                    String.format(
                        ProjectConstants.PROJECT_READERS_GROUP_TEMPLATE,
                        projectsCommunity.getID().toString()
                    )
                )
                .addMember(ePerson1).build();

        publicationsCollection = CollectionBuilder.createCollection(context, projectsCommunity)
                                                  .withName("Publication Collection")
                                                  .withSubmitterGroup(projectsCommunityGroup)
                                                  .withAdminGroup(projectsCommunityCoordinatorsGroup)
                                                  .withTemplateItem().build();

        Item templateItem = publicationsCollection.getTemplateItem();
        itemService.addMetadata(context, templateItem, "cris", "policy", "group", null,
                                "GROUP_POLICY_PLACEHOLDER");
        itemService.addMetadata(context, templateItem, "cris", "project", "shared", null, "project");

        subprojectsCommunity = CommunityBuilder.createSubCommunity(context, projectsCommunity)
                                               .withName("Sub Projects Community").build();

        subprojectAComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectA of SubprojectsCommunity").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectAComm.getID().toString() + "_members_group")
                       .addMember(ePerson1).build();

        collectionSubProjectA = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectAGroup)
                                                 .withName("Collection Sub Project A").build();

        subprojectBComm = CommunityBuilder.createSubCommunity(context, subprojectsCommunity)
                                          .withName("Sub ProjectB of SubprojectsCommunity").build();

        Group subprojectBGroup = GroupBuilder.createGroup(context)
                       .withName("project_" + subprojectBComm.getID().toString() + "_members_group")
                       .addMember(ePerson2).build();

        collectionSubProjectB = CollectionBuilder.createCollection(context, subprojectAComm)
                                                 .withSubmitterGroup(subprojectBGroup)
                                                 .withName("Collection Sub Project B").build();
        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(delete("/api/core/communities/" + projectsCommunity.getID().toString()))
                             .andExpect(status().isNoContent());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + projectsCommunityGroup.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + projectsCommunityCoordinatorsGroup.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + projectsCommunityFunderGroup.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + projectsCommunityReaderGroup.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + subprojectAGroup.getID()))
            .andExpect(status().isNotFound());

        getClient(tokenAdmin).perform(get("/api/eperson/groups/" + subprojectBGroup.getID()))
            .andExpect(status().isNotFound());
    }

}