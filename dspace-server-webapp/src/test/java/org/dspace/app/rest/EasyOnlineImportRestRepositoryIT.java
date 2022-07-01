/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static com.jayway.jsonpath.JsonPath.read;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Integration test class for the easyonlineimport endpoint
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImportRestRepositoryIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

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
    @SuppressWarnings("deprecation")
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
    @SuppressWarnings("deprecation")
    public void easyOnlineImportAdminTest() throws Exception {
        context.turnOffAuthorisationSystem();

        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();


        Collection col1 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Collection 1")
                                           .withEntityType("Funding")
                                           .build();

        Collection col2 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Project partners")
                                           .withEntityType("projectpartner")
                                           .withSubmissionDefinition("projectpartners")
                                           .build();


        Collection col3 = CollectionBuilder.createCollection(context, parentCommunity)
                                           .withName("Parent Project Collection Title")
                                           .build();

        Item parentProjectItem = ItemBuilder.createItem(context, col3)
                                            .withTitle("Parent Project Item Title")
                                            .build();

        Item fundingItem = ItemBuilder.createItem(context, col1)
                                  .withTitle("Test Title")
                                  .withParentproject(parentProjectItem.getName(), parentProjectItem.getID().toString())
                                  .build();


        ItemBuilder.createItem(context, col2)
                   .withTitle("project partner item Title")
                   .withEasyImport("no")
                   .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient(tokenAdmin).perform(fileUpload("/api/integration/easyonlineimports/" + fundingItem.getID())
                             .file(xmlFile))
                             .andExpect(status().isCreated());

        getClient().perform(get("/api/core/items/" + fundingItem.getID()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.title",
                              "Forschungsinformationssystem und Evaluierungsverfahren für Leistungen der"
                            + " Forschung für Praxis und Gesellschaft – ausgereift im Pilot-Betrieb für"
                            + " Projektträger in der Agrarforschung")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("dc.title.alternative",
                           "Research Information System and Evaluation Concept for Research Contributions to Practice"
                         + " and Society – Optimized Through Pilot Studies Within Funding Agencies"
                         + " for Agricultural Research")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("dc.description.abstract",
                           "Das Projekt strebt eine Weiterentwicklung des im Projekt Praxis-Impact II (FKZ 2812NA102)"
                         + " erarbeiteten Dokumentations- und Evaluierungskonzeptes an, welches eine auf praktischen"
                         + " und gesellschaftlichen Nutzen ausgerichtete Forschungskultur fördern soll. Dieses Konzept"
                         + " soll entlang der Anforderungen des Agrar-Innovationssystems (Förderer, Forschung,"
                         + " Wissenstransfer/-nutzer) bis zur Anwendbarkeit im Pilotbetrieb weiterentwickelt werden."
                         + " Das Dokumentationskonzept soll umgesetzt werden, indem ein Open Source"
                         + " Forschungsinformationssystem (FIS) erweitert wird, a) um Leistungen der Forschung für"
                         + " Praxis und Gesellschaft und b) um Informationen, die in der Forschungsförderung zusätzlich"
                         + " benötigt werden (um Teile von Anträgen und Berichten zu ersetzen)."
                         + " Das erweiterte FIS soll a) Förderentscheidungen und die Projektbegleitung unterstützen,"
                         + " b) eine qualitativ hochwertige Evaluierung gesellschaftlicher Leistungen ermöglichen und"
                         + " c) Qualität, Transfer und Zugänglichkeit zielgruppengerechter Outputs erhöhen."
                         + " Das Verfahren für die Projektevaluierung soll a) durch partizipative Dokumentations-"
                         + " und Evaluierungsprozesse die Ausrichtung der Projekte auf gesellschaftlichen Nutzen"
                         + " steigern, b) die Wirksamkeit von Forschungsprogrammen nachweisen können und"
                         + " c) es ermöglichen besonders erfolgreiche Projekte auszuzeichnen.")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("oairecerif.acronym", "SynSICRIS")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.description.workplan",
                           "Die Anforderungen des Agrarinnovationssystems an das erweiterte FIS und das"
                         + " Evaluierungskonzept werden über Beteiligungs- und Erprobungsprozesse und"
                         + " Experteneinbindung mit einem Living Lab Ansatz ermittelt und in mehreren"
                         + " Rückkopplungsschlaufen umgesetzt. Die Erweiterung des Open Source FIS DSpace-CRIS erfolgt"
                         + " über ein agiles Vorgehensmodell der Softwareentwicklung. Die nutzbaren Zwischenversionen"
                         + " und das weiterentwickelte Evaluierungskonzept werden für Sichtungs- und Testprozesse im"
                         + " Living Lab genutzt. Ca. 8 Projektträger und 5 Stiftungen werden in Sichtungen einbezogen,"
                         + " Erprobungen finden in der BLE und bei drei weiteren Projektträgern statt." )));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void easyOnlineImportAdminWithRandomUuidTest() throws Exception {

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient(tokenAdmin).perform(fileUpload("/api/integration/easyonlineimports/" + UUID.randomUUID())
                             .file(xmlFile))
                             .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void easyOnlineImportForbiddenTest() throws Exception {
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
                                  .withParentproject(parentProjectItem.getName(), parentProjectItem.getID().toString())
                                  .build();


        ItemBuilder.createItem(context, col2)
                   .withTitle("project partner item Title")
                   .withEasyImport("no")
                   .build();

        context.restoreAuthSystemState();

        String tokenEPerson = getAuthToken(eperson.getEmail(), password);
        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient(tokenEPerson).perform(fileUpload("/api/integration/easyonlineimports/" + projectItem.getID())
                             .file(xmlFile))
                             .andExpect(status().isForbidden());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void easyOnlineImportAdminMissingFileisTest() throws Exception {
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
                                  .withParentproject(parentProjectItem.getName(), parentProjectItem.getID().toString())
                                  .build();


        ItemBuilder.createItem(context, col2)
                   .withTitle("project partner item Title")
                   .withEasyImport("no")
                   .build();

        context.restoreAuthSystemState();

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(fileUpload("/api/integration/easyonlineimports/" + projectItem.getID()))
                             .andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void easyOnlineImportTest() throws Exception {
        context.turnOffAuthorisationSystem();

        EPerson ePerson1 = EPersonBuilder.createEPerson(context)
                                         .withNameInMetadata("Mykhaylo", "Boychuk")
                                         .withEmail("mykhaylo.boychuk@email.com")
                                         .withPassword(password).build();

        Community projectA = CommunityBuilder.createCommunity(context)
                                             .withName("project A").build();


        CollectionBuilder.createCollection(context, projectA)
                         .withName("Project partners")
                         .withEntityType("projectpartner")
                         .withSubmissionDefinition("projectpartners")
                         .withSubmitterGroup(ePerson1)
                         .build();

        Collection joinProjects = CollectionBuilder.createCollection(context, projectA)
                                           .withName("Parent Project Collection Title")
                                           .build();

        Item projectAItem = ItemBuilder.createItem(context, joinProjects)
                                       .withTitle("project A")
                                       .build();

        Community fundingRootComm = CommunityBuilder.createSubCommunity(context, projectA)
                                                         .withName("Funding").build();

        Community subprojectA = CommunityBuilder.createSubCommunity(context, fundingRootComm)
                                          .withName("subproject A - project A all grants").build();

        Group subprojectAGroup = GroupBuilder.createGroup(context)
                       .withName("funding_" + subprojectA.getID().toString() + "_members_group")
                       .addMember(ePerson1).build();

        Collection Funding = CollectionBuilder.createCollection(context, subprojectA)
                                               .withName("Funding")
                                               .withSubmitterGroup(subprojectAGroup)
                                               .withEntityType("Funding")
                                               .build();

        Item fundingItem = ItemBuilder.createItem(context, Funding)
                .withTitle("Funding Item Title")
                .withParentproject(projectAItem.getName(), projectAItem.getID().toString())
                .build();

        configurationService.setProperty("project.funding-community-name", fundingRootComm.getName());

        context.restoreAuthSystemState();

        AtomicReference<String> idRef1 = new AtomicReference<>();

        try {
        String tokenUser = getAuthToken(ePerson1.getEmail(), password);
        InputStream xfile = getClass().getResourceAsStream("SynKassel.xml");
        final MockMultipartFile xmlFile = new MockMultipartFile("file", "SynKassel.xml", "application/xml", xfile);
        getClient(tokenUser).perform(fileUpload("/api/integration/easyonlineimports/" + fundingItem.getID())
                            .file(xmlFile))
                         .andExpect(status().isCreated())
                         .andDo(result -> idRef1.set(read(result.getResponse().getContentAsString(), "$.created.[0]")));

        getClient().perform(get("/api/core/items/" + fundingItem.getID()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.title",
                              "Forschungsinformationssystem und Evaluierungsverfahren für Leistungen der"
                            + " Forschung für Praxis und Gesellschaft – ausgereift im Pilot-Betrieb für"
                            + " Projektträger in der Agrarforschung")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("dc.title.alternative",
                           "Research Information System and Evaluation Concept for Research Contributions to Practice"
                         + " and Society – Optimized Through Pilot Studies Within Funding Agencies"
                         + " for Agricultural Research")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("dc.description.abstract",
                           "Das Projekt strebt eine Weiterentwicklung des im Projekt Praxis-Impact II (FKZ 2812NA102)"
                         + " erarbeiteten Dokumentations- und Evaluierungskonzeptes an, welches eine auf praktischen"
                         + " und gesellschaftlichen Nutzen ausgerichtete Forschungskultur fördern soll. Dieses Konzept"
                         + " soll entlang der Anforderungen des Agrar-Innovationssystems (Förderer, Forschung,"
                         + " Wissenstransfer/-nutzer) bis zur Anwendbarkeit im Pilotbetrieb weiterentwickelt werden."
                         + " Das Dokumentationskonzept soll umgesetzt werden, indem ein Open Source"
                         + " Forschungsinformationssystem (FIS) erweitert wird, a) um Leistungen der Forschung für"
                         + " Praxis und Gesellschaft und b) um Informationen, die in der Forschungsförderung zusätzlich"
                         + " benötigt werden (um Teile von Anträgen und Berichten zu ersetzen)."
                         + " Das erweiterte FIS soll a) Förderentscheidungen und die Projektbegleitung unterstützen,"
                         + " b) eine qualitativ hochwertige Evaluierung gesellschaftlicher Leistungen ermöglichen und"
                         + " c) Qualität, Transfer und Zugänglichkeit zielgruppengerechter Outputs erhöhen."
                         + " Das Verfahren für die Projektevaluierung soll a) durch partizipative Dokumentations-"
                         + " und Evaluierungsprozesse die Ausrichtung der Projekte auf gesellschaftlichen Nutzen"
                         + " steigern, b) die Wirksamkeit von Forschungsprogrammen nachweisen können und"
                         + " c) es ermöglichen besonders erfolgreiche Projekte auszuzeichnen.")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("oairecerif.acronym", "SynSICRIS")))
                   .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.description.workplan",
                           "Die Anforderungen des Agrarinnovationssystems an das erweiterte FIS und das"
                         + " Evaluierungskonzept werden über Beteiligungs- und Erprobungsprozesse und"
                         + " Experteneinbindung mit einem Living Lab Ansatz ermittelt und in mehreren"
                         + " Rückkopplungsschlaufen umgesetzt. Die Erweiterung des Open Source FIS DSpace-CRIS erfolgt"
                         + " über ein agiles Vorgehensmodell der Softwareentwicklung. Die nutzbaren Zwischenversionen"
                         + " und das weiterentwickelte Evaluierungskonzept werden für Sichtungs- und Testprozesse im"
                         + " Living Lab genutzt. Ca. 8 Projektträger und 5 Stiftungen werden in Sichtungen einbezogen,"
                         + " Erprobungen finden in der BLE und bei drei weiteren Projektträgern statt." )));

            getClient().perform(get("/api/core/items/" + idRef1.get()))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.metadata", matchMetadata("dc.title", "disy Informationssysteme GmbH")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("oairecerif.identifier.url", "www.disy.net")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("organization.address.addressCountry",
                                                                       "Deutschland")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("organization.parentOrganization",
                                                                       "disy Informationssysteme GmbH")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personadmin.email",
                                                                       "andreas.abecker@disy.net")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.address.addressCity", "Karlsruhe")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.address.addressPostcode", "76131")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.address.addressStreetnamenumber",
                                                                       "Ludwig-Erhard-Allee 6")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.identifier.registernumber",
                                                                       "HRB 107964")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.orgunits.email", "office@disy.net")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.orgunits.phone",
                                                                       "+49 721 16006-000")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personadmin", "Abecker, Andreas")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personadmin.degree", "Dr.")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personadmin.gender", "m")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personadmin.phone",
                                                                       "+49 721 16006-256")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personleader", "Abecker, Andreas")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personleader.degree", "Dr.")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personleader.email",
                                                                       "andreas.abecker@disy.net")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personleader.gender", "m")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.personleader.phone",
                                                                       "+49 721 16006-256")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.type.easy-import", "Yes")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.type.legalform", "GmbH")))
                       .andExpect(jsonPath("$.metadata", matchMetadata("synsicris.type.orgform", "SME")));
        } finally {
            if (idRef1.get() != null) {
                ItemBuilder.deleteItem(UUID.fromString(idRef1.get()));
            }
        }
    }

}