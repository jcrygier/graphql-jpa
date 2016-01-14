GraphQL for JPA
===============

If you're already using JPA, then you already have a schema defined...don't define it again just for GraphQL!

This is a simple project to extend [graphql-java](https://github.com/andimarek/graphql-java) and have it derive the
schema from a JPA model.  It also implements an execution platform to generate and run JPA queries based on
GraphQL queries.

While limited, there is a lot of power bundled within.  This project takes a somewhat opinionated view of GraphQL, by
introducing things like Pagination, Aggregations, and Sorting.

This project is intended on depending on very little: graphql-java, and some javax annotation packages.  The tests depend
on Spring (with Hibernate for JPA), and Spock for testing.  These tests are a good illustration of how this library might
be used, but any stack (with JPA) should be able to be utilized.

Schema Generation
-----------------

Using a JPA Entity Manager, the models are introspected, and a GraphQL Schema is built.  With this GraphQL schema,
graphql-java does most of the work, except for querying.

Schema Documentation
--------------------

A major part of GraphQL is the ability to have a well documented schema.  This project takes advantage of this, and produces
descriptions for each Entity in the schema.  For the built in types (e.g. PaginationObject) these are rather hard-coded
without much control from the end user.

However, for each Entity / Member that is in your JPA schema, you can document what it's for.  These descriptions are controlled
by the `@SchemaDocumentation` attribute on either a class level, or a field level of your model.

These descriptions will show up in the GraphiQL browser automatically, and generally helps when providing an API to your
end-users.  See the GraphiQL section below for more details.

Pagination
----------

GraphQL does not specify any language or idioms for performing Pagination.  Therefore, this library takes an opinionated
view, similar to that of Spring.

Each model (say Human or Droid - see tests) will have two representations in the generated schema:

- One that models the Entities directly (Human or Droid)
- One that wraps the Entity in a page request (HumanConnection or DroidConnection)

This allows you to query for the "Page" version of any Entity, and return metadata (like total count) alongside of the
actual requested data.  For example:

    {
        HumanConnection(paginationRequest: { page: 1, size: 2 }) {
            totalPages
            totalElements
            content {
                name
            }
        }
    }

Will return:

    {
        HumanConnection: {
            totalPages: 3,
            totalElements: 5,
            content: [
                { name: 'Luke Skywalker' },
                { name: 'Darth Vader' }
            ]
        }
    }

Of course, an extra query is needed to get the total elements, so if you have not requested 'totalPages' or 'totalElements'
this query will not be executed.

NOTE: The "Connection" name is used here for further extension (Aggregations, etc...).  The name is borrowed
from suggestions by Facebook developers: https://github.com/facebook/graphql/issues/4

Aggregations
------------

Not yet implemented, but will be similar to Pagination

Sorting
-------

Sorting is supported on any field.  Simply pass in an 'orderBy' argument with the value of ASC or DESC.  Here's an example
of sorting by name for Human objects:

    {
        Human {
            name(orderBy: DESC)
            homePlanet
        }
    }

Query Injectors
---------------

Not yet implemented.  Main use case would be to intercept query execution for security purposes.

GraphiQL
--------

GraphiQL (https://github.com/graphql/graphiql) has been introduced for simple testing (in the test package, as I don't
want to assume your web stack).  Simply launch TestApplication as a Java Application, and navigate to http://localhost:8080/
to launch.  You will notice a 'Docs' button at the upper right, that when expanded will show you the running schema (Star
Wars in this demo).

You can enter GraphQL queries in the left pannel, and hit the run button, and the results should come back in the right
panel.  If your query has variables, there is a minimized panel at the bottom left.  Simply click on this to expand, and
type in your variables as a JSON string (don't forget to quote the keys!).  Enjoy!