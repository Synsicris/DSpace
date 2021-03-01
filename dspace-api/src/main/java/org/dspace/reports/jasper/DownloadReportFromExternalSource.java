/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.reports.jasper;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.kernel.ServiceManager;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.utils.DSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link DSpaceRunnable} to download reports with external service as Jasper
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class DownloadReportFromExternalSource extends
             DSpaceRunnable<DownloadReportFromExternalSourceScriptConfiguration<DownloadReportFromExternalSource>> {

    private static final Logger log = LoggerFactory.getLogger(DownloadReportFromExternalSource.class);

    private Map<String, DownloadReportService> downloadReportServices = new HashMap<>();

    private Context context;

    private String service;

    private String format;

    private String reportType;

    private String resourceId;

    private long timeToSleep;

    private int maxAttempt;

    private ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();

    @Override
    public void setup() throws ParseException {
        ServiceManager serviceManager = new DSpace().getServiceManager();
        downloadReportServices.put("jasper", serviceManager.getServiceByName(DownloadReportFromJasper.class.getName(),
                                                                             DownloadReportFromJasper.class));
        this.service = commandLine.getOptionValue('s');
        this.format = commandLine.getOptionValue('f');
        this.reportType = commandLine.getOptionValue('t');
        this.resourceId = commandLine.getOptionValue('i');
        this.timeToSleep = Integer.valueOf(configurationService.getProperty("reports.timetosleep"));
        this.maxAttempt = Integer.valueOf(configurationService.getProperty("reports.max_attempt"));
    }

    @Override
    public void internalRun() throws Exception {
        assignCurrentUserInContext();
        if (service == null) {
            throw new IllegalArgumentException("The name of service must be provided");
        }
        if (StringUtils.isBlank(this.format)) {
            this.format = "pdf";
        }
        DownloadReportService externalService = downloadReportServices.get(this.service.toLowerCase());
        if (externalService == null) {
            throw new IllegalArgumentException("The name of service must be provided");
        }
        try {
            context.turnOffAuthorisationSystem();
            List<ReportDetailDTO> reportsToDownload = null;
            ReportDetailDTO reportDetail = externalService.executeExtractingOfReport(
                                           this.format.toLowerCase(),
                                           this.reportType,
                                           this.resourceId);
            if (StringUtils.isNotBlank(reportDetail.getRequestId())) {
                StringBuilder pathRequestId = new StringBuilder("/");
                pathRequestId.append(reportDetail.getRequestId());
                boolean trigger = true;
                int attemptCount = 0;
                while (trigger) {
                    int counrInExecution = 0;
                    List<ReportDetailDTO> reports = externalService.getReportStatus(pathRequestId.toString());
                    for (ReportDetailDTO report : reports) {
                        if (!report.isReady()) {
                            counrInExecution ++;
                        }
                    }
                    if (counrInExecution == 0) {
                        trigger = false;
                        reportsToDownload = reports;
                    } else {
                        if (attemptCount > this.maxAttempt) {
                            handler.logInfo("The downloading process was interrupted\n" +
                                            "as the maximum number of attempts has been reached.\n" +
                                            " RequestID: " + reportDetail.getRequestId());
                            break;
                        }
                        attemptCount ++;
                        handler.logInfo("Sleep for " + this.timeToSleep / 1000 + " seconds.");
                        Thread.sleep(this.timeToSleep);
                    }
                }
                // download reports
                if (Objects.nonNull(reportsToDownload)) {
                    try {
                        downloadReports(reportsToDownload, externalService);
                    } catch (IOException | SQLException | AuthorizeException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            context.complete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            handler.handleException(e);
            context.abort();
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private void downloadReports(List<ReportDetailDTO> reportsToDownload, DownloadReportService externalService)
            throws IOException, SQLException, AuthorizeException {
        for (ReportDetailDTO report : reportsToDownload) {
            StringBuilder path = new StringBuilder("/");
            path.append(report.getRequestId()).append("/exports/").append(report.getId()).append("/outputResource");
            InputStream is = externalService.downloadReport(path.toString());
            String fileName = report.getId();
            if (StringUtils.isNotBlank(report.getFileName())) {
                fileName = report.getFileName();
            }
            handler.writeFilestream(context, fileName, is, report.getContentType());
            handler.logInfo("Report with id: " + report.getId() + " exported successfully!");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public DownloadReportFromExternalSourceScriptConfiguration
                            <DownloadReportFromExternalSource> getScriptConfiguration() {
        return new DSpace().getServiceManager().getServiceByName("download-report",
                DownloadReportFromExternalSourceScriptConfiguration.class);
    }

    private void assignCurrentUserInContext() throws SQLException {
        context = new Context();
        UUID uuid = getEpersonIdentifier();
        if (uuid != null) {
            EPerson ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
            context.setCurrentUser(ePerson);
        }
    }

    public Map<String, DownloadReportService> getDownloadReportServices() {
        return downloadReportServices;
    }

    public void setDownloadReportServices(Map<String, DownloadReportService> downloadReportServices) {
        this.downloadReportServices = downloadReportServices;
    }

    public int getMaxAttempt() {
        return maxAttempt;
    }

    public void setMaxAttempt(int maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

}