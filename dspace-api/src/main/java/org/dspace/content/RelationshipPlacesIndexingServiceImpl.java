/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.solr.common.SolrInputDocument;
import org.dspace.content.dao.RelationshipDAO;
import org.dspace.content.service.RelationshipPlacesIndexingService;
import org.dspace.core.Context;
import org.dspace.discovery.IndexingService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 * @author Corrado Lombardi (corrado.lombardi at 4science.it)
 *
 */
public class RelationshipPlacesIndexingServiceImpl implements RelationshipPlacesIndexingService {

    @Autowired
    private IndexingService indexingService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private RelationshipDAO relationshipDAO;

    @Override
    public void updateRelationReferences(final Context context, final Relationship relationship) throws SQLException {

        final List<SolrInputDocument> relationDocuments = new LinkedList<>();
        // If only left position is used to sort this relation, we index all other impacted item positions starting
        // from left item
        if (singleDirectionRelationship("left", relationship.getRelationshipType())) {
            Item leftItem = relationship.getLeftItem();
            final List<Relationship> relations = relationshipDAO.findByItem(context, leftItem,
                                                                            -1, -1, false, false);

            List<String> rightItemsIdsToAdd = new LinkedList<>();

            for (final Relationship relation : relations) {
                Optional.ofNullable(addLeftItemsReferences(context, relationship, relation))
                    .ifPresent(relationDocuments::add);

                int times = 1;
                if (singleDirectionRelationship("right", relationship.getRelationshipType())) {
                    times = relation.getLeftPlace() - relation.getRightPlace();
                }
                rightItemsIdsToAdd.addAll(Collections.nCopies(times, relation.getRightItem().getID().toString()));
            }
            if (!rightItemsIdsToAdd.isEmpty()) {
                Optional.ofNullable(
                    indexingService.generateSolrRelationDocumentForItem(
                        leftItem.getID().toString(),
                        relationship.getRelationshipType().getRightwardType(),
                        rightItemsIdsToAdd
                    )
                )
                    .ifPresent(relationDocuments::add);
            }
        } else {
            // if both or only right place is used to sort relationship, impacted items are indexed starting from
            // right item.
            Item rightItem = relationship.getRightItem();
            final List<Relationship> relations = relationshipDAO.findByItem(context, rightItem, -1, -1,
                                                                            false, false);

            List<String> leftItemsIdsToAdd = new LinkedList<>();

            for (final Relationship relation : relations) {
                Optional.ofNullable(addRightItemsReferences(context, relationship, relation))
                    .ifPresent(relationDocuments::add);

                int times = 1;
                if (singleDirectionRelationship("left", relationship.getRelationshipType())) {
                    times = relation.getRightPlace() - relation.getLeftPlace();
                }
                leftItemsIdsToAdd.addAll(Collections.nCopies(times, relation.getLeftItem().getID().toString()));
            }
            if (!leftItemsIdsToAdd.isEmpty()) {
                Optional.ofNullable(
                    indexingService.generateSolrRelationDocumentForItem(
                        rightItem.getID().toString(),
                        relationship.getRelationshipType().getRightwardType(),
                        leftItemsIdsToAdd
                    )
                )
                    .ifPresent(relationDocuments::add);
            }
        }
        this.indexingService.updateSolrDocuments(relationDocuments);
    }


    private SolrInputDocument addRightItemsReferences(final Context context, final Relationship relationship,
                                         final Relationship relation) throws SQLException {
        final Item leftItem = relation.getLeftItem();
        final List<Relationship> leftItemRelationships = relationshipDAO.findByItem(context, leftItem,
                                                                                    -1, -1, false, false);
        List<String> rightItemsToAdd = new LinkedList<>();
        SolrInputDocument solrDoc = null;
        for (final Relationship leftItemRelation : leftItemRelationships) {
            int times = 1;
            if (singleDirectionRelationship("right", relationship.getRelationshipType())) {
                times = leftItemRelation.getLeftPlace() - leftItemRelation.getRightPlace();
            }
            rightItemsToAdd.addAll(Collections.nCopies(times, leftItemRelation.getRightItem().getID().toString()));
        }
        if (!rightItemsToAdd.isEmpty())  {
            solrDoc =
                indexingService.generateSolrRelationDocumentForItem(
                    leftItem.getID().toString(),
                    relation.getRelationshipType().getLeftwardType(),
                    rightItemsToAdd
                );
        }
        return solrDoc;
    }

    private SolrInputDocument addLeftItemsReferences(final Context context, final Relationship relationship,
                                         final Relationship relation) throws SQLException {
        final Item rightItem = relation.getRightItem();
        final List<Relationship> leftItemRelationships = relationshipDAO.findByItem(context, rightItem,
                                                                                    -1, -1, false, false);
        List<String> rightItemsToAdd = new LinkedList<>();
        SolrInputDocument solrDoc = null;
        for (final Relationship leftItemRelation : leftItemRelationships) {
            int times = 1;
            if (singleDirectionRelationship("left", relationship.getRelationshipType())) {
                times = leftItemRelation.getRightPlace() - leftItemRelation.getLeftPlace();
            }
            rightItemsToAdd.addAll(Collections.nCopies(times, leftItemRelation.getLeftItem().getID().toString()));
        }
        if (!rightItemsToAdd.isEmpty())  {
            solrDoc =
                indexingService.generateSolrRelationDocumentForItem(
                    rightItem.getID().toString(),
                    relation.getRelationshipType().getRightwardType(),
                    rightItemsToAdd
                );
        }
        return solrDoc;
    }


    private boolean singleDirectionRelationship(final String direction, final RelationshipType relationshipType) {
        final String[] placesSettings = configurationService
                                            .getArrayProperty("relationship.places.only" + direction);
        if (placesSettings == null) {
            return false;
        }
        final String leftTypeLabel = Optional.ofNullable(relationshipType.getLeftType())
                                             .map(EntityType::getLabel).orElse("null");
        final String rightTypeLabel = Optional.ofNullable(relationshipType.getRightType())
                                              .map(EntityType::getLabel).orElse("null");

        return Arrays.stream(placesSettings)
                     .anyMatch(v -> v.equals(String.join("::",
                                                         leftTypeLabel,
                                                         rightTypeLabel,
                                                         relationshipType.getLeftwardType(),
                                                         relationshipType.getRightwardType())));
    }
}
