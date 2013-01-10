package org.motechproject.whp.adherence.mapping;


import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.whp.adherence.contract.AdherenceRecord;
import org.motechproject.whp.adherence.domain.AdherenceLog;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class AdherenceLogMapperTest {
    @Test
    public void shouldMapAdherenceDataToDomainObject() {
        AdherenceRecord datum = new AdherenceRecord("ext_id", "treatment_id", LocalDate.now());
        datum.providerId("providerId");
        datum.district("district");
        datum.tbId("tbId");

        AdherenceLog log = new AdherenceLogMapper().map(datum);

        assertEquals(datum.externalId(), log.externalId());
        assertEquals(datum.district(), log.district());
        assertEquals(datum.treatmentId(), log.treatmentId());
        assertEquals(datum.doseDate(), log.doseDate());
        assertEquals(datum.tbId(), log.tbId());
        assertEquals(datum.providerId(), log.providerId());
    }

    @Test
    public void shouldMapListOfAdherenceDataToDomainObject() {
        AdherenceRecord datum1 = new AdherenceRecord("ext_id1", "treatment_id1", LocalDate.now());
        datum1.status(2);
        datum1.district("district1");
        AdherenceRecord datum2 = new AdherenceRecord("ext_id2", "treatment_id2", LocalDate.now());
        datum2.status(1);
        datum2.district("district2");

        List<AdherenceLog> adherenceLogs = new AdherenceLogMapper().map(asList(datum1, datum2));

        assertEquals(2, adherenceLogs.size());
        assertEquals(datum1.externalId(), adherenceLogs.get(0).externalId());
        assertEquals(datum1.status(), adherenceLogs.get(0).status());
        assertEquals(datum1.district(), adherenceLogs.get(0).district());
        assertEquals(datum2.externalId(), adherenceLogs.get(1).externalId());
        assertEquals(datum2.status(), adherenceLogs.get(1).status());
        assertEquals(datum2.district(), adherenceLogs.get(1).district());
    }
}
