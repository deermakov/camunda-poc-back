package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poc.app.api.BpmnEngine;
import poc.app.api.StartProcessInbound;

import java.util.Map;

/**
 * todo Document type StartProcessUseCase
 */
@Component
@RequiredArgsConstructor
public class StartProcessUseCase implements StartProcessInbound {
    private final BpmnEngine bpmnEngine;
    @Override
    public void execute(Map<String, Object> variables) {
        bpmnEngine.startProcess(variables);
    }
}
