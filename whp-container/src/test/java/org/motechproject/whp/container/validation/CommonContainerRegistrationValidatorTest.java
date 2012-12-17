package org.motechproject.whp.container.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.whp.common.error.ErrorWithParameters;
import org.motechproject.whp.container.contract.CmfAdminContainerRegistrationRequest;
import org.motechproject.whp.container.contract.ContainerRegistrationRequest;
import org.motechproject.whp.container.domain.ContainerId;
import org.motechproject.whp.container.domain.ContainerRegistrationMode;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.containermapping.service.ProviderContainerMappingService;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.user.service.ProviderService;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;
import static org.motechproject.whp.common.domain.ChannelId.WEB;
import static org.motechproject.whp.common.domain.RegistrationInstance.InTreatment;
import static org.motechproject.whp.container.domain.ContainerRegistrationMode.NEW_CONTAINER;
import static org.motechproject.whp.container.domain.ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER;

public class CommonContainerRegistrationValidatorTest {
    @Mock
    private ContainerService containerService;
    @Mock
    private ProviderService providerService;
    @Mock
    private ProviderContainerMappingService providerContainerMappingService;
    private CommonContainerRegistrationValidator registrationRequestValidator;
    private Provider validProvider;

    @Before
    public void setUp() {
        validProvider = new Provider("validProvider", "123", "dist", now());
        initMocks(this);
        when(providerService.findByProviderId(validProvider.getProviderId())).thenReturn(validProvider);
        registrationRequestValidator = new CommonContainerRegistrationValidator(containerService, providerService);
    }

    @Test
    public void shouldValidateDuplicateContainerId() {
        String containerID = "12345";
        ContainerRegistrationRequest registrationRequest = new ContainerRegistrationRequest(validProvider.getProviderId(), containerID, InTreatment.getDisplayText(), WEB.name(), null);
        when(containerService.exists(new ContainerId(validProvider.getProviderId(), containerID, ON_BEHALF_OF_PROVIDER).value())).thenReturn(true);

        List<ErrorWithParameters> errors = registrationRequestValidator.validate(registrationRequest);

        verify(containerService).exists(new ContainerId(validProvider.getProviderId(), containerID, ON_BEHALF_OF_PROVIDER).value());
        assertTrue(errors.contains(new ErrorWithParameters("container.already.registered.error")));
    }

    @Test
    public void shouldValidateDuplicateContainerIdForNewContainers() {
        String containerID = "12345678901";
        ContainerRegistrationRequest registrationRequest = new CmfAdminContainerRegistrationRequest(validProvider.getProviderId(), containerID, InTreatment.getDisplayText(), ContainerRegistrationMode.NEW_CONTAINER, WEB, null);
        when(containerService.exists(new ContainerId(validProvider.getProviderId(), containerID, NEW_CONTAINER).value())).thenReturn(true);

        List<ErrorWithParameters> errors = registrationRequestValidator.validate(registrationRequest);

        verify(containerService).exists(new ContainerId(validProvider.getProviderId(), containerID, NEW_CONTAINER).value());
        assertTrue(errors.contains(new ErrorWithParameters("container.already.registered.error")));
    }

    @Test
    public void shouldValidateInstance() {
        ContainerRegistrationRequest request = new ContainerRegistrationRequest(validProvider.getProviderId(), "12345", "invalid_instance", WEB.name(), null);
        List<ErrorWithParameters> invalidInstanceErrors = registrationRequestValidator.validate(request);
        assertTrue(invalidInstanceErrors.contains(new ErrorWithParameters("invalid.instance.error", "invalid_instance")));
    }

    @Test
    public void shouldValidatePresenceOfProviderId_whenProviderIdIsUndefined() {
        ContainerRegistrationRequest registrationRequest = new ContainerRegistrationRequest("", "12345", InTreatment.getDisplayText(), WEB.name(), null);
        List<ErrorWithParameters> validationErrors = registrationRequestValidator.validate(registrationRequest);
        assertTrue(validationErrors.contains(new ErrorWithParameters("provider.id.invalid.error", "")));
    }

