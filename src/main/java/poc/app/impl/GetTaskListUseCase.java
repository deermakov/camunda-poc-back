package poc.app.impl;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import poc.app.api.GetTaskListInbound;
import poc.domain.UserTask;

import java.util.List;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class GetTaskListUseCase implements GetTaskListInbound {

    private final TaskList taskList;

    @Override
    public List<UserTask> execute(String assignee) {
        if (StringUtils.hasText(assignee)) {
            return taskList.getActiveUserTasks(assignee);
        } else {
            return taskList.getAllActiveUserTasks();
        }
    }
}
