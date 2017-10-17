package org.crygier.graphql.model.embeddings;

import org.crygier.graphql.annotation.GraphQLIgnore;
import org.crygier.graphql.model.starwars.Character;
import org.crygier.graphql.model.starwars.Episode;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

//@GraphQLIgnore
//@Embeddable
public class EmbeddingId {

//    @ManyToOne
    Character character;

//    @ManyToOne
    Episode episode;

}
