package org.motechproject.whp.uimodel;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.PhaseName;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.refdata.domain.WHPConstants;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatientDTOTest {

    @Test
    public void shouldSetPhaseStartDatesFromPatientObject() {
        Patient patient = new PatientBuilder().withDefaults().build();
        patient.startTherapy(new LocalDate());
        patient.currentTherapy().getPhases().setEIPStartDate(new LocalDate());
        patient.currentTherapy().getPhases().setCPStartDate(new LocalDate());
        PatientDTO patientDTO = new PatientDTO(patient);
        Therapy therapy = patient.currentTherapy();

        assertEquals(therapy.getPhases().getByPhaseName(PhaseName.IP).getStartDate().toString(WHPConstants.DATE_FORMAT), patientDTO.getIpStartDate());
        assertEquals(therapy.getPhases().getByPhaseName(PhaseName.EIP).getStartDate().toString(WHPConstants.DATE_FORMAT), patientDTO.getEipStartDate());
        assertEquals(therapy.getPhases().getByPhaseName(PhaseName.CP).getStartDate().toString(WHPConstants.DATE_FORMAT), patientDTO.getCpStartDate());
    }

    @Test
    public void shouldMapNewPhaseInfoToPatient() {
        Patient patient = new PatientBuilder().withDefaults().build();
        PatientDTO patientDTO = new PatientDTO(patient);
        patientDTO.setIpStartDate("21/05/2012");
        patientDTO.setEipStartDate("22/05/2012");
        patientDTO.setCpStartDate("23/05/2012");
        patientDTO.mapNewPhaseInfoToPatient(patient);

        assertEquals(new LocalDate(2012, 5, 21), patient.currentTherapy().getStartDate());
        assertEquals(patient.currentTherapy().getPhases().getByPhaseName(PhaseName.IP).getStartDate(), patient.currentTherapy().getStartDate());
        assertEquals(new LocalDate(2012, 5, 22), patient.currentTherapy().getPhases().getByPhaseName(PhaseName.EIP).getStartDate());
        assertEquals(new LocalDate(2012, 5, 23), patient.currentTherapy().getPhases().getByPhaseName(PhaseName.CP).getStartDate());
    }

    @Test
    public void shouldResetTreatmentStartDateIfIPStartDateIsEmpty() {
        Patient patient = new PatientBuilder().withDefaults().build();
        patient.startTherapy(new LocalDate());
        PatientDTO patientDTO = new PatientDTO(patient);
        patientDTO.setIpStartDate("");
        patientDTO.setEipStartDate("22/05/2012");
        patientDTO.setCpStartDate("23/05/2012");
        patientDTO.mapNewPhaseInfoToPatient(patient);

        assertNull(patient.currentTherapy().getStartDate());
    }

    @Test
    public void shouldSetPhaseStartDatesToNullIfFormDataIsEmpty() {
        Patient patient = new PatientBuilder().withDefaults().build();
        patient.startTherapy(new LocalDate());
        patient.currentTherapy().getPhases().getByPhaseName(PhaseName.EIP).setStartDate(new LocalDate());
        patient.currentTherapy().getPhases().getByPhaseName(PhaseName.CP).setStartDate(new LocalDate());
        PatientDTO patientDTO = new PatientDTO(patient);
        patientDTO.setIpStartDate("");
        patientDTO.setEipStartDate("");
        patientDTO.setCpStartDate("");
        patientDTO.mapNewPhaseInfoToPatient(patient);

        assertNull(patient.currentTherapy().getStartDate());
        assertNull(patient.currentTherapy().getPhases().getByPhaseName(PhaseName.IP).getStartDate());
        assertNull(patient.currentTherapy().getPhases().getByPhaseName(PhaseName.EIP).getStartDate());
        assertNull(patient.currentTherapy().getPhases().getByPhaseName(PhaseName.CP).getStartDate());
    }

}