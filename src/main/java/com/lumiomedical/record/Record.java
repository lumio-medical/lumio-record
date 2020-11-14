package com.lumiomedical.record;

import com.fasterxml.jackson.annotation.*;
import com.lumiomedical.record.source.AbstractSource;
import com.lumiomedical.record.source.SourceCollection;
import com.lumiomedical.record.source.Sourceable;
import com.lumiomedical.record.source.Sourcing;
import com.noleme.store.query.Identifiable;

import java.time.Instant;
import java.util.HashSet;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/27
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public abstract class Record<R> implements Identifiable<String>, UidHolder, HashHolder, Sourceable<R>
{
    @JsonIgnore
    private String id;
    @JsonProperty(required = true)
    @JsonPropertyDescription("Entity unique UID identifier")
    private String uid;
    @JsonPropertyDescription("Record hash value")
    private String hash;
    @JsonPropertyDescription("Entity source references")
    private Sourcing sources;
    @JsonProperty(required = true)
    @JsonPropertyDescription("Record validity start time (used for determining a record's validity for a given time referential)")
    private Instant validityStart;
    @JsonProperty(required = true)
    @JsonPropertyDescription("Record validity end time (used for determining a record's validity for a given time referential)")
    private Instant validityEnd;
    @JsonPropertyDescription("Record official validity start time")
    private Instant officialValidityStart;
    @JsonPropertyDescription("Record official validity end time")
    private Instant officialValidityEnd;

    public Record()
    {
        this.sources = new Sourcing();
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public Record setId(String id)
    {
        this.id = id;
        return this;
    }

    @Override
    public String getUid()
    {
        return uid;
    }

    @Override
    public Record setUid(String uid)
    {
        this.uid = uid;
        return this;
    }

    @Override
    public String getHash()
    {
        return hash;
    }

    @Override
    public Record setHash(String hash)
    {
        this.hash = hash;
        return this;
    }

    @Override
    public <S> boolean hasSource(AbstractSource<S, R> source)
    {
        return this.sources.hasSource(source);
    }

    @Override
    public <S> S getSourceId(AbstractSource<S, R> source)
    {
        return this.sources.getSourceId(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> R setSourceId(AbstractSource<S, R> source, S value)
    {
        this.sources.setSourceId(source, value);
        return (R) this;
    }

    @Override
    public <S> R addSourceId(SourceCollection<S, R> source, S value)
    {
        if (!this.sources.hasSource(source))
            this.sources.setSourceId(source, new HashSet<>());
        return null;
    }

    public Sourcing getSources()
    {
        return sources;
    }

    @SuppressWarnings("unchecked")
    public R setSources(Sourcing sources)
    {
        this.sources = sources;
        return (R) this;
    }

    public Instant getValidityStart()
    {
        return validityStart;
    }

    public Record setValidityStart(Instant validityStart)
    {
        this.validityStart = validityStart;
        return this;
    }

    public Instant getValidityEnd()
    {
        return validityEnd;
    }

    public Record setValidityEnd(Instant validityEnd)
    {
        this.validityEnd = validityEnd;
        return this;
    }

    public Instant getOfficialValidityStart()
    {
        return officialValidityStart;
    }

    public Record setOfficialValidityStart(Instant officialValidityStart)
    {
        this.officialValidityStart = officialValidityStart;
        return this;
    }

    public Instant getOfficialValidityEnd()
    {
        return officialValidityEnd;
    }

    public Record setOfficialValidityEnd(Instant officialValidityEnd)
    {
        this.officialValidityEnd = officialValidityEnd;
        return this;
    }
}
