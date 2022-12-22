package poc.app.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import poc.app.api.ClasspathResource;
import poc.app.api.ElasticSearch;
import poc.app.api.GetHeatmapInbound;
import poc.domain.heatmap.DataPoint;
import poc.domain.heatmap.Heatmap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * todo Document type ProcessDataUseCase
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetHeatmapUseCase implements GetHeatmapInbound {

    private final ClasspathResource classpathResource;

    private final ElasticSearch elasticSearch;

    String ELEMENT_XPATH_EXPRESSION = "//bpmndi:BPMNShape[@bpmnElement='%s']/dc:Bounds";

    @Override
    public Heatmap execute() {

        try {
            Document xmlDocument = getXmlDocument();

            XPathExpression inputDataNodeXPathExpression = getXPathExpression("input-data");
            XPathExpression processDataNodeXPathExpression = getXPathExpression("process-data");
            XPathExpression terminateMessageNodeXPathExpression = getXPathExpression("terminate");

            int inputDataActivationsCount = elasticSearch.getInputDataActivationsCount();
            int processDataActivationsCount = elasticSearch.getProcessDataActivationsCount();
            int terminateActivationsCount = elasticSearch.getTerminateActivationsCount();
            int startActivationsCount = elasticSearch.getStartActivationsCount();
            log.info("starts = {}, inputData activations = {}, processData activations = {}", startActivationsCount, inputDataActivationsCount,
                processDataActivationsCount);

            float denominator = startActivationsCount > 0 ? startActivationsCount + 0f : 1f;
            float inputDataActivationsFreq = inputDataActivationsCount / denominator;
            float processDataActivationsFreq = processDataActivationsCount / denominator;
            float terminateActivationsFreq = terminateActivationsCount / denominator;
            log.info("inputData activations freq = {}, processData activations freq = {}", inputDataActivationsFreq, processDataActivationsFreq);

            List<DataPoint> dataPoints = new ArrayList<>();
            dataPoints.addAll(getHeatmapForElement(xmlDocument, inputDataNodeXPathExpression, inputDataActivationsFreq));
            dataPoints.addAll(getHeatmapForElement(xmlDocument, processDataNodeXPathExpression, processDataActivationsFreq));
            dataPoints.addAll(getHeatmapForElement(xmlDocument, terminateMessageNodeXPathExpression, terminateActivationsFreq));

            return Heatmap.builder()
                .min(0)
                .max(1)
                .data(dataPoints)
                .build();
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    private Document getXmlDocument() throws Exception {
        InputStream is = classpathResource.getBpmnFile().getInputStream();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return builder.parse(is);
    }

    private XPathExpression getXPathExpression(String elementId) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public Iterator getPrefixes(String arg0) {
                return List.of("bpmndi").iterator();
            }

            @Override
            public String getPrefix(String arg0) {
                return null;
            }

            @Override
            public String getNamespaceURI(String arg0) {
                if ("bpmndi".equals(arg0)) {
                    return "http://www.omg.org/spec/BPMN/20100524/DI";
                } else if ("dc".equals(arg0)) {
                    return "http://www.omg.org/spec/DD/20100524/DC";
                }
                return null;
            }
        });

        String xPathExpression = String.format(ELEMENT_XPATH_EXPRESSION, elementId);
        return xPath.compile(xPathExpression);
    }

    private List<DataPoint> getHeatmapForElement(Document xmlDocument, XPathExpression xPathExpression, float value) throws Exception{

        if (value == 0){
            return new ArrayList<>();
        }

        Node elementNode = (Node) xPathExpression.evaluate(xmlDocument, XPathConstants.NODE);
        NamedNodeMap attrs = elementNode.getAttributes();
        String xStr = attrs.getNamedItem("x").getNodeValue();
        String yStr = attrs.getNamedItem("y").getNodeValue();
        String widthStr = attrs.getNamedItem("width").getNodeValue();
        String heightStr = attrs.getNamedItem("height").getNodeValue();

        int x = Integer.parseInt(xStr);
        int y = Integer.parseInt(yStr);
        int height = Integer.parseInt(heightStr);
        int width = Integer.parseInt(widthStr);

        int radius = height / 2;
        DataPoint centerDataPoint = new DataPoint(x + width / 2, y + height / 2, value, radius * 1.5f);
        DataPoint lowerLeftDataPoint = new DataPoint(x + radius / 2, y + height - radius / 2, value, radius);
        DataPoint lowerMiddleDataPoint = new DataPoint(x + width / 2, y + height - radius / 2, value, radius);
        DataPoint lowerRightDataPoint = new DataPoint(x + width - radius / 2, y + height - radius / 2, value, radius);
        DataPoint upperLeftDataPoint = new DataPoint(x + radius / 2, y + radius / 2, value, radius);
        DataPoint upperMiddleDataPoint = new DataPoint(x + width / 2, y + radius / 2, value, radius);
        DataPoint upperRightDataPoint = new DataPoint(x + width - radius / 2, y + radius / 2, value, radius);

        return List.of(centerDataPoint, upperLeftDataPoint, upperMiddleDataPoint, upperRightDataPoint, lowerLeftDataPoint, lowerMiddleDataPoint, lowerRightDataPoint);
    }
}
