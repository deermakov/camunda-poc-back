package poc.adapter.zeebe;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import poc.adapter.zeebe.state.UserTaskInfoHolder;
import poc.app.api.BpmnEngine;
import poc.app.api.ProcessDataInbound;

import java.util.HashMap;
import java.util.Map;

/**
 * todo Document type ZeebeAdapter
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZeebeAdapter implements BpmnEngine {
    private final ZeebeClientLifecycle client;

    private final ProcessDataInbound processDataInbound;

    private final UserTaskInfoHolder userTaskInfoHolder;

    private final String PROCESS_DEFINITION_ID = "poc-process";

    @Override
    public long startProcess(String startParam){
        Map<String, Object> variables = new HashMap<>();
        variables.put("startParam", startParam);

        final ProcessInstanceEvent event =
            client
                .newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_DEFINITION_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        long processId = event.getProcessInstanceKey();

        variables.put("processId", processId);
        client
            .newSetVariablesCommand(processId)
            .variables(variables)
            .send()
            .join();

        log.info("startProcess(): process started for processDefinitionKey='{}', bpmnProcessId='{}', version='{}' with processInstanceKey='{}'",
            event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());

        return processId;
    }

    @Override
    public void inputData(long processId, String inputData) {

        log.info("inputData(): processId = {}, inputData = {}", processId, inputData);

        long key = userTaskInfoHolder.getUserTaskKey(processId, "input-data");

        Map<String, Object> variables = new HashMap<>();
        variables.put("inputData", inputData);

        client
            .newPublishMessageCommand()
            .messageName("input-data-arrived")
            .correlationKey(String.valueOf(processId))
            .variables(variables)
            .send()
            .join();

        userTaskInfoHolder.unregisterUserTask(processId, key);
    }

    @JobWorker(type = "io.camunda.zeebe:userTask")
    public void handleUserTask(final ActivatedJob job) {

        long processId = job.getProcessInstanceKey();
        String elementId = job.getElementId();
        long key = job.getKey();

        log.info("handleUserTask(): processId = {}, elementId = {}, key = {}", processId, elementId, key);

        userTaskInfoHolder.registerUserTask(processId, elementId, key);
    }

    @JobWorker(type = "process-data")
    public void processData(final ActivatedJob job) {

        final String message_content = (String) job.getVariablesAsMap().get("message_content");
        log.info("processData(): {}", message_content);

        processDataInbound.execute();
    }
}
