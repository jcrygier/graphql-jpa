package org.crygier.graphql.model.embeddings;

import org.crygier.graphql.annotation.GraphQLIgnore;
import org.crygier.graphql.model.starwars.Character;
import org.crygier.graphql.model.starwars.Episode;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
class EmbeddingId implements Serializable {

    @Basic
    private int id;

    public EmbeddingId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmbeddingId that = (EmbeddingId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
