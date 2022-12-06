/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.eperson;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dspace.core.Context;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * This class is responsible to provide access to the configuration of the
 * Group System
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public class GroupConfiguration {

    public static final String FUNDERS_PROJECT_MANAGER = "funders-project-managers.group";
    public static final String ORGANISATIONAL_MANAGER = "funder-organisational-managers.group";
    public static final String SYSTEM_MEMBERS = "system_members.group";

    /**
     * A static reference to the {@link ConfigurationService} see the init method for initialization
     */
    private static ConfigurationService configurationService;

    private static GroupService groupService;

    /**
     * Default constructor
     */
    private GroupConfiguration() { }

    /**
     * Complete the initialization of the class retrieving a reference to the {@link ConfigurationService}. MUST be
     * called at the start of each method
     */
    private synchronized static void init() {
        if (configurationService != null) {
            return;
        }
        configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        if (groupService != null) {
            return;
        }
        groupService = EPersonServiceFactory.getInstance().getGroupService();
    }

    protected static Group getGroupFromProperty(Context context, String property) throws SQLException {
        init();
        String groupId = configurationService.getProperty(property);
        Group group = null;
        if (StringUtils.isNotEmpty(groupId)) {
            group = groupService.find(context, UUID.fromString(groupId));
        }
        return group;
    }

}
