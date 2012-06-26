package org.motechproject.whp.controller;

import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.security.service.MotechAuthenticationService;
import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.refdata.domain.District;
import org.motechproject.whp.refdata.objectcache.AllDistrictsCache;
import org.motechproject.whp.uimodel.ProviderRow;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.refdata.domain.WHPConstants;
import org.motechproject.whp.user.service.ProviderService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProviderControllerTest {

    @Mock
    Model uiModel;
    @Mock
    HttpServletRequest request;
    @Mock
    private ProviderService providerService;
    @Mock
    private AllDistrictsCache allDistricts;

    Provider provider1 = new Provider("aa", "9845678761", "districtA",
            DateTimeFormat.forPattern(WHPConstants.DATE_TIME_FORMAT).parseDateTime("12/01/2012 10:10:10"));
    Provider provider2 = new Provider("ab", "9845678761", "districtB",
            DateTimeFormat.forPattern(WHPConstants.DATE_TIME_FORMAT).parseDateTime("12/01/2012 10:10:10"));
    Provider provider3 = new Provider("ac", "9845678761", "districtA",
            DateTimeFormat.forPattern(WHPConstants.DATE_TIME_FORMAT).parseDateTime("12/01/2012 10:10:10"));

    private ProviderController providerController;

    @Before
    public void setup() {
        initMocks(this);

        MotechUser user1 = mock(MotechUser.class);
        when(user1.getUserName()).thenReturn(provider1.getProviderId());
        MotechUser user2 = mock(MotechUser.class);
        when(user1.isActive()).thenReturn(true);

        when(user2.getUserName()).thenReturn(provider2.getProviderId());
        when(user2.isActive()).thenReturn(true);

        MotechUser user3 = mock(MotechUser.class);
        when(user3.getUserName()).thenReturn(provider3.getProviderId());
        when(user3.isActive()).thenReturn(true);

        Map<String, MotechUser> motechUsers = new HashMap();
        motechUsers.put(user1.getUserName(), user1);
        motechUsers.put(user2.getUserName(), user2);
        motechUsers.put(user3.getUserName(), user3);

        when(providerService.fetchAllWebUsers()).thenReturn(motechUsers);

        ArrayList<District> districts = new ArrayList<>();
        districts.add(new District("districtA"));
        districts.add(new District("districtB"));
        when(allDistricts.getAll()).thenReturn(districts);

        providerController = new ProviderController(providerService, allDistricts);
    }

    @Test
    public void shouldLoadProviderSearchPage_verifyViewMappingForGET() throws Exception {
        String viewName = providerController.loadProviderSearchPage(uiModel);
        assertEquals("provider/list", viewName);
    }

    @Test
    public void shouldLoadProviderSearchPage_withProvidersByFirstDistrict() throws Exception {
        List<Provider> expectedList = Arrays.asList(provider1, provider3);
        when(providerService.fetchBy("districtA", "")).thenReturn(expectedList);

        providerController.loadProviderSearchPage(uiModel);
        verify(uiModel).addAttribute(eq(providerController.PROVIDER_LIST), eq(wrapIntoProviderRows(expectedList)));
    }

    @Test
    public void shouldLoadProviderSearchPage_verifyViewMappingForPOST() throws Exception {
        String viewName = providerController.searchMatchingProviders("districtA", "providerId", uiModel);
        assertEquals("provider/list", viewName);
    }

    private List<ProviderRow> wrapIntoProviderRows(List<Provider> providerList) {
        List<ProviderRow> providerRows = new ArrayList();
        for(Provider provider : providerList) {
            providerRows.add(new ProviderRow(provider, true));
        }
        return providerRows;
    }
}