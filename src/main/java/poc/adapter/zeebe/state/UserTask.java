package poc.adapter.zeebe.state;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * todo Document type UserTask
 */
@Data
@AllArgsConstructor
public class UserTask {
    private String processId;
    private String elementId; // user task element id
    private long key; // user task key
}
