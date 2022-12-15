package poc.app.api;

import poc.domain.bpmn.BpmnUserTask;

import java.util.List;

/**
 * todo Document type ProcessDataInbound
 */
public interface GetTaskListInbound {
    List<BpmnUserTask> execute(String assignee);
}
