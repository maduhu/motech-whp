package org.motechproject.whp.remedi.service;

import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.service.RemediProperties;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.remedi.model.ContainerRegistrationModel;
import org.motechproject.whp.remedi.util.RemediXmlRequestBuilder;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RemediServiceTest {
    @Mock
    RemediXmlRequestBuilder remediXmlRequestBuilder;
    @Mock
    HttpClientService httpClientService;
    @Mock
    RemediProperties remediProperties;
    RemediService remediService;
    private final String remediUrl = "remediUrl";

    @Before
    public void setUp() {
        initMocks(this);
        when(remediProperties.getUrl()).thenReturn(remediUrl);
        remediService = new RemediService(httpClientService, remediXmlRequestBuilder, remediProperties);
    }

    @Test
    public void shouldSendContainerRegistrationDetails() throws IOException, TemplateException {
        ContainerRegistrationModel containerRegistrationModel = new ContainerRegistrationModel("", "", SputumTrackingInstance.PreTreatment, DateUtil.now());
        String xmlRequestToBeSent = "xml Request";

        when(remediXmlRequestBuilder.buildTemplatedXmlFor(containerRegistrationModel)).thenReturn(xmlRequestToBeSent);

        remediService.sendContainerRegistrationResponse(containerRegistrationModel);

        verify(httpClientService).post(remediUrl, xmlRequestToBeSent);
    }
}
