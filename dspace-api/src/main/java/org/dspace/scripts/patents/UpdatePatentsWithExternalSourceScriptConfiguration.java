/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.cli.Options;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.core.Context;
import org.dspace.scripts.configuration.ScriptConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link ScriptConfiguration} for the {@link UpdatePatentsWithExternalSource}.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class UpdatePatentsWithExternalSourceScriptConfiguration<T extends UpdatePatentsWithExternalSource>
        extends ScriptConfiguration<T> {

    private static final Logger log = LoggerFactory.getLogger(UpdatePatentsWithExternalSourceScriptConfiguration.class);

    private Class<T> dspaceRunnableClass;

    /**
     * this map is used to manage the non-repeatable fields of the submission-form
     * for the patent entity, but which may receive multiple values from the external provider
     * during the update procedure. with this map, we only save the first value (e.g. title or description).
     */
    private Map<String, Boolean> nonRepeatableMetadata;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public boolean isAllowedToExecute(Context context) {
        try {
            return authorizeService.isAdmin(context);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Options getOptions() {
        if (Objects.isNull(options)) {
            Options options = new Options();
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

    public Map<String, Boolean> getNonRepeatableMetadata() {
        return nonRepeatableMetadata;
    }

    public void setNonRepeatableMetadata(Map<String, Boolean> nonRepeatableMetadata) {
        this.nonRepeatableMetadata = nonRepeatableMetadata;
    }

}