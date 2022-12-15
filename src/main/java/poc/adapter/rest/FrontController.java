package poc.adapter.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poc.app.api.*;
import poc.domain.BpmnUserTask;

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

    private final GetBpmnFileInbound getBpmnFileInbound;

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
    public List<BpmnUserTask> getTaskList(@PathVariable(required = false) String assignee) {
        log.info("getTaskList(): {}", assignee);
        return getTaskListInbound.execute(assignee);
    }

    @GetMapping("/bpmn-file")
    public ResponseEntity<Resource> getBpmnFile() {
        log.info("getBpmnFile()");

        Resource bpmnFile = getBpmnFileInbound.execute();
        try {
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(bpmnFile.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                        .filename(bpmnFile.getFilename())
                        .build().toString())
                .body(bpmnFile);
        } catch (Exception e) {
            log.error("", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
