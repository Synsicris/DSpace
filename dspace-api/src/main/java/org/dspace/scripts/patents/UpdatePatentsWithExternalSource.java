/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.scripts.patents;

import static org.dspace.content.Item.ANY;
import static org.dspace.project.util.ProjectConstants.COORDINATORS_ROLE;
import static org.dspace.project.util.ProjectConstants.DEFAULT_STATUS;
import static org.dspace.project.util.ProjectConstants.MD_PATENTNO;
import static org.dspace.project.util.ProjectConstants.MD_PROJECT_RELATION;
import static org.dspace.project.util.ProjectConstants.MD_PROJECT_STATUS;
import static org.dspace.project.util.ProjectConstants.RUNNING_STATUS;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.ItemServiceImpl;
import org.dspace.content.MetadataValue;
import org.dspace.content.dto.MetadataValueDTO;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.Email;
import org.dspace.core.I18nUtil;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResultIterator;
import org.dspace.discovery.indexobject.IndexableItem;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.external.model.ExternalDataObject;
import org.dspace.external.provider.impl.LiveImportDataProvider;
import org.dspace.importer.external.metadatamapping.MetadataFieldConfig;
import org.dspace.kernel.ServiceManager;
import org.dspace.scripts.DSpaceRunnable;
import org.dspace.submit.consumer.service.ProjectConsumerService;
import org.dspace.submit.consumer.service.ProjectConsumerServiceImpl;
import org.dspace.utils.DSpace;

/**
 * Implementation of {@link DSpaceRunnable} to update Patents with external service as European Patent Office (EPO)
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.com)
 */
