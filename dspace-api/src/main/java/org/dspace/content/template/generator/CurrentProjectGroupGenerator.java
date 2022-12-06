/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.template.generator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.vo.MetadataValueVO;
import org.dspace.core.Context;
import org.dspace.project.util.ProjectConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link TemplateValueGenerator} that generates a value
 * related to the current project/funding group.
 * <p>
 * Syntax is: ###CURRENTPROJECT.[project|funding].[admin|members]###, so for
 * example ###CURRENTPROJECT.project.admin### will set metadata with value of
 * admin group for the parent project community.
 *
 * @author Giuseppe Digilio (giuseppe.digilio at 4science.it)
 */
public class CurrentProjectGroupGenerator extends AbstractProjectGenerator {

    private static final Logger log = LoggerFactory.getLogger(CurrentProjectGroupGenerator.class);

    public CurrentProjectGroupGenerator() {}

    @Override
    public List<MetadataValueVO> generator(Context context, Item targetItem, Item templateItem, String extraParams) {
        try {
            String role = "";
            MetadataValueVO metadataVO;
            String[] params = StringUtils.split(extraParams, "\\.");
            String scope = params[0];
            if (params.length > 1) {
                role = params[1];
            }

            switch (scope) {
                case ProjectConstants.PROJECT:
                    metadataVO =
                        this.projectGeneratorService.getProjectCommunityMetadata(
                            context, getProjectCommunity(templateItem), role
                        );
                    break;
                case ProjectConstants.FUNDING:
                    metadataVO =
                        this.projectGeneratorService.getFundingCommunityMetadata(
                            context, getOwningCommunity(templateItem), role
                        );
                    break;
                default:
                    throw new IllegalArgumentException("Unable to find mapper for : " + extraParams);
            }

            return List.of(metadataVO);
        } catch (Exception e) {
            log.error(
                "Error while evaluating resource policies for collection {}: {}",
                templateItem.getTemplateItemOf().getID(), e.getMessage(), e
            );
            return List.of();
        }
    }

}
