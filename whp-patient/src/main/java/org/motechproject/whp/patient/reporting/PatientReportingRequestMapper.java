package org.motechproject.whp.patient.reporting;

import org.motechproject.whp.common.domain.Phase;
import org.motechproject.whp.common.util.WHPDateUtil;
import org.motechproject.whp.patient.alerts.processor.CumulativeMissedDosesCalculator;
import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.patient.domain.alerts.PatientAlerts;
import org.motechproject.whp.reports.contract.enums.YesNo;
import org.motechproject.whp.reports.contract.patient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.whp.common.util.WHPDateUtil.toSqlDate;

@Component
public class PatientReportingRequestMapper {

    private CumulativeMissedDosesCalculator cumulativeMissedDosesCalculator;

    @Autowired
    public PatientReportingRequestMapper(CumulativeMissedDosesCalculator cumulativeMissedDosesCalculator) {
        this.cumulativeMissedDosesCalculator = cumulativeMissedDosesCalculator;
    }

    public PatientDTO mapToReportingRequest(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(patient.getFirstName());
        patientDTO.setLastName(patient.getLastName());
        patientDTO.setGender(patient.getGender().name());
        patientDTO.setOnActiveTreatment(YesNo.value(patient.isOnActiveTreatment()).code());
        patientDTO.setPatientId(patient.getPatientId());
        patientDTO.setPatientStatus(patient.getStatus().name());
        patientDTO.setPhi(patient.getPhi());
        patientDTO.setPhoneNumber(patient.getPhoneNumber());

        mapPatientAddress(patient, patientDTO);
        mapTherapies(patient, patientDTO);
        mapPatientAlerts(patient, patientDTO);
        mapCumulativeMissedDoses(patient, patientDTO);

        return patientDTO;
    }

    private void mapCumulativeMissedDoses(Patient patient, PatientDTO patientDTO) {
        TherapyDTO currentTherapyDTO = patientDTO.getTherapies().get(patientDTO.getTherapies().size() - 1);
        int cumulativeMissedDoses = cumulativeMissedDosesCalculator.getCumulativeMissedDoses(patient);
        currentTherapyDTO.setCumulativeMissedDoses(cumulativeMissedDoses);
    }

    private void mapPatientAlerts(Patient patient, PatientDTO patientDTO) {
        PatientAlertsDTO patientAlertsDTO = new PatientAlertsDTO();
        PatientAlerts patientAlerts = patient.getPatientAlerts();

        patientAlertsDTO.setAdherenceMissingWeeks((int) patientAlerts.adherenceMissingAlert().getValue());
        patientAlertsDTO.setAdherenceMissingWeeksAlertSeverity(patientAlerts.adherenceMissingAlert().getAlertSeverity());
        patientAlertsDTO.setAdherenceMissingWeeksAlertDate(WHPDateUtil.toSqlDate(patientAlerts.adherenceMissingAlert().getAlertDate()));

        patientAlertsDTO.setCumulativeMissedDoses((int) patientAlerts.cumulativeMissedDoseAlert().getValue());
        patientAlertsDTO.setCumulativeMissedDosesAlertSeverity(patientAlerts.cumulativeMissedDoseAlert().getAlertSeverity());
        patientAlertsDTO.setCumulativeMissedDosesAlertDate(WHPDateUtil.toSqlDate(patientAlerts.cumulativeMissedDoseAlert().getAlertDate()));

        patientAlertsDTO.setTreatmentNotStarted((int) patientAlerts.treatmentNotStartedAlert().getValue());
        patientAlertsDTO.setTreatmentNotStartedAlertSeverity(patientAlerts.treatmentNotStartedAlert().getAlertSeverity());
        patientAlertsDTO.setTreatmentNotStartedAlertDate(WHPDateUtil.toSqlDate(patientAlerts.treatmentNotStartedAlert().getAlertDate()));

        patientDTO.setPatientAlerts(patientAlertsDTO);
    }

    private void mapPatientAddress(Patient patient, PatientDTO patientDTO) {
        AddressDTO patientAddress = new AddressDTO();
        Address address = patient.getCurrentTreatment().getPatientAddress();
        patientAddress.setBlock(address.getAddress_block());
        patientAddress.setDistrict(address.getAddress_district());
        patientAddress.setLandmark(address.getAddress_landmark());
        patientAddress.setLocation(address.getAddress_location());
        patientAddress.setState(address.getAddress_state());
        patientAddress.setVillage(address.getAddress_village());

        patientDTO.setPatientAddress(patientAddress);
    }

