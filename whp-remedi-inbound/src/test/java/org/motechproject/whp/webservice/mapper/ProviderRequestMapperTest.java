package org.motechproject.whp.webservice.mapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.whp.user.contract.ProviderRequest;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;


public class ProviderRequestMapperTest {

    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();

    ProviderRequestMapper providerRequestMapper;

    @Before
    public void setUp() {
        providerRequestMapper = new ProviderRequestMapper();
    }

    @Test
    public void shouldCreateProvider() {
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withDefaults().build();

        ProviderRequest provider = providerRequestMapper.map(providerWebRequest);

        assertEquals("providerid", provider.getProviderId());
        assertEquals("9880123456", provider.getPrimaryMobile());
        assertEquals("9880123457", provider.getSecondaryMobile());
        assertEquals("9880123458", provider.getTertiaryMobile());
        assertEquals("Patna", provider.getDistrict());
        assertEquals("12/01/2012 10:10:10", provider.getLastModifiedDate().toString(DATE_TIME_FORMAT));
    }

    @Test
    public void shouldThrowExceptionIfDateIsNull() {
        expectNullPointerException();
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withDefaults().withDate(null).build();

        providerRequestMapper.map(providerWebRequest);
    }

    private void expectNullPointerException() {
        exceptionThrown.expect(NullPointerException.class);
    }
}

