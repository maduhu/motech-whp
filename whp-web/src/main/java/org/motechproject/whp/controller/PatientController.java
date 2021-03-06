package org.motechproject.whp.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.applicationservice.orchestrator.TreatmentUpdateOrchestrator;
import org.motechproject.whp.common.domain.Phase;
import org.motechproject.whp.common.domain.WHPConstants;
import org.motechproject.whp.common.util.WHPDate;
import org.motechproject.whp.mapper.PatientInfoMapper;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.remarks.ProviderRemarksService;
import org.motechproject.whp.treatmentcard.service.TreatmentCardService;
import org.motechproject.whp.uimodel.PatientInfo;
import org.motechproject.whp.uimodel.PhaseStartDates;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.user.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.flash.Flash.in;
import static org.motechproject.flash.Flash.out;
import static org.motechproject.whp.common.domain.TreatmentWeekInstance.currentAdherenceCaptureWeek;
import static org.motechproject.whp.common.util.WHPDate.date;

@Controller
@RequestMapping(value = "/patients")
public class PatientController extends BaseWebController {

    public static final String PATIENT_LIST = "patientList";

    private ProviderService providerService;
    private PatientService patientService;
    private TreatmentUpdateOrchestrator treatmentUpdateOrchestrator;
    private AbstractMessageSource messageSource;
    private TreatmentCardService treatmentCardService;
    private ProviderRemarksService providerRemarksService;

    @Autowired
    public PatientController(PatientService patientService,
                             TreatmentCardService treatmentCardService,
                             TreatmentUpdateOrchestrator treatmentUpdateOrchestrator,
                             ProviderService providerService,
                             @Qualifier("messageBundleSource")
                             AbstractMessageSource messageSource,
                             ProviderRemarksService providerRemarksService) {

        this.patientService = patientService;
        this.treatmentCardService = treatmentCardService;
        this.providerService = providerService;
        this.treatmentUpdateOrchestrator = treatmentUpdateOrchestrator;
        this.messageSource = messageSource;
        this.providerRemarksService = providerRemarksService;
    }

    @RequestMapping(value = "listByProvider", method = RequestMethod.GET)
    public String listByProvider(Model uiModel, HttpServletRequest request) {
        String idOfLoggedInProvider = loggedInUser(request).getUserName();
        List<Patient> patientsForProvider = patientService.getAllWithActiveTreatmentForProvider(idOfLoggedInProvider);
        prepareModelForListView(uiModel, patientsForProvider);
        passAdherenceSavedMessageToListView(uiModel, request);
        return "patient/listByProvider";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{patientId}/updateFlag")
    @ResponseBody
    public String updateFlag(@PathVariable("patientId") String patientId, @RequestParam("value") Boolean value) {
        return (patientService.updateFlag(patientId, value)) ? "success" : "failure";
    }

    @RequestMapping(value = "print/{id}", method = RequestMethod.GET)
    public String print(@PathVariable("id") String patientId, Model uiModel, HttpServletRequest request) {
        Patient patient = patientService.findByPatientId(patientId);
        setupPrintDashboardModel(uiModel, patient);
        return "patient/print";
    }

    public static String redirectToPatientDashboardURL(String patientId) {
        return "redirect:/patients/show?patientId=" + patientId;
    }

    @RequestMapping(value = "adjustPhaseStartDates", method = RequestMethod.POST)
    public String adjustPhaseStartDates(@RequestParam("patientId") String patientId, PhaseStartDates phaseStartDates, HttpServletRequest httpServletRequest) {
        treatmentUpdateOrchestrator.adjustPhaseStartDates(
                patientId,
                date(phaseStartDates.getIpStartDate()).date(),
                date(phaseStartDates.getEipStartDate()).date(),
                date(phaseStartDates.getCpStartDate()).date()
        );
        flashOutDateUpdatedMessage(patientId, phaseStartDates, httpServletRequest);
        return String.format("redirect:/patients/show?patientId=%s", patientId);
    }

    private void passAdherenceSavedMessageToListView(Model uiModel, HttpServletRequest request) {
        String message = in(WHPConstants.NOTIFICATION_MESSAGE, request);
        if (StringUtils.isNotEmpty(message)) {
            uiModel.addAttribute(WHPConstants.NOTIFICATION_MESSAGE, message);
        }
    }

    private void prepareModelForListView(Model uiModel, List<Patient> patientsForProvider) {
        List<PatientInfo> patientList = new ArrayList<>();
        PatientInfoMapper patientInfoMapper = new PatientInfoMapper();
        for (Patient patient : patientsForProvider) {
            PatientInfo patientInfo = patientInfoMapper.map(patient);
            patientList.add(patientInfo);
        }
        uiModel.addAttribute(PATIENT_LIST, patientList);
        uiModel.addAttribute("weekStartDate", WHPDate.date(currentAdherenceCaptureWeek().startDate()).value());
        uiModel.addAttribute("weekEndDate", WHPDate.date(currentAdherenceCaptureWeek().endDate()).value());

    }

    private void flashOutDateUpdatedMessage(String patientId, PhaseStartDates phaseStartDates, HttpServletRequest httpServletRequest) {
        String ipStartDate = dateMessage(phaseStartDates.getIpStartDate());
        String eipStartDate = dateMessage(phaseStartDates.getEipStartDate());
        String cpStartDate = dateMessage(phaseStartDates.getCpStartDate());
        out(WHPConstants.NOTIFICATION_MESSAGE, messageSource.getMessage("dates.changed.message", new Object[]{patientId, ipStartDate, eipStartDate, cpStartDate}, Locale.ENGLISH), httpServletRequest);
    }

    private void setUpModelForRemarks(Model uiModel, Patient patient) {
        uiModel.addAttribute("cmfAdminRemarks", patientService.getCmfAdminRemarks(patient));
        uiModel.addAttribute("providerRemarks", providerRemarksService.getRemarks(patient));
    }

    private void setupPrintDashboardModel(Model uiModel, Patient patient) {
        Treatment currentTreatment = patient.getCurrentTherapy().getCurrentTreatment();
        Provider provider = providerService.findByProviderId(currentTreatment.getProviderId());
        PatientInfoMapper patientInfoMapper = new PatientInfoMapper();
        uiModel.addAttribute("patient", patientInfoMapper.map(patient, provider));
        uiModel.addAttribute("treatmentCard", treatmentCardService.treatmentCard(patient));
    }

    private String dateMessage(String date) {
        return isBlank(date) ? "Not Set" : date;
    }

    @RequestMapping(value = "transitionPhase/{id}", method = RequestMethod.GET)
    public String update(@PathVariable("id") String patientId, @RequestParam("to") String phaseName) {
        treatmentUpdateOrchestrator.setNextPhase(patientId, Phase.valueOf(phaseName));
        return String.format("redirect:/patients/show?patientId=%s", patientId);
    }

    @RequestMapping(value = "addRemark/{id}", method = RequestMethod.POST)
    public String addRemark(Model uiModel, @PathVariable("id") String patientId, @RequestParam("patientRemark") String remark, HttpServletRequest httpServletRequest) {
        MotechUser authenticatedUser = loggedInUser(httpServletRequest);
        Patient patient = patientService.findByPatientId(patientId);
        patientService.addRemark(patient, remark, authenticatedUser.getUserName());
        setUpModelForRemarks(uiModel, patient);
        return "patient/remarks";
    }
}
