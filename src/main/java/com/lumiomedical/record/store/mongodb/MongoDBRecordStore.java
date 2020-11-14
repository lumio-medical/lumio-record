package com.lumiomedical.record.store.mongodb;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.Referential;
import com.lumiomedical.record.UidHolder;
import com.lumiomedical.record.logging.Logging;
import com.lumiomedical.record.source.Source;
import com.lumiomedical.record.store.RecordStore;
import com.lumiomedical.record.store.Referentialized;
import com.lumiomedical.record.store.mongodb.factory.RecordFactory;
import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.noleme.mongodb.MongoDBClient;
import com.noleme.store.factory.Builder;
import com.noleme.store.factory.Factory;
import com.noleme.store.factory.Transcriber;
import com.noleme.store.mongodb.DefaultMongoDBStore;
import com.noleme.store.query.Filter;
import com.noleme.store.query.Query;
import net.openhft.hashing.LongHashFunction;
import org.bson.BSONObject;
import org.bson.BsonBinaryWriter;
import org.bson.Document;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.OutputBuffer;
import org.bson.types.BasicBSONList;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static com.lumiomedical.record.store.mongodb.DBObjects.dbList;
import static com.lumiomedical.record.store.mongodb.DBObjects.dbObject;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/27
 */
public abstract class MongoDBRecordStore<R extends Record<R>> extends DefaultMongoDBStore<R> implements RecordStore<R>
{
    private final RecordFactory<R> recordFactory;
    private final LongHashFunction hashFunction;
    private boolean enabledTransactions;

    /**
     *
     * @param client
     * @param factory
     */
    public MongoDBRecordStore(MongoDBClient client, Factory<BasicDBObject, R> factory)
    {
        super(client, factory);
        this.recordFactory = new RecordFactory<>();
        this.hashFunction = LongHashFunction.city_1_1();
        this.setEnabledTransactions(true);
    }

    @Override
    public void createIndexes()
    {
        this.collection.createIndex("uid");
        this.collection.createIndex("hash");
        this.collection.createIndex("validity_start");
        this.collection.createIndex("validity_end");
        this.collection.createIndex(
            dbObject()
                .append("validity_start", 1)
                .append("validity_end", 1)
        );
        this.collection.createIndex(
            dbObject()
                .append("uid", 1)
                .append("validity_start", 1),
            null,
            true
        );
        this.collection.createIndex(
            dbObject()
                .append("uid", 1)
                .append("hash", 1)
                .append("validity_start", 1),
            null,
            true
        );
    }

    /**
     *
     * @param obj
     * @return
     */
    protected R buildRecord(BasicDBObject obj)
    {
        return this.recordFactory.enrichBuild(obj, this.factory.build(obj));
    }

    @Override
    public R find(String uid)
    {
        return this.find(uid, Referential.now());
    }

    @Override
    public R find(String uid, Referential referential)
    {
        return this.find(new Query("uid", uid), referential);
    }

    @Override
    public R find(Query query)
    {
        return this.find(query, Referential.now());
    }

    @Override
    public R find(Query query, Referential referential)
    {
        return this.find(query, new Filter(), referential);
    }

    @Override
    public R find(Query query, Filter filter)
    {
        return this.find(query, filter, Referential.now());
    }

    @Override
    public R find(Query query, Filter filter, Referential referential)
    {
        BasicDBObject dbObject = (BasicDBObject) this.buildQuery(query);

        dbObject = this.applyReferentialCriteria(dbObject, referential);

        return this.findByDbObject(dbObject, filter);
    }

    @Override
    public long count()
    {
        return this.count(Referential.now());
    }

    @Override
    public long count(Referential referential)
    {
        return this.count(new Query(), referential);
    }

    @Override
    public long count(Query query)
    {
        return this.count(query, Referential.now());
    }

