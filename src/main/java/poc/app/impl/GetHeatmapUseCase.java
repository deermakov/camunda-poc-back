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
            DataPoint inputDataElementDataPoint = getBpmnElementCenterCoords(xmlDocument, inputDataNodeXPathExpression);
            XPathExpression processDataNodeXPathExpression = getXPathExpression("process-data-element");
            DataPoint processDataElementDataPoint = getBpmnElementCenterCoords(xmlDocument, processDataNodeXPathExpression);

            int inputDataActivationsCount = elasticSearch.getInputDataActivationsCount();
            int processDataActivationsCount = elasticSearch.getProcessDataActivationsCount();
            int startActivationsCount = elasticSearch.getStartActivationsCount();
            log.info("starts = {}, inputData activations = {}, processData activations = {}", startActivationsCount, inputDataActivationsCount,
                processDataActivationsCount);

            float denominator = startActivationsCount > 0 ? startActivationsCount + 0f : 1f;
            float inputDataActivationsFreq = inputDataActivationsCount / denominator;
            float processDataActivationsFreq = processDataActivationsCount / denominator;
            log.info("inputData activations freq = {}, processData activations freq = {}", inputDataActivationsFreq, processDataActivationsFreq);

            inputDataElementDataPoint.setValue(inputDataActivationsFreq);
            processDataElementDataPoint.setValue(processDataActivationsFreq);

            return Heatmap.builder()
                .min(0)
                .max(1)
                .data(List.of(
                    inputDataElementDataPoint,
                    processDataElementDataPoint
                ))
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

    private DataPoint getBpmnElementCenterCoords(Document xmlDocument, XPathExpression xPathExpression) throws Exception {
        Node elementNode = (Node) xPathExpression.evaluate(xmlDocument, XPathConstants.NODE);
        NamedNodeMap attrs = elementNode.getAttributes();
        String xStr = attrs.getNamedItem("x").getNodeValue();
        String yStr = attrs.getNamedItem("y").getNodeValue();
        String widthStr = attrs.getNamedItem("width").getNodeValue();
        String heightStr = attrs.getNamedItem("height").getNodeValue();

        int x = Integer.parseInt(xStr);
        int y = Integer.parseInt(yStr);
        int heigth = Integer.parseInt(heightStr);
        int width = Integer.parseInt(widthStr);
        int centerX = x + width / 2;
        int centerY = y + heigth / 2;

        return new DataPoint(centerX, centerY, 90);
    }
}
