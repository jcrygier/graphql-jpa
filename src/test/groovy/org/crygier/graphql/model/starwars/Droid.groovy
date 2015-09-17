package org.crygier.graphql.model.starwars

import groovy.transform.CompileStatic

import javax.persistence.Entity

@Entity
@CompileStatic
class Droid extends Character {

    String primaryFunction;

}
