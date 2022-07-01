/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public interface ProjectConsumerService {

    public void processItem(Context context, EPerson currentUser, Item item);

    public void checkGrants(Context context, EPerson currentUser, Item item);

    public Community isMemberOfFunding(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException;

    public List<Community> getAllFundingsByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException;

    public Community getProjectCommunityByRelationProject(Context context, Item item) throws SQLException;

    public Community getProjectCommunity(Context context, Item item) throws SQLException;

    public Item getParentProjectItemByCollectionUUID(Context context, UUID collectionUUID) throws SQLException;

    public boolean isProjectItem(Item item);

    public Group getProjectCommunityGroupByRole(Context context, Community projectCommunity, String role)
            throws SQLException;

    public Group getFundingCommunityGroupByRole(Context context, Community fundingCommunity, String role)
            throws SQLException;

}