    private void mapTherapies(Patient patient, PatientDTO patientDTO) {
        List<Therapy> therapies = patient.getAllTherapiesWithCurrentTherapy();
        List<TherapyDTO> therapyDTOs = new ArrayList<>();

        for(Therapy therapy : therapies){
            TherapyDTO therapyDTO = new TherapyDTO();
            therapyDTO.setTherapyId(therapy.getUid());
            therapyDTO.setCloseDate(toSqlDate(therapy.getCloseDate()));
            therapyDTO.setStartDate(toSqlDate(therapy.getStartDate()));
            therapyDTO.setCreationDate(WHPDateUtil.toSqlDate(therapy.getCreationDate()));
            therapyDTO.setCurrentPhase(therapy.getCurrentPhaseName());
            therapyDTO.setDiseaseClass(getDiseaseClass(therapy));
            therapyDTO.setPatientAge(therapy.getPatientAge());
            therapyDTO.setStatus(therapy.getStatus().name());

            therapyDTO.setCpStartDate(WHPDateUtil.toSqlDate(therapy.getPhaseStartDate(Phase.CP)));
            therapyDTO.setIpStartDate(WHPDateUtil.toSqlDate(therapy.getPhaseStartDate(Phase.IP)));
            therapyDTO.setEipStartDate(WHPDateUtil.toSqlDate(therapy.getPhaseStartDate(Phase.EIP)));

            therapyDTO.setCpEndDate(WHPDateUtil.toSqlDate(therapy.getPhaseEndDate(Phase.CP)));
            therapyDTO.setIpEndDate(WHPDateUtil.toSqlDate(therapy.getPhaseEndDate(Phase.IP)));
            therapyDTO.setEipEndDate(WHPDateUtil.toSqlDate(therapy.getPhaseEndDate(Phase.EIP)));

            therapyDTO.setCpPillsTaken(therapy.getDosesTakenForPhase(Phase.CP));
            therapyDTO.setIpPillsTaken(therapy.getDosesTakenForPhase(Phase.IP));
            therapyDTO.setEipPillsTaken(therapy.getDosesTakenForPhase(Phase.EIP));

            therapyDTO.setCpPillsRemaining(therapy.getRemainingDoses(Phase.CP));
            therapyDTO.setIpPillsRemaining(therapy.getRemainingDoses(Phase.IP));
            therapyDTO.setEipPillsRemaining(therapy.getRemainingDoses(Phase.EIP));

            therapyDTO.setCpTotalDoses(therapy.getTotalDoesIn(Phase.CP));
            therapyDTO.setIpTotalDoses(therapy.getTotalDoesIn(Phase.IP));
            therapyDTO.setEipTotalDoses(therapy.getTotalDoesIn(Phase.EIP));

            if(therapy.getTreatmentCategory() != null){
                therapyDTO.setTreatmentCategory(therapy.getTreatmentCategory().getName());
            }

            therapyDTO.setCurrentTherapy(YesNo.No.code());

            therapyDTO.setTreatments(mapTreatmentDTOs(therapy.getAllTreatments()));

            therapyDTOs.add(therapyDTO);
        }

        setCurrentTherapyFlagForLastTherapy(therapyDTOs);
        List<TreatmentDTO> treatmentsForCurrentTherapy = lastTherapy(therapyDTOs).getTreatments();
        setCurrentTreatmentFlagForLastTreatment(treatmentsForCurrentTherapy);

        patientDTO.setTherapies(therapyDTOs);
    }

    private String getDiseaseClass(Therapy therapy) {
        if(therapy.getDiseaseClass() == null)
            return null;
        return therapy.getDiseaseClass().value();
    }

    private TherapyDTO lastTherapy(List<TherapyDTO> therapyDTOs) {
        return therapyDTOs.get(therapyDTOs.size() - 1);
    }

    private void setCurrentTherapyFlagForLastTherapy(List<TherapyDTO> therapyDTOs) {
        lastTherapy(therapyDTOs).setCurrentTherapy(YesNo.Yes.code());
    }

    private void setCurrentTreatmentFlagForLastTreatment(List<TreatmentDTO> treatmentsForCurrentTherapy) {
        treatmentsForCurrentTherapy.get(treatmentsForCurrentTherapy.size() - 1).setCurrentTreatment(YesNo.Yes.code());
    }

