package org.motechproject.whp.patient.domain;

import org.junit.Test;
import org.motechproject.whp.refdata.domain.Phase;
import org.motechproject.whp.refdata.domain.TreatmentCategory;

import static junit.framework.Assert.assertEquals;

public class PhaseRecordTest {

    @Test
    public void shouldReturnRemainingDosesInPhase() {
        TreatmentCategory category = new TreatmentCategory();
        category.setNumberOfDosesInIP(24);

        PhaseRecord phase = new PhaseRecord(Phase.IP);

        phase.setNumberOfDosesTaken(11);

        assertEquals(13, phase.remainingDoses(category));
    }
}