    @Override
    public long count(Query query, Referential referential)
    {
        BasicDBObject dbObject = (BasicDBObject) this.buildQuery(query);

        dbObject = this.applyReferentialCriteria(dbObject, referential);

        return this.collection.count(dbObject);
    }

    @Override
    public List<R> list(Collection<String> uids)
    {
        return this.list(uids, Referential.now());
    }

    @Override
    public List<R> list(Collection<String> uids, Referential referential)
    {
        return this.list(uids, new Filter(), referential);
    }

    @Override
    public List<R> list(Collection<String> uids, Filter filter)
    {
        return this.list(uids, filter, Referential.now());
    }

    @Override
    public List<R> list(Collection<String> uids, Filter filter, Referential referential)
    {
        return this.list(new Query().in("uid", uids), filter, referential);
    }

    @Override
    public List<R> list(Query query)
    {
        return this.list(query, Referential.now());
    }

    @Override
    public List<R> list(Query query, Referential referential)
    {
        return this.list(query, new Filter(), referential);
    }

    @Override
    public List<R> list(Query query, Filter filter)
    {
        return this.list(query, filter, Referential.now());
    }

    @Override
    public List<R> list(Query query, Filter filter, Referential referential)
    {
        BasicDBObject dbObject = (BasicDBObject) this.buildQuery(query);

        dbObject = this.applyReferentialCriteria(dbObject, referential);

        return this.listByDbObject(dbObject, filter);
    }

    @Override
    public Map<String, R> map(Collection<String> uids)
    {
        return this.map(uids, Referential.now());
    }

    @Override
    public Map<String, R> map(Collection<String> uids, Referential referential)
    {
        return this.map(uids, new Filter(), referential);
    }

    @Override
    public Map<String, R> map(Collection<String> uids, Filter filter)
    {
        return this.map(uids, filter, Referential.now());
    }

    @Override
    public Map<String, R> map(Collection<String> uids, Filter filter, Referential referential)
    {
        return this.map(new Query().in("uids", uids), filter, referential);
    }

    @Override
    public Map<String, R> map(Query query)
    {
        return this.map(query, Referential.now());
    }

    @Override
    public Map<String, R> map(Query query, Referential referential)
    {
        return this.map(query, new Filter(), referential);
    }

    @Override
    public Map<String, R> map(Query query, Filter filter)
    {
        return this.map(query, filter, Referential.now());
    }

    @Override
    public Map<String, R> map(Query query, Filter filter, Referential referential)
    {
        BasicDBObject dbObject = (BasicDBObject) this.buildQuery(query);

        dbObject = this.applyReferentialCriteria(dbObject, referential);

        return this.mapByDbObject(dbObject, filter);
    }

    @Override
    public void put(R item)
    {
        this.put(item, Referential.now(), this.factory);
    }

    @Override
    public void put(R item, Referential referential)
    {
        this.put(item, referential, this.factory);
    }

    @Override
    public void put(R item, Transcriber<BasicDBObject, R> transcriber)
    {
        this.put(item, Referential.now(), transcriber);
    }

    /**
     *
     * @param item
     * @param referential
     * @param transcriber
     */
    public void put(R item, Referential referential, Transcriber<BasicDBObject, R> transcriber)
    {
        this.executeTransaction(() -> {
            BulkWriteOperation op = this.collection.initializeOrderedBulkOperation();
            this.addPutOperation(op, referential, item, transcriber);
            op.execute();
        }, "Record insertion successful", "Record insertion aborted");
    }

    @Override
    public void put(Collection<R> items)
    {
        this.put(items, Referential.now(), this.factory);
    }

    @Override
    public void put(Collection<R> items, Referential referential)
    {
        this.put(items, referential, this.factory);
    }

    @Override
    public void put(Collection<R> items, Transcriber<BasicDBObject, R> transcriber)
    {
        this.put(items, Referential.now(), transcriber);
    }