    private List<TreatmentDTO> mapTreatmentDTOs(List<Treatment> allTreatments) {

        List<TreatmentDTO> treatmentDTOs = new ArrayList<>();
        for (Treatment treatment : allTreatments){
            TreatmentDTO treatmentDTO = new TreatmentDTO();
            treatmentDTO.setProviderId(treatment.getProviderId());
            treatmentDTO.setProviderDistrict(treatment.getProviderDistrict());
            treatmentDTO.setTbId(treatment.getTbId());
            treatmentDTO.setStartDate(WHPDateUtil.toSqlDate(treatment.getStartDate()));
            treatmentDTO.setEndDate(WHPDateUtil.toSqlDate(treatment.getEndDate()));
            treatmentDTO.setTreatmentOutcome(getTreatmentOutcome(treatment));
            treatmentDTO.setPatientType(getPatientType(treatment));
            treatmentDTO.setTbRegistrationNumber(treatment.getTbRegistrationNumber());
            treatmentDTO.setPreTreatmentSmearTestResult(getPreTreatmentSmearTestResult(treatment));
            treatmentDTO.setPreTreatmentWeight(getPreTreatmentWeight(treatment));
            treatmentDTO.setIsPaused(YesNo.value(treatment.isPaused()).code());
            treatmentDTO.setPausedDate(WHPDateUtil.toSqlDate(treatment.getInterruptions().getPauseDateForOngoingInterruption()));
            treatmentDTO.setReasonsForPause(treatment.getInterruptions().getPauseReasonForOngoingInterruption());
            treatmentDTO.setTotalPausedDuration(treatment.getInterruptions().totalPausedDuration());
            treatmentDTO.setCurrentTreatment(YesNo.No.code());
            treatmentDTO.setCloseTreatmentRemarks(treatment.getCloseTreatmentRemarks());
            treatmentDTO.setCreationDate(WHPDateUtil.toSqlTimestamp(treatment.getCreationDate()));
            treatmentDTO.setTreatmentDetails(mapTreatmentDetails(treatment.getTreatmentDetails()));
            treatmentDTOs.add(treatmentDTO);
        }

        return treatmentDTOs;
    }

    private TreatmentDetailsDTO mapTreatmentDetails(TreatmentDetails treatmentDetails) {
        TreatmentDetailsDTO treatmentDetailsDTO = new TreatmentDetailsDTO();
        treatmentDetailsDTO.setDistrictWithCode(treatmentDetails.getDistrictWithCode());
        treatmentDetailsDTO.setTbUnitWithCode(treatmentDetails.getTbUnitWithCode());
        treatmentDetailsDTO.setEpSite(treatmentDetails.getEpSite());
        treatmentDetailsDTO.setOtherInvestigations(treatmentDetails.getOtherInvestigations());
        treatmentDetailsDTO.setPreviousTreatmentHistory(treatmentDetails.getPreviousTreatmentHistory());
        treatmentDetailsDTO.setHivStatus(treatmentDetails.getHivStatus());
        treatmentDetailsDTO.setHivTestDate(toSqlDate(treatmentDetails.getHivTestDate()));
        treatmentDetailsDTO.setMembersBelowSixYears(treatmentDetails.getMembersBelowSixYears());
        treatmentDetailsDTO.setPhcReferred(treatmentDetails.getPhcReferred());
        treatmentDetailsDTO.setProviderName(treatmentDetails.getProviderName());
        treatmentDetailsDTO.setDotCentre(treatmentDetails.getDotCentre());
        treatmentDetailsDTO.setProviderType(treatmentDetails.getProviderType());
        treatmentDetailsDTO.setCmfDoctor(treatmentDetails.getCmfDoctor());
        treatmentDetailsDTO.setContactPersonName(treatmentDetails.getContactPersonName());
        treatmentDetailsDTO.setContactPersonPhoneNumber(treatmentDetails.getContactPersonPhoneNumber());
        treatmentDetailsDTO.setXpertTestResult(treatmentDetails.getXpertTestResult());
        treatmentDetailsDTO.setXpertDeviceNumber(treatmentDetails.getXpertDeviceNumber());
        treatmentDetailsDTO.setXpertTestDate(toSqlDate(treatmentDetails.getXpertTestDate()));
        treatmentDetailsDTO.setRifResistanceResult(treatmentDetails.getRifResistanceResult());
        return treatmentDetailsDTO;
    }

    private Double getPreTreatmentWeight(Treatment treatment) {
        if(treatment.getPreTreatmentWeightRecord() == null){
            return null;
        }
        return treatment.getPreTreatmentWeightRecord().getWeight();
    }

    private String getPreTreatmentSmearTestResult(Treatment treatment) {
        if(treatment.getPreTreatmentSmearTestResult() == null)
            return null;

        return treatment.getPreTreatmentSmearTestResult().value();
    }

    private String getPatientType(Treatment treatment) {
        if(treatment.getPatientType() == null){
            return null;
        }
        return treatment.getPatientType().value();
    }

    private String getTreatmentOutcome(Treatment treatment) {
        TreatmentOutcome treatmentOutcome = treatment.getTreatmentOutcome();
        return treatmentOutcome != null ? treatmentOutcome.getOutcome() : null;
    }

}
