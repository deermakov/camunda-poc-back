package poc.domain.heatmap;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * todo Document type DataPoint
 */
@Data
@AllArgsConstructor
public class DataPoint {
    private int x;
    private int y;
    private float value;
}
