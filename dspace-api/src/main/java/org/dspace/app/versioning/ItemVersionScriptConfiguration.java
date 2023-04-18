/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.core.Context;
import org.dspace.scripts.configuration.ScriptConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 * @param <T>
 */
public class ItemVersionScriptConfiguration<T extends ItemVersionScript> extends ScriptConfiguration<T> {

    @Autowired
    private AuthorizeService authorizeService;

    private Class<T> dspaceRunnableClass;

    // list of actions to accomplish
    private List<VersioningActionConfiguration> actions;

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
        // TODO: handle permissions for versioning
        return true;
    }

    @Override
    public Options getOptions() {
        return new Options()
            .addOption(
                Option.builder("i")
                    .longOpt("item")
                    .desc("UUID of item to be versioned")
                    .hasArg()
                    .required()
                    .build()
            )
            .addOption(
                Option.builder("s")
                    .longOpt("summary")
                    .desc("Summary of the version")
                    .hasArg()
                    .required(false)
                    .build()
            )
            .addOption(
                Option.builder("h")
                    .longOpt("help")
                    .desc("help")
                    .hasArg(false)
                    .required(false)
                    .build()
            );
    }

}
