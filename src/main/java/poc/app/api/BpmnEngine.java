package poc.app.api;

import java.util.Map;

/**
 * todo Document type BpmnEngine
 */
public interface BpmnEngine {
    void startProcess(String startParam, String processExternalId);

    void inputData(String processExternalId, String inputData);

    void terminate(String processExternalId);
}
