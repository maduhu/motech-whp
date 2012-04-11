package org.motechproject.whp.adherence.domain;

import com.sun.istack.internal.NotNull;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'DosageLog'")
public class DosageLog extends MotechBaseDataObject {

    @NotNull
    private String patientId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    @NotNull
    private int doseTakenCount;
    @NotNull
    private int idealDoseCount;

    private Map<String, String> metaData = new HashMap<String, String>();

    public DosageLog() {
        super();
    }

    public DosageLog(String patientId, LocalDate fromDate, LocalDate toDate, int doseTakenCount, int idealDoseCount, Map<String, String> metaData) {
        super();
        setPatientId(patientId);
        setFromDate(fromDate);
        setToDate(toDate);
        setDoseTakenCount(doseTakenCount);
        setIdealDoseCount(idealDoseCount);
        setMetaData(metaData);
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public DosageLog setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public DosageLog setToDate(LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public int getDoseTakenCount() {
        return doseTakenCount;
    }

    public DosageLog setDoseTakenCount(int doseTakenCount) {
        this.doseTakenCount = doseTakenCount;
        return this;
    }

    public int getIdealDoseCount() {
        return idealDoseCount;
    }

    public DosageLog setIdealDoseCount(int idealDoseCount) {
        this.idealDoseCount = idealDoseCount;
        return this;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public DosageLog setMetaData(Map<String, String> metaData) {
        if (metaData != null)
            this.metaData = metaData;
        return this;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void addMetaData(Map<String, String> metaData) {
        this.metaData.putAll(metaData);
    }

}
