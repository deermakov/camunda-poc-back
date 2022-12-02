package poc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * todo Document type BpmnUserTask
 */
@Data
@AllArgsConstructor
public class BpmnUserTask {
    private long key;
    private String elementId;
    private String assignee;
    private BpmnProcess process;
}
