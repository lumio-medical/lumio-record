package com.lumiomedical.record.store;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.Referential;
import com.lumiomedical.record.store.model.ComplexRecord;
import com.lumiomedical.record.store.mongodb.MongoDBComplexRecordStore;
import com.lumiomedical.record.store.mongodb.TestHelper;
import com.lumiomedical.record.store.mongodb.factory.ComplexRecordFactory;
import com.noleme.mongodb.MongoDBClientException;
import com.noleme.store.query.Query;
import org.junit.jupiter.api.*;

import java.time.Instant;

/**
 * Mostly for testing hash computations, see SimpleRecordStoreTest for validity intervals and Referential computations.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/11
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ComplexRecordStoreTest
{
    private ComplexRecordStore store;

    @BeforeAll
    void setup() throws MongoDBClientException
    {
        this.store = new MongoDBComplexRecordStore(TestHelper.provideClient(), new ComplexRecordFactory());
    }

    @BeforeEach
    void clear()
    {
        this.store.remove(new Query());
    }

    @Test
    void testInsert()
    {
        /* We create an instance and insert it in the database (total count = 1, uid count = 1) */
        var r1 = provideRecord();

        this.store.put(r1);

        Assertions.assertEquals(1, this.store.count(Referential.any()));

        /* We create an instance and insert it in the database (total count = 2, uid count = 2) */
        var r2 = provideRecord();

        this.store.put(r2);

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(2, this.store.map(new Query()).keySet().size());

        /*
         * We create another instance with the same values, and set its UID to the same as r1.
         * Insertion should do nothing (total count = 2, uid count = 2)
         */
        ComplexRecord r3 = (ComplexRecord) provideRecord().setUid(r1.getUid());
        this.store.put(r3);

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(2, this.store.map(new Query()).keySet().size());

        /*
         * We create another instance with the same values, but alter the name afterwards and re-use the same UID.
         * Insertion should close the previous record for that UID and add a new one (total count = 3, uid count = 2)
         */
        ComplexRecord r4 = (ComplexRecord) provideRecord()
            .setName("OtherName")
            .setUid(r1.getUid());
        this.store.put(r4);

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(2, this.store.map(new Query()).keySet().size());

        /*
         * We create another instance with the same values, but alter the long values afterwards and re-use the same UID as r2.
         * Insertion should close the previous record for that UID and add a new one (total count = 4, uid count = 2)
         */
        ComplexRecord r5 = (ComplexRecord) provideRecord()
            .setLongValues(3L, 2L, 5L)
            .setUid(r2.getUid());

        this.store.put(r5);

        Assertions.assertEquals(4, this.store.count(Referential.any()));
        Assertions.assertEquals(2, this.store.map(new Query()).keySet().size());

        /*
         * We create another instance with the same values, but alter a sub-item, and re-use the same UID as r1.
         * Insertion should close the previous record for that UID and add a new one (total count = 5, uid count = 2)
         */
        ComplexRecord r6 = (ComplexRecord) provideRecord()
            .setUid(r1.getUid());
        r6.getSubitems()
            .get(0)
            .setAge(1L);

        this.store.put(r6);

        Assertions.assertEquals(5, this.store.count(Referential.any()));
        Assertions.assertEquals(2, this.store.map(new Query()).keySet().size());

    }

    /**
     *
     * @return
     */
    private static ComplexRecord provideRecord()
    {
        return new ComplexRecord()
            .setName("MyNameIs")
            .setAge(12L)
            .setBirthDate(Instant.ofEpochMilli(50000000))
            .setTrueness(true)
            .setLongValues(2L, 3L, 5L)
            .setStringValues("abc", "def", "ghi")
            .addSubitem(
                new ComplexRecord()
                    .setName("SubMyNameIs1")
                    .setAge(3L)
                    .setBirthDate(Instant.ofEpochMilli(500005000))
                    .setTrueness(false)
                    .setLongValues(3L, 2L)
            )
            .addSubitem(
                new ComplexRecord()
                    .setName("SubMyNameIs2")
                    .setAge(2L)
                    .setBirthDate(Instant.ofEpochMilli(500005200))
                    .setTrueness(false)
            )
            .addSubitemToSet(
                new ComplexRecord()
                    .setName("SubMyNameIs2")
                    .setAge(2L)
                    .setBirthDate(Instant.ofEpochMilli(500005200))
                    .setTrueness(false)
            )
            .addSubitemToSet(
                new ComplexRecord()
                    .setName("SubMyNameIs1")
                    .setAge(3L)
                    .setBirthDate(Instant.ofEpochMilli(500005000))
                    .setTrueness(false)
                    .setLongValues(3L, 2L)
            )
            .putMap("meh", false)
            .putMap("meuh", true);
    }

    /**
     *
     */
    private void printRecords()
    {
        this.store.list(new Query(), Referential.any()).forEach(this::printRecord);
        System.out.println();
    }

    /**
     *
     * @param record
     */
    private void printRecord(Record record)
    {
        if (record == null) {
            System.out.println("No record found");
            return;
        }
        System.out.println("#" + record.getUid() + " (#" + record.getId() + ")");
        System.out.println(" - " + record.getValidityStart() + " -> " + record.getValidityEnd());
        if (record instanceof ComplexRecord) {
            ComplexRecord complexRecord = (ComplexRecord) record;
            System.out.println(" + " + complexRecord.getName());
            System.out.println(" + " + complexRecord.getAge());
        }
    }
}
