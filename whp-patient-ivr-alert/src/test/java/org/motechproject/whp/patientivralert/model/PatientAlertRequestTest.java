package org.motechproject.whp.patientivralert.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class PatientAlertRequestTest {

    @Test
    public void shouldConvertToJSON() throws IOException {
        PatientAlertRequest patientAlertRequest = new PatientAlertRequest();
        patientAlertRequest.setCallType("callType");
        patientAlertRequest.setBatchId("batchId");
        patientAlertRequest.setRequestId("requestId");
        patientAlertRequest.setMessageId("messageId");
        patientAlertRequest.setData(asList(new PatientAdherenceRecord("patient1", "1234567890", 1), new PatientAdherenceRecord("patient2", "1234567891", 1)));

        String json = new ObjectMapper().writer().writeValueAsString(patientAlertRequest);

        String expectedJson = "{" +
                "\"requestId\":\"requestId\"," +
                "\"batchId\":\"batchId\"," +
                "\"messageId\":\"messageId\"," +
                "\"data\":[" +
                "{" +
                "\"patientId\":\"patient1\"," +
                "\"msisdn\":\"1234567890\"," +
                "\"missing_weeks\":1" +
                "}," +
                "{" +
                "\"patientId\":\"patient2\"," +
                "\"msisdn\":\"1234567891\"," +
                "\"missing_weeks\":1" +
                "}" +
                "]," +
                "\"call_type\":\"callType\"" +
                "}";
        
        assertEquals(expectedJson, json);
    }
}
