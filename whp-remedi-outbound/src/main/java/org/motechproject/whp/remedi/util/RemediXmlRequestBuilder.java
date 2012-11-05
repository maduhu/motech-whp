package org.motechproject.whp.remedi.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.motechproject.whp.common.service.RemediProperties;
import org.motechproject.whp.remedi.model.ContainerRegistrationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RemediXmlRequestBuilder {

    private RemediProperties remediProperties;

    @Autowired
    public RemediXmlRequestBuilder(RemediProperties remediProperties) {
        this.remediProperties = remediProperties;
    }

    public String buildTemplatedXmlFor(ContainerRegistrationModel containerRegistrationModel) throws IOException, TemplateException {
        HashMap<String, Object> modelMap = new HashMap();
        modelMap.put("containerRegistrationModel", containerRegistrationModel);
        populateApiKey(modelMap);

        Template template = getXmlTemplate("containerRegistration.ftl");
        return processTemplate(modelMap, template);
    }

    private String processTemplate(HashMap<String, Object> modelMap, Template template) throws TemplateException, IOException {
        CharArrayWriter writer = new CharArrayWriter();
        template.process(modelMap, writer);
        return writer.toString();
    }

    private Template getXmlTemplate(String fileName) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(),"/");
        return cfg.getTemplate(fileName);
    }

    private void populateApiKey(Map<String, Object> modelMap) {
        modelMap.put("apiKey", remediProperties.getApiKey());
    }
}
