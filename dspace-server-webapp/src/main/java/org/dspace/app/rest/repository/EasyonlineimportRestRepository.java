/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.dspace.app.easyliveimport.EasyOnlineImport;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.EasyOnlineImportRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EasyonlineimportServiceImpl;
import org.dspace.content.Item;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.authority.Choices;
import org.dspace.content.service.CollectionService;
import org.dspace.content.service.InstallItemService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkspaceItemService;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultIterator;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.project.util.ProjectConstants;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This is the repository responsible to manage EasyOnlineImport Rest object
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component(EasyOnlineImportRest.CATEGORY + "." + EasyOnlineImportRest.NAME)
public class EasyonlineimportRestRepository extends DSpaceRestRepository<EasyOnlineImportRest, UUID> {

    @Autowired
    private ItemService itemService;

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private EasyonlineimportServiceImpl easyOnlineImportService;

    @Autowired
    private ProjectConsumerService projectConsumerService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Override
    public Page<EasyOnlineImportRest> findAll(Context context, Pageable pageable) {
        throw new RepositoryMethodNotImplementedException(EasyOnlineImportRest.NAME, "findAll");
    }

    @Override
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public EasyOnlineImportRest findOne(Context context, UUID id) {
        throw new RepositoryMethodNotImplementedException(EasyOnlineImportRest.NAME, "findOne");
    }

    @Override
    @PreAuthorize("hasPermission(#id, 'easyonlineimport', 'WRITE')")
    public EasyOnlineImportRest upload(HttpServletRequest request,String apiCategory, String model,
            UUID id, MultipartFile multipartFile)
            throws SQLException, IOException, AuthorizeException {
        EasyOnlineImport easyOnlineImport = new EasyOnlineImport();
        easyOnlineImport.setId(id);
        List<UUID> modified = new ArrayList<UUID>();

        Context context = obtainContext();
        Item item = itemService.find(context, id);
        if (Objects.isNull(item)) {
            throw new UnprocessableEntityException("The uuid: " + id +
                                                   " provided is not correspond to a valid Project");
        }
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
        if (!StringUtils.equals(ProjectConstants.FUNDING_ENTITY, entityType)) {
            throw new UnprocessableEntityException("The uuid:" + id +
                                                   " proviede is non correspond to Project, is " + entityType);
        }
        Item projectPartnerItem = null;
        try {
            Document document = Utils.extractDocument(multipartFile);
            // import Project item
            easyOnlineImportService.importFile(context, item, document, entityType);
            modified.add(item.getID());
            // import project partner item
            Collection projectPartnerCollection = getProjectPartnerCollection(context, item);
            Iterator<Item> items =
                Optional.ofNullable(projectPartnerCollection)
                    .map(collection -> findItems(context, collection, item))
                    .orElseThrow(
                        () -> new UnprocessableEntityException(
                            "Missing projectpartner linked collection of the item: " + item.getID().toString()
                        )
                    );
            if (items != null && items.hasNext()) {
                projectPartnerItem = items.next();
                easyOnlineImportService.importFile(context, projectPartnerItem, document, "projectpartner");
                modified.add(projectPartnerItem.getID());
            } else {
                WorkspaceItem workspaceItem = workspaceItemService.create(context, projectPartnerCollection, true);
                Item newItem = installItemService.installItem(context, workspaceItem);
                easyOnlineImportService.importFile(context, newItem, document, ProjectConstants.PROJECTPARTNER_ENTITY);
                collectionService.addItem(context, projectPartnerCollection, newItem);
                itemService.replaceMetadata(context, newItem, ProjectConstants.MD_EASYIMPORT.schema,
                        ProjectConstants.MD_EASYIMPORT.element, ProjectConstants.MD_EASYIMPORT.qualifier, null,
                        "executing orgunit", null, Choices.CF_UNSET, 0);
                itemService.replaceMetadata(context, newItem, ProjectConstants.MD_FUNDING_RELATION.schema,
                        ProjectConstants.MD_FUNDING_RELATION.element, ProjectConstants.MD_FUNDING_RELATION.qualifier,
                        null, item.getName(), item.getID().toString(), Choices.CF_ACCEPTED, 0);
                newItem = context.reloadEntity(newItem);
                easyOnlineImport.setCreated(Collections.singletonList(newItem.getID()));
            }
            easyOnlineImport.setModified(modified);
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new UnprocessableEntityException(e.getMessage());
        }
        context.commit();
        return converter.toRest(easyOnlineImport, utils.obtainProjection());
    }

    private Collection getProjectPartnerCollection(Context context, Item item) throws SQLException {
        Community parentCommunity = projectConsumerService.getFirstOwningCommunity(context, item);
        if (Objects.nonNull(parentCommunity)) {
            return collectionService.retriveCollectionByEntityType(context, parentCommunity,
                    ProjectConstants.PROJECTPARTNER_ENTITY);
        }
        return null;
    }

    private Iterator<Item> findItems(Context context, Collection collection, Item projectItem) {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.setDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addFilterQueries("dspace.entity.type:" + ProjectConstants.PROJECTPARTNER_ENTITY);
        discoverQuery.addFilterQueries(ProjectConstants.MD_EASYIMPORT.toString() + ":executing orgunit");
        discoverQuery.addFilterQueries("location.coll:" + collection.getID().toString());
        discoverQuery.addFilterQueries(
            ProjectConstants.MD_FUNDING_RELATION.toString() + "_authority:" +
                projectItem.getID().toString()
        );
        return new DiscoverResultIterator<Item, UUID>(context, discoverQuery);
    }

    @Override
    public Class<EasyOnlineImportRest> getDomainClass() {
        return EasyOnlineImportRest.class;
    }

}