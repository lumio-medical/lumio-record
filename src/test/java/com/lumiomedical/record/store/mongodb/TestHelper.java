package com.lumiomedical.record.store.mongodb;

import com.noleme.mongodb.MongoDBClient;
import com.noleme.mongodb.MongoDBClientException;
import com.noleme.mongodb.configuration.MongoDBConfiguration;
import com.noleme.mongodb.test.MockDBClient;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
public final class TestHelper
{
    private TestHelper() {}

    /**
     *
     * @return
     * @throws MongoDBClientException
     */
    public static MongoDBClient provideClient() throws MongoDBClientException
    {
        return new MockDBClient(new MongoDBConfiguration().set("database", "lumio"));
    }
}
