package com.lumiomedical.record.store.mongodb;

import com.lumiomedical.record.store.model.SimpleRecord;
import com.lumiomedical.record.store.SimpleRecordStore;
import com.mongodb.BasicDBObject;
import com.noleme.mongodb.MongoDBClient;
import com.noleme.store.factory.Factory;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/16
 */
public class MongoDBSimpleRecordStore extends MongoDBRecordStore<SimpleRecord> implements SimpleRecordStore
{
    /**
     * @param client
     * @param factory
     */
    public MongoDBSimpleRecordStore(MongoDBClient client, Factory<BasicDBObject, SimpleRecord> factory)
    {
        super(client, factory);
        this.setEnabledTransactions(false);
    }

    @Override
    protected String getCollectionName()
    {
        return "simple_record";
    }
}
