package org.motechproject.whp.it.importer.csv.request;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.importer.csv.PatientRecordImporter;
import org.motechproject.whp.importer.csv.builder.ImportPatientRequestBuilder;
import org.motechproject.whp.importer.csv.request.ImportPatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.user.repository.AllProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationITContext.xml")
@Ignore("Not required anymore")
public class PatientRecordImporterIT {

    @Autowired
    PatientRecordImporter patientRecordImporter;

    @Autowired
    AllPatients allPatients;

    @Autowired
    AllProviders allProviders;


    @After
    @Before
    public void tearDown() {
        allPatients.removeAll();
    }


    @Test
    public void shouldStoreOnlyValidPatientData() {
        ImportPatientRequest importPatientRequest1 = new ImportPatientRequestBuilder().withDefaults().withCaseId("12344").build();

        patientRecordImporter.post(Arrays.asList((Object) importPatientRequest1));

        assertNotNull(allPatients.findByPatientId("12344"));
        assertEquals(1,allPatients.getAll().size());
    }
    @Test
    public void shouldSetMigratedFieldAsTrueOnStoring() {
        ImportPatientRequest importPatientRequest1 = new ImportPatientRequestBuilder().withDefaults().withCaseId("12344").build();

        patientRecordImporter.post(Arrays.asList((Object) importPatientRequest1));

        Patient patient = allPatients.findByPatientId("12344");
        assertTrue(patient.isMigrated());
    }
}
