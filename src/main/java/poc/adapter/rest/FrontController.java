package poc.adapter.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import poc.app.api.InputDataInbound;
import poc.app.api.StartProcessInbound;

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

    @PostMapping("/start")
    public StartProcessResponseDto startProcess(@RequestBody StartProcessRequestDto request){
        log.info("startProcess(): {}", request);
        long processId = startProcessInbound.execute(request.getStartParam());
        return new StartProcessResponseDto(processId);
    }

    @PostMapping("/input-data")
    public void inputData(@RequestBody InputDataDto request){
        log.info("inputData(): {}", request);
        inputDataInbound.execute(request.getProcessId(), request.getInputData());
    }

}
