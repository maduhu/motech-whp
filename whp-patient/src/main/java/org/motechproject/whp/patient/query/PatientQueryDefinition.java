package org.motechproject.whp.patient.query;

import lombok.Getter;
import org.motechproject.couchdb.lucene.query.QueryDefinition;
import org.motechproject.couchdb.lucene.query.field.Field;
import org.motechproject.couchdb.lucene.query.field.QueryField;
import org.motechproject.couchdb.lucene.query.field.RangeField;
import org.motechproject.whp.common.domain.alerts.PatientAlertType;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.couchdb.lucene.query.field.FieldType.DATE;
import static org.motechproject.couchdb.lucene.query.field.FieldType.STRING;

public class PatientQueryDefinition implements QueryDefinition {

    public static final String ALERT_SEVERITY = "AlertSeverity";
    public static final String ALERT_VALUE = "AlertValue";
    public static final String ALERT_DATE = "AlertDate";

    @Getter
    protected static final QueryField isActive = new QueryField("isActive", STRING);
    protected static final QueryField patientId = new QueryField("patientId", STRING);
    protected static final QueryField providerId = new QueryField("providerId", STRING);
    protected static final QueryField providerDistrict = new QueryField("providerDistrict", STRING);
    protected static final QueryField treatmentCategory = new QueryField("treatmentCategory", STRING);
    protected static final QueryField cumulativeMissedDoses = new QueryField("cumulativeMissedDoses", STRING);
    protected static final QueryField adherenceMissingWeeks = new QueryField("adherenceMissingWeeks", STRING);
    protected static final QueryField hasAlerts = new QueryField("hasAlerts", STRING);
    private static final String FROM = "From";
    private static final String TO = "To";

    @Override
    public List<Field> fields() {
        List<Field> fields = new ArrayList<>();
        fields.add(isActive);
        fields.add(patientId);
        fields.add(providerId);
        fields.add(providerDistrict);
        fields.add(treatmentCategory);
        fields.add(cumulativeMissedDoses);
        fields.add(adherenceMissingWeeks);
        fields.add(hasAlerts);

        for(PatientAlertType alertType : PatientAlertType.values()){
            fields.add(new QueryField(alertType.name() + ALERT_SEVERITY, STRING));
            fields.add(new QueryField(alertType.name() + ALERT_VALUE, STRING));
            fields.add(new RangeField(ALERT_DATE, DATE, ALERT_DATE + FROM, ALERT_DATE + TO));
        }

        return fields;
    }

    @Override
    public String viewName() {
        return "PatientDashboard";
    }

    @Override
    public String searchFunctionName() {
        return "findPatientsByCriteria";
    }

    @Override
    public String indexFunction() {
        return "function(doc) { " +
                "if(doc.type == 'Patient') { " +
                    "var index=new Document(); " +
                    "index.add(doc.patientId, {field: 'patientId'}); " +
                    "index.add(doc.onActiveTreatment, {field: 'isActive'}); " +
                    "index.add(doc.patientAlerts.hasAlerts, {field: 'hasAlerts'}); " +
                    "index.add(doc.currentTherapy.currentTreatment.providerId, {field: 'providerId'}); " +
                    "index.add(doc.currentTherapy.currentTreatment.providerDistrict, {field: 'providerDistrict'}); " +
                    "index.add(doc.currentTherapy.treatmentCategory.code, {field: 'treatmentCategory'}); " +

                    "var alertTypes = Object.keys(doc.patientAlerts.alerts); " +
                    " if(doc.patientAlerts.alerts['CumulativeMissedDoses']) { " +
                        "index.add(doc.patientAlerts.alerts['CumulativeMissedDoses'].value, {field: 'cumulativeMissedDoses'}); " +
                    " } "+

                    " if(doc.patientAlerts.alerts['AdherenceMissing']) { " +
                        "index.add(doc.patientAlerts.alerts['AdherenceMissing'].value, {field: 'adherenceMissingWeeks'}); " +
                    " } "+

                    "for (var i=0; i<alertTypes.length ;i++) { " +
                        "var alertType =  alertTypes[i]; " +
                        "index.add(doc.patientAlerts.alerts[alertType].alertSeverity, {field: alertType + '"+ ALERT_SEVERITY + "'}); "+
                        "index.add(doc.patientAlerts.alerts[alertType].value, {field: alertType + '"+ ALERT_VALUE + "'}); "+
                        "index.add(doc.patientAlerts.alerts[alertType].alertDate, {type : 'date', field: '"+ ALERT_DATE + "'}); "+
                    "} "+

                    "return index;" +
                " }}";
    }

    public static String alertDateParamForType(PatientAlertType alertType) {
        return alertType.name() + ALERT_DATE;
    }

    public static String alertDateFromParam() {
        return ALERT_DATE + FROM;
    }

    public static String alertDateToParam() {
        return ALERT_DATE + TO;
    }

    public static String alertSeverityParam(PatientAlertType alertType) {
        return alertType.name() + ALERT_SEVERITY;
    }

    public static String alertStatusFieldName() {
        return hasAlerts.getName();
    }
}