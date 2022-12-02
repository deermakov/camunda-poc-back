package poc.app.api;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import poc.domain.UserTask;

import java.util.List;

/**
 * todo Document type ProcessDataInbound
 */
public interface GetTaskListInbound {
    List<UserTask> execute(String assignee);
}
