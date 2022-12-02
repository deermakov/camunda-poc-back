package poc.app.impl;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessInstanceCreationRecordValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import poc.domain.Process;
import poc.domain.UserTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo Document type TaskList
 */
@Component
@Slf4j
public class TaskList {

    private static final String VAR_PROCESS_EXTERNAL_ID = "processExternalId";
    private static final String HEADER_ASSIGNEE = "io.camunda.zeebe:assignee";

    // ключ - ключ процесса (value.processInstanceKey)
    private final Map<Long, Process> activeProcesses = new HashMap<>();


    synchronized public void registerProcessStart(long processInstanceKey, ProcessInstanceCreationRecordValue value) {
        log.info("registerProcessStart(): processInstanceKey = {}", processInstanceKey);
        activeProcesses.put(processInstanceKey, new Process(processInstanceKey, (String)value.getVariables().get(VAR_PROCESS_EXTERNAL_ID)));
    }

    synchronized public void registerProcessEnd(long processInstanceKey) {
        log.info("registerProcessEnd(): processInstanceKey = {}", processInstanceKey);
        activeProcesses.remove(processInstanceKey);
    }

    synchronized public void registerUserTaskStart(long processInstanceKey, long key, Record<JobRecordValue> record) {
        log.info("registerUserTaskStart(): processInstanceKey = {}, key = {}", processInstanceKey, key);
        Process process = activeProcesses.get(processInstanceKey);
        UserTask userTask = new UserTask(key,
            record.getValue().getElementId(),
            record.getValue().getCustomHeaders().get(HEADER_ASSIGNEE),
            process);
        process.getUserTasks().put(key, userTask);
    }

    synchronized public void registerUserTaskEnd(long processInstanceKey, long key) {
        log.info("registerUserTaskEnd(): processInstanceKey = {}, key = {}", processInstanceKey, key);
        Process process = activeProcesses.get(processInstanceKey);
        if (process != null) {
            process.getUserTasks().remove(key);
        } else {
            log.warn("Unknown process, ignoring: {}", processInstanceKey);
        }
    }

    public List<UserTask> getAllActiveUserTasks() {
        List<UserTask> result = new ArrayList<>();

        activeProcesses.values().forEach(
            process -> {
                log.info("getAllActiveUserTasks(): process = {}, tasks = {}", process.getProcessInstanceKey(), process.getUserTasks().size());
                result.addAll(process.getUserTasks().values());
            }
        );

        log.info("getAllActiveUserTasks(): result = {}", result.size());

        return result;
    }

    public List<UserTask> getActiveUserTasks(String assignee) {
        List<UserTask> result = new ArrayList<>();

        activeProcesses.values().forEach(
            process -> {
                log.info("getActiveUserTasks(): process = {}, tasks = {}", process.getProcessInstanceKey(), process.getUserTasks().size());

                process.getUserTasks().values()
                    .stream()
                    .filter(userTask -> assignee.equals(userTask.getAssignee()))
                    .forEach(result::add);
            }
        );

        log.info("getActiveUserTasks(): result = {}", result.size());

        return result;
    }
}
