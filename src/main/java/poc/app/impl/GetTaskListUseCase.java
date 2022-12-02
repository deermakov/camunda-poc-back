package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import poc.app.api.GetTaskListInbound;
import poc.domain.BpmnUserTask;

import java.util.List;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class GetTaskListUseCase implements GetTaskListInbound {

    private final TaskList taskList;

    @Override
    public List<BpmnUserTask> execute(String assignee) {
        if (StringUtils.hasText(assignee)) {
            return taskList.getActiveUserTasks(assignee);
        } else {
            return taskList.getAllActiveUserTasks();
        }
    }
}
