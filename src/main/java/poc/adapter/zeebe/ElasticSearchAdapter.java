package poc.adapter.zeebe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.protocol.jackson.ZeebeProtocolModule;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RecordValue;
import io.camunda.zeebe.protocol.record.value.ProcessInstanceRecordValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;
import poc.app.api.ElasticSearch;

import java.util.List;

/**
 * todo Document type ElasticSearchPoller
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchAdapter implements ElasticSearch {

    private static final String ELEMENT_ID_INPUT_DATA = "input-data";
    private static final String ELEMENT_ID_PROCESS_DATA = "process-data";

    private static final String ELEMENT_ID_TERMINATE_MESSAGE = "terminate";

    private static final String ELEMENT_ID_START = "StartEvent_1";

    // mapper для десериализации объектов протокола Zeebe (в т.ч. объектов RecordValue)
    final ObjectMapper zeebeMapper = new ObjectMapper().registerModule(new ZeebeProtocolModule());

    final ObjectMapper rawMapper = new ObjectMapper();

    private final ElasticsearchOperations elasticsearchOperations;

    private final String activationsCountQueryString = """
        {
            "bool": {
                "must": [
                    {
                        "match": {
                            "valueType": "PROCESS_INSTANCE"
                        }
                    },
                    {
                        "match": {
                            "intent": "ELEMENT_ACTIVATED"
                        }
                    }
                ]
            }
        }
        """;

    private <T extends RecordValue> List<Record<T>> getActivationsEvents() {
        log.info("getActivationsEvents()");

        // вычитываем все записи, поэтому самые свежие - которые укладываются в последний
        // refresh interval индекса в Elastic (1 сек) - могут не попасть в выборку
        // (из-за того, что индекс не полностью обновлен)
        Pageable pageable = PageRequest.of(0, 1000);
        Sort sort = Sort.by(Sort.Direction.ASC, "position");
        Query query = new StringQuery(activationsCountQueryString, pageable, sort);

        SearchHits<?> searchHits = elasticsearchOperations.search(query, Object.class, IndexCoordinates.of("zeebe-record-*"));
        List<Record<T>> activationEvents = searchHits.getSearchHits().stream().map(
            searchHit -> {
                //log.info("jsonNodeSearchHit = {}", searchHit);
                Object obj = searchHit.getContent();
                Record<T> record;
                try {
                    String json = rawMapper.writeValueAsString(obj);
                    log.info("poll(): {}", json);
                    record = zeebeMapper.readValue(json, new TypeReference<Record<T>>() {
                    });
                    //log.info("class = {}", record.getValue().getClass());
                    return record;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        ).toList();

        return activationEvents;
    }

    private int getActivationsCount(String elementId) {
        List<Record<RecordValue>> activationEvents = getActivationsEvents();

        long cnt = activationEvents.stream().filter(
            record -> {
                boolean result = false;
                if (record.getValue() instanceof ProcessInstanceRecordValue value) {
                    result = elementId.equals(value.getElementId());
                }
                return result;
            }
        ).count();

        return (int) cnt;
    }

    @Override
    public int getInputDataActivationsCount() {
        return getActivationsCount(ELEMENT_ID_INPUT_DATA);
    }

    @Override
    public int getProcessDataActivationsCount() {
        return getActivationsCount(ELEMENT_ID_PROCESS_DATA);
    }

    @Override
    public int getStartActivationsCount() {
        return getActivationsCount(ELEMENT_ID_START);
    }

    @Override
    public int getTerminateActivationsCount() {
        return getActivationsCount(ELEMENT_ID_TERMINATE_MESSAGE);
    }}
