/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents.service;

import static org.dspace.content.Item.ANY;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.external.model.ExternalDataObject;
import org.dspace.external.provider.impl.LiveImportDataProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class UpdatePatentServiceImpl implements UpdatePatentService {

    @Autowired
    private ItemService itemService;

    @Override
    public boolean updatePatent(Context context, Item item, LiveImportDataProvider liveImportDataProvider)
            throws SQLException {
        String patentNo = itemService.getMetadataFirstValue(item, "dc", "identifier", "patentno", ANY);
        if (StringUtils.isBlank(patentNo)) {
            return false;
        }
        List<ExternalDataObject> patents = liveImportDataProvider.searchExternalDataObjects(patentNo, 0, -1);
        if (CollectionUtils.isEmpty(patents)) {
            //TODO: manage case is the Patent was deleted!
            return false;
        }
        ExternalDataObject newPatent = getPatentMoreUpToDateThanCurrentPatent(context, item, patents);
        if (Objects.isNull(newPatent)) {
            return false;
        }
        return updateCurrentPatentWithNewOne(context, item, newPatent);
    }

    private ExternalDataObject getPatentMoreUpToDateThanCurrentPatent(Context context, Item currentPatent,
            List<ExternalDataObject> patents) {
        var publishedDateOfCurrentPatent = itemService.getMetadataFirstValue(currentPatent, "dc", "date", "issued",ANY);
        LocalDate currentD = LocalDate.parse(publishedDateOfCurrentPatent);
        for (ExternalDataObject externalPatent : patents) {
            Optional<MetadataValueDTO> publishedDateOfexternalPatent = getPublishedDateOfexternalPatent(externalPatent);
            if (!publishedDateOfexternalPatent.isPresent()) {
                continue;
            }
            String date = publishedDateOfexternalPatent.get().getValue();
            LocalDate dateFromString = LocalDate.parse(date);
            if (dateFromString.isAfter(currentD)) {
                return externalPatent;
            }
        }
        return null;
    }

    private Optional<MetadataValueDTO> getPublishedDateOfexternalPatent(ExternalDataObject externalPatent) {
        return externalPatent.getMetadata()
                             .stream()
                             .filter(mv -> StringUtils.equals(mv.getSchema(), "dc") &&
                                           StringUtils.equals(mv.getElement(), "date") &&
                                           StringUtils.equals(mv.getQualifier(), "issued"))
                             .findFirst();
    }

    private boolean updateCurrentPatentWithNewOne(Context context, Item currentPatent, ExternalDataObject newPatent)
            throws SQLException {
        for (MetadataValueDTO mv : newPatent.getMetadata()) {
            itemService.clearMetadata(context, currentPatent, mv.getSchema(), mv.getElement(), mv.getQualifier(), null);
            itemService.addMetadata(context, currentPatent, mv.getSchema(), mv.getElement(), mv.getQualifier(), ANY,
                                    mv.getValue());
        }
        return true;
    }

}