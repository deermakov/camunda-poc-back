package poc.app.api;

/**
 * todo Document type ProcessDataInbound
 */
public interface InputDataInbound {
    void execute(long taskKey, String inputData);
}
