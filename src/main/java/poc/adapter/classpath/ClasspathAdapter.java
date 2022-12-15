package poc.adapter.classpath;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import poc.app.api.ClasspathResource;

/**
 * todo Document type ClasspathAdapter
 */
@Component
@Slf4j
public class ClasspathAdapter implements ClasspathResource {
    @Value("classpath:bpmn/poc-process.bpmn")
    private Resource bpmnFile;

    @Override
    public Resource getBpmnFile() {
        return bpmnFile;
    }
}
