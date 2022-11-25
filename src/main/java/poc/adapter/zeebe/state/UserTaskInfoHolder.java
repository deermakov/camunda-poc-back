package poc.adapter.zeebe.state;

import lombok.NonNull;
import lombok.Synchronized;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * todo Document type UserTaskInfoHolder
 */
@Component
public class UserTaskInfoHolder {
    private final List<UserTask> userTasks = new ArrayList<>();

    synchronized public void registerUserTask(long processId, String elementId, long key){
        UserTask userTask = new UserTask(processId, elementId, key);
        userTasks.add(userTask);
    }

    synchronized public void unregisterUserTask(long processId, long key){
        userTasks.stream()
            .filter(userTask -> processId == userTask.getProcessId() &&
                key == userTask.getKey())
            .findFirst()
            .map(userTasks::remove);
    }

    synchronized public long getUserTaskKey(long processId, @NonNull String elementId){
        return userTasks.stream()
            .filter(userTask -> processId == userTask.getProcessId() &&
                elementId.equals(userTask.getElementId()))
            .findFirst()
            .map(UserTask::getKey).orElseThrow();
    }
}
