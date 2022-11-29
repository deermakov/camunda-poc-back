package poc.app.api;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;

import java.util.List;

/**
 * todo Document type ProcessDataInbound
 */
public interface GetTaskListInbound {
    List<Record<JobRecordValue>> execute(String assignee);
}
