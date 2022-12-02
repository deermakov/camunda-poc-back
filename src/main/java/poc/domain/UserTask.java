package poc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * todo Document type UserTask
 */
@Data
@AllArgsConstructor
public class UserTask {
    private long key;
    private String elementId;
    private String assignee;
    private Process process;
}
