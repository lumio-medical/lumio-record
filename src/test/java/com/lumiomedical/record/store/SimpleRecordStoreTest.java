package com.lumiomedical.record.store;

import com.lumiomedical.record.Record;
import com.lumiomedical.record.Referential;
import com.lumiomedical.record.store.model.SimpleRecord;
import com.lumiomedical.record.store.mongodb.MongoDBSimpleRecordStore;
import com.lumiomedical.record.store.mongodb.TestHelper;
import com.lumiomedical.record.store.mongodb.factory.SimpleRecordFactory;
import com.noleme.commons.time.TimeHelper;
import com.noleme.mongodb.MongoDBClientException;
import com.noleme.store.query.Identifier;
import com.noleme.store.query.Query;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/03/16
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleRecordStoreTest
{
    private SimpleRecordStore store;

    @BeforeAll
    void setup() throws MongoDBClientException
    {
        this.store = new MongoDBSimpleRecordStore(TestHelper.provideClient(), new SimpleRecordFactory());
        ((MongoDBSimpleRecordStore) this.store).createIndexes();
    }

    @BeforeEach
    void clear()
    {
        this.store.remove(new Query());
    }

    @Test
    void testInsert()
    {
        var r1 = provideRecord();
        this.store.put(r1);

        Assertions.assertEquals(1, this.store.count());

        r1.setName("Meh");
        this.store.put(r1);

        Assertions.assertEquals(1, this.store.count());

        var r2 = provideRecord()
            .setAge(12L);
        this.store.put(r2);

        Assertions.assertEquals(2, this.store.count());

        var r1Reloaded = this.store.find(new Query("name", "Meh"));
        var r2Reloaded = this.store.find(new Query("name", "Meuh"));
        Assertions.assertEquals(17L, r1Reloaded.getAge());
        Assertions.assertEquals(12L, r2Reloaded.getAge());
    }

    @Test
    void testUniqueIndexes()
    {
        /*
         * We try to insert two records with the same uid at the same time.
         * The unique index over uid + validity_start should prevent this, but we remove the overlapping record if the hash differs.
         * A failure at this step might indicate a bug in the insert sequence ordering.
         */
        var at = daysAgo(1);

        var r1 = provideRecord();
        this.store.put(r1, Referential.at(at));

        var r2 = provideRecord(r1.getUid()).setAge(2L);
        this.store.put(r2, Referential.at(at));

        Assertions.assertEquals(1, this.store.count(Referential.at(at)));

        /*
         * We try to insert two records with the same uid at the same time (with an interval this time).
         * The unique index over uid + validity_start should prevent this, but we remove the overlapping record if the hash differs.
         * A failure at this step might indicate a bug in the insert sequence ordering.
         */

        var from = daysAgo(3);
        var to = daysAgo(2);

        var r3 = provideRecord();
        this.store.put(r3, Referential.between(from, to));

        var r4 = provideRecord(r3.getUid()).setAge(2L);
        this.store.put(r4, Referential.between(from, to));

        Assertions.assertEquals(1, this.store.count(Referential.at(from)));
    }

    @Test
    void testInsertAt()
    {
        /*
         * We insert a first record R1 6 days in the past.
         * A new entry is expected to be created with the corresponding validity_start time.
         */
        var r1 = provideRecord();
        var r1Start = daysAgo(6);
        this.store.put(r1, Referential.at(r1Start));

        var dbR1a = this.store.find(r1.getUid());

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(5))));
        Assertions.assertEquals(r1Start, dbR1a.getValidityStart());
        Assertions.assertNull(dbR1a.getValidityEnd());

        /*
         * We insert an equivalent record R2 2 days in the past.
         * Nothing should change as the period was already covered in the previous insert.
         */
        var r2 = provideRecord(r1.getUid());
        var r2Start = daysAgo(2);
        this.store.put(r2, Referential.at(r2Start));

        var dbR1b = this.store.find(r1.getUid());

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(dbR1a.getId(), dbR1b.getId());
        Assertions.assertEquals(dbR1a.getValidityStart(), dbR1b.getValidityStart());
        Assertions.assertNotEquals(r2Start, dbR1b.getValidityStart());
        Assertions.assertNull(dbR1b.getValidityEnd());

        /*
         * We insert an equivalent record R3 7 days in the past.
         * R3 shouldn't be inserted, instead R1 should be updated with the new extended validity period.
         */
        var r3 = provideRecord(r1.getUid());
        var r3Start = daysAgo(7);
        this.store.put(r3, Referential.at(r3Start));

        var dbR1c = this.store.find(r1.getUid());

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(8))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(6))));
        Assertions.assertEquals(dbR1a.getId(), dbR1c.getId());
        Assertions.assertNotEquals(dbR1a.getValidityStart(), dbR1c.getValidityStart());
        Assertions.assertEquals(r3Start, dbR1c.getValidityStart());
        Assertions.assertNull(dbR1c.getValidityEnd());

        /*
         * We insert a new record R4 5 days in the past.
         * R4 should be inserted, R1 should be updated so that its validity_end is equal to the validity_start of R4.
         */
        var r4 = provideRecord(r1.getUid()).setName("Meh");
        var r4Start = daysAgo(5);
        this.store.put(r4, Referential.at(r4Start));

        var dbR1d = this.store.find(r1.getUid(), Referential.at(daysAgo(6)));
        var dbR4a = this.store.find(r4.getUid());

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertNotEquals(dbR1a.getId(), dbR4a.getId());
        Assertions.assertEquals(r4Start, dbR4a.getValidityStart());
        Assertions.assertEquals(dbR1d.getValidityEnd(), dbR4a.getValidityStart());
        Assertions.assertNull(dbR4a.getValidityEnd());

        /*
         * We insert a new record R5 equivalent to R1 3 days in the past.
         * R5 should be inserted as a new one due to the time discontinuity with R1.
         * R4 should be truncated to a 2 days-long duration (down from 5, ranging from -5 days to -3 days).
         */
        var r5 = provideRecord(r1.getUid());
        var r5Start = daysAgo(3);
        this.store.put(r5, Referential.at(r5Start));

        var dbR4b = this.store.find(r4.getUid(), Referential.at(daysAgo(4)));
        var dbR5a = this.store.find(r5.getUid());

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertNotEquals(dbR1a.getId(), dbR5a.getId());
        Assertions.assertNotEquals(dbR4b.getId(), dbR5a.getId());
        Assertions.assertEquals(dbR4b.getValidityEnd(), dbR5a.getValidityStart());
        Assertions.assertNull(dbR5a.getValidityEnd());

        /*
         * We insert a new record R6 equivalent to R1 2 days in the past.
         * Nothing should change as the period was already covered in the previous insert.
         */
        var r6 = provideRecord(r1.getUid());
        var r6Start = daysAgo(2);
        this.store.put(r6, Referential.at(r6Start));

        var dbR6 = this.store.find(r6.getUid());

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(dbR5a.getId(), dbR6.getId());
        Assertions.assertNotEquals(r6Start, dbR6.getValidityStart());
        Assertions.assertEquals(dbR5a.getValidityStart(), dbR6.getValidityStart());
        Assertions.assertNull(dbR6.getValidityEnd());

        /*
         * We insert a new record R7 equivalent to R1 4 days in the past (ie. 1 day before R5).
         * R7 shouldn't be inserted, instead R5 should be updated with the new extended validity period.
         */
        var r7 = provideRecord(r1.getUid());
        var r7Start = daysAgo(4);
        this.store.put(r7, Referential.at(r7Start));

        var dbR5b = this.store.find(r5.getUid());
        var dbR7 = this.store.find(r7.getUid());

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(dbR5a.getId(), dbR7.getId());
        Assertions.assertEquals(r7Start, dbR5b.getValidityStart());
        Assertions.assertNull(dbR5b.getValidityEnd());

        /*
         * We insert a new record R8 equivalent to R4 6 days in the past (ie. 1 day before R4).
         * R8 shouldn't be inserted, instead R4 should be updated with the new extended validity period.
         * R5 should be deleted.
         */
        var r8 = provideRecord(r1.getUid()).setName(r4.getName());
        var r8Start = daysAgo(6);
        this.store.put(r8, Referential.at(r8Start));

        var dbR1e = this.store.find(new Query("_id", Identifier.from(dbR1a.getId())), Referential.at(daysAgo(7)));
        var dbR5c = this.store.find(new Query("_id", Identifier.from(dbR5b.getId())));
        var dbR8 = this.store.find(r8.getUid());

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(1, this.store.count(Referential.now()));
        Assertions.assertEquals(dbR4b.getId(), dbR8.getId());
        Assertions.assertNull(dbR5c);
        Assertions.assertEquals(r8Start, dbR8.getValidityStart());
        Assertions.assertNull(dbR8.getValidityEnd());
        Assertions.assertEquals(dbR1e.getValidityEnd(), dbR8.getValidityStart());
    }

    @Test
    void test()
    {
        var patient = provideRecord();
        patient.setName("Arnold");

        /* Let's insert it at his birthdate */
        store.put(patient, Referential.at(Instant.parse("2001-01-01T00:00:00.00Z")));

        /* Let's change his first name */
        patient.setName("Bernard");

        /* Let's insert this some time later */
        store.put(patient, Referential.at(Instant.parse("2020-01-01T00:00:00.00Z")));

        /* This should print Bernard */
        System.out.println(store.find(patient.getUid()).getName());
        /* This should print Arnold */
        System.out.println(store.find(patient.getUid(), Referential.at(Instant.parse("2010-01-01T00:00:00.00Z"))).getName());
    }

    @Test
    void testInsertBetween()
    {
        /*
         * We insert a first record R1 with a validity set between 6 and 3 days in the past.
         * A new entry is expected to be created with the corresponding validity_start and validity_end time.
         */
        var r1 = provideRecord();
        var r1From = daysAgo(6);
        var r1To = daysAgo(3);
        this.store.put(r1, Referential.between(r1From, r1To));

        var dbR1a = this.store.find(r1.getUid(), Referential.at(r1From));

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(5))));
        Assertions.assertEquals(r1From, dbR1a.getValidityStart());
        Assertions.assertEquals(r1To, dbR1a.getValidityEnd());

        /*
         * We insert an equivalent record R2 with a validity set between 5 and 4 days in the past.
         * Nothing should change as the period was already covered in the previous insert.
         */
        var r2 = provideRecord(r1.getUid());
        var r2From = daysAgo(5);
        var r2To = daysAgo(4);
        this.store.put(r2, Referential.between(r2From, r2To));

        var dbR1b = this.store.find(r1.getUid(), Referential.at(r2From));

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(5))));
        Assertions.assertEquals(dbR1a.getId(), dbR1b.getId());
        Assertions.assertNotEquals(r2From, dbR1b.getValidityStart());
        Assertions.assertNotEquals(r2To, dbR1b.getValidityEnd());

        /*
         * We insert an equivalent record R3 with a validity set between 7 and 4 days in the past.
         * R3 shouldn't be inserted, instead R1 should be updated with the new extended validity period.
         */
        var r3 = provideRecord(r1.getUid());
        var r3From = daysAgo(7);
        var r3To = daysAgo(4);
        this.store.put(r3, Referential.between(r3From, r3To));

        var dbR1c = this.store.find(r1.getUid(), Referential.at(r3From));

        Assertions.assertEquals(1, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(8))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(5))));
        Assertions.assertEquals(dbR1a.getId(), dbR1c.getId());
        Assertions.assertEquals(r3From, dbR1c.getValidityStart());
        Assertions.assertNotEquals(r3To, dbR1c.getValidityEnd());
        Assertions.assertEquals(r1To, dbR1c.getValidityEnd());

        /*
         * We insert a new record R4 with a validity set between 5 and 2 days in the past.
         * R4 should be inserted, R1 should be updated so that its validity_end is equal to the validity_start of R4.
         */
        var r4 = provideRecord(r1.getUid()).setName("Meh");
        var r4From = daysAgo(5);
        var r4To = daysAgo(2);
        this.store.put(r4, Referential.between(r4From, r4To));

        var dbR1d = this.store.find(r1.getUid(), Referential.at(daysAgo(6)));
        var dbR4a = this.store.find(r4.getUid(), Referential.at(daysAgo(3)));

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(4))));
        Assertions.assertEquals(0, this.store.count(Referential.at(daysAgo(1))));
        Assertions.assertNotEquals(dbR1a.getId(), dbR4a.getId());
        Assertions.assertEquals(r4From, dbR4a.getValidityStart());
        Assertions.assertEquals(r4To, dbR4a.getValidityEnd());
        Assertions.assertEquals(dbR1d.getValidityEnd(), dbR4a.getValidityStart());

        /*
         * We insert a new record R5 equivalent to R1 with a validity set between 3 and 1 days in the past.
         * R5 should be inserted as a new one due to the time discontinuity with R1.
         * R4 should be truncated to a 2 days-long duration (down from 5, ranging from -5 days to -3 days).
         */
        var r5 = provideRecord(r1.getUid());
        var r5From = daysAgo(3);
        var r5To = daysAgo(1);
        this.store.put(r5, Referential.between(r5From, r5To));

        var dbR4b = this.store.find(r4.getUid(), Referential.at(daysAgo(4)));
        var dbR5a = this.store.find(r5.getUid(), Referential.at(daysAgo(2)));

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(4))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(2))));
        Assertions.assertNotEquals(dbR1a.getId(), dbR5a.getId());
        Assertions.assertNotEquals(dbR4b.getId(), dbR5a.getId());
        Assertions.assertEquals(r5From, dbR5a.getValidityStart());
        Assertions.assertEquals(r5To, dbR5a.getValidityEnd());
        Assertions.assertEquals(dbR4b.getValidityEnd(), dbR5a.getValidityStart());

        /*
         * We insert a new record R6 equivalent to R1 with a validity set between 4 and 2 days in the past (ie. shifted 1 day left compared to R5).
         * R6 shouldn't be inserted, instead R5 should be updated with the new extended validity period.
         */
        var r6 = provideRecord(r1.getUid());
        var r6From = daysAgo(4);
        var r6To = daysAgo(2);
        this.store.put(r6, Referential.between(r6From, r6To));

        var dbR5b = this.store.find(r5.getUid(), Referential.at(daysAgo(2)));
        var dbR6 = this.store.find(r6.getUid(), Referential.at(daysAgo(3)));

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(4))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(2))));
        Assertions.assertEquals(dbR5b.getId(), dbR6.getId());
        Assertions.assertEquals(r6From, dbR5b.getValidityStart());
        Assertions.assertNotEquals(r6To, dbR5b.getValidityEnd());
        Assertions.assertEquals(r5To, dbR5b.getValidityEnd());

        /*
         * We insert a new record R7 equivalent to R4 with a validity set between 6 and 2 days in the past.
         * R7 shouldn't be inserted, instead R4 should be updated with the new extended validity period.
         * R5 should be updated with a validity period ranging from 2 to 1 day in the past.
         */
        var r7 = provideRecord(r1.getUid()).setName(r4.getName());
        var r7From = daysAgo(6);
        var r7To = daysAgo(2);
        this.store.put(r7, Referential.between(r7From, r7To));

        var dbR1e = this.store.find(new Query("_id", Identifier.from(dbR1a.getId())), Referential.at(daysAgo(7)));
        var dbR5c = this.store.find(new Query("_id", Identifier.from(dbR5b.getId())), Referential.at(daysAgo(3)));
        var dbR7 = this.store.find(r7.getUid(), Referential.at(daysAgo(5)));

        Assertions.assertEquals(3, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(4))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(2))));
        Assertions.assertEquals(dbR4b.getId(), dbR7.getId());
        Assertions.assertNull(dbR5c);
        Assertions.assertEquals(r7From, dbR7.getValidityStart());
        Assertions.assertEquals(r7To, dbR7.getValidityEnd());
        Assertions.assertEquals(dbR1e.getValidityEnd(), dbR7.getValidityStart());

        /*
         * We insert a new record R8 equivalent to R4 with a validity set between 6 days and 5 minutes in the past.
         * R8 shouldn't be inserted, instead R4 should be updated with the new extended validity period.
         * R5 should be deleted.
         */
        var r8 = provideRecord(r1.getUid()).setName(r4.getName());
        var r8From = daysAgo(6);
        var r8To = minutesAgo(5);
        this.store.put(r8, Referential.between(r8From, r8To));

        var dbR1f = this.store.find(new Query("_id", Identifier.from(dbR1a.getId())), Referential.at(daysAgo(7)));
        var dbR5d = this.store.find(new Query("_id", Identifier.from(dbR5b.getId())), Referential.at(daysAgo(1)));
        var dbR8 = this.store.find(r7.getUid(), Referential.at(daysAgo(5)));

        Assertions.assertEquals(2, this.store.count(Referential.any()));
        Assertions.assertEquals(0, this.store.count(Referential.now()));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(7))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(4))));
        Assertions.assertEquals(1, this.store.count(Referential.at(daysAgo(2))));
        Assertions.assertEquals(dbR4b.getId(), dbR8.getId());
        Assertions.assertNull(dbR5d);
        Assertions.assertEquals(r8From, dbR8.getValidityStart());
        Assertions.assertEquals(r8To, dbR8.getValidityEnd());
        Assertions.assertEquals(dbR1f.getValidityEnd(), dbR8.getValidityStart());
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
        System.out.println(" - " + record.getHash());
        if (record instanceof SimpleRecord) {
            SimpleRecord simpleRecord = (SimpleRecord) record;
            System.out.println(" + " + simpleRecord.getName());
        }
    }

    private static SimpleRecord provideRecord(String uid)
    {
        return (SimpleRecord) provideRecord().setUid(uid);
    }

    private static SimpleRecord provideRecord()
    {
        return new SimpleRecord()
            .setName("Meuh")
            .setAge(17L)
            .setTrueness(true);
    }

    private static Instant daysAgo(long days)
    {
        return TimeHelper.inDays(-days).truncatedTo(ChronoUnit.SECONDS);
    }

    private static Instant minutesAgo(long minutes)
    {
        return TimeHelper.inMinutes(-minutes).truncatedTo(ChronoUnit.SECONDS);
    }
}
