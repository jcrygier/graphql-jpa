package org.crygier.graphql.model.users

import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
public class DateAndUser {
	public Date date;
	@ManyToOne
	public User user;
}
