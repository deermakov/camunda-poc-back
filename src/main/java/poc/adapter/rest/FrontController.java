package poc.adapter.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import poc.app.api.StartProcessInbound;

/**
 * todo Document type FrontController
 */
@RestController
@RequiredArgsConstructor
public class FrontController {

    private final StartProcessInbound startProcessInbound;

    @PostMapping
    public void startProcess(){
        startProcessInbound.execute();
    }
}
