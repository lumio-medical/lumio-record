package com.lumiomedical.record.store.mongodb;

import com.lumiomedical.record.source.Source;
import com.lumiomedical.record.source.Sourceable;
import com.lumiomedical.record.store.SourceableStore;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.noleme.mongodb.MongoDBClient;
import com.noleme.store.factory.Builder;
import com.noleme.store.factory.Factory;
import com.noleme.store.mongodb.DefaultMongoDBStore;
import com.noleme.store.query.Identifiable;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/04/10
 */
public abstract class MongoDBSourceableStore<T extends Identifiable<String> & Sourceable<T>> extends DefaultMongoDBStore<T> implements SourceableStore<T>
{
    /**
     *
     * @param client
     * @param factory
     */
    public MongoDBSourceableStore(MongoDBClient client, Factory<BasicDBObject, T> factory)
    {
        super(client, factory);
    }

    @Override
    public <S> Map<S, String> mapUidBySourceIds(Source<S, T> source, Collection<S> ids)
    {
        return this.mapBySourceIds(source, ids, doc -> doc.getList("uid", String.class).get(0));
    }

    /**
     *
     * @param source
     * @param ids
     * @param builder
     * @param <S>
     * @param <C>
     * @return
     */
    protected <S, C> Map<S, C> mapBySourceIds(Source<S, T> source, Collection<S> ids, Builder<Document, C> builder)
    {
        var aggregationResult = this.mongoCollection.aggregate(
            List.of(
                Aggregates.match(Filters.in(source.queryName(), ids)),
                Aggregates.group("$" + source.queryName(), Accumulators.addToSet("uid", "$uid"))
            )
        );

        Map<S, C> map = new HashMap<>();

        aggregationResult.forEach((Consumer<? super Document>) doc -> {
            var sourceId = doc.get("_id", source.type());
            var value = builder.build(doc);

            map.put(sourceId, value);
        });

        return map;
    }
}
