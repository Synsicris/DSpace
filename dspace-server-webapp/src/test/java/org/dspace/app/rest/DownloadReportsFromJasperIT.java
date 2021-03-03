/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.scripts.handler.impl.TestDSpaceRunnableHandler;
import org.dspace.reports.jasper.DownloadReportFromExternalSource;
import org.dspace.reports.jasper.DownloadReportFromJasper;
import org.dspace.reports.jasper.DownloadReportService;
import org.dspace.reports.jasper.JasperRestConnector;
import org.dspace.reports.jasper.ReportDetailDTO;
import org.dspace.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public class DownloadReportsFromJasperIT extends AbstractControllerIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

    @Mock
    private JasperRestConnector mockJasperConnector;

    @InjectMocks
    private DownloadReportFromJasper downloadReportFromJasper;

    private Map<String, DownloadReportService> downloadReportServices;
    private DownloadReportFromExternalSource downloadReportService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        configurationService.setProperty("reports.timetosleep", "5000");
        configurationService.setProperty("reports.max_attempt", "2");
        downloadReportServices = new HashMap<String, DownloadReportService>();
        downloadReportService = new DownloadReportFromExternalSource();
    }

    @Test
    public void downloadReportFromJasperTest() throws Exception {
        try (FileInputStream file = new FileInputStream(testProps.get("test.jasperPostResponce").toString())) {
             String httpPostResponce = IOUtils.toString(file, Charset.defaultCharset());
             InputStream is = IOUtils.toInputStream(httpPostResponce, Charset.defaultCharset());
             when(this.mockJasperConnector.sendPostRequest("pdf", null, null)).thenReturn(is);
        }
        try (FileInputStream file = new FileInputStream(testProps.get("test.jasperGetDetaileResponce").toString())) {
            String httpGetResponce = IOUtils.toString(file, Charset.defaultCharset());
            InputStream is = IOUtils.toInputStream(httpGetResponce, Charset.defaultCharset());
            when(this.mockJasperConnector.sendGetRequest("/6c480a11-f001-492d-97a4-91a558268e9d")).thenReturn(is);
       }
        String downloadedFile = null;
        try (FileInputStream file = new FileInputStream(testProps.get("test.jasperDownloadedFile").toString())) {
            downloadedFile = IOUtils.toString(file, Charset.defaultCharset());
            InputStream isDownloadedFile = IOUtils.toInputStream(downloadedFile, Charset.defaultCharset());
            String dowloadPath =
                    "/6c480a11-f001-492d-97a4-91a558268e9d/exports/89d2c54b-8104-41bc-af7a-74af9333962b/outputResource";
            when(this.mockJasperConnector.sendGetRequest(dowloadPath)).thenReturn(isDownloadedFile);
       }

        String[] args = new String[] { "download-report", "-s", "jasper", "-f", "pdf" };
        TestDSpaceRunnableHandler handler = new TestDSpaceRunnableHandler();
        downloadReportServices.put("jasper", downloadReportFromJasper);
        downloadReportService.initialize(args, handler, admin);
        downloadReportService.setDownloadReportServices(downloadReportServices);
        downloadReportService.run();

        File fileRecovery = new File("Collection.pdf");
        assertTrue(fileRecovery.exists());
        FileInputStream isOfFileRecovery = new FileInputStream(fileRecovery);
        String stringOfFileRecovery = IOUtils.toString(isOfFileRecovery, Charset.defaultCharset());
        assertTrue(StringUtils.equals(stringOfFileRecovery, downloadedFile));
        fileRecovery.deleteOnExit();
    }


    @Test
    public void oneReportIsFailedTest() throws Exception {
        try (FileInputStream file = new FileInputStream(testProps.get("test.jasperPostResponce").toString())) {
             String httpPostResponce = IOUtils.toString(file, Charset.defaultCharset());
             InputStream is = IOUtils.toInputStream(httpPostResponce, Charset.defaultCharset());
             when(this.mockJasperConnector.sendPostRequest("pdf", null, null)).thenReturn(is);
        }
        try (FileInputStream file =
                        new FileInputStream(testProps.get("test.jasperDetaileResponceTwoReport").toString())) {
            String httpGetResponce = IOUtils.toString(file, Charset.defaultCharset());
            InputStream is = IOUtils.toInputStream(httpGetResponce, Charset.defaultCharset());
            when(this.mockJasperConnector.sendGetRequest("/6c480a11-f001-492d-97a4-91a558268e9d")).thenReturn(is);
       }
        String downloadedFile = null;
        try (FileInputStream file = new FileInputStream(testProps.get("test.jasperDownloadedFile").toString())) {
            downloadedFile = IOUtils.toString(file, Charset.defaultCharset());
            InputStream isDownloadedFile = IOUtils.toInputStream(downloadedFile, Charset.defaultCharset());
            String dowloadPath =
                    "/6c480a11-f001-492d-97a4-91a558268e9d/exports/89d2c54b-8104-41bc-af7a-74af9333962b/outputResource";
            when(this.mockJasperConnector.sendGetRequest(dowloadPath)).thenReturn(isDownloadedFile);
       }

        String[] args = new String[] { "download-report", "-s", "jasper", "-f", "pdf" };
        TestDSpaceRunnableHandler handler = new TestDSpaceRunnableHandler();
        downloadReportServices.put("jasper", downloadReportFromJasper);
        downloadReportService.initialize(args, handler, admin);
        downloadReportService.setDownloadReportServices(downloadReportServices);
        downloadReportService.run();

        File fileRecovery = new File("Collection.pdf");
        assertTrue(fileRecovery.exists());
        FileInputStream isOfFileRecovery = new FileInputStream(fileRecovery);
        String stringOfFileRecovery = IOUtils.toString(isOfFileRecovery, Charset.defaultCharset());
        assertTrue(StringUtils.equals(stringOfFileRecovery, downloadedFile));
        fileRecovery.deleteOnExit();
    }

    @Test
    public void tooMuchTimeInExecutionTest() throws Exception {
        context.turnOffAuthorisationSystem();
        downloadReportFromJasper = Mockito.mock(DownloadReportFromJasper.class);

        ReportDetailDTO status = new ReportDetailDTO();
        status.setHeaderStatus("ready");
        status.setRequestId("6c480a11-f001-492d-97a4-91a558268e9d");

        when(downloadReportFromJasper.executeExtractingOfReport("pdf", null, null)).thenReturn(status);

        ReportDetailDTO first = new ReportDetailDTO();
        first.setContentType("application/pdf");
        first.setExportStatus("execution");
        first.setRequestId("6c480a11-f001-492d-97a4-91a558268e9d");
        first.setId("76d2c54b-8104-45bc-af7a-74af9333954v");

        ReportDetailDTO second = new ReportDetailDTO();
        second.setContentType("application/pdf");
        second.setExportStatus("ready");
        second.setReady(true);
        second.setRequestId("6c480a11-f001-492d-97a4-91a558268e9d");
        second.setId("89d2c54b-8104-41bc-af7a-74af9333962b");

        List<ReportDetailDTO> reports = new ArrayList<ReportDetailDTO>();
        reports.add(first);
        reports.add(second);
        when(downloadReportFromJasper.getReportStatus("/6c480a11-f001-492d-97a4-91a558268e9d")).thenReturn(reports);

        context.restoreAuthSystemState();

        String[] args = new String[] { "download-report", "-s", "jasper", "-f", "pdf" };
        TestDSpaceRunnableHandler handler = new TestDSpaceRunnableHandler();
        downloadReportServices.put("jasper", downloadReportFromJasper);
        downloadReportService.initialize(args, handler, admin);
        downloadReportService.setDownloadReportServices(downloadReportServices);
        downloadReportService.run();

        File fileRecovery = new File("Collection.pdf");
        assertFalse(fileRecovery.exists());

    }
}