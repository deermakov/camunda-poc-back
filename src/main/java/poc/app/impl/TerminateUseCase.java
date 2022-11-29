package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.app.api.BpmnEngine;
import poc.app.api.TerminateInbound;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class TerminateUseCase implements TerminateInbound {

    private final BpmnEngine bpmnEngine;

    @Override
    public void execute(String processExternalId) {
        bpmnEngine.terminate(processExternalId);
    }
}
