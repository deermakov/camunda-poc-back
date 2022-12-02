package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.app.api.BpmnEngine;
import poc.app.api.InputDataInbound;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class InputDataUseCase implements InputDataInbound {

    private final BpmnEngine bpmnEngine;

    @Override
    public void execute(long taskKey, String inputData) {
        bpmnEngine.inputData(taskKey, inputData);
    }
}
