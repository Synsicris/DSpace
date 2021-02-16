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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class DownloadReportFromJasper implements DownloadReportService {

    public static final String READY_STATUS = "ready";
    public static final String FAILED_STATUS = "failed";
    public static final String EXECUTION_STATUS = "execution";

    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(DownloadReportFromJasper.class);

    @Autowired
    private JasperRestConnector jasperRestConnector;

    @Override
    public ReportDetailDTO executeExtractingOfReport(String format, String reportType, String resourceId) {
        InputStream is = jasperRestConnector.sendPostRequest(format, reportType, resourceId);
        if (Objects.nonNull(is)) {
            return getHeaderStatus(is);
        }
        return new ReportDetailDTO();
    }

    @Override
    public List<ReportDetailDTO> getReportStatus(String requestId) {
        InputStream is =  jasperRestConnector.sendGetRequest(requestId);
        if (Objects.nonNull(is)) {
            return getReports(is);
        }
        return Collections.emptyList();
    }

    @Override
    public InputStream downloadReport(String path) {
        return jasperRestConnector.sendGetRequest(path);
    }

    private List<ReportDetailDTO> getReports(InputStream is) {
        List<ReportDetailDTO> reports = new ArrayList<ReportDetailDTO>();
        try {
            JSONObject json = new JSONObject(IOUtils.toString(is, StandardCharsets.UTF_8));
            if (Objects.nonNull(json)) {
                String headerStatus = json.get("status").toString();
                if (StringUtils.isNotBlank(headerStatus)) {
                    if (headerStatus.equals(FAILED_STATUS)) {
                        throw new RuntimeException();
                    }
                    String requestId = json.get("requestId").toString();
                    JSONArray exports = json.getJSONArray("exports");
                    fillReportsDetail(reports, requestId, exports);
                }
            }
        } catch (JSONException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return reports;
    }

    private void fillReportsDetail(List<ReportDetailDTO> reports, String requestId, JSONArray exports) {
        for (int i = 0; i < exports.length(); i++) {
            ReportDetailDTO reportDetail = new ReportDetailDTO();
            String status = exports.getJSONObject(i).get("status").toString();
            String contentType = exports.getJSONObject(i).getJSONObject("outputResource").get("contentType").toString();
            if (exports.getJSONObject(i).getJSONObject("outputResource").has("fileName")) {
                reportDetail.setFileName(exports.getJSONObject(i)
                            .getJSONObject("outputResource").get("fileName").toString());
            }
            reportDetail.setContentType(contentType);
            reportDetail.setRequestId(requestId);
            switch (status) {
                case READY_STATUS :
                    reportDetail.setExportStatus(status);
                    reportDetail.setReady(true);
                    String id = exports.getJSONObject(i).get("id").toString();
                    reportDetail.setId(id);
                    reports.add(reportDetail);
                    continue;
                case EXECUTION_STATUS :
                    reportDetail.setExportStatus(status);
                    reports.add(reportDetail);
                    continue;
                default:
            }
        }
    }

    private ReportDetailDTO getHeaderStatus(InputStream is) {
        ReportDetailDTO reportDetail = new ReportDetailDTO();
        try {
            JSONObject json = new JSONObject(IOUtils.toString(is, StandardCharsets.UTF_8));
            if (Objects.nonNull(json)) {
                String headerStatus = json.get("status").toString();
                if (StringUtils.isNotBlank(headerStatus)) {
                    if (headerStatus.equals(FAILED_STATUS)) {
                        throw new RuntimeException();
                    }
                    reportDetail.setHeaderStatus(headerStatus);
                    String requestId = json.get("requestId").toString();
                    if (StringUtils.isNotBlank(requestId)) {
                        reportDetail.setRequestId(requestId);
                    }
                } else {
                    return reportDetail;
                }
            }
        } catch (JSONException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return reportDetail;
    }

    public ReportDetailDTO getStatus(InputStream is) {
        ReportDetailDTO reportDetail = new ReportDetailDTO();
        if (Objects.nonNull(is)) {
            try {
                JSONObject json = new JSONObject(IOUtils.toString(is, StandardCharsets.UTF_8));
                if (Objects.nonNull(json)) {
                    String status = json.get("status").toString();
                    if (StringUtils.isNotBlank(status)) {
                        if (status.equals("ready")) {
                            reportDetail.setExportStatus(status);
                            reportDetail.setReady(true);
                            String requestId = json.get("requestId").toString();
                            if (StringUtils.isNotBlank(requestId)) {
                                reportDetail.setRequestId(requestId);
                            }
                            String id = json.getJSONArray("exports").getJSONObject(0).get("id").toString();
                            if (StringUtils.isNotBlank(id)) {
                                reportDetail.setId(id);
                            }
                        } else {
                            return reportDetail;
                        }
                    }
                }
            } catch (JSONException | IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return reportDetail;
    }

}