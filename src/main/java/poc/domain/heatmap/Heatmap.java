package poc.domain.heatmap;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * todo Document type Heatmap
 */
@Data
@Builder
public class Heatmap {
    private int max;
    private int min;
    private List<DataPoint> data;
}
