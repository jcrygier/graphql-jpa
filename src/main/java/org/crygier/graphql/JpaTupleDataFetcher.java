package org.crygier.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.metamodel.SingularAttribute;

public class JpaTupleDataFetcher implements DataFetcher {

    private final String name;

    public JpaTupleDataFetcher(String name) {
        this.name = name;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        Object source = environment.getSource();
        if (source instanceof Tuple) {
            Tuple tuple = (Tuple) source;
            try {
                return tuple.get(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

}
