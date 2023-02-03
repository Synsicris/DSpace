/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;
import java.sql.SQLException;
import java.util.Iterator;
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

    public void setGrantsByFundingPolicy(Context context, Item item);

    public boolean isMemberOfFunding(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException;

    public List<Community> getAllFundingsByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException;

    public String getDefaultSharedValueByItemProject(Context context, Item projectItem);

    public Community getProjectCommunityByRelationProject(Context context, Item item) throws SQLException;

    public Community getFundingCommunityByRelationFunding(Context context, Item item) throws SQLException;

    public String getOwningFundigPolicy(Context context, Item item) throws SQLException;

    /**
     * Get the first parent community which the given item belong to. If the given
     * entity is a project's one it returns the Project community. If the item is a
     * funding's entity it returns the Funding community
     *
     * @param context
     * @param item
     */
    public Community getFirstOwningCommunity(Context context, Item item) throws SQLException;

    /**
     * Get the community that represent the Project which the given item belong to.
     *
     * @param context
     * @param item
     */
    public Community getProjectCommunity(Context context, Item item) throws SQLException;

    public Item getParentProjectItemByCollectionUUID(Context context, UUID collectionUUID) throws SQLException;

    public Item getParentProjectItemByCommunityUUID(Context context, UUID communityUUID) throws SQLException;

    public boolean isProjectItem(Item item);

    public Group getProjectCommunityGroupByRole(Context context, Community projectCommunity, String role)
            throws SQLException;

    public Group getFundingCommunityGroupByRole(Context context, Community fundingCommunity, String role)
            throws SQLException;

    public Iterator<Item> findVersionedItemsOfProject(
        Context context, Community projectCommunity, Item projectItem, String version
    );

    public Iterator<Item> findVersionedItemsRelatedToProject(
        Context context, Community projectCommunity, Item projectItem, String version
    );

    public Iterator<Item> findPreviousVisibleVersionsInCommunity(
        Context context, Community projectCommunity, String versionNumber
    );

    public Iterator<Item> findLastVersionVisibleInCommunity(
        Context context, Community projectCommunity
    );

    public Community getFundingCommunityByUser(Context context, EPerson ePerson, Community projectCommunity)
            throws SQLException;

    public Iterator<Item> findVersionedProjectItemsBy(Context context, UUID projectId);

}
