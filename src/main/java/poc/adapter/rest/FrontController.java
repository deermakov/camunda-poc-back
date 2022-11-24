package poc.adapter.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import poc.app.api.InputDataInbound;
import poc.app.api.StartProcessInbound;

import java.util.HashMap;
import java.util.Map;

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
    public void startProcess(@RequestBody StartProcessDto request){
        log.info("request: {}", request);
        Map<String, Object> variables = new HashMap<>();
        variables.put("startParam", request.getStartParam());
        startProcessInbound.execute(variables);
    }

    @PostMapping("/input-data")
    public void inputData(){
        inputDataInbound.execute();
    }

}
