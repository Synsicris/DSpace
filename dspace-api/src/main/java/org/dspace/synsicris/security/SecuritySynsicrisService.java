/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.synsicris.security;

import java.sql.SQLException;

import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Service interface class for checking custom authorizations
 * used in PermissionEvaluetorPlugin or AuthorizationFeature.
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
public interface SecuritySynsicrisService {

    /**
     * This method check if the user being to context is member of the RoleGroup,
     * if yes return true otherwise false.
     * 
     * @param context          DSpace context object
     * @param item             Item uset to reach ItemProject
     * @param groupRole        Name of the RoleGroup of Project
     * @throws SQLException    If database error
     */
    public boolean isMemberOfRoleGroupOfProject(Context context, Item item, String groupRole) throws SQLException;

}