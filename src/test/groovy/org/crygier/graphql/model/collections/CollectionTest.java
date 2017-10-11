package org.crygier.graphql.model.collections;

import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CollectionTest {

    //testing that the Schema Builder does not break
    //when building collection of non-enum objects
    
    @SchemaDocumentation("A List of Strings")
    @ElementCollection(targetClass=String.class)
    List<String> codas = new ArrayList<String>();
}
