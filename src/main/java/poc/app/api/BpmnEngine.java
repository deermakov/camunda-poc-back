package poc.app.api;

import java.util.Map;

/**
 * todo Document type BpmnEngine
 */
public interface BpmnEngine {
    long startProcess(String startParam);

    void inputData(long processId, String inputData);

    void terminate(long processId);
}
