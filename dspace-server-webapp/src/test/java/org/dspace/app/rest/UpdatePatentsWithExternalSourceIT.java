/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.scripts.handler.impl.TestDSpaceRunnableHandler;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.external.model.ExternalDataObject;
import org.dspace.external.provider.impl.LiveImportDataProvider;
import org.dspace.importer.external.epo.service.EpoImportMetadataSourceServiceImpl;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.dspace.scripts.patents.UpdatePatentsWithExternalSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
* This class contains the tests that verify the correct behavior
* of the "update-patents" script {UpdatePatentsWithExternalSource}
* 
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4Science.com)
*/
public class UpdatePatentsWithExternalSourceIT extends AbstractControllerIntegrationTest {

    private Collection patentCollection;
    // the script for "update-patents"
    private LiveImportDataProvider mockEpoProvider;
    private EpoImportMetadataSourceServiceImpl querySource;
    private UpdatePatentsWithExternalSource updatePatentsWithExternalSource;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        context.turnOffAuthorisationSystem();
        parentCommunity = CommunityBuilder.createCommunity(context)
                                          .withName("Parent Community")
                                          .build();

        this.patentCollection = CollectionBuilder.createCollection(context, parentCommunity)
                                                 .withEntityType("Patent")
                                                 .withName("Collection for Patent")
                                                 .build();