    /**
     *
     * @param items
     * @param referential
     * @param transcriber
     */
    public void put(Collection<R> items, Referential referential, Transcriber<BasicDBObject, R> transcriber)
    {
        if (items.isEmpty())
            return;
        this.executeTransaction(() -> {
            BulkWriteOperation op = this.collection.initializeOrderedBulkOperation();
            for (R item : items)
                this.addPutOperation(op, referential, item, transcriber);
            op.execute();
        }, "Record insertion successful", "Record insertion aborted");
    }

    /**
     *
     * @param referentializedItems
     */
    public void putAt(Collection<Referentialized<R>> referentializedItems)
    {
        this.putReferentialized(referentializedItems, this.factory);
    }

    /**
     *
     * @param referentializedItems
     * @param transcriber
     */
    public void putReferentialized(Collection<Referentialized<R>> referentializedItems, Transcriber<BasicDBObject, R> transcriber)
    {
        if (referentializedItems.isEmpty())
            return;
        this.executeTransaction(() -> {
            BulkWriteOperation op = this.collection.initializeOrderedBulkOperation();
            for (Referentialized<R> referentializedItem : referentializedItems)
                this.addPutOperation(op, referentializedItem.referential, referentializedItem.record, transcriber);
            op.execute();
        }, "Record insertion successful", "Record insertion aborted");
    }

