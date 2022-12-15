package poc.domain.bpmn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * todo Document type BpmnProcess
 */
@Data
@AllArgsConstructor
public class BpmnProcess {
    @JsonIgnore// для исключения циркулярной зависимости
    @ToString.Exclude
    private final Map<Long, BpmnUserTask> userTasks = new HashMap<>();
    private long processInstanceKey;
    private String processExternalId;
}
