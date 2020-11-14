package com.lumiomedical.record.source;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/06
 */
public interface Sourceable<T>
{
    /**
     *
     * @param source
     * @param <S>
     * @return
     */
    <S> boolean hasSource(AbstractSource<S, T> source);

    /**
     *
     * @param source
     * @param <S>
     * @return
     */
    <S> S getSourceId(AbstractSource<S, T> source);

    /**
     *
     * @param source
     * @param value
     * @param <S>
     * @return
     */
    <S> T setSourceId(AbstractSource<S, T> source, S value);

    /**
     *
     * @param source
     * @param value
     * @param <S>
     * @return
     */
    <S> T addSourceId(SourceCollection<S, T> source, S value);
}
