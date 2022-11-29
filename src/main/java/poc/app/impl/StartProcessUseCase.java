package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.app.api.BpmnEngine;
import poc.app.api.StartProcessInbound;

import java.util.UUID;

/**
 * todo Document type StartProcessUseCase
 */
@Component
@RequiredArgsConstructor
public class StartProcessUseCase implements StartProcessInbound {
    private final BpmnEngine bpmnEngine;

    @Override
    public String execute(String inputData) {
        String processExternalId = UUID.randomUUID().toString();
        bpmnEngine.startProcess(inputData, processExternalId);
        return processExternalId;
    }
}
