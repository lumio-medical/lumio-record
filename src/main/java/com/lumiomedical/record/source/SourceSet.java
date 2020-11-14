package com.lumiomedical.record.source;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/07
 */
public interface SourceSet<OT>
{
    Class<OT> getSourcedType();

    /**
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    default <T> Source<T, OT> sourceOf(String name, Class<T> type)
    {
        return Source.of(name, type, this.getSourcedType());
    }

    /**
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    default <T> SourceCollection<T, OT> sourceCollectionOf(String name, Class<T> type)
    {
        return SourceCollection.of(name, type, this.getSourcedType());
    }
}
