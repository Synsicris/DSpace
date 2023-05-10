/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.capture;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.dspace.core.Context;
import org.dspace.scripts.configuration.ScriptConfiguration;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class CaptureWebsiteScriptConfiguration<T extends CaptureWebsiteScript> extends ScriptConfiguration<T> {

    private Class<T> dspaceRunnableClass;

    @Override
    public Class<T> getDspaceRunnableClass() {
        return this.dspaceRunnableClass;
    }

    @Override
    public void setDspaceRunnableClass(Class<T> dspaceRunnableClass) {
        this.dspaceRunnableClass = dspaceRunnableClass;
    }

    @Override
    public boolean isAllowedToExecute(Context context) {
        return true;
    }

    @Override
    public Options getOptions() {
        return new Options()
            .addOption(
                Option.builder("r")
                    .longOpt("resource-url")
                    .desc("Resource URL after the server name")
                    .hasArg()
                    .required()
                    .build()
            )
            .addOption(
                Option.builder("t")
                    .longOpt("token")
                    .desc("Authentication token of this application")
                    .hasArg()
                    .required()
                    .build()
            )
            .addOption(
                Option.builder("c")
                    .longOpt("cookie")
                    .desc("XSRF cookie of this application")
                    .hasArg()
                    .required()
                    .build()
            )
            .addOption(
                Option.builder("x")
                    .longOpt("remove-elements")
                    .desc("Browser elements to be removed")
                    .hasArg()
                    .required(false)
                    .build()
            )
            .addOption(
                Option.builder("e")
                    .longOpt("element")
                    .desc("Element to capture")
                    .hasArg()
                    .required(false)
                    .build()
            )
            .addOption(
                Option.builder("s")
                    .longOpt("style")
                    .desc("CSS style to embed inside the page to capture")
                    .hasArg()
                    .required(false)
                    .build()
            )
            .addOption(
                Option.builder("h")
                    .longOpt("help")
                    .desc("Prints help informations")
                    .hasArg(false)
                    .required(false)
                    .build()
            );
    }

}
