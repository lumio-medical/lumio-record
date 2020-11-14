package com.lumiomedical.record.store;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.Referential;
import com.lumiomedical.record.source.Source;
import com.noleme.store.Store;
import com.noleme.store.query.Filter;
import com.noleme.store.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/27
 */
public interface RecordStore<R extends Record<R>> extends Store<R>
{
    /**
     *
     * @param uid
     * @return
     */
    R find(String uid);

    /**
     *
     * @param uid
     * @param referential
     * @return
     */
    R find(String uid, Referential referential);

    /**
     *
     * @param query
     * @return
     */
    R find(Query query);

    /**
     *
     * @param query
     * @param referential
     * @return
     */
    R find(Query query, Referential referential);

    /**
     *
     * @param query
     * @param filter
     * @return
     */
    R find(Query query, Filter filter);

    /**
     *
     * @param query
     * @param filter
     * @param referential
     * @return
     */
    R find(Query query, Filter filter, Referential referential);

    /**
     * Counts all entities.
     *
     * @return The number of entities in the store.
     */
    long count();

    /**
     *
     * @param referential
     * @return
     */
    long count(Referential referential);

    /**
     * Counts all entities matching the custom query description.
     *
     * @param query The query description.
     * @return The number of entities matching the provided query in the store.
     */
    long count(Query query);

    /**
     *
     * @param query
     * @param referential
     * @return
     */
    long count(Query query, Referential referential);

    /**
     * Finds a collection of entities using their UIDs.
     *
     * @param uids The entities UIDs.
     * @return A collection containing all entities matching the provided UIDs.
     */
    List<R> list(Collection<String> uids);

    /**
     * Finds a collection of entities using their UIDs.
     *
     * @param uids The entities UIDs.
     * @param referential
     * @return A collection containing all entities matching the provided UIDs.
     */
    List<R> list(Collection<String> uids, Referential referential);

    /**
     * Finds a collection of entities using their UIDs and a query modifier.
     *
     * @param uids The entities UIDs.
     * @param filter A Filter query modifier.
     * @return A collection containing all entities matching the provided UIDs.
     */
    List<R> list(Collection<String> uids, Filter filter);

    /**
     * Finds a collection of entities using their UIDs and a query modifier.
     *
     * @param uids The entities UIDs.
     * @param filter A Filter query modifier.
     * @param referential
     * @return A collection containing all entities matching the provided UIDs.
     */
    List<R> list(Collection<String> uids, Filter filter, Referential referential);

    /**
     * Finds a collection of entities using a custom query description.
     *
     * @param query The query description.
     * @return A collection containing all entities matching the provided query.
     */
    List<R> list(Query query);

    /**
     * Finds a collection of entities using a custom query description.
     *
     * @param query The query description.
     * @param referential
     * @return A collection containing all entities matching the provided query.
     */
    List<R> list(Query query, Referential referential);

    /**
     * Finds a collection of entities using a custom query description and a query modifier.
     *
     * @param query The query description.
     * @param filter A Filter query modifier.
     * @return A collection containing all entities matching the provided query.
     */
    List<R> list(Query query, Filter filter);

    /**
     * Finds a collection of entities using a custom query description and a query modifier.
     *
     * @param query The query description.
     * @param filter A Filter query modifier.
     * @param referential
     * @return A collection containing all entities matching the provided query.
     */
    List<R> list(Query query, Filter filter, Referential referential);

    /**
     * Finds a collection of entities using their UIDs.
     *
     * @param uids The entities UIDs.
     * @return A collection containing all entities matching the provided UIDs.
     */
    Map<String, R> map(Collection<String> uids);

    /**
     * Finds a collection of entities using their UIDs.
     *
     * @param uids The entities UIDs.
     * @param referential
     * @return A collection containing all entities matching the provided UIDs.
     */
    Map<String, R> map(Collection<String> uids, Referential referential);

    /**
     * Finds a collection of entities using their UIDs and a query modifier.
     *
     * @param uids The entities UIDs.
     * @param filter A Filter query modifier.
     * @return A collection containing all entities matching the provided UIDs.
     */
    Map<String, R> map(Collection<String> uids, Filter filter);

    /**
     * Finds a collection of entities using their UIDs and a query modifier.
     *
     * @param uids The entities UIDs.
     * @param filter A Filter query modifier.
     * @param referential
     * @return A collection containing all entities matching the provided UIDs.
     */
    Map<String, R> map(Collection<String> uids, Filter filter, Referential referential);

    /**
     * Finds a collection of entities using a custom query description.
     *
     * @param query The query description.
     * @return A collection containing all entities matching the provided query.
     */
    Map<String, R> map(Query query);

    /**
     * Finds a collection of entities using a custom query description.
     *
     * @param query The query description.
     * @param referential
     * @return A collection containing all entities matching the provided query.
     */
    Map<String, R> map(Query query, Referential referential);

    /**
     * Finds a collection of entities using a custom query description and a query modifier.
     *
     * @param query The query description.
     * @param filter A Filter query modifier.
     * @return A collection containing all entities matching the provided query.
     */
    Map<String, R> map(Query query, Filter filter);

    /**
     * Finds a collection of entities using a custom query description and a query modifier.
     *
     * @param query The query description.
     * @param filter A Filter query modifier.
     * @param referential
     * @return A collection containing all entities matching the provided query.
     */
    Map<String, R> map(Query query, Filter filter, Referential referential);

    /**
     *
     * @param source
     * @param ids
     * @param <S>
     * @return
     */
    <S> Map<S, String> mapUidBySourceIds(Source<S, R> source, Collection<S> ids);

    /**
     *
     * @param source
     * @param ids
     * @param referential
     * @param <S>
     * @return
     */
    <S> Map<S, String> mapUidBySourceIds(Source<S, R> source, Collection<S> ids, Referential referential);

    /**
     *
     * @param item
     */
    void put(R item);

    /**
     *
     * @param item
     * @param referential
     */
    void put(R item, Referential referential);

    /**
     *
     * @param items
     */
    void put(Collection<R> items);

    /**
     *
     * @param items
     * @param referential
     */
    void put(Collection<R> items, Referential referential);

    /**
     *
     * @param items
     */
    void putAt(Collection<Referentialized<R>> items);
}
