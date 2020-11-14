package com.lumiomedical.record.store;

import com.lumiomedical.record.source.Source;
import com.lumiomedical.record.source.Sourceable;

import java.util.Collection;
import java.util.Map;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/04/10
 */
public interface SourceableStore<T extends Sourceable<T>>
{
    /**
     *
     * @param source
     * @param ids
     * @param <S>
     * @return
     */
    <S> Map<S, String> mapUidBySourceIds(Source<S, T> source, Collection<S> ids);
}
