package poc.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import poc.app.api.GetHeatmapInbound;
import poc.app.api.GetTaskListInbound;
import poc.domain.bpmn.BpmnUserTask;
import poc.domain.heatmap.DataPoint;
import poc.domain.heatmap.Heatmap;

import java.util.List;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
public class GetHeatmapUseCase implements GetHeatmapInbound {

    @Override
    public Heatmap execute() {
        Heatmap heatmap = Heatmap.builder()
            .min(0)
            .max(100)
            .data(List.of(
                new DataPoint(330, 120, 30),
                new DataPoint(750, 120, 70)
            ))
            .build();
        return heatmap;
    }
}
