package org.motechproject.whp.patient.mapper;

import org.junit.Test;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.builder.TreatmentBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.domain.TreatmentDetails;

import static org.junit.Assert.assertNotNull;
import static org.motechproject.whp.patient.mapper.AssertTreatmentDetails.assertTreatmentDetails;

public class TreatmentDetailsMapperTest {
    
    TreatmentDetailsMapper treatmentDetailsMapper = new TreatmentDetailsMapper();
    
    @Test
    public void shouldUpdateTreatmentDetails() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaultTreatmentDetails().build();
        
        Treatment treatment = new Treatment();

        TreatmentDetails treatmentDetails = treatmentDetailsMapper.map(patientRequest, treatment);

        assertTreatmentDetails(patientRequest, treatmentDetails);
    }

    @Test
    public void shouldConsiderOnlyFieldsWhichAreNotNullInPatientRequest() {
        PatientRequest patientRequest = new PatientRequestBuilder().build();
        Treatment treatment = new TreatmentBuilder().withDefaultTreatmentDetails().build();

        TreatmentDetails treatmentDetails = treatmentDetailsMapper.mapWithNullCheck(patientRequest, treatment);

        assertNotNull(treatmentDetails.getCmfDoctor());
        assertNotNull(treatmentDetails.getDistrictWithCode());
        assertNotNull(treatmentDetails.getTbUnitWithCode());
        assertNotNull(treatmentDetails.getEpSite());
        assertNotNull(treatmentDetails.getOtherInvestigations());
        assertNotNull(treatmentDetails.getPreviousTreatmentHistory());
        assertNotNull(treatmentDetails.getHivStatus());
        assertNotNull(treatmentDetails.getHivTestDate());
        assertNotNull(treatmentDetails.getMembersBelowSixYears());
        assertNotNull(treatmentDetails.getPhcReferred());
        assertNotNull(treatmentDetails.getProviderName());
        assertNotNull(treatmentDetails.getDotCentre());
        assertNotNull(treatmentDetails.getProviderType());
        assertNotNull(treatmentDetails.getCmfDoctor());
        assertNotNull(treatmentDetails.getContactPersonName());
        assertNotNull(treatmentDetails.getContactPersonPhoneNumber());
        assertNotNull(treatmentDetails.getXpertTestResult());
        assertNotNull(treatmentDetails.getXpertDeviceNumber());
        assertNotNull(treatmentDetails.getXpertTestDate());
        assertNotNull(treatmentDetails.getRifResistanceResult());
    }
}
