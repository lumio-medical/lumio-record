package com.lumiomedical.record.source;

import java.util.Set;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/29
 */
public final class SourceCollection<T, OT> extends AbstractSource<Set<T>, OT>
{
    private final Class<T> itemType;

    /**
     *
     * @param name
     * @param itemType
     * @param <T>
     * @param <OT>
     * @return
     */
    public static <T, OT> SourceCollection<T, OT> of(String name, Class<T> itemType, Class<OT> objectType)
    {
        return new SourceCollection<>(name, itemType, objectType);
    }

    /**
     *
     * @param name
     * @param itemType
     * @param objectType
     */
    private SourceCollection(String name, Class<T> itemType, Class<OT> objectType)
    {
        super(name, Set.class, objectType);
        this.itemType = itemType;
    }

    public Class<T> itemType()
    {
        return this.itemType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Set<T>> type()
    {
        return (Class<Set<T>>) super.type();
    }
}
