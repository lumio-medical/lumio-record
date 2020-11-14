package com.lumiomedical.record.store.mongodb;

import com.lumiomedical.record.store.model.ComplexRecord;
import com.lumiomedical.record.store.ComplexRecordStore;
import com.mongodb.BasicDBObject;
import com.noleme.mongodb.MongoDBClient;
import com.noleme.store.factory.Factory;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/11
 */
public class MongoDBComplexRecordStore extends MongoDBRecordStore<ComplexRecord> implements ComplexRecordStore
{
    /**
     * @param client
     * @param factory
     */
    public MongoDBComplexRecordStore(MongoDBClient client, Factory<BasicDBObject, ComplexRecord> factory)
    {
        super(client, factory);
        this.setEnabledTransactions(false);
    }

    @Override
    protected String getCollectionName()
    {
        return "complex_record";
    }
}
