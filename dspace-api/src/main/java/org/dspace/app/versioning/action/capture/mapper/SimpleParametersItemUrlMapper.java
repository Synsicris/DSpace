/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.versioning.action.capture.mapper;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dspace.content.Item;
import org.dspace.core.Context;

public class SimpleParametersItemUrlMapper extends ItemUrlMapper {

    protected final Map<String, String> parameters;
    protected final ItemUrlMapper itemUrlMapper;

    public SimpleParametersItemUrlMapper(
        Map<String, String> parameters, ItemUrlMapper itemUrlMapper
    ) {
        super(null);
        this.parameters = parameters;
        this.itemUrlMapper = itemUrlMapper;
    }

    @Override
    public String mapToUrl(Context context, Item item) {
        String url = itemUrlMapper.mapToUrl(context, item);
        try {
            return new URIBuilder(url)
                .addParameters(
                    parameters
                        .entrySet()
                        .stream()
                        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
                )
                .build()
                .toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

}
