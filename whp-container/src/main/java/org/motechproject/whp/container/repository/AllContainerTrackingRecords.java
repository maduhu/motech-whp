package org.motechproject.whp.container.repository;

import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;
import org.codehaus.jackson.type.TypeReference;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.couchdb.lucene.query.field.Field;
import org.motechproject.couchdb.lucene.repository.LuceneAwareMotechBaseRepository;
import org.motechproject.couchdb.lucene.util.WhiteSpaceEscape;
import org.motechproject.paginator.contract.FilterParams;
import org.motechproject.paginator.contract.SortParams;
import org.motechproject.whp.common.domain.RegistrationInstance;
import org.motechproject.whp.common.ektorp.SearchFunctionUpdater;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.query.ContainerTrackingQueryDefinition;
import org.motechproject.whp.container.query.InTreatmentContainerTrackingQueryDefinition;
import org.motechproject.whp.container.query.PreTreatmentContainerTrackingQueryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AllContainerTrackingRecords extends LuceneAwareMotechBaseRepository<Container> {

    public static final String SORT_BY_ASCENDING = "asc";

    @Autowired
    public AllContainerTrackingRecords(@Qualifier("whpLuceneAwareCouchDbConnector") LuceneAwareCouchDbConnector whpLuceneAwareCouchDbConnector, WhiteSpaceEscape whiteSpaceEscape) {
        super(Container.class, whpLuceneAwareCouchDbConnector, whiteSpaceEscape);
        IndexUploader uploader = new IndexUploader();

        ContainerTrackingQueryDefinition containerQueryDefinition = new ContainerTrackingQueryDefinition() {
            public List<Field> fields() {
               return new ArrayList<>();
            }
        };

        uploader.updateSearchFunctionIfNecessary(db, containerQueryDefinition.viewName(), containerQueryDefinition.searchFunctionName(), containerQueryDefinition.indexFunction());
        new SearchFunctionUpdater().updateAnalyzer(db, containerQueryDefinition.viewName(), containerQueryDefinition.searchFunctionName(), "keyword");
    }

    @View(name = "find_by_containerId", map = "function(doc) {if (doc.type ==='Container') {emit(doc.containerId, doc._id);}}")
    public Container findByContainerId(String containerId) {
        ViewQuery findByContainerId = createQuery("find_by_containerId").key(containerId).includeDocs(true);
        return singleResult(db.queryView(findByContainerId, Container.class));
    }

    public void updateAll(List<Container> containerTrackingRecords) {
        db.executeAllOrNothing(containerTrackingRecords);
    }

    @View(name = "find_by_providerId", map = "function(doc) {if (doc.type ==='Container' && doc.providerId) {emit(doc.providerId, doc._id);}}")
    public List<Container> withProviderId(String providerId) {
        ViewQuery findByContainerId = createQuery("find_by_providerId").key(providerId.toLowerCase()).includeDocs(true);
        return db.queryView(findByContainerId, Container.class);
    }

    @View(name = "find_by_patientId", map = "function(doc) {if (doc.type ==='Container' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<Container> withPatientId(String patientId) {
        ViewQuery findByContainerId = createQuery("find_by_patientId").key(patientId.toLowerCase()).includeDocs(true);
        return db.queryView(findByContainerId, Container.class);
    }

    public TypeReference<CustomLuceneResult<Container>> getTypeReference() {
        return new TypeReference<CustomLuceneResult<Container>>() {
        };
    }

    public List<Container> filter(RegistrationInstance instance, FilterParams filterParams, SortParams sortParams, int skip, int limit) {
        ContainerTrackingQueryDefinition queryDefinition = getNewQueryDefinition(instance);
        if (queryDefinition != null) {
            filterParams.put(queryDefinition.getContainerInstanceFieldName(), instance.name());
            sortParams.put(queryDefinition.getContainerIdFieldName(), SORT_BY_ASCENDING);
            return super.filter(queryDefinition, filterParams, sortParams, skip, limit);
        }
        return Collections.emptyList();
    }

    public int count(RegistrationInstance instance, FilterParams filterParams) {
        ContainerTrackingQueryDefinition queryDefinition = getNewQueryDefinition(instance);
        if (queryDefinition != null) {
            filterParams.put(queryDefinition.getContainerInstanceFieldName(), instance.name());
            return super.count(queryDefinition, filterParams);
        }
        return 0;
    }

    private ContainerTrackingQueryDefinition getNewQueryDefinition(RegistrationInstance instance) {
        switch (instance) {

            case PreTreatment:
                return new PreTreatmentContainerTrackingQueryDefinition();
            case InTreatment:
                return new InTreatmentContainerTrackingQueryDefinition();
        }
        return null;
    }
}