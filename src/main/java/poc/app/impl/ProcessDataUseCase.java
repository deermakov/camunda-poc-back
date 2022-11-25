package poc.app.impl;

import org.springframework.stereotype.Component;
import poc.app.api.ProcessDataInbound;

import java.text.MessageFormat;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
public class ProcessDataUseCase implements ProcessDataInbound {
    @Override
    public String execute(String startParam, String inputData) {
        return MessageFormat.format("{0} {1}", startParam, inputData);
    }
}
