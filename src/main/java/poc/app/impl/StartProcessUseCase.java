package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.app.api.BpmnEngine;
import poc.app.api.StartProcessInbound;

/**
 * todo Document type StartProcessUseCase
 */
@Component
@RequiredArgsConstructor
public class StartProcessUseCase implements StartProcessInbound {
    private final BpmnEngine bpmnEngine;
    @Override
    public void execute() {
        bpmnEngine.startProcess();
    }
}