    @Test
    public void shouldValidatePresenceOfProviderId_whenProviderExists() {
        String containerId = "11111";
        ContainerRegistrationRequest registrationRequest = new ContainerRegistrationRequest(validProvider.getProviderId(), containerId, InTreatment.getDisplayText(), WEB.name(), null);
        when(providerContainerMappingService.isValidContainerForProvider(validProvider.getProviderId(), containerId)).thenReturn(true);

        List<ErrorWithParameters> validationErrors = registrationRequestValidator.validate(registrationRequest);


        verify(providerService).findByProviderId(validProvider.getProviderId());
        assertTrue(validationErrors.isEmpty());
    }

    @Test
    public void shouldValidatePresenceOfProviderId_whenProviderDoesNotExist() {
        String unregisteredProviderId = "UnregisteredProviderId";
        ContainerRegistrationRequest registrationRequest = new ContainerRegistrationRequest(unregisteredProviderId, "11111", InTreatment.getDisplayText(), WEB.name(), null);
        List<ErrorWithParameters> validationErrors = registrationRequestValidator.validate(registrationRequest);

        verify(providerService).findByProviderId(unregisteredProviderId);
        assertTrue(!validationErrors.isEmpty());
        assertTrue(validationErrors.contains(new ErrorWithParameters("provider.not.registered.error", unregisteredProviderId)));
    }

    @Test
    public void shouldValidateContainerIdLength_onBehalfOfProvider() {
        String sixDigitContainerId = "123456";
        String fourDigitContainerId = "1234";

        ContainerRegistrationRequest registrationRequest = createContainerRegistrationRequest(sixDigitContainerId);
        List<ErrorWithParameters> validationErrors = registrationRequestValidator.validate(registrationRequest);
        assertTrue(!validationErrors.isEmpty());
        assertTrue(validationErrors.contains(new ErrorWithParameters("container.id.length.error", String.valueOf(ON_BEHALF_OF_PROVIDER.getValidContainerIdLength()))));

        registrationRequest = createContainerRegistrationRequest(fourDigitContainerId);
        validationErrors = registrationRequestValidator.validate(registrationRequest);
        assertTrue(!validationErrors.isEmpty());
        assertTrue(validationErrors.contains(new ErrorWithParameters("container.id.length.error", String.valueOf(ON_BEHALF_OF_PROVIDER.getValidContainerIdLength()))));
    }

    @Test
    public void shouldValidateContainerIdLength_newContainer() {
        String tenDigitContainerId = "1234567890";
        String twelveDigitContainerId = "123456789012";

        ContainerRegistrationRequest registrationRequest = createCMFAdminContainerRegistrationRequest(tenDigitContainerId);
        List<ErrorWithParameters> validationErrors = registrationRequestValidator.validate(registrationRequest);
        assertTrue(!validationErrors.isEmpty());
        assertTrue(validationErrors.contains(new ErrorWithParameters("container.id.length.error", String.valueOf(NEW_CONTAINER.getValidContainerIdLength()))));

        registrationRequest = createCMFAdminContainerRegistrationRequest(twelveDigitContainerId);
        validationErrors = registrationRequestValidator.validate(registrationRequest);
        assertTrue(!validationErrors.isEmpty());
        assertTrue(validationErrors.contains(new ErrorWithParameters("container.id.length.error", String.valueOf(NEW_CONTAINER.getValidContainerIdLength()))));
    }

    private ContainerRegistrationRequest createContainerRegistrationRequest(String containerId) {
        return new ContainerRegistrationRequest(validProvider.getProviderId(), containerId, InTreatment.getDisplayText(), WEB.name(), null);
    }

    private ContainerRegistrationRequest createCMFAdminContainerRegistrationRequest(String containerId) {
        return new CmfAdminContainerRegistrationRequest(validProvider.getProviderId(), containerId, InTreatment.getDisplayText(), NEW_CONTAINER, WEB, null);
    }
}
