package poc.adapter.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import poc.app.api.InputDataInbound;
import poc.app.api.StartProcessInbound;
import poc.app.api.TerminateInbound;

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

    @PostMapping("/start")
    public StartProcessResponseDto startProcess(@RequestBody StartProcessRequestDto request){
        log.info("startProcess(): {}", request);
        String processId = startProcessInbound.execute(request.getStartParam());
        return new StartProcessResponseDto(processId);
    }

    @PostMapping("/input-data")
    public void inputData(@RequestBody InputDataDto request){
        log.info("inputData(): {}", request);
        inputDataInbound.execute(request.getProcessId(), request.getInputData());
    }

    @PostMapping("/terminate")
    public void terminate(@RequestBody TerminateDto request){
        log.info("terminate(): {}", request);
        terminateInbound.execute(request.getProcessId());
    }

}
