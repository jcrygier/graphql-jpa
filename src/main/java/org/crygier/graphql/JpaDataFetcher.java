package org.crygier.graphql;

import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaDataFetcher implements DataFetcher {

    protected EntityManager entityManager;
    protected EntityType<?> entityType;

    public JpaDataFetcher(EntityManager entityManager, EntityType<?> entityType) {
        this.entityManager = entityManager;
        this.entityType = entityType;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        return getQuery(environment, environment.getFields().iterator().next()).getResultList();
    }

    protected TypedQuery getQuery(DataFetchingEnvironment environment, Field field) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery((Class) entityType.getJavaType());
        Root root = query.from(entityType);

        // Loop through all of the fields being requested
        field.getSelectionSet().getSelections().forEach(selection -> {
            if (selection instanceof Field) {
                Field selectedField = (Field) selection;
                Path fieldPath = root.get(selectedField.getName());

                // Process the orderBy clause
                Optional<Argument> orderByArgument = selectedField.getArguments().stream().filter(it -> "orderBy".equals(it.getName())).findFirst();
                if (orderByArgument.isPresent()) {
                    if ("DESC".equals(((EnumValue)orderByArgument.get().getValue()).getName()))
                        query.orderBy(cb.desc(fieldPath));
                    else
                        query.orderBy(cb.asc(fieldPath));
                }

                // Check if it's an object and the foreign side is One.  Then we can eagerly fetch causing an inner join instead of 2 queries
                if (fieldPath.getModel() instanceof SingularAttribute) {
                    SingularAttribute attribute = (SingularAttribute) fieldPath.getModel();
                    if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE)
                        root.fetch(selectedField.getName());
                }
            }
        });

        List<Predicate> predicates = field.getArguments().stream().map(it -> cb.equal(root.get(it.getName()), convertValue(environment, it.getValue()))).collect(Collectors.toList());
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        return entityManager.createQuery(query);
    }

    protected Object convertValue(DataFetchingEnvironment environment, Value value) {
        if (value instanceof StringValue)
            return ((StringValue) value).getValue();
        else if (value instanceof VariableReference)
            return environment.getArguments().get(((VariableReference) value).getName());

        return value.toString();
    }
}
