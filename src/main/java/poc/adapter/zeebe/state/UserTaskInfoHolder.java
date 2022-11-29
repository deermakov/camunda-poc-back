package poc.adapter.zeebe.state;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * todo Document type UserTaskInfoHolder
 */
@Component
@Slf4j
public class UserTaskInfoHolder {
    private final List<UserTask> userTasks = new ArrayList<>();

    synchronized public void registerUserTask(String processId, String elementId, long key) {
        log.info("registerUserTask(): processId = {}, elementId = {}, key = {}", processId, elementId, key);

        unregisterUserTask(processId, key);//чтобы не было дубликата

        UserTask userTask = new UserTask(processId, elementId, key);
        userTasks.add(userTask);
    }

    synchronized public void unregisterUserTask(String processId, long key) {
        userTasks.stream()
            .filter(userTask -> processId.equals(userTask.getProcessId()) &&
                key == userTask.getKey())
            .findFirst()
            .map(userTasks::remove);
    }

    synchronized public long getUserTaskKey(String processId, @NonNull String elementId) {
        return userTasks.stream()
            .filter(userTask -> processId.equals(userTask.getProcessId()) &&
                elementId.equals(userTask.getElementId()))
            .findFirst()
            .map(UserTask::getKey).orElseThrow();
    }
}
