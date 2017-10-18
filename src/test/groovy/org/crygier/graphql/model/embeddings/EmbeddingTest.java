package org.crygier.graphql.model.embeddings;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GraphQLIgnore;
import org.hibernate.annotations.Target;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@CompileStatic
public class EmbeddingTest {

    @EmbeddedId
    private EmbeddingId embeddingId;

}
