package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import poc.app.api.ClasspathResource;
import poc.app.api.GetBpmnFileInbound;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class GetBpmnFileUseCase implements GetBpmnFileInbound {
    private final ClasspathResource classpathResource;

    @Override
    public Resource execute() {
        return classpathResource.getBpmnFile();
    }
}
