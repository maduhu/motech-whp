package org.motechproject.whp.containermapping.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class AdminContainerMappingTest {

    @Test
    public void shouldAddContainerMapping(){
        AdminContainerMapping adminContainerMapping = new AdminContainerMapping();
        ContainerRange containerRange = new ContainerRange(100, 200);
        adminContainerMapping.add(containerRange);

        assertThat(adminContainerMapping.getContainerRanges(), hasItem(containerRange));
    }
}
