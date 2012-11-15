package org.motechproject.whp.ivr.util;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FlowSessionStub implements FlowSession {

    public static final String CURRENT_NODE = "CURRENT_NODE";
    private Map<String, Object> sessionAttributes = new HashMap<>();

    @Override
    public String getSessionId() {
        return "test-session-id";
    }

    @Override
    public String getLanguage() {
        return "en";
    }

    @Override
    public void setLanguage(String s) {
    }

    @Override
    public String getPhoneNumber() {
        return this.get("cid");
    }

    public <T extends Serializable> void set(String key, T value) {
        sessionAttributes.put(key, value);
    }

    public <T extends Serializable> T get(String key) {
        return (T) sessionAttributes.get(key);
    }

    @Override
    public void setCurrentNode(Node node) {
        set(CURRENT_NODE, node);
    }

    @Override
    public Node getCurrentNode() {
        return get(CURRENT_NODE);
    }

}
