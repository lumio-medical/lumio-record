# Lumio Record

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lumio-medical/lumio-record/Java%20CI%20with%20Maven)](https://github.com/lumio-medical/lumio-record/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/com.lumiomedical/lumio-record/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lumiomedical/lumio-record)
[![javadoc](https://javadoc.io/badge2/com.lumiomedical/lumio-record/javadoc.svg)](https://javadoc.io/doc/com.lumiomedical/lumio-record)
![GitHub](https://img.shields.io/github/license/lumio-medical/lumio-record)

This library provides utilities for handling document versioning and sourcing in a MongoDB database.

Implementations found in this package will shouldn't be tied to any specific Lumio project.

_Note: This library is considered as "in beta" and as such significant API changes may occur without prior warning._

## I. Installation

Add the following in your `pom.xml`:

```xml
<dependency>
    <groupId>com.lumiomedical</groupId>
    <artifactId>lumio-record</artifactId>
    <version>0.1.1</version>
</dependency>
```

## II. Notes on Structure and Design

The code in this library was initially found in the `lumio-core` repository.
As such, although most of its components were, from their inception, designed to be "stand-alone" and not tied to specific business needs, some components can be less mature as a stand-alone than they could be.

Now that this is out of the way, this library features three groups of components:
* the `com.lumiomedical.record` package contains tools for defining versioned entities
* the `com.lumiomedical.record.store` package contains contracts and implementations for storing versioned entities using the `noleme-store` library
* the `com.lumiomedical.record.source` package contains tools for defining sourced entities (ie. entities with references to one or several external sources via a foreign unique identifier), note that a `Record` is a `Sourceable`

Further documentation of these three groups is underway. 

_TODO_

## III. Usage

Here is a simple example of what this library looks like when in use:

```java
Patient patient;
/* find the patient using their unique identifier */
patient = patientStore.find(someUid);
/* ..this is equivalent to doing */
patient = patientStore.find(someUid, Referential.now());

/* ..this time we want an earlier version */
patient = patientStore.find(someUid, Referential.at(inDays(-10)));

List<Patient> records;
/* find all records for the patient with this unique identifier */
records = patientStore.list(List.of(someUid), Referential.any());

/* ..this time we only want versions from a given timeframe */
records = patientStore.list(
    List.of(someUid),
    Referential.between(inDays(-60), inDays(-30))
);

/* insert a record version at the present time */
patientStore.put(patient);
/* ..or at a given date */
patientStore.put(patient, Referential.at(inDays(-15)));
```

Now, let's look a bit closer at what it looks like when creating a `Record` entity and its store. 

First, we'll start by defining the record entity, let's say it's a very basic Patient record:

```java
public class Patient extends Record<Patient>
{
    public String firstName;
    public String lastName;
    public Instant birthDate;
}
```

We now have to define a store interface contract and an implementation for it.

Start by creating an interface, we'll call it `PatientStore`, and make it extend the `RecordStore` interface.

This interface is where all possible requests are declared, the `RecordStore` contract already provides the most common ones but if you have any specific behaviour (eg. custom aggregation pipes), this is the place to declare them.
In our case, we just want the default behaviour, so we'll use `RecordStore` and be done with it.  

```java
public interface PatientStore extends RecordStore<Patient> {}
```

Now that we have the interface, we want a MongoDB implementation ; note that it extends the `MongoDBRecordStore` abstract class, which provides implementations for the `RecordStore` contract, as well as a host of utility methods:

```java
public class MongoDBPatientStore extends MongoDBRecordStore<Patient> implements PatientStore
{
    /* Note that we are given a parent constructor which expects a MongoDBClient and a Factory, we'll talk about them in a moment */
    public MongoDBPatientStore(MongoDBClient client, Factory<BasicDBObject, Patient> factory)
    {
        super(client, factory);
    }
    
    @Override
    protected String getCollectionName()
    {
        /* Here, we return the name of the collection in the mongodb database, it can be anything we want */
        return "patient";
    }
}
```

Now that we have our `PatientStore` and its MongoDB incarnation, the final step is creating a `Factory`.
The factory class is responsible for the translation between the `Patient` POJO and its MongoDB document, here is what it could look like for `Patient`.

```java
public class PatientFactory implements Factory<BasicDBObject, Patient>
{
    @Override
    public Patient build(BasicDBObject input)
    {
        /* This bit is responsible for the build of a Patient POJO out of a MongoDB document */
        var patient = new Patient();
        patient.firstName = input.getString("first_name");
        patient.lastName = input.getString("last_name");
        return patient;
    }

    @Override
    public BasicDBObject transcript(Patient input)
    {
        /* This bit is responsible for the transcription of a Patient POJO into a MongoDB document */
        return new BasicDBObject()
            .append("first_name", input.getFirstName())
            .append("last_name", input.getLastName())
        ;
    }
}
```

We have everything now, so let's do a few requests:

```java
var patient = new Patient();
patient.firstName = "Arnold";
patient.lastName = "Weber";
patient.birthDate = Instant.parse("2001-01-01T00:00:00.00Z");

var store = new PatientStore(new MongoDBClient(), new PatientFactory());

/* Let's insert it at his birthdate */
store.put(patient, Referential.at(patient.birthDate));

/* Let's change his first name */
patient.firstName = "Bernard";

/* Let's insert this some time later */
store.put(patient, Referential.at(Instant.parse("2020-01-01T00:00:00.00Z")));

/* This should print Bernard */
System.out.println(store.find(patient.getUid()).getName());
/* This should print Arnold */
System.out.println(store.find(patient.getUid(), Referential.at(Instant.parse("2010-01-01T00:00:00.00Z"))).getName());
```

_TODO_

## IV. Dev Installation

This project will require you to have the following:

* Java 13+
* Git (versioning)
* Maven (dependency resolving, publishing and packaging) 
