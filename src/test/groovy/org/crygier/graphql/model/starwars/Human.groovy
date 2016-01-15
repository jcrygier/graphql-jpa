package org.crygier.graphql.model.starwars

import groovy.transform.CompileStatic

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "Human")
@CompileStatic
public class Human extends Character {

    String homePlanet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_droid_id")
    Droid favoriteDroid;

    @ManyToOne
    @JoinColumn(name = "gender_code_id")
    CodeList gender;

}
