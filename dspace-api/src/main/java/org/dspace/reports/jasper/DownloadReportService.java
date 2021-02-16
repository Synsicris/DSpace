/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.reports.jasper;
import java.io.InputStream;
import java.util.List;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public interface DownloadReportService {

    public ReportDetailDTO executeExtractingOfReport(String format, String reportType, String resourceId);

    public List<ReportDetailDTO> getReportStatus(String requestId);

    public InputStream downloadReport(String path);

}