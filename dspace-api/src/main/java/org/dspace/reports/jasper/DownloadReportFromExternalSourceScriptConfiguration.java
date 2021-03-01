/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.reports.jasper;
import org.apache.commons.cli.Options;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.scripts.configuration.ScriptConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class DownloadReportFromExternalSourceScriptConfiguration<T extends DownloadReportFromExternalSource>
       extends ScriptConfiguration<T> {

    private static final Logger log = LoggerFactory
                                      .getLogger(DownloadReportFromExternalSourceScriptConfiguration.class);

    private Class<T> dspaceRunnableClass;


    @Override
    public boolean isAllowedToExecute(Context context) {
        EPerson currentUser = context.getCurrentUser();
        return currentUser != null;
    }

    @Override
    public Options getOptions() {
        if (options == null) {
            Options options = new Options();
            options.addOption("s", "service", true, "the name of the external service to use");
            options.getOption("s").setType(String.class);
            options.getOption("s").setRequired(true);

            options.addOption("f", "format", true, "the report format");
            options.getOption("f").setType(String.class);
            options.getOption("f").setRequired(false);

            options.addOption("t", "type", true, "the report type");
            options.getOption("t").setType(String.class);
            options.getOption("t").setRequired(false);

            options.addOption("i", "resourceId", true, "the resourceId");
            options.getOption("i").setType(String.class);
            options.getOption("i").setRequired(false);

            super.options = options;
        }
        return options;
    }

    @Override
    public Class<T> getDspaceRunnableClass() {
        return dspaceRunnableClass;
    }

    @Override
    public void setDspaceRunnableClass(Class<T> dspaceRunnableClass) {
        this.dspaceRunnableClass = dspaceRunnableClass;
    }

}