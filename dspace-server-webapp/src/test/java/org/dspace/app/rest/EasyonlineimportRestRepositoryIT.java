/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.UUID;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Integration test class for the easyonlineimport endpoint
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyonlineimportRestRepositoryIT extends AbstractControllerIntegrationTest {

    @Test
    public void findAllTest() throws Exception {
        String authToken = getAuthToken(admin.getEmail(), password);
        getClient(authToken).perform(get("/api/integration/easyonlineimports"))
                            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void findOneTest() throws Exception {
        String authToken = getAuthToken(admin.getEmail(), password);
        getClient(authToken).perform(get("/api/integration/easyonlineimports/" + UUID.randomUUID()))
                            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void easyOnlineImportUnauthorizedTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();


        Collection col1 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Collection 1")
                                           .withEntityType("Project")
                                           .build();

        Item projectItem = ItemBuilder.createItem(context, col1)
                                      .withTitle("Test Title")
                                      .build();

        context.restoreAuthSystemState();

        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient().perform(fileUpload("/api/integration/easyonlineimports/" + projectItem.getID())
                   .file(xmlFile))
                   .andExpect(status().isUnauthorized());

    }

    @Test
    public void easyOnlineImportAdminTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();


        Collection col1 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Collection 1")
                                           .withEntityType("Project")
                                           .build();

        Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Project partners")
                                           .withEntityType("projectpartner")
                                           .build();


        Collection col3 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Parent Project Collection Title")
                                           .build();

        Item parentProjectItem = ItemBuilder.createItem(context, col3)
                                            .withTitle("Parent Project Item Title")
                                            .build();

        Item projectItem = ItemBuilder.createItem(context, col1)
                                      .withTitle("Test Title")
                                      .withParentproject(parentProjectItem.getID().toString())
                                      .build();


        ItemBuilder.createItem(context, col2)
                   .withTitle("project partner item Title")
                   .withEasyImport("no")
                   .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient(tokenAdmin).perform(fileUpload("/api/integration/easyonlineimports/" + projectItem.getID())
                             .file(xmlFile))
                             .andExpect(status().isCreated());

        getClient().perform(get("/api/core/items/" + projectItem.getID()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.metadata", matchMetadata("dc.title",
                           "orschungsinformationssystem und Evaluierungsverfahren für Leistungen der"
                         + " Forschung für Praxis und Gesellschaft – ausgereift im Pilot-Betrieb für"
                         + " Projektträger in der Agrarforschung")));
    }

}