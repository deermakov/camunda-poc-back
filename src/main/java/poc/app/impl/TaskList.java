package poc.app.impl;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    // 1й ключ - ключ процесса (value.processInstanceKey)
    // 2й ключ - ключ user task job'а (key)
    // значение - Record
    private final Map<Long, Map<Long, Record<JobRecordValue>>> activeUserTasks = new HashMap<>();

    synchronized public void registerProcessStart(long processInstanceKey) {
        log.info("registerProcessStart(): processInstanceKey = {}", processInstanceKey);
        activeUserTasks.put(processInstanceKey, new HashMap<>());
    }

    synchronized public void registerProcessEnd(long processInstanceKey) {
        log.info("registerProcessEnd(): processInstanceKey = {}", processInstanceKey);
        activeUserTasks.remove(processInstanceKey);
    }

    synchronized public void registerUserTaskStart(long processInstanceKey, long key, Record<JobRecordValue> record) {
        log.info("registerUserTaskStart(): processInstanceKey = {}, key = {}", processInstanceKey, key);
        Map<Long, Record<JobRecordValue>> userTasks = activeUserTasks.get(processInstanceKey);
        userTasks.put(key, record);
    }

    synchronized public void registerUserTaskEnd(long processInstanceKey, long key) {
        log.info("registerUserTaskEnd(): processInstanceKey = {}, key = {}", processInstanceKey, key);
        Map<Long, Record<JobRecordValue>> userTasks = activeUserTasks.get(processInstanceKey);
        userTasks.remove(key);
    }

    public List<Record<JobRecordValue>> getAllActiveUserTasks() {
        List<Record<JobRecordValue>> result = new ArrayList<>();

        activeUserTasks.values().forEach(
            processUserTasks -> result.addAll(processUserTasks.values())
        );

        return result;
    }

    public List<Record<JobRecordValue>> getActiveUserTasks(String assignee) {
        List<Record<JobRecordValue>> result = new ArrayList<>();

        activeUserTasks.values().forEach(
            processUserTasks -> processUserTasks.values()
                .stream()
                .filter(jobRecordValue -> assignee.equals(jobRecordValue.getValue().getCustomHeaders().get("io.camunda.zeebe:assignee")))
                .forEach(result::add)
        );

        return result;
    }
}
