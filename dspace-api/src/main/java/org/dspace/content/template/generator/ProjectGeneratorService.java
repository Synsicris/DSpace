/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.sql.SQLException;

import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.eperson.Group;

/**
 * @author Vincenzo Mecca (vins01-4science - vincenzo.mecca at 4science.com)
 *
 */
public interface ProjectGeneratorService {

    Community getOwningCommunity(Item templateItem) throws SQLException;

    Community getProjectCommunity(Item templateItem) throws SQLException;

    MetadataValueVO getProjectCommunityMetadata(Context context, Community community, String role);

    MetadataValueVO getFundingCommunityMetadata(Context context, Community community, String role);

    Group getProjectCommunityGroup(Context context, Community community, String role);

    String getOwningFundigPolicy(Context context, Item Item) throws SQLException;

}