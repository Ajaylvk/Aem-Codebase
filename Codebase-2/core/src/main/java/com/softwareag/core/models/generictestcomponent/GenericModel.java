package com.softwareag.core.models.generictestcomponent;

import com.google.common.collect.Iterators;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Generic model that provides access to its properties and children
 * via {@link Map#get(Object)} within a single namespace.
 * <br>
 * This allows access using a javascript-like notation. Also children can be got by calling children
 * <ul>
 *     <li>model.title</li>
 *     <li>model.child1.title</li>
 *     <li>model.child1.grandchild1.title</li>
 *     <li>model.child2.title</li>
 *     <li>model.child2.children</li>
 * </ul>
 * Alternatively, property values may be accessed using
 */
@Model(adaptables = Resource.class)
public class GenericModel implements Resource, Map<String, Object> {

    private static final String NOT_IMPLEMENTED = "not implemented";

    private Resource resource;

    private ValueMap valueMap;

    public GenericModel(final Resource resource) {
        this.resource = resource;
        this.valueMap = resource.getValueMap();
    }

    private static GenericModel wrap(final Resource resource) {
        if (resource != null) {
            return new GenericModel(resource);
        }
        return null;
    }

    @Override
    public String getName() {
        return resource.getName();
    }

    @Override
    public Resource getParent() {
        return wrap(resource.getParent());
    }

    @Override
    public String getPath() {
        return resource.getPath();
    }

    @Override
    public ResourceMetadata getResourceMetadata() {
        return resource.getResourceMetadata();
    }

    @Override
    public ResourceResolver getResourceResolver() {
        // TODO: this escapes the wrapping with GenericModel
        return resource.getResourceResolver();
    }

    @Override
    public String getResourceSuperType() {
        return resource.getResourceSuperType();
    }

    @Override
    public String getResourceType() {
        return resource.getResourceType();
    }

    @Override
    public boolean isResourceType(final String resourceType) {
        return resource.isResourceType(resourceType);
    }

    @Override
    public ValueMap getValueMap() {
        return resource.getValueMap();
    }

    @Override
    public boolean hasChildren() {
        return resource.hasChildren();
    }

    @Override
    public GenericModel getChild(final String relPath) {
        return wrap(resource.getChild(relPath));
    }

    @Override
    public Iterator<Resource> listChildren() {
        return Iterators.filter(
            Iterators.transform(resource.listChildren(), GenericModel::wrap),
            Objects::nonNull
        );
    }

    @Override
    public Iterable<Resource> getChildren() {
        return this::listChildren;
    }

    @Override
    public Object get(final Object key) {
        Object object = valueMap.get(key);
        if (object != null) {
            return object;
        }
        return getChild((String) key);
    }

    @Override
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        return resource.adaptTo(type);
    }

    @Override
    public int size() {
        Iterator<Resource> children = resource.listChildren();
        int sizeChildren = 0;
        while (children.hasNext()) {
            children.next();
            sizeChildren++;
        }
        return sizeChildren;
    }

    @Override
    public boolean isEmpty() {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean containsKey(Object key) {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean containsValue(Object value) {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object put(String key, Object value) {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object remove(Object key) {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void clear() {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Set<String> keySet() {
        // needed for sightly call
        return Collections.emptySet();
    }

    @Override
    public Collection<Object> values() {

        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    public List<GenericModel> getChildrenAsList() {
        List<GenericModel> list = new ArrayList<>();
        Iterator<Resource> children = this.resource.listChildren();
        while (children.hasNext()) {
            Resource next = children.next();
            GenericModel nextGenericModel = next.adaptTo(GenericModel.class);
            list.add(nextGenericModel);
        }
        return list;
    }
}
