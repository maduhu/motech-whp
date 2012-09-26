package org.motechproject.whp.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.whp.container.mapping.domain.AdminContainerMapping;
import org.motechproject.whp.container.mapping.domain.ContainerRange;
import org.motechproject.whp.container.mapping.repository.AllAdminContainerMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminContainerMappingSeed {

    @Autowired
    private AllAdminContainerMappings allAdminContainerMappings;

    @Seed(priority = 0, version = "3.0")
    public void load() {
        ContainerRange range = new ContainerRange(90000000010L, 90000000020L);
        AdminContainerMapping mapping = new AdminContainerMapping();
        mapping.add(range);
        allAdminContainerMappings.add(mapping);
    }
}