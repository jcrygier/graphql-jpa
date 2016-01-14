package org.crygier.graphql.model.starwars

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation

import javax.persistence.*

@Entity
@SchemaDocumentation("Abstract representation of an entity in the Star Wars Universe")
@CompileStatic
abstract class Character {

    @Id
    @SchemaDocumentation("Primary Key for the Character Class")
    String id;

    @SchemaDocumentation("Name of the character")
    String name;

    @SchemaDocumentation("Who are the known friends to this character")
    @ManyToMany
    @JoinTable(name="character_friends",
            joinColumns=@JoinColumn(name="source_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="friend_id", referencedColumnName="id"))
    Collection<Character> friends;

    @SchemaDocumentation("What Star Wars episodes does this character appear in")
    @ElementCollection(targetClass = Episode)
    @Enumerated(EnumType.ORDINAL)
    Collection<Episode> appearsIn;

}
