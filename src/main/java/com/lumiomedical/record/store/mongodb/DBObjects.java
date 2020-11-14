package com.lumiomedical.record.store.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.time.Instant;
import java.util.Arrays;

public final class DBObjects
{
    private DBObjects()
    {

    }

    /**
     *
     * @param criteria
     * @return
     */
    public static BasicDBList dbList(BasicDBObject... criteria)
    {
        var list = new BasicDBList();
        list.addAll(Arrays.asList(criteria));
        return list;
    }

    /**
     *
     * @param values
     * @return
     */
    public static BasicDBList dbList(Object... values)
    {
        var list = new BasicDBList();
        list.addAll(Arrays.asList(values));
        return list;
    }

    public static BasicDBObject dbObject()
    {
        return new BasicDBObject();
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static BasicDBObject dbObject(String key, Object value)
    {
        return new BasicDBObject(key, value);
    }

    /**
     *
     * @param obj
     * @param field
     * @return
     */
    public static Instant getInstantOrNull(BasicDBObject obj, String field)
    {
        return getInstantOrDefault(obj, field, null);
    }

    /**
     *
     * @param obj
     * @param field
     * @param defaultValue
     * @return
     */
    public static Instant getInstantOrDefault(BasicDBObject obj, String field, Instant defaultValue)
    {
        return obj.get(field) != null
            ? obj.getDate(field).toInstant()
            : defaultValue;
    }
}
