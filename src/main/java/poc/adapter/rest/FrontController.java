package poc.adapter.rest;

import com.fasterxml.jackson.databind.JsonNode;
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
    public void startProcess(@RequestBody JsonNode request){
        log.info("request: {}", request);
        startProcessInbound.execute();
    }

    @PostMapping("/input-data")
    public void inputData(){
        inputDataInbound.execute();
    }

}
