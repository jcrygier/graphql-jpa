package org.crygier.graphql.model.collections;

import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@CompileStatic
public class CollectionTest {

    //testing that the Schema Builder does not break
    //when building collection of non-enum objects

    @Id
    @SchemaDocumentation("Primary Key for the CollectionTest Class")
    String id;

    @SchemaDocumentation("A List of Strings")
    @ElementCollection(targetClass=String.class)
    List<String> codas = new ArrayList<String>();
}
