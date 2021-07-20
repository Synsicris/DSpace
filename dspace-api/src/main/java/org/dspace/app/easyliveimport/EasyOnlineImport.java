/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.easyliveimport;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class EasyOnlineImport implements Serializable {

    private static final long serialVersionUID = 3256085881203495459L;

    private List<UUID> created;

    private List<UUID> modified;

    private String type;

    public List<UUID> getCreated() {
        if (Objects.isNull(this.created)) {
            return Collections.emptyList();
        }
        return this.created;
    }

    public void setCreated(List<UUID> created) {
        this.created = created;
    }

    public List<UUID> getModified() {
        if (Objects.isNull(this.modified)) {
            return Collections.emptyList();
        }
        return this.modified;
    }

    public void setModified(List<UUID> modified) {
        this.modified = modified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}