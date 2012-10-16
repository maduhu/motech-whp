package org.motechproject.whp.container.tracking.repository;

import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.container.tracking.model.ContainerTrackingRecord;
import org.motechproject.whp.container.tracking.query.ContainerDashboardQueryDefinition;
import org.motechproject.whp.container.tracking.query.InTreatmentContainerDashboardQueryDefinition;
import org.motechproject.whp.container.tracking.query.PreTreatmentContainerDashboardQueryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

@Repository
public class AllInTreatmentContainerTrackingRecordsImpl extends AllContainerTrackingRecords {

    @Autowired
    public AllInTreatmentContainerTrackingRecordsImpl(@Qualifier("whpContainerTrackingCouchDbConnector") LuceneAwareCouchDbConnector whpLuceneAwareCouchDbConnector) {
        super(whpLuceneAwareCouchDbConnector);
        IndexUploader uploader = new IndexUploader();
        ContainerDashboardQueryDefinition queryDefinition = getNewQueryDefinition();
        uploader.updateSearchFunctionIfNecessary(db, queryDefinition.viewName(), queryDefinition.searchFunctionName(), queryDefinition.indexFunction());
    }

    protected ContainerDashboardQueryDefinition getNewQueryDefinition() {
        return new InTreatmentContainerDashboardQueryDefinition();
    }

    public List<ContainerTrackingRecord> filter(Properties filterParams, int skip, int limit) {
        ContainerDashboardQueryDefinition queryDefinition = getNewQueryDefinition();
        filterParams.put(queryDefinition.getContainerInstanceFieldName(), SputumTrackingInstance.InTreatment.name());
        return super.filter(queryDefinition, filterParams, skip, limit);
    }

    public int count(Properties filterParams) {
        ContainerDashboardQueryDefinition queryDefinition = getNewQueryDefinition();
        filterParams.put(queryDefinition.getContainerInstanceFieldName(), SputumTrackingInstance.InTreatment.name());
        return super.count(queryDefinition, filterParams);
    }
}
