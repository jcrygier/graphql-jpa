package org.crygier.graphql.model.embeddings;

import org.crygier.graphql.model.starwars.Character;
import org.crygier.graphql.model.starwars.Episode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
class EmbeddingId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "character_id")
    private Character character;

    @Column(name = "episode")
    private Episode episode;

    public EmbeddingId(Character character, Episode episode) {
        this.character = character;
        this.episode = episode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmbeddingId that = (EmbeddingId) o;

        if (character != null ? !character.equals(that.character) : that.character != null) return false;
        return episode == that.episode;
    }

    @Override
    public int hashCode() {
        int result = character != null ? character.hashCode() : 0;
        result = 31 * result + (episode != null ? episode.hashCode() : 0);
        return result;
    }
}
