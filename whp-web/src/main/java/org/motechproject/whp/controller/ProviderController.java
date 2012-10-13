package org.motechproject.whp.controller;

import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.common.domain.District;
import org.motechproject.whp.common.repository.AllDistricts;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.user.service.ProviderService;
import org.motechproject.whp.user.uimodel.ProviderRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/providers")
public class ProviderController extends BaseWebController {

    private ProviderService providerService;
    private AllDistricts allDistrictsCache;

    private static final String PROVIDER_LIST = "providerList";
    public static final String DISTRICT_LIST = "districts";
    public static final String PROVIDER_ID = "selectedProvider";
    public static final String SELECTED_DISTRICT = "selectedDistrict";

    @Autowired
    public ProviderController(ProviderService providerService, AllDistricts allDistrictsCache) {
        this.providerService = providerService;
        this.allDistrictsCache = allDistrictsCache;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String loadProviderSearchPage(Model uiModel, HttpServletRequest request) throws IOException {
        List<District> districtList = allDistrictsCache.getAll();
        String district = districtList.get(0).getName();
        String providerId = "";
        initQueryModel(uiModel, district, providerId);

        return "provider/list";
    }


    @RequestMapping(value = "byDistrict/{districtName}", method = RequestMethod.GET)
    public String allProvidersForDistrict(@PathVariable("districtName") String districtName, Model uiModel) {
        prepareResultsModel(uiModel, providerService.fetchBy(districtName));
        return "provider/listByDistrict";
    }

    private void initQueryModel(Model uiModel, String districtName, String providerId) {
        uiModel.addAttribute(DISTRICT_LIST, allDistrictsCache.getAll());
        uiModel.addAttribute(SELECTED_DISTRICT, districtName);
        uiModel.addAttribute(PROVIDER_ID, providerId);
    }

    private void prepareResultsModel(Model uiModel, List<Provider> matchingProviders) {
        Map<String, MotechUser> users = providerService.fetchAllWebUsers();

        List<ProviderRow> providerRows = new ArrayList<ProviderRow>();
        for (Provider provider : matchingProviders) {
            MotechUser motechUser = users.get(provider.getProviderId());
            if (motechUser != null) {
                providerRows.add(new ProviderRow(provider, motechUser.isActive()));
            }
        }
        uiModel.addAttribute(PROVIDER_LIST, providerRows);
    }
}
