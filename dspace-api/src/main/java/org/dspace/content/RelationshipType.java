/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.dspace.core.Context;
import org.dspace.core.ReloadableEntity;

/**
 * Class representing a RelationshipType
 * This class contains an Integer ID that will be the unique value and primary key in the database.
 * This key is automatically generated
 * It also has a leftType and rightType EntityType that describes the relationshipType together with a leftwardType and
 * rightwardType.
 * The cardinality properties describe how many of each relations this relationshipType can support
 */
@Entity
@Table(name = "relationship_type")
public class RelationshipType implements ReloadableEntity<Integer> {

    /**
     * The Integer ID used as a primary key for this database object.
     * This is generated by a sequence
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relationship_type_id_seq")
    @SequenceGenerator(name = "relationship_type_id_seq", sequenceName = "relationship_type_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, insertable = true, updatable = false)
    protected Integer id;

    /**
     * The leftType EntityType field for the relationshipType
     * This is stored as an ID and cannot be null
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "left_type", nullable = true)
    private EntityType leftType;

    /**
     * The rightType EntityType field for the relationshipType
     * This is stored as an ID and cannot be null
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "right_type", nullable = true)
    private EntityType rightType;

    /**
     * The leftwardType String field for the relationshipType
     * This is stored as a String and cannot be null
     * This is a textual representation of the name of the relationship that this RelationshipType is connected to
     */
    @Column(name = "leftward_type", nullable = false)
    private String leftwardType;

    /**
     * The rightwardType String field for the relationshipType
     * This is stored as a String and cannot be null
     * This is a textual representation of the name of the relationship that this RelationshipType is connected to
     */
    @Column(name = "rightward_type", nullable = false)
    private String rightwardType;

    /**
     * The minimum amount of relations for the leftItem that need to be present at all times
     * This is stored as an Integer
     */
    @Column(name = "left_min_cardinality")
    private Integer leftMinCardinality;

    /**
     * The maximum amount of relations for the leftItem that can to be present at all times
     * This is stored as an Integer
     */
    @Column(name = "left_max_cardinality")
    private Integer leftMaxCardinality;

    /**
     * The minimum amount of relations for the rightItem that need to be present at all times
     */
    @Column(name = "right_min_cardinality")
    private Integer rightMinCardinality;

    /**
     * Tha maximum amount of relations for the rightItem that can be present at all times
     */
    @Column(name = "right_max_cardinality")
    private Integer rightMaxCardinality;

    /**
     * The boolean indicating whether the metadata should be copied on left item or not
     */
    @Column(name = "copy_to_left", nullable = false)
    private boolean copyToLeft;

    /**
     * The boolean indicating whether the metadata should be copied on right item or not
     */
    @Column(name = "copy_to_right", nullable = false)
    private boolean copyToRight;

    /**
     * The value indicating whether relationships of this type should be ignored on the right/left/neither.
     */
    @Column(name = "tilted")
    private Tilted tilted;

    /**
     * Protected constructor, create object using:
     * {@link org.dspace.content.service.RelationshipTypeService#create(Context)} }
     */
    protected RelationshipType() {}

    /**
     * Standard getter for the ID of this RelationshipType
     * @param id    The ID that this RelationshipType should receive
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Standard getter for The leftType EntityType for this RelationshipType
     * @return  The leftType EntityType of this RelationshipType
     */
    public EntityType getLeftType() {
        return leftType;
    }

    /**
     * Standard setter for the leftType EntityType for this RelationshipType
     * @param leftType  The leftType EntityType that this RelationshipType should receive
     */
    public void setLeftType(EntityType leftType) {
        this.leftType = leftType;
    }

    /**
     * Standard getter for The rightType EntityType for this RelationshipType
     * @return  The rightType EntityType of this RelationshipType
     */
    public EntityType getRightType() {
        return rightType;
    }

    /**
     * Standard setter for the rightType EntityType for this RelationshipType
     * @param rightType  The rightType EntityType that this RelationshipType should receive
     */
    public void setRightType(EntityType rightType) {
        this.rightType = rightType;
    }

    /**
     * Standard getter for the leftwardType String for this RelationshipType
     * @return  The leftwardType String of this RelationshipType
     */
    public String getLeftwardType() {
        return leftwardType;
    }

