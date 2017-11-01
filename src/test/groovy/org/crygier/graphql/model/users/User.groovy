package org.crygier.graphql.model.users

import javax.persistence.Entity
import javax.persistence.Id

import org.crygier.graphql.annotation.SchemaDocumentation

import groovy.transform.CompileStatic

@Entity
@SchemaDocumentation("User who uses the application")
@CompileStatic
class User {

	@Id
	@SchemaDocumentation("Primary Key for the User Class")
	String id;
	
	String firstName;
	
	String lastName;
}
