package com.lumiomedical.record.store.mongodb.factory;

import com.lumiomedical.record.store.model.SimpleRecord;
import com.mongodb.BasicDBObject;
import com.noleme.store.factory.Factory;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/16
 */
public class SimpleRecordFactory implements Factory<BasicDBObject, SimpleRecord>
{
    @Override
    public SimpleRecord build(BasicDBObject input)
    {
        return new SimpleRecord()
            .setName(input.getString("name"))
            .setAge(input.getLong("age"))
            .setTrueness(input.getBoolean("trueness"));
    }

    @Override
    public BasicDBObject transcript(SimpleRecord input)
    {
        return new BasicDBObject()
            .append("name", input.getName())
            .append("age", input.getAge())
            .append("trueness", input.getTrueness());
    }
}
