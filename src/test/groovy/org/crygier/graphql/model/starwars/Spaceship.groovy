package org.crygier.graphql.model.starwars

import javax.persistence.*

import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.model.users.DateAndUser

import groovy.transform.CompileStatic

@Entity
@SchemaDocumentation("Spaceships in the Star Wars Universe")
@CompileStatic
public class Spaceship {

	@Id
	@SchemaDocumentation("Primary Key for the Spaceship Class")
	public String id;

	@SchemaDocumentation("Name of the spaceship")
	String name;
	
	@Embedded
	@AttributeOverrides ([
		@AttributeOverride(name="date",column=@Column(name="createddate"))	
	])
	@AssociationOverrides ([
		@AssociationOverride(name="user",joinColumns=@JoinColumn(name="createduser"))
	])
	public DateAndUser created;
	
	@Embedded
	@AttributeOverrides ([
		@AttributeOverride(name="date",column=@Column(name="modifieddate"))
	])
	@AssociationOverrides ([
		@AssociationOverride(name="user",joinColumns=@JoinColumn(name="modifieduser"))
	])
	DateAndUser modified;
}
