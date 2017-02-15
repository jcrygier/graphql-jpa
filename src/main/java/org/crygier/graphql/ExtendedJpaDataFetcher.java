package org.crygier.graphql;

import graphql.language.Argument;
import graphql.language.Field;
import graphql.language.IntValue;
import graphql.language.ObjectValue;
import graphql.schema.DataFetchingEnvironment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtendedJpaDataFetcher extends JpaDataFetcher {

    public ExtendedJpaDataFetcher(EntityManager entityManager, EntityType<?> entityType) {
        super(entityManager, entityType);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        Field field = environment.getFields().iterator().next();
        Map<String, Object> result = new LinkedHashMap<>();

        PageInformation pageInformation = extractPageInformation(environment, field);

        // See which fields we're requesting
        Optional<Field> totalPagesSelection = getSelectionField(field, "totalPages");
        Optional<Field> totalElementsSelection = getSelectionField(field, "totalElements");
        Optional<Field> contentSelection = getSelectionField(field, "content");

        if (contentSelection.isPresent())
            result.put("content", getQuery(environment, contentSelection.get()).setMaxResults(pageInformation.size).setFirstResult((pageInformation.page - 1) * pageInformation.size).getResultList());

        if (totalElementsSelection.isPresent() || totalPagesSelection.isPresent()) {
            final Long totalElements = contentSelection
                    .map(contentField -> getCountQuery(environment, contentField).getSingleResult())
                    // if no "content" was selected an empty Field can be used
                    .orElseGet(() -> getCountQuery(environment, new Field()).getSingleResult());

            result.put("totalElements", totalElements);
            result.put("totalPages", ((Double) Math.ceil(totalElements / (double) pageInformation.size)).longValue());
        }

        return result;
    }

    private TypedQuery<Long> getCountQuery(DataFetchingEnvironment environment, Field field) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root root = query.from(entityType);

        SingularAttribute idAttribute = entityType.getId(Object.class);
        query.select(cb.count(root.get(idAttribute.getName())));
        List<Predicate> predicates = field.getArguments().stream().map(it -> cb.equal(root.get(it.getName()), convertValue(environment, it, it.getValue()))).collect(Collectors.toList());
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        return entityManager.createQuery(query);
    }

    private Optional<Field> getSelectionField(Field field, String fieldName) {
        return field.getSelectionSet().getSelections().stream().filter(it -> it instanceof Field).map(it -> (Field) it).filter(it -> fieldName.equals(it.getName())).findFirst();
    }

    private PageInformation extractPageInformation(DataFetchingEnvironment environment, Field field) {
        Optional<Argument> paginationRequest = field.getArguments().stream().filter(it -> GraphQLSchemaBuilder.PAGINATION_REQUEST_PARAM_NAME.equals(it.getName())).findFirst();
        if (paginationRequest.isPresent()) {
            field.getArguments().remove(paginationRequest.get());

            ObjectValue paginationValues = (ObjectValue) paginationRequest.get().getValue();
            IntValue page = (IntValue) paginationValues.getObjectFields().stream().filter(it -> "page".equals(it.getName())).findFirst().get().getValue();
            IntValue size = (IntValue) paginationValues.getObjectFields().stream().filter(it -> "size".equals(it.getName())).findFirst().get().getValue();

            return new PageInformation(page.getValue().intValue(), size.getValue().intValue());
        }

        return new PageInformation(1, Integer.MAX_VALUE);
    }

    private static final class PageInformation {
        public Integer page;
        public Integer size;

        public PageInformation(Integer page, Integer size) {
            this.page = page;
            this.size = size;
        }
    }


}