        this.updatePatentsWithExternalSource = new UpdatePatentsWithExternalSource();
        this.mockEpoProvider = Mockito.mock(LiveImportDataProvider.class);
        this.querySource = Mockito.mock(EpoImportMetadataSourceServiceImpl.class);
    }

    @Test
    public void updatePatentsWithEPOscriptTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Item patentA = ItemBuilder.createItem(context, patentCollection)
                                  .withPatentNo("EP1989642")
                                  .withKind("A1")
                                  .withApplicationNumber("06720704")
                                  .withTitle("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")
                                  .withAuthor("GARDNER, JON, S")
                                  .withAuthor("WANG, JUIN, J")
                                  .withAuthor("SCOTT, MATTHEW, V")
                                  .withAuthor("MISHA, JON, I")
                                  .withIssueDate("2008-11-12")
                                  .withPatentRegistrationDate("2006-02-13")
                                  .withSynsicrisSubject("G06F17/28AI")
                                  .withSynsicrisSubject("H04L12/58AI")
                                  .withSynsicrisSubject("H04L29/06AI")
                                   // inline-group
                                  .withPatentKindCode("A1")
                                  .withPatentHistoryTitle("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")
                                  .withPatentPublicationDate("2008-11-12")
                                  .build();

        Item patentB = ItemBuilder.createItem(context, patentCollection)
                                  .withTitle("Patent without information")
                                  .withIssueDate("2016-02-13")
                                  .withAuthor("Smith, Maria")
                                  .withSynsicrisSubject("ExtraEntry")
                                  .build();

        //define record
        MetadataValueDTO patentno = new MetadataValueDTO("dc", "identifier", "patentno", null, "EP1989642");
        MetadataValueDTO kind = new MetadataValueDTO("crispatent", "kind", null, null, "A4");
        MetadataValueDTO applicationNo = new MetadataValueDTO("dc", "identifier", "applicationnumber",null, "06720704");
        MetadataValueDTO publicationDate = new MetadataValueDTO("dc", "date", "issued", null, "2009-04-29");
        MetadataValueDTO registrationDate = new MetadataValueDTO("dcterms", "dateSubmitted", null, null, "2006-02-13");
        MetadataValueDTO publishedIn = new MetadataValueDTO("dcterms", "rightsHolder", null, null,
                                                            "EPOSTAL SERVICES, INC");
        MetadataValueDTO author = new MetadataValueDTO("dc", "contributor", "author", null, "GARDNER, JON, S");
        MetadataValueDTO author2 = new MetadataValueDTO("dc", "contributor", "author", null, "WANG, JUIN, J");
        MetadataValueDTO author3 = new MetadataValueDTO("dc", "contributor", "author", null, "SCOTT, MATTHEW, V");
        MetadataValueDTO mainTitle = new MetadataValueDTO("dc","title", null,null,
                                                          "MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD");
        MetadataValueDTO mainTitle2 = new MetadataValueDTO("dc","title", null,null,
                                                          "SYSTÈME ET MÉTHODE DE MESSAGERIE ET DE GESTION DE DOCUMENT");
        MetadataValueDTO mainTitle3 = new MetadataValueDTO("dc","title", null,null,
                                                           "MITTEILUNGS- UND DOKUMENTVERWALTUNGSSYSTEM UND -VERFAHREN");
        MetadataValueDTO subject = new MetadataValueDTO("synsicris", "subject", "ipc", null, "G06F17/28AI");
        MetadataValueDTO subject2 = new MetadataValueDTO("synsicris", "subject", "ipc", null, "H04L12/58AI");
        MetadataValueDTO subject3 = new MetadataValueDTO("synsicris", "subject", "ipc", null, "H04L29/06AI");
        // patent history (inline-group)
        MetadataValueDTO kindCode = new MetadataValueDTO("crispatent", "document", "kind", null, "A1");
        MetadataValueDTO kindCode2 = new MetadataValueDTO("crispatent", "document", "kind", null, "A4");
        MetadataValueDTO historyDate = new MetadataValueDTO("crispatent", "document", "issueDate", null, "2008-11-12");
        MetadataValueDTO historyDate2 = new MetadataValueDTO("crispatent", "document", "issueDate", null, "2009-04-29");
        MetadataValueDTO historyTitle = new MetadataValueDTO("crispatent", "document", "title", null,
                                                             "MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD");
        MetadataValueDTO historyTitle2 = new MetadataValueDTO("crispatent", "document", "title", null,
                                                              "MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD");

        List<MetadataValueDTO> metadataFirstRecord = Arrays.asList(patentno,
                                                                       kind,
                                                              applicationNo,
                                                            publicationDate,
                                                           registrationDate,
                                                                publishedIn,
                                                   author, author2, author3,
                                          mainTitle, mainTitle2, mainTitle3,
                                                 subject, subject2,subject3,
                                                        kindCode, kindCode2,
                                                  historyDate, historyDate2,
                                                historyTitle, historyTitle2);

        ExternalDataObject firstRecord = new ExternalDataObject();
        firstRecord.setMetadata(metadataFirstRecord);

        when(mockEpoProvider.getExternalDataObject(ArgumentMatchers.contains("EP1989642")))
        .thenReturn(Optional.of(firstRecord));

        List<MetadataFieldConfig> supportedMetadata = getSupportedMetadata();
        when(mockEpoProvider.getQuerySource()).thenReturn(querySource);
        when(querySource.getSupportedMetadataFields()).thenReturn(supportedMetadata);

        context.restoreAuthSystemState();

        // verify patents before launching the update script

        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/items/" + patentA.getID()))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.metadata.['dc.title'][0].value",
                         is("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")))
                 .andExpect(jsonPath("$.metadata.['dcterms.dateSubmitted'][0].value", is("2006-02-13")))
                 .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("GARDNER, JON, S")))
                 .andExpect(jsonPath("$.metadata.['dc.contributor.author'][1].value", is("WANG, JUIN, J")))
                 .andExpect(jsonPath("$.metadata.['dc.contributor.author'][2].value", is("SCOTT, MATTHEW, V")))
                 .andExpect(jsonPath("$.metadata.['dc.contributor.author'][3].value", is("MISHA, JON, I")))
                 .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2008-11-12")))
                 .andExpect(jsonPath("$.metadata.['dc.identifier.applicationnumber'][0].value", is("06720704")))
                 .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value", is("EP1989642")))
                 .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("G06F17/28AI")))
                 .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][1].value", is("H04L12/58AI")))
                 .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][2].value", is("H04L29/06AI")))
                 .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value", is("A1")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value", is("A1")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value", is("2008-11-12")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['crispatent.document.title'][0].value",
                         is("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.title'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['dcterms.rightsHolder'][1].value").doesNotExist());

        getClient(tokenAdmin).perform(get("/api/core/items/" + patentB.getID()))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.metadata.['dc.title'][0].value", is("Patent without information")))
                         .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("Smith, Maria")))
                         .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2016-02-13")))
                         .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("ExtraEntry")))
                         .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value").doesNotExist())
                         .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value").doesNotExist())
                         .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value").doesNotExist())
                         .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value").doesNotExist())
                         .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].title").doesNotExist());

        String[] args = new String[] {"update-patents"};
        TestDSpaceRunnableHandler handler = new TestDSpaceRunnableHandler();
        updatePatentsWithExternalSource.initialize(args, handler, admin);
        updatePatentsWithExternalSource.setLiveImportDataProvider(mockEpoProvider);
        updatePatentsWithExternalSource.run();

        // verify patents after update

        getClient(tokenAdmin).perform(get("/api/core/items/" + patentA.getID()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.metadata.['dc.title'][0].value",
                                is("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")))
                        .andExpect(jsonPath("$.metadata.['dcterms.dateSubmitted'][0].value", is("2006-02-13")))
                        .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("GARDNER, JON, S")))
                        .andExpect(jsonPath("$.metadata.['dc.contributor.author'][1].value", is("WANG, JUIN, J")))
                        .andExpect(jsonPath("$.metadata.['dc.contributor.author'][2].value", is("SCOTT, MATTHEW, V")))
                        .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2009-04-29")))
                        .andExpect(jsonPath("$.metadata.['dc.identifier.applicationnumber'][0].value", is("06720704")))
                        .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value", is("EP1989642")))
                        .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("G06F17/28AI")))
                        .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][1].value", is("H04L12/58AI")))
                        .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][2].value", is("H04L29/06AI")))
                        .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value", is("A4")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value", is("A1")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][1].value", is("A4")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value", is("2008-11-12")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][1].value", is("2009-04-29")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.title'][0].value",
                                is("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.title'][1].value",
                                is("MESSAGING AND DOCUMENT MANAGEMENT SYSTEM AND METHOD")));

        getClient(tokenAdmin).perform(get("/api/core/items/" + patentB.getID()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.metadata.['dc.title'][0].value", is("Patent without information")))
                        .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("Smith, Maria")))
                        .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2016-02-13")))
                        .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("ExtraEntry")))
                        .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].title").doesNotExist());
    }

    @Test
    public void updatePatentWithNoUpdateTest() throws Exception {
        context.turnOffAuthorisationSystem();

        Item patent = ItemBuilder.createItem(context, patentCollection)
                                 .withPatentNo("IT1234567")
                                 .withKind("A")
                                 .withApplicationNumber("1234567")
                                 .withTitle("Patent Title")
                                 .withAuthor("Misha, Boychuk")
                                 .withIssueDate("2010-06-18")
                                 .withPatentRegistrationDate("2009-02-13")
                                 .withSynsicrisSubject("subject-1")
                                  // inline-group
                                 .withPatentKindCode("A")
                                 .withPatentHistoryTitle("Patent Title")
                                 .withPatentPublicationDate("2010-06-18")
                                 .build();

        //define record
        MetadataValueDTO patentno = new MetadataValueDTO("dc", "identifier", "patentno", null, "IT1234567");
        MetadataValueDTO kind = new MetadataValueDTO("crispatent", "kind", null, null, "A");
        MetadataValueDTO applicationNo = new MetadataValueDTO("dc", "identifier", "applicationnumber",null, "1234567");
        MetadataValueDTO publicationDate = new MetadataValueDTO("dc", "date", "issued", null, "2010-06-18");
        MetadataValueDTO registrationDate = new MetadataValueDTO("dcterms", "dateSubmitted", null, null, "2009-02-13");
        MetadataValueDTO author = new MetadataValueDTO("dc", "contributor", "author", null, "Misha, Boychuk");
        MetadataValueDTO mainTitle = new MetadataValueDTO("dc","title", null, null, "Patent Title");
        MetadataValueDTO subject = new MetadataValueDTO("synsicris", "subject", "ipc", null, "subject-1");
        // patent history (inline-group)
        MetadataValueDTO kindCode = new MetadataValueDTO("crispatent", "document", "kind", null, "A");
        MetadataValueDTO historyDate = new MetadataValueDTO("crispatent", "document", "issueDate", null, "2010-06-18");
        MetadataValueDTO historyTitle = new MetadataValueDTO("crispatent", "document", "title", null, "Patent Title");

        List<MetadataValueDTO> metadataFirstRecord = Arrays.asList(patentno, kind, applicationNo, publicationDate,
                                                                   registrationDate, author, mainTitle, subject,
                                                                   kindCode, historyDate, historyTitle);

        ExternalDataObject firstRecord = new ExternalDataObject();
        firstRecord.setMetadata(metadataFirstRecord);

        when(mockEpoProvider.getExternalDataObject(ArgumentMatchers.contains("IT1234567")))
        .thenReturn(Optional.of(firstRecord));

        List<MetadataFieldConfig> supportedMetadata = getSupportedMetadata();
        when(mockEpoProvider.getQuerySource()).thenReturn(querySource);
        when(querySource.getSupportedMetadataFields()).thenReturn(supportedMetadata);

        context.restoreAuthSystemState();

        // verify patents before launching the update script
        String tokenAdmin = getAuthToken(admin.getEmail(), password);
        getClient(tokenAdmin).perform(get("/api/core/items/" + patent.getID()))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.metadata.['dc.title'][0].value", is("Patent Title")))
                 .andExpect(jsonPath("$.metadata.['dcterms.dateSubmitted'][0].value", is("2009-02-13")))
                 .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("Misha, Boychuk")))
                 .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2010-06-18")))
                 .andExpect(jsonPath("$.metadata.['dc.identifier.applicationnumber'][0].value", is("1234567")))
                 .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value", is("IT1234567")))
                 .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("subject-1")))
                 .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value", is("A")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value", is("A")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value", is("2010-06-18")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['crispatent.document.title'][0].value", is("Patent Title")))
                 .andExpect(jsonPath("$.metadata.['crispatent.document.title'][1].value").doesNotExist())
                 .andExpect(jsonPath("$.metadata.['dcterms.rightsHolder'][1].value").doesNotExist());

        String[] args = new String[] {"update-patents"};
        TestDSpaceRunnableHandler handler = new TestDSpaceRunnableHandler();
        updatePatentsWithExternalSource.initialize(args, handler, admin);
        updatePatentsWithExternalSource.setLiveImportDataProvider(mockEpoProvider);
        updatePatentsWithExternalSource.run();

        // verify that the patent hasn't been updated
        getClient(tokenAdmin).perform(get("/api/core/items/" + patent.getID()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.metadata.['dc.title'][0].value", is("Patent Title")))
                        .andExpect(jsonPath("$.metadata.['dcterms.dateSubmitted'][0].value", is("2009-02-13")))
                        .andExpect(jsonPath("$.metadata.['dc.contributor.author'][0].value", is("Misha, Boychuk")))
                        .andExpect(jsonPath("$.metadata.['dc.date.issued'][0].value", is("2010-06-18")))
                        .andExpect(jsonPath("$.metadata.['dc.identifier.applicationnumber'][0].value", is("1234567")))
                        .andExpect(jsonPath("$.metadata.['dc.identifier.patentno'][0].value", is("IT1234567")))
                        .andExpect(jsonPath("$.metadata.['synsicris.subject.ipc'][0].value", is("subject-1")))
                        .andExpect(jsonPath("$.metadata.['crispatent.kind'][0].value", is("A")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][0].value", is("A")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.kind'][1].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][0].value", is("2010-06-18")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.issueDate'][1].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['crispatent.document.title'][0].value", is("Patent Title")))
                        .andExpect(jsonPath("$.metadata.['crispatent.document.title'][1].value").doesNotExist())
                        .andExpect(jsonPath("$.metadata.['dcterms.rightsHolder'][1].value").doesNotExist());
    }

    private List<MetadataFieldConfig> getSupportedMetadata() {
        MetadataFieldConfig identifierOther = new MetadataFieldConfig("dc", "identifier", "other");
        MetadataFieldConfig identifierPatentno = new MetadataFieldConfig("dc", "identifier", "patentno");
        MetadataFieldConfig crispatentKind = new MetadataFieldConfig("crispatent", "kind", null);
        MetadataFieldConfig applicationnumber = new MetadataFieldConfig("dc", "identifier", "applicationnumber");
        MetadataFieldConfig publicationDate = new MetadataFieldConfig("dc", "date", "issued");
        MetadataFieldConfig registrationDate = new MetadataFieldConfig("dcterms", "dateSubmitted", null);
        MetadataFieldConfig publishedIn = new MetadataFieldConfig("dc", "contributor", null);
        MetadataFieldConfig author = new MetadataFieldConfig("dc", "contributor", "author");
        MetadataFieldConfig mainTitle = new MetadataFieldConfig("dc", "title", null);
        MetadataFieldConfig subject = new MetadataFieldConfig("synsicris", "subject", "ipc");
        MetadataFieldConfig mainDescription = new MetadataFieldConfig("dc", "description", "abstract");
        // history
        MetadataFieldConfig kindCode = new MetadataFieldConfig("crispatent", "document", "kind");
        MetadataFieldConfig issueDate = new MetadataFieldConfig("crispatent", "document", "issueDate");
        MetadataFieldConfig title = new MetadataFieldConfig("crispatent", "document", "title");
        MetadataFieldConfig description = new MetadataFieldConfig("crispatent", "document", "description");
        return Arrays.asList(identifierOther, identifierPatentno, crispatentKind, applicationnumber,
                             publicationDate, registrationDate, publishedIn, author, mainTitle, subject,
                             mainDescription, kindCode, issueDate, title, description);
    }

}