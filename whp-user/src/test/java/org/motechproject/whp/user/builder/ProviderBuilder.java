package org.motechproject.whp.user.builder;

import org.motechproject.util.DateUtil;
import org.motechproject.whp.user.domain.Provider;

public class ProviderBuilder {

    private Provider provider;

    public ProviderBuilder() {
        provider = new Provider();
    }

    public ProviderBuilder withDefaults() {
        provider.setProviderId("providerId");
        provider.setLastModifiedDate(DateUtil.now());
        provider.setPrimaryMobile("1234567890");
        return this;
    }

    public ProviderBuilder withId(String id) {
        provider.setId(id);
        return this;
    }

    public ProviderBuilder withProviderId(String providerId) {
        provider.setProviderId(providerId);
        return this;
    }

    public static ProviderBuilder newProviderBuilder() {
        return new ProviderBuilder();
    }

    public Provider build() {
        return provider;
    }

    public ProviderBuilder withPrimaryMobileNumber(String primaryMobileNumber) {
        provider.setPrimaryMobile(primaryMobileNumber);
        return this;
    }

    public ProviderBuilder withDistrict(String districtName) {
        provider.setDistrict(districtName);
        return this;
    }
}
