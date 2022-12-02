package poc.adapter.rest;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.JobRecordValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import poc.app.api.GetTaskListInbound;
import poc.app.api.InputDataInbound;
import poc.app.api.StartProcessInbound;
import poc.app.api.TerminateInbound;
import poc.domain.UserTask;

import java.util.List;

/**
 * todo Document type FrontController
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FrontController {

    private final StartProcessInbound startProcessInbound;
    private final InputDataInbound inputDataInbound;
    private final TerminateInbound terminateInbound;
    private final GetTaskListInbound getTaskListInbound;

    @PostMapping("/start")
    public StartProcessResponseDto startProcess(@RequestBody StartProcessRequestDto request) {
        log.info("startProcess(): {}", request);
        String processId = startProcessInbound.execute(request.getStartParam());
        return new StartProcessResponseDto(processId);
    }

    @PostMapping("/input-data")
    public void inputData(@RequestBody InputDataDto request) {
        log.info("inputData(): {}", request);
        inputDataInbound.execute(request.getTaskKey(), request.getInputData());
    }

    @PostMapping("/terminate")
    public void terminate(@RequestBody TerminateDto request) {
        log.info("terminate(): {}", request);
        terminateInbound.execute(request.getProcessExternalId());
    }

    @GetMapping("/tasklist/{assignee}")
    public List<UserTask> getTaskList(@PathVariable(required = false) String assignee) {
        log.info("getTaskList(): {}", assignee);
        return getTaskListInbound.execute(assignee);
    }
}
