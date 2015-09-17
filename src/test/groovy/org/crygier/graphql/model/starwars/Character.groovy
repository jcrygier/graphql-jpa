package org.crygier.graphql.model.starwars

import groovy.transform.CompileStatic

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
@CompileStatic
abstract class Character {

    @Id
    String id;

    String name;

    @ManyToMany
    @JoinTable(name="character_friends",
            joinColumns=@JoinColumn(name="source_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="friend_id", referencedColumnName="id"))
    Collection<Character> friends;

    @ElementCollection(targetClass = Episode)
    @Enumerated(EnumType.ORDINAL)
    Collection<Episode> appearsIn;

}
