package org.motechproject.whp.container.dashboard.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.whp.container.dashboard.model.ContainerDashboardRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AllContainerDashboardRows extends MotechBaseRepository<ContainerDashboardRow> {

    @Autowired
    public AllContainerDashboardRows(@Qualifier("whpDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(ContainerDashboardRow.class, dbCouchDbConnector);
    }

    @View(name = "find_by_containerId", map = "function(doc) {if (doc.type ==='ContainerDashboardRow') {emit(doc.container.containerId, doc._id);}}")
    public ContainerDashboardRow findByContainerId(String containerId) {
        ViewQuery findByContainerId = createQuery("find_by_containerId").key(containerId).includeDocs(true);
        return singleResult(db.queryView(findByContainerId, ContainerDashboardRow.class));
    }
}