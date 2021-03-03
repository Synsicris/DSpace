/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.submit.consumer.service;

import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

/**
*
* @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
*/
public interface ProjectConsumerService {

    public void processItem(Context context, EPerson currentUser, Item item);
}
