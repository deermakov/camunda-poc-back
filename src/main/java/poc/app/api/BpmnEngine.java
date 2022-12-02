package poc.app.api;

/**
 * todo Document type BpmnEngine
 */
public interface BpmnEngine {
    void startProcess(String startParam, String processExternalId);

    void inputData(long taskKey, String inputData);

    void terminate(String processExternalId);
}
