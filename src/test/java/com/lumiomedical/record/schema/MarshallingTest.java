package com.lumiomedical.record.schema;

import com.noleme.json.Json;
import org.junit.jupiter.api.Assertions;

import java.util.function.Supplier;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/30
 */
public abstract class MarshallingTest
{
    /**
     * IMPORTANT: very important caveat to this technique, its nice because it doesn't require lots of fiddling for each individual entity being test..
     * However, it will occasionally fail when Sets are involved due to values being sorted in different ways, so instances submitted to this method shouldn't have sets with more than 1 element.
     *
     * @param c
     * @param supplier
     * @param <T>
     */
    protected <T> void marshallTest(Class<T> c, Supplier<T> supplier)
    {
        var source = supplier.get();

        var json1 = Json.prettyPrint(Json.toJson(source));
        var marshalled = Json.fromJson(Json.parse(json1), c);
        var json2 = Json.prettyPrint(Json.toJson(marshalled));

        Assertions.assertEquals(json1, json2);
    }

    /**
     *
     * @param c
     * @param <T>
     */
    protected <T> void schemaTest(Class<T> c)
    {
        Assertions.assertDoesNotThrow(() -> {
            Json.generateSchema(c);
        });
    }
}
