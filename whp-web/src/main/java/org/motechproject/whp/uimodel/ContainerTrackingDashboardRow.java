package org.motechproject.whp.uimodel;

import lombok.Data;
import org.joda.time.LocalDate;
import org.motechproject.whp.container.tracking.model.ContainerTrackingRecord;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.domain.LabResults;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.user.domain.Provider;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Data
public class ContainerTrackingDashboardRow {

    public static final String DIAGNOSIS_POSITIVE = "Positive";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private String containerId;
    private String containerIssuedOn;
    private String consultationOneDate;
    private String consultationTwoDate;
    private String consultationOneResult;
    private String consultationTwoResult;
    private String consultation;
    private String diagnosis;
    private String patientId;
    private String district;
    private String providerId;
    private String labName;
    private String containerStatus;
    private String reasonForClosure;

    public ContainerTrackingDashboardRow(ContainerTrackingRecord containerTrackingRecord) {
        Container container = containerTrackingRecord.getContainer();

        extractContainerInformation(container);
        extractPatientInformation(containerTrackingRecord.getPatient(), container.getTbId());
        extractProviderInformation(containerTrackingRecord.getProvider());
    }

    private void extractProviderInformation(Provider provider) {
        if (provider != null) {
            providerId = provider.getProviderId();
            district = provider.getDistrict();
        }
    }

    private void extractPatientInformation(Patient patient, String tbId) {
        if (patient != null) {
            patientId = patient.getPatientId();
            consultation = inDesiredFormat(patient.getTreatmentStartDate(tbId));
        }
    }

    private void extractContainerInformation(Container container) {
        containerId = container.getContainerId();
        containerIssuedOn = inDesiredFormat(container.getCreationTime().toLocalDate());
        containerStatus = container.getStatus().name();
        reasonForClosure = container.getReasonForClosure();
        populateLabResultsData(container.getLabResults());

        if (isNotBlank(container.getTbId()))
            diagnosis = DIAGNOSIS_POSITIVE;
    }

    private void populateLabResultsData(LabResults labResults) {
        if(labResults != null) {
            labName = labResults.getLabName();
            consultationOneDate = inDesiredFormat(labResults.getSmearTestDate1());
            consultationTwoDate = inDesiredFormat(labResults.getSmearTestDate2());
            consultationOneResult = labResults.getSmearTestResult1().name();
            consultationTwoResult = labResults.getSmearTestResult2().name();
        }
    }

    private String inDesiredFormat(LocalDate date) {
        return date.toString(DATE_FORMAT);
    }

    public String getAction()
    {
        return containerStatus.equals("Closed") ? "Open" : "Close";
    }
}