    /**
     * Standard setter for the leftwardType String for this RelationshipType
     * @param leftwardType The leftwardType String that this RelationshipType should receive
     */
    public void setLeftwardType(String leftwardType) {
        this.leftwardType = leftwardType;
    }

    /**
     * Standard getter for the rightwardType String for this RelationshipType
     * @return  The rightwardType String of this RelationshipType
     */
    public String getRightwardType() {
        return rightwardType;
    }

    /**
     * Standard setter for the rightwardType String for this RelationshipType
     * @param rightwardType The rightwardType String that this RelationshipType should receive
     */
    public void setRightwardType(String rightwardType) {
        this.rightwardType = rightwardType;
    }

    /**
     * Standard getter for the leftMinCardinality Integer for this RelationshipType
     * @return  the leftMinCardinality Integer of this RelationshipType
     */
    public Integer getLeftMinCardinality() {
        return leftMinCardinality;
    }

    /**
     * Standard setter for the leftMinCardinality Integer for this RelationshipType
     * @param leftMinCardinality    The leftMinCardinality Integer that this RelationshipType should receive
     */
    public void setLeftMinCardinality(Integer leftMinCardinality) {
        this.leftMinCardinality = leftMinCardinality;
    }

    /**
     * Standard getter for the leftMaxCardinality Integer for this RelationshipType
     * @return  the leftMaxCardinality Integer of this RelationshipType
     */
    public Integer getLeftMaxCardinality() {
        return leftMaxCardinality;
    }

    /**
     * Standard setter for the leftMaxCardinality Integer for this RelationshipType
     * @param leftMaxCardinality    The leftMaxCardinality Integer that this RelationshipType should receive
     */
    public void setLeftMaxCardinality(Integer leftMaxCardinality) {
        this.leftMaxCardinality = leftMaxCardinality;
    }

    /**
     * Standard getter for the rightMinCardinality Integer for this RelationshipType
     * @return  the rightMinCardinality Integer of this RelationshipType
     */
    public Integer getRightMinCardinality() {
        return rightMinCardinality;
    }

    /**
     * Standard setter for the rightMinCardinality Integer for this RelationshipType
     * @param rightMinCardinality    The rightMinCardinality Integer that this RelationshipType should receive
     */
    public void setRightMinCardinality(Integer rightMinCardinality) {
        this.rightMinCardinality = rightMinCardinality;
    }

    /**
     * Standard getter for the rightMaxCardinality Integer for this RelationshipType
     * @return  the rightMaxCardinality Integer of this RelationshipType
     */
    public Integer getRightMaxCardinality() {
        return rightMaxCardinality;
    }

    /**
     * Standard setter for the rightMaxCardinality Integer for this RelationshipType
     * @param rightMaxCardinality    The rightMaxCardinality Integer that this RelationshipType should receive
     */
    public void setRightMaxCardinality(Integer rightMaxCardinality) {
        this.rightMaxCardinality = rightMaxCardinality;
    }

    /**
     * Generic getter for the copyToLeft
     * @return the copyToLeft value of this RelationshipType
     */
    public boolean isCopyToLeft() {
        return copyToLeft;
    }

    /**
     * Generic setter for the copyToLeft
     * @param copyToLeft   The copyToLeft to be set on this RelationshipType
     */
    public void setCopyToLeft(boolean copyToLeft) {
        this.copyToLeft = copyToLeft;
    }

    /**
     * Generic getter for the copyToRight
     * @return the copyToRight value of this RelationshipType
     */
    public boolean isCopyToRight() {
        return copyToRight;
    }

    /**
     * Generic setter for the copyToRight
     * @param copyToRight   The copyToRight to be set on this RelationshipType
     */
    public void setCopyToRight(boolean copyToRight) {
        this.copyToRight = copyToRight;
    }

    /**
     * Generic getter for tilted
     * @return the tilted value of this RelationshipType
     */
    public Tilted getTilted() {
        return tilted;
    }

    /**
     * Generic setter for tilted
     * @param tilted   The tilted to be set on this RelationshipType
     */
    public void setTilted(Tilted tilted) {
        this.tilted = tilted;
    }

    public enum Tilted {
        NONE, LEFT, RIGHT;
    }

    /**
     * Standard getter for the ID of this RelationshipType
     * @return  The ID of this RelationshipType
     */
    @Override
    public Integer getID() {
        return id;
    }
}
