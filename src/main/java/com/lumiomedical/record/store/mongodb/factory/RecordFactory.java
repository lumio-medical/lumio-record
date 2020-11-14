package com.lumiomedical.record.store.mongodb.factory;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.store.mongodb.DBObjects;
import com.mongodb.BasicDBObject;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/27
 */
public class RecordFactory<R extends Record>
{
    /**
     *
     * @param input
     * @param output
     * @return
     */
    public R enrichBuild(BasicDBObject input, R output)
    {
        output
            .setId(input.getObjectId("_id").toHexString())
            .setUid(input.getString("uid"))
            .setHash(input.getString("hash"))
            .setValidityStart(DBObjects.getInstantOrNull(input, "validity_start"))
            .setValidityEnd(DBObjects.getInstantOrNull(input, "validity_end"))
            .setOfficialValidityStart(DBObjects.getInstantOrNull(input, "official_validity_start"))
            .setOfficialValidityEnd(DBObjects.getInstantOrNull(input, "official_validity_end"));

        return output;
    }

    /**
     *
     * @param input
     * @param output
     * @return
     */
    public BasicDBObject enrichTranscript(R input, BasicDBObject output)
    {
        /* validity_start and validity_end are added at the query stage */
        return output
            .append("uid", input.getUid())
            .append("hash", input.getHash())
            .append("official_validity_start", input.getOfficialValidityStart())
            .append("official_validity_end", input.getOfficialValidityEnd());
    }
}