public class UpdatePatentsWithExternalSource
        extends DSpaceRunnable<UpdatePatentsWithExternalSourceScriptConfiguration<UpdatePatentsWithExternalSource>> {

    private final static Logger log = LogManager.getLogger();

    private Context context;
    // services
    private ItemService itemService;
    private ProjectConsumerService projectService;
    private LiveImportDataProvider liveImportDataProvider;

    @Override
    public void setup() throws ParseException {
        ServiceManager serviceManager = new DSpace().getServiceManager();
        liveImportDataProvider = serviceManager.getServiceByName("epoLiveImportDataProvider",
                                                                 LiveImportDataProvider.class);
        itemService = serviceManager.getServiceByName(ItemServiceImpl.class.getName(),
                                                      ItemServiceImpl.class);
        projectService = serviceManager.getServiceByName(ProjectConsumerServiceImpl.class.getName(),
                                                         ProjectConsumerServiceImpl.class);
    }

    @Override
    public void internalRun() throws Exception {
        assignCurrentUserInContext();
        try {
            context.turnOffAuthorisationSystem();
            Iterator<Item> itemIterator = findPatents();
            handler.logInfo("Update start");

            int count = 0;
            int countFoundItems = 0;
            int countUpdatedItems = 0;
            Set<Item> updatedPatentsToSendEmail = new HashSet<Item>();

            while (itemIterator.hasNext()) {
                Item localPatent = itemIterator.next();
                count ++;
                countFoundItems++;
                boolean isUpdated = updatePatent(context, localPatent, liveImportDataProvider);

                if (isUpdated) {
                    countUpdatedItems++;
                    if (isTheProjectRunning(localPatent)) {
                        updatedPatentsToSendEmail.add(localPatent);
                    }
                }

                if (count == 20) {
                    context.commit();
                    sendEmail(updatedPatentsToSendEmail);
                    count = 0;
                }
            }

            if (count > 0) {
                sendEmail(updatedPatentsToSendEmail);
            }

            context.complete();
            handler.logInfo("Found " + countFoundItems + " items");
            handler.logInfo("Updated " + countUpdatedItems + " Patents");
            handler.logInfo("Update end");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            handler.handleException(e);
            context.abort();
        } finally {
            context.restoreAuthSystemState();
        }
    }

    private void sendEmail(Set<Item> updatedPatentsToSendEmail) throws SQLException {
        for (Item patent : updatedPatentsToSendEmail) {
            Group coordinatorGroup = getCoordinatorGroup(patent);
            sendEmailToCoordinator(coordinatorGroup.getMembers(), patent);
        }
        updatedPatentsToSendEmail.clear();
    }

    private void sendEmailToCoordinator(List<EPerson> coordinators, Item patent) {
        for (EPerson coordinator : coordinators) {
            try {
                Locale supportedLocale = I18nUtil.getEPersonLocale(coordinator);
                Email email = Email.getEmail(I18nUtil.getEmailFilename(supportedLocale, "notification_updated_patent"));
                email.addRecipient(coordinator.getEmail());
                email.addArgument(patent.getName());
                email.addArgument(patent.getID());
                email.send();
            } catch (Exception e) {
                log.warn("Error during sendig notification about updated patent! " + e.getMessage(), e);
            }
        }
    }

    private Group getCoordinatorGroup(Item patent) throws SQLException {
        Community projectCommunity = projectService.getProjectCommunityByRelationProject(context, patent);
        return projectService.getProjectCommunityGroupByRole(context, projectCommunity, COORDINATORS_ROLE);
    }

    private boolean isTheProjectRunning(Item patent) throws SQLException {
        Item project = getProjet(patent);
        if (Objects.isNull(project)) {
            log.error("Patent with uuid: " + patent.getID() + " does not belong to any project");
            return false;
        }
        List<MetadataValue> projectStatusValue = itemService.getMetadata(project, MD_PROJECT_STATUS.schema,
                                                                                  MD_PROJECT_STATUS.element,
                                                                                  MD_PROJECT_STATUS.qualifier, ANY);
        return projectStatusValue.stream()
                                 .filter(mv -> StringUtils.equalsAny(mv.getValue(), DEFAULT_STATUS, RUNNING_STATUS))
                                 .findFirst()
                                 .isPresent();
    }

    private Item getProjet(Item patent) throws SQLException {
        List<MetadataValue> metadataValues = itemService.getMetadata(patent, MD_PROJECT_RELATION.schema,
                                                                             MD_PROJECT_RELATION.element,
                                                                             MD_PROJECT_RELATION.qualifier, ANY);
        if (metadataValues.isEmpty()) {
            return null;
        }
        UUID projectUUID = UUID.fromString(metadataValues.get(0).getAuthority());
        return itemService.find(this.context, projectUUID);
    }

    public boolean updatePatent(Context context, Item item, LiveImportDataProvider liveImportDataProvider)
            throws SQLException {
        String patentNo = itemService.getMetadataFirstValue(item, MD_PATENTNO.schema,
                                                                  MD_PATENTNO.element,
                                                                  MD_PATENTNO.qualifier, ANY);
        if (StringUtils.isBlank(patentNo)) {
            return false;
        }
        Optional<ExternalDataObject> externalPatent = liveImportDataProvider.getExternalDataObject(patentNo);
        if (externalPatent.isEmpty()) {
            return false;
        }

        List<MetadataFieldConfig> supportedMetadataFields = liveImportDataProvider.getQuerySource()
                                                                                  .getSupportedMetadataFields();
        return isMoreUpToDateThanCurrentPatent(context, item, externalPatent.get()) ?
                 updateCurrentPatentWithNewOne(context, item, supportedMetadataFields, externalPatent.get()) : false;
    }

    private boolean isMoreUpToDateThanCurrentPatent(Context context, Item currentPatent,
            ExternalDataObject externalPatent) {
        var publishedDateOfCurrentPatent = itemService.getMetadataFirstValue(currentPatent, "dc", "date", "issued",ANY);
        LocalDate publicationDateOfLocalPatent = LocalDate.parse(publishedDateOfCurrentPatent);
        LocalDate publicationDateOfExternalPatent = getPublicationDateOfExternalPatent(externalPatent);
        return publicationDateOfExternalPatent.isAfter(publicationDateOfLocalPatent);
    }

    private LocalDate getPublicationDateOfExternalPatent(ExternalDataObject externalPatent) {
        Optional<MetadataValueDTO> publicationDateOfExternalPatent = externalPatent.getMetadata()
                                                 .stream()
                                                 .filter(mv -> StringUtils.equals(mv.getSchema(), "dc") &&
                                                               StringUtils.equals(mv.getElement(), "date") &&
                                                               StringUtils.equals(mv.getQualifier(), "issued"))
                                                 .findFirst();
        return publicationDateOfExternalPatent.isPresent() ?
               LocalDate.parse(publicationDateOfExternalPatent.get().getValue()) : LocalDate.MIN;
    }

    private boolean updateCurrentPatentWithNewOne(Context context, Item localPatent,
            List<MetadataFieldConfig> supportedMetadataFields, ExternalDataObject externalPatent) {
        Map<String, Boolean> nonRepeatableMetadata = getScriptConfiguration().getNonRepeatableMetadata();
        try {
            localPatent = clearMetadataOfLocalPatent(context, localPatent, supportedMetadataFields);
            for (MetadataValueDTO mv : externalPatent.getMetadata()) {
                addMetadata(context, localPatent, nonRepeatableMetadata, mv);
            }
            itemService.update(context, localPatent);
        } catch (SQLException | AuthorizeException e) {
            log.error("The Patent with uuid " + localPatent.getID() + " was not updated by the fallowing cause:"
                                              + e.getCause(), e.getMessage());
            return false;
        }
        return true;
    }

    private void addMetadata(Context context, Item localPatent, Map<String, Boolean> nonRepeatableMetadata,
            MetadataValueDTO mv) throws SQLException {
        var metadataFiled = mv.getMetadataFiled();
        if (!nonRepeatableMetadata.containsKey(metadataFiled)) {
            itemService.addMetadata(context, localPatent, mv.getSchema(),
                                                          mv.getElement(),
                                                          mv.getQualifier(), null, mv.getValue());
        } else if (nonRepeatableMetadata.containsKey(metadataFiled) && !nonRepeatableMetadata.get(metadataFiled)) {
            itemService.addMetadata(context, localPatent, mv.getSchema(),
                                                          mv.getElement(),
                                                          mv.getQualifier(), null, mv.getValue());
            nonRepeatableMetadata.put(mv.getMetadataFiled(), true);
        }
    }

    private Item clearMetadataOfLocalPatent(Context context, Item localPatent,
            List<MetadataFieldConfig> supportedMetadataFields) throws SQLException, AuthorizeException {
        for (MetadataFieldConfig mfc : supportedMetadataFields) {
            itemService.clearMetadata(context, localPatent, mfc.getSchema(), mfc.getElement(), mfc.getQualifier(), ANY);
        }
        return localPatent;
    }

    private Iterator<Item> findPatents() {
        DiscoverQuery discoverQuery = new DiscoverQuery();
        discoverQuery.setDSpaceObjectFilter(IndexableItem.TYPE);
        discoverQuery.addFilterQueries("search.entitytype:Patent");
        discoverQuery.addFilterQueries("-(relation.isVersionOf:*)");
        discoverQuery.setMaxResults(20);
        return new DiscoverResultIterator<Item, UUID>(context, discoverQuery);
    }

    private void assignCurrentUserInContext() throws SQLException {
        context = new Context();
        UUID uuid = getEpersonIdentifier();
        if (Objects.nonNull(uuid)) {
            EPerson ePerson = EPersonServiceFactory.getInstance().getEPersonService().find(context, uuid);
            context.setCurrentUser(ePerson);
        }
    }

    public LiveImportDataProvider getLiveImportDataProvider() {
        return liveImportDataProvider;
    }

    public void setLiveImportDataProvider(LiveImportDataProvider liveImportDataProvider) {
        this.liveImportDataProvider = liveImportDataProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UpdatePatentsWithExternalSourceScriptConfiguration<UpdatePatentsWithExternalSource>
           getScriptConfiguration() {
        return new DSpace().getServiceManager().getServiceByName("update-patents",
                                                UpdatePatentsWithExternalSourceScriptConfiguration.class);
    }

}