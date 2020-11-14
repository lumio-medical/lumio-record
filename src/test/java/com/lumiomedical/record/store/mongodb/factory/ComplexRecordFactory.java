package com.lumiomedical.record.store.mongodb.factory;

import com.lumiomedical.record.store.model.ComplexRecord;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.noleme.store.factory.Factory;
import com.noleme.store.mongodb.MongoDBFactories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/11
 */
public class ComplexRecordFactory implements Factory<BasicDBObject, ComplexRecord>
{
    @Override
    public ComplexRecord build(BasicDBObject input)
    {
        return new ComplexRecord()
            .setName(input.getString("name"))
            .setAge(input.getLong("age"))
            .setBirthDate(input.getDate("birth_date").toInstant())
            .setTrueness(input.getBoolean("trueness"))
            .setLongValues(MongoDBFactories.buildCollection(new ArrayList<>(), (BasicDBList) input.get("long_values")))
            .setStringValues(MongoDBFactories.buildCollection(new HashSet<>(), (BasicDBList) input.get("string_values")))
            .setSubitems(MongoDBFactories.buildCollection(new ArrayList<>(), this, (BasicDBList) input.get("sub_items")))
            .setSubitemSet(MongoDBFactories.buildCollection(new HashSet<>(), this, (BasicDBList) input.get("sub_item_set")))
            .setMap(MongoDBFactories.buildMap(new HashMap<>(), (BasicDBObject) input.get("map")));
    }

    @Override
    public BasicDBObject transcript(ComplexRecord input)
    {
        return new BasicDBObject()
            .append("name", input.getName())
            .append("age", input.getAge())
            .append("birth_date", input.getBirthDate())
            .append("trueness", input.getTrueness())
            .append("long_values", MongoDBFactories.transcriptCollection(input.getLongValues()))
            .append("string_values", MongoDBFactories.transcriptCollection(input.getStringValues()))
            .append("sub_items", MongoDBFactories.transcriptCollection(input.getSubitems(), this))
            .append("sub_item_set", MongoDBFactories.transcriptUnorderedCollection(input.getSubitemSet(), this))
            .append("map", MongoDBFactories.transcriptMap(input.getMap()));

    }
}
