/*******************************************************************************
 * Copyright (c) 2013-2018 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Daniel Persson (Husqvarna Group) - Attribute support
 *******************************************************************************/
package org.eclipse.leshan.core.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link LwM2mAttribute} instances that are handled as a collection that must adhere to rules that are
 * specified in LwM2m, e.g. that the 'pmin' attribute must be less than the 'pmax' attribute, if they're both part of
 * the same AttributeSet.
 */
public class LwM2mAttributeSet {

    private final Map<String, LwM2mAttribute> attributeMap = new LinkedHashMap<>();

    public LwM2mAttributeSet(LwM2mAttribute... attributes) {
        this(Arrays.asList(attributes));
    }

    public LwM2mAttributeSet(Collection<LwM2mAttribute> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            for (LwM2mAttribute attr : attributes) {
                // Check for duplicates
                if (attributeMap.containsKey(attr.getCoRELinkParam())) {
                    throw new IllegalArgumentException(String.format(
                            "Cannot create attribute set with duplicates (attr: '%s')", attr.getCoRELinkParam()));
                }
                attributeMap.put(attr.getCoRELinkParam(), attr);
            }
        }
    }

    public void validate(AssignationLevel assignationLevel) {
        // Can all attributes be assigned to this level?
        for (LwM2mAttribute attr : attributeMap.values()) {
            if (!attr.canBeAssignedTo(assignationLevel)) {
                throw new IllegalArgumentException(String.format("Attribute '%s' cannot be assigned to level %s",
                        attr.getCoRELinkParam(), assignationLevel.name()));
            }
        }
        LwM2mAttribute pmin = attributeMap.get(LwM2mAttributeModel.MINIMUM_PERIOD);
        LwM2mAttribute pmax = attributeMap.get(LwM2mAttributeModel.MAXIMUM_PERIOD);
        if ((pmin != null) && (pmax != null) && (Long) pmin.getValue() > (Long) pmax.getValue()) {
            throw new IllegalArgumentException(String.format("Cannot write attributes where '%s' > '%s'",
                    pmin.getCoRELinkParam(), pmax.getCoRELinkParam()));
        }

        LwM2mAttribute epmin = attributeMap.get(LwM2mAttributeModel.EVALUATE_MINIMUM_PERIOD);
        LwM2mAttribute epmax = attributeMap.get(LwM2mAttributeModel.EVALUATE_MAXIMUM_PERIOD);
        if ((epmin != null) && (epmax != null) && (Long) epmin.getValue() > (Long) epmax.getValue()) {
            throw new IllegalArgumentException(String.format("Cannot write attributes where '%s' > '%s'",
                    epmin.getCoRELinkParam(), epmax.getCoRELinkParam()));
        }
    }

    /**
     * Returns a new AttributeSet, containing only the attributes that have a matching Attachment level.
     * 
     * @param attachment the Attachment level to filter by
     * @return a new {@link LwM2mAttributeSet} containing the filtered attributes
     */
    public LwM2mAttributeSet filter(Attachment attachment) {
        List<LwM2mAttribute> attrs = new ArrayList<>();
        for (LwM2mAttribute attr : getAttributes()) {
            if (attr.getAttachment() == attachment) {
                attrs.add(attr);
            }
        }
        return new LwM2mAttributeSet(attrs);
    }

    /**
     * Creates a new AttributeSet by merging another AttributeSet onto this instance.
     * 
     * @param attributes the AttributeSet that should be merged onto this instance. Attributes in this set will
     *        overwrite existing attribute values, if present. If this is null, the new attribute set will effectively
     *        be a clone of the existing one
     * @return the merged AttributeSet
     */
    public LwM2mAttributeSet merge(LwM2mAttributeSet attributes) {
        Map<String, LwM2mAttribute> merged = new LinkedHashMap<>();
        for (LwM2mAttribute attr : getAttributes()) {
            merged.put(attr.getCoRELinkParam(), attr);
        }
        if (attributes != null) {
            for (LwM2mAttribute attr : attributes.getAttributes()) {
                merged.put(attr.getCoRELinkParam(), attr);
            }
        }
        return new LwM2mAttributeSet(merged.values());
    }

    /**
     * Returns the attributes as a map with the CoRELinkParam as key and the attribute value as map value.
     * 
     * @return the attributes map
     */
    public Map<String, Object> getMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (LwM2mAttribute attr : attributeMap.values()) {
            result.put(attr.getCoRELinkParam(), attr.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    public Collection<LwM2mAttribute> getAttributes() {
        return attributeMap.values();
    }

    public String[] toQueryParams() {
        List<String> queries = new LinkedList<>();
        for (LwM2mAttribute attr : attributeMap.values()) {
            if (attr.getValue() != null) {
                queries.add(String.format("%s=%s", attr.getCoRELinkParam(), attr.getValue()));
            } else {
                queries.add(attr.getCoRELinkParam());
            }
        }
        return queries.toArray(new String[queries.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String[] queryParams = toQueryParams();
        for (int a = 0; a < queryParams.length; a++) {
            sb.append(a < queryParams.length - 1 ? queryParams[a] + "&" : queryParams[a]);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeMap == null) ? 0 : attributeMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LwM2mAttributeSet other = (LwM2mAttributeSet) obj;
        if (attributeMap == null) {
            if (other.attributeMap != null)
                return false;
        } else if (!attributeMap.equals(other.attributeMap))
            return false;
        return true;
    }

    /**
     * Create an AttributeSet from a uri queries string.
     * 
     * @param uriQueries the URI queries to parse. e.g. {@literal pmin=10&pmax=60}
     */
    public static LwM2mAttributeSet parse(String uriQueries) {
        if (uriQueries == null)
            return null;

        String[] queriesArray = uriQueries.split("&");
        return LwM2mAttributeSet.parse(queriesArray);
    }

    /**
     * Create an AttributeSet from an array of string. Each elements is an attribute with its value.
     * 
     * <pre>
     * queryParams[0] = "pmin=10";
     * queryParams[1] = "pmax=10";
     * </pre>
     */
    public static LwM2mAttributeSet parse(String... queryParams) {
        return LwM2mAttributeSet.parse(Arrays.asList(queryParams));
    }

    /**
     * Create an AttributeSet from a collection of string. Each elements is an attribute with its value.
     * 
     * <pre>
     * queryParams.get(0) = "pmin=10";
     * queryParams.get(1) = "pmax=10";
     * </pre>
     */
    public static LwM2mAttributeSet parse(Collection<String> queryParams) {
        ArrayList<LwM2mAttribute> attributes = new ArrayList<>();

        for (String param : queryParams) {
            String[] keyAndValue = param.split("=");
            if (keyAndValue.length == 1) {
                attributes.add(new LwM2mAttribute(keyAndValue[0]));
            } else if (keyAndValue.length == 2) {
                attributes.add(new LwM2mAttribute(keyAndValue[0], keyAndValue[1]));
            } else {
                throw new IllegalArgumentException(String.format("Cannot parse query param '%s'", param));
            }

        }
        return new LwM2mAttributeSet(attributes);
    }
}