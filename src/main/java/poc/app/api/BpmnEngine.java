package poc.app.api;

import java.util.Map;

/**
 * todo Document type BpmnEngine
 */
public interface BpmnEngine {
    void startProcess(Map<String, Object> variables);
}