    /**
     *
     * @param operation
     * @param successMessage
     * @param failureMessage
     */
    protected void executeTransaction(Runnable operation, String successMessage, String failureMessage)
    {
        if (!this.hasEnabledTransactions())
            operation.run();
        else {
            try (ClientSession session = this.client.db().getMongoClient().startSession()) {
                TransactionOptions txnOptions = TransactionOptions.builder()
                    .readPreference(ReadPreference.primary())
                    .readConcern(ReadConcern.LOCAL)
                    .writeConcern(WriteConcern.MAJORITY)
                    .build();

                TransactionBody<String> txnBody = () -> {
                    operation.run();
                    return successMessage;
                };

                session.withTransaction(txnBody, txnOptions);
            }
            catch (RuntimeException e) {
                Logging.logger.error(failureMessage + ": " + e.getMessage() + " (" + e.getClass().getName() + ")");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected R findByDbObject(DBObject query, Filter filter)
    {
        var record = this.findByDbObject(query, filter, this::buildRecord);
        return this.afterFind(record);
    }

    @Override
    protected List<R> listByDbObject(DBObject query, Filter filter)
    {
        var list = this.listByDbObject(query, filter, this::buildRecord);
        return this.afterList(list);
    }

    @Override
    protected Map<String, R> mapByDbObject(DBObject query, Filter filter)
    {
        var map = this.mapByDbObject(query, filter, this::buildRecord);
        return this.afterMap(map);
    }

    @Override
    public <S> Map<S, String> mapUidBySourceIds(Source<S, R> source, Collection<S> ids)
    {
        return this.mapUidBySourceIds(source, ids, Referential.now());
    }

    @Override
    public <S> Map<S, String> mapUidBySourceIds(Source<S, R> source, Collection<S> ids, Referential referential)
    {
        return this.mapBySourceIds(source, ids, doc -> doc.getList("uid", String.class).get(0), referential);
    }

    /**
     *
     * @param source
     * @param ids
     * @param builder
     * @param referential
     * @param <S>
     * @param <T>
     * @return
     */
    protected <S, T> Map<S, T> mapBySourceIds(Source<S, R> source, Collection<S> ids, Builder<Document, T> builder, Referential referential)
    {
        Map<S, T> map = new HashMap<>();

        if (!ids.isEmpty()) {
            var aggregationResult = this.mongoCollection.aggregate(
                Arrays.asList(
                    Aggregates.match(applyReferentialCriteria(Filters.in(source.queryName(), ids), referential)),
                    Aggregates.group("$" + source.queryName(), Accumulators.addToSet("uid", "$uid"))
                )
            );

            aggregationResult.forEach((Consumer<? super Document>) doc -> {
                var sourceId = doc.get("_id", source.type());
                var value = builder.build(doc);

                map.put(sourceId, value);
            });
        }

        return map;
    }

    /**
     *
     * @param op
     * @param referential
     * @param item
     * @param transcriber
     * @throws IllegalArgumentException If the provided Referential is a Referential.any() instance or if it's in an unknown state.
     */
    private void addPutOperation(BulkWriteOperation op, Referential referential, R item, Transcriber<BasicDBObject, R> transcriber)
    {
        BasicDBObject obj = transcriber.transcript(item);

        if (item.getUid() == null)
            item.setUid(UidHolder.generateUid());
        item.setHash(this.hash(obj));

        obj = this.recordFactory.enrichTranscript(item, obj);

        /*
         * A "now" or "at" referential represents the insertion of a "time ray" ("half-line") in the history.
         * To make it possible we have to:
         * - insert the record if there isn't already a record with the same uid and hash for an overlapping time ray.
         * - remove any record with a different uid/hash where start_time >= referential.at
         * - truncate any record with a different uid/hash where start_time < referential.at and end_time > referential.at
         */
        if (referential.isNow() || referential.isAt()) {
            Instant start = referential.isNow()
                ? Instant.now()
                : referential.asAt().getAt();

            /*
             * We check whether there already exists a record with the same hash signature that has an overlap with the Referential being used.
             * If it exists we ensure it is "open" by setting the validity_end field to null and updating the validity_start if the new one is before the current one.
             * Otherwise we will perform the insert.
             */
            var currentQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", item.getHash())
                .append(
                    "$or", dbList(
                        new BasicDBObject("validity_end", null),
                        new BasicDBObject("validity_end", new BasicDBObject("$gte", start))
                    )
                );
            var currentUpsert = new BasicDBObject()
                .append("$set", new BasicDBObject("validity_end", null))
                .append("$min", new BasicDBObject("validity_start", start))
                .append("$setOnInsert", obj);

            /* Full overlaps have to be removed */
            var previousFullOverlapQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", new BasicDBObject("$ne", item.getHash()))
                .append("validity_start", new BasicDBObject("$gte", start));

            /* Partial overlaps have to be truncated */
            var previousPartialOverlapQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", new BasicDBObject("$ne", item.getHash()))
                .append("validity_start", new BasicDBObject("$lt", start))
                .append(
                    "$or", dbList(
                        new BasicDBObject("validity_end", null),
                        new BasicDBObject("validity_end", new BasicDBObject("$gt", start))
                    )
                );
            var previousPartialOverlapUpdate = new BasicDBObject()
                .append("$set", new BasicDBObject("validity_end", start));

            op.find(previousFullOverlapQuery).remove();
            op.find(currentQuery).upsert().updateOne(currentUpsert);
            op.find(previousPartialOverlapQuery).updateOne(previousPartialOverlapUpdate);
        }
        /*
         * A "between" referential represents the insertion of a "time segment" in the history.
         * To make it possible we have to:
         * - insert the record if there isn't already a record with the same uid and hash for an overlapping time segment.
         * - remove any record where start_time >= referential.from && end_time <= referential.to
         * - truncate any record where start_time < referential.from && end_time > referential.from (note the upper boundary isn't "end_time <= referential.to", see following comment)
         * - truncate any record where start_time < referential.to && end_time > referential.to
         *
         * The insert will truncate any record where start_time < referential.from && end_time > referential.to in such a manner that the early extraneous time segment is preserved,
         * while the late extraneous time segment is effectively "removed". This means that if a shorter segment is inserted in the middle of a larger one of another identity, the larger one
         * will not be split into two parts, instead the oldest extraneous segment will be preserved, and the newest will be "removed".
         */
        else if (referential.isBetween()) {
            Instant from = referential.asBetween().getFrom();
            Instant to = referential.asBetween().getTo();

            /*
             * We check whether there already exists a record with the same hash signature that has an overlap with the Referential being used.
             * If it exists we update its validity interval so the largest time segment is used. Otherwise we will perform the insert.
             */
            var currentQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", item.getHash())
                .append(
                    "$or", dbList(
                        new BasicDBObject()
                            .append("validity_start", new BasicDBObject("$lte", from))
                            .append("validity_end", new BasicDBObject("$gte", from)),
                        new BasicDBObject()
                            .append("validity_start", new BasicDBObject("$lte", to))
                            .append("validity_end", new BasicDBObject("$gte", to)),
                        new BasicDBObject()
                            .append("validity_start", new BasicDBObject("$gte", from))
                            .append("validity_end", new BasicDBObject("$lte", to))
                    )
                );
            var currentUpsert = new BasicDBObject()
                .append("$min", new BasicDBObject("validity_start", from))
                .append("$max", new BasicDBObject("validity_end", to))
                .append("$setOnInsert", transcriber.transcript(item));

            /* Full overlaps have to be removed */
            var previousFullOverlapQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", new BasicDBObject("$ne", item.getHash()))
                .append("validity_start", new BasicDBObject("$gte", from))
                .append("validity_end", new BasicDBObject("$lte", to));

            /* Partial overlaps from the left side have to be truncated on the right side */
            var previousPartialLeftOverlapQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", new BasicDBObject("$ne", item.getHash()))
                .append("validity_start", new BasicDBObject("$lt", from))
                .append("validity_end", new BasicDBObject("$gt", from));
            var previousPartialLeftOverlapUpdate = new BasicDBObject()
                .append("$set", new BasicDBObject("validity_end", from));

            /* Partial overlaps from the right side have to be truncated on the left side */
            var previousPartialRightOverlapQuery = new BasicDBObject()
                .append("uid", item.getUid())
                .append("hash", new BasicDBObject("$ne", item.getHash()))
                .append("validity_start", new BasicDBObject("$lt", to))
                .append("validity_end", new BasicDBObject("$gt", to));
            var previousPartialRightOverlapUpdate = new BasicDBObject()
                .append("$set", new BasicDBObject("validity_start", to));

            op.find(previousFullOverlapQuery).remove();
            op.find(currentQuery).upsert().updateOne(currentUpsert);
            op.find(previousPartialLeftOverlapQuery).updateOne(previousPartialLeftOverlapUpdate);
            op.find(previousPartialRightOverlapQuery).updateOne(previousPartialRightOverlapUpdate);
        }
        else if (referential.isAny())
            throw new IllegalArgumentException("Cannot use Referential.any() for insertion.");
        else
            throw new IllegalArgumentException("The provided Referential instance is in an unknown state.");
    }

    /**
     *
     * @param query
     * @param referential
     * @return
     * @throws IllegalArgumentException If the provided Referential is in an unknown state.
     */
    protected Bson applyReferentialCriteria(Bson query, Referential referential)
    {
        Bson validityFilter;

        if (referential.isAny())
            return query;
        else if (referential.isNow() || referential.isAt()) {
            Instant at = referential.isNow() ? Instant.now() : referential.asAt().getAt();

            validityFilter = Filters.and(
                Filters.lte("validity_start", at),
                Filters.or(
                    Filters.eq("validity_end", null),
                    Filters.gt("validity_end", at)
                )
            );
        }
        else if (referential.isBetween()) {
            validityFilter = Filters.and(
                Filters.lte("validity_start", referential.asBetween().getFrom()),
                Filters.or(
                    Filters.eq("validity_end", null),
                    Filters.gt("validity_end", referential.asBetween().getTo())
                )
            );
        }
        else
            throw new IllegalArgumentException("The provided Referential instance is in an unknown state.");

        return Filters.and(query, validityFilter);
    }

    /**
     *
     * @param query
     * @param referential
     * @return
     * @throws IllegalArgumentException If the provided Referential is in an unknown state.
     */
    protected BasicDBObject applyReferentialCriteria(BasicDBObject query, Referential referential)
    {
        BasicDBObject validityCriteria;

        if (referential.isAny())
            return query;
        else if (referential.isNow() || referential.isAt()) {
            Instant at = referential.isNow() ? Instant.now() : referential.asAt().getAt();

            validityCriteria = dbObject()
                .append("validity_start", dbObject("$lte", at))
                .append(
                    "$or", dbList(
                        dbObject("validity_end", null),
                        dbObject("validity_end", dbObject("$gt", at))
                    )
                );
        }
        else if (referential.isBetween()) {
            validityCriteria = dbObject()
                .append("validity_start", dbObject("$lte", referential.asBetween().getFrom()))
                .append(
                    "$or", dbList(
                        dbObject("validity_end", null),
                        dbObject("validity_end", dbObject("$gt", referential.asBetween().getTo()))
                    )
                );
        }
        else
            throw new IllegalArgumentException("The provided Referential instance is in an unknown state.");

        if (query.isEmpty())
            return validityCriteria;

        return new BasicDBObject(
            "$and", dbList(
                query,
                validityCriteria
            )
        );
    }

    /**
     *
     * @param obj
     * @return
     */
    private String hash(BasicDBObject obj)
    {
        byte[] bsonBytes = toBson(canonicalizeBSONObject(obj));
        return Long.toHexString(this.hashFunction.hashBytes(bsonBytes));
    }

    /**
     *
     * @param dbObject
     * @return
     */
    private static byte[] toBson(final DBObject dbObject)
    {
        OutputBuffer outputBuffer = new BasicOutputBuffer();
        MongoClientSettings.getDefaultCodecRegistry()
            .get(DBObject.class)
            .encode(
                new BsonBinaryWriter(outputBuffer),
                dbObject,
                EncoderContext.builder().build()
            );
        return outputBuffer.toByteArray();
    }

    /**
     * create a copy of "from", but with keys ordered alphabetically
     *
     * @param from
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Object canonicalize(final Object from)
    {
        if (from instanceof BSONObject && !(from instanceof BasicBSONList))
            return canonicalizeBSONObject((BSONObject) from);
        /* BasicBSONList extends ArrayList, so it falls in this category */
        else if (from instanceof List)
            return canonicalizeList((List<Object>) from);
        else if (from instanceof Map)
            return canonicalizeMap((Map<String, Object>) from);
        return from;
    }

    /**
     *
     * @param from
     * @return
     */
    private static Map<String, Object> canonicalizeMap(final Map<String, Object> from)
    {
        Map<String, Object> canonicalized = new LinkedHashMap<>(from.size());
        TreeSet<String> keysInOrder = new TreeSet<>(from.keySet());
        for (String key : keysInOrder) {
            Object val = from.get(key);
            canonicalized.put(key, canonicalize(val));
        }
        return canonicalized;
    }

    /**
     *
     * @param from
     * @return
     */
    private static DBObject canonicalizeBSONObject(final BSONObject from)
    {
        BasicDBObject canonicalized = new BasicDBObject();
        TreeSet<String> keysInOrder = new TreeSet<>(from.keySet());
        for (String key : keysInOrder) {
            Object val = from.get(key);
            canonicalized.put(key, canonicalize(val));
        }
        return canonicalized;
    }

    /**
     *
     * @param list
     * @return
     */
    private static List canonicalizeList(final List<Object> list)
    {
        List<Object> canonicalized = new ArrayList<>(list.size());
        for (Object cur : list) {
            canonicalized.add(canonicalize(cur));
        }
        return canonicalized;
    }

    public boolean hasEnabledTransactions()
    {
        return enabledTransactions;
    }

    public MongoDBRecordStore<R> setEnabledTransactions(boolean enabledTransactions)
    {
        this.enabledTransactions = enabledTransactions;
        return this;
    }
}
