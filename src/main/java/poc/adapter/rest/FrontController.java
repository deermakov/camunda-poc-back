package poc.adapter.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import poc.app.api.InputDataInbound;
import poc.app.api.StartProcessInbound;

/**
 * todo Document type FrontController
 */
@RestController
@RequiredArgsConstructor
public class FrontController {

    private final StartProcessInbound startProcessInbound;
    private final InputDataInbound inputDataInbound;

    @PostMapping("/start")
    public void startProcess(){
        startProcessInbound.execute();
    }

    @PostMapping("/input-data")
    public void inputData(){
        inputDataInbound.execute();
    }

}
