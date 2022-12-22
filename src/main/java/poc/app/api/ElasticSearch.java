package poc.app.api;

/**
 * todo Document type BpmnEngine
 */
public interface ElasticSearch {
    int getInputDataActivationsCount();

    int getProcessDataActivationsCount();

    int getTerminateActivationsCount();

    int getStartActivationsCount();
}
