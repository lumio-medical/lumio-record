package com.lumiomedical.record;

import net.openhft.hashing.LongHashFunction;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/07
 */
public interface UidHolder
{
    LongHashFunction hasher = LongHashFunction.city_1_1();

    /**
     *
     * @return
     */
    String getUid();

    /**
     *
     * @param uid
     * @return
     */
    UidHolder setUid(String uid);

    /**
     * Generates a random UID.
     *
     * @return
     */
    static String generateUid()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a UID using a sequence of strings that will be individually hashed and joined.
     *
     * @param components
     * @return
     */
    static String generateUid(String... components)
    {
        var joiner = new StringJoiner("-");

        for (String component : components)
            joiner.add(Long.toHexString(hasher.hashChars(component)));

        return joiner.toString();
    }

    /**
     *
     * @param uid
     * @return
     */
    static UidHolder readOnlyHolder(String uid)
    {
        return new UidHolder() {
            @Override
            public String getUid()
            {
                return uid;
            }

            @Override
            public UidHolder setUid(String uid)
            {
                return this;
            }
        };
    }
}
