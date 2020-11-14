package com.lumiomedical.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/02/27
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = ReferentialAny.class, name = "ANY"),
        @JsonSubTypes.Type(value = ReferentialAt.class, name = "AT"),
        @JsonSubTypes.Type(value = ReferentialBetween.class, name = "BETWEEN"),
        @JsonSubTypes.Type(value = ReferentialNow.class, name = "NOW"),
    }
)
public abstract class Referential
{
    /**
     * Produces a referential matching any point in time.
     *
     * @return
     */
    public static ReferentialAny any()
    {
        return ReferentialAny.singleton;
    }

    /**
     * Produces a referential matching all periods presently active.
     *
     * @return
     */
    public static ReferentialNow now()
    {
        return ReferentialNow.singleton;
    }

    /**
     * Produces a referential matching all periods active at the provided instant.
     *
     * @param validityPoint
     * @return
     * @throws IllegalArgumentException If the provided validityPoint is null.
     */
    public static ReferentialAt at(Instant validityPoint)
    {
        assertNotNull(validityPoint);
        return new ReferentialAt(validityPoint);
    }

    /**
     * Produces a referential matching all periods active between the two provided instant.
     *
     * @param from
     * @param to
     * @return
     * @throws IllegalArgumentException If the provided from or to values are null.
     */
    public static ReferentialBetween between(Instant from, Instant to)
    {
        assertNotNull(from);
        assertNotNull(to);
        return new ReferentialBetween(from, to);
    }

    public ReferentialAny asAny()
    {
        throw new IllegalArgumentException("The referential cannot be cast to ReferentialAny.");
    }

    public ReferentialNow asNow()
    {
        throw new IllegalArgumentException("The referential cannot be cast to ReferentialNow.");
    }

    public ReferentialAt asAt()
    {
        throw new IllegalArgumentException("The referential cannot be cast to ReferentialAt.");
    }

    public ReferentialBetween asBetween()
    {
        throw new IllegalArgumentException("The referential cannot be cast to ReferentialNow.");
    }

    @JsonIgnore
    public boolean isAny()
    {
        return false;
    }

    @JsonIgnore
    public boolean isNow()
    {
        return false;
    }

    @JsonIgnore
    public boolean isAt()
    {
        return false;
    }

    @JsonIgnore
    public boolean isBetween()
    {
        return false;
    }

    /**
     *
     * @param instant
     */
    private static void assertNotNull(Instant instant)
    {
        if (instant == null)
            throw new IllegalArgumentException("The provided instant was null.");
    }

    public String toString()
    {
        if (this.isNow())
            return "Referential.now";
        else if (this.isAny())
            return "Referential.any";
        else if (this.isAt())
            return "Referential.at(" + ((ReferentialAt) this).getAt() + ")";
        else if (this.isBetween())
            return "Referential.between(" + ((ReferentialBetween) this).getFrom() + " -> " + ((ReferentialBetween) this).getTo() + ")";
        return super.toString();
    }
}
