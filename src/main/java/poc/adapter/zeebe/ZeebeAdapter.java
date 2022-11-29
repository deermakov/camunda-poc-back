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
import poc.app.impl.TaskList;

import java.util.HashMap;
import java.util.Map;

/**
 * todo Document type ZeebeAdapter
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZeebeAdapter implements BpmnEngine {
    private final TaskList taskList;

    private final ZeebeClientLifecycle client;

    private final ProcessDataInbound processDataInbound;

    private final UserTaskInfoHolder userTaskInfoHolder;

    private final String PROCESS_DEFINITION_ID = "poc-process";

    @Override
    public void startProcess(String startParam, String processExternalId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("startParam", startParam);
        variables.put("processExternalId", processExternalId);

        final ProcessInstanceEvent event =
            client
                .newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_DEFINITION_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        log.info("startProcess(): process started for processExternalId = {}, processDefinitionKey={}, bpmnProcessId={}, version={}, processInstanceKey={}",
            processExternalId, event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
    }

    @Override
    public void inputData(String processExternalId, String inputData) {

        log.info("inputData(): processExternalId = {}, inputData = {}", processExternalId, inputData);

        long taskKey = userTaskInfoHolder.getUserTaskKey(processExternalId, "input-data");

        Map<String, Object> variables = new HashMap<>();
        variables.put("inputData", inputData);

        client
            .newCompleteCommand(taskKey)
            .variables(variables)
            .send()
            .join();

        userTaskInfoHolder.unregisterUserTask(processExternalId, taskKey);
    }

    @Override
    public void terminate(String processExternalId) {

        log.info("TaskList: getAllActiveUserTasks() = {}", taskList.getAllActiveUserTasks());
        log.info("TaskList: getActiveUserTasks() = {}", taskList.getActiveUserTasks("demo"));

        log.info("terminate(): processId = {}", processExternalId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("terminated", true);

        client
            .newPublishMessageCommand()
            .messageName("terminate")
            .correlationKey(processExternalId)
            .variables(variables)
            .send()
            .join();
    }

    // autoComplete = false чтобы user task'а не завершалась и висела в task list'е
    // до выполнения inputData() через rest
    @JobWorker(type = "io.camunda.zeebe:userTask", autoComplete = false, fetchVariables = "processExternalId")
    public void handleUserTask(final ActivatedJob job) {

        String processExternalId = (String) job.getVariablesAsMap().get("processExternalId");
        String elementId = job.getElementId();
        long key = job.getKey();

        log.info("handleUserTask(): processExternalId = {}, elementId = {}, key = {}", processExternalId, elementId, key);

        userTaskInfoHolder.registerUserTask(processExternalId, elementId, key);
    }

    // autoComplete = false чтобы можно было обновить переменные в процессе, см. в коде
    @JobWorker(type = "process-data", autoComplete = false)
    public void processData(final ActivatedJob job) {

        log.info("TaskList: getAllActiveUserTasks() = {}", taskList.getAllActiveUserTasks());
        log.info("TaskList: getActiveUserTasks() = {}", taskList.getActiveUserTasks("demo"));

        String startParam = (String) job.getVariablesAsMap().get("startParam");
        String inputData = (String) job.getVariablesAsMap().get("inputData");

        String processedData = processDataInbound.execute(startParam, inputData);
        log.info("processedData: {}", processedData);

        Map<String, Object> variables = new HashMap<>();
        variables.put("processedData", processedData);

        client
            .newCompleteCommand(job.getKey())
            .variables(variables)
            .send()
            .join();
    }
}
