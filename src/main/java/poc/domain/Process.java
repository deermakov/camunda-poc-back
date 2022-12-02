package poc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo Document type Process
 */
@Data
@AllArgsConstructor
public class Process {
    private long processInstanceKey;
    private String processExternalId;
    @JsonIgnore// для исключения циркулярной зависимости
    private final Map<Long, UserTask> userTasks = new HashMap<>();
}
