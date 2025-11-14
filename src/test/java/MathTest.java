import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
//import org.junit.jupiter.api.Disabled;
//import witupgraph.witupedge.BooleanCFGEdge;
import witupgraph.witupnode.ThrowStatementNode;
import witupgraph.witupnode.WITUpNode;
import org.junit.jupiter.api.Test;

import witupgraph.WITUpGraph;

import static org.junit.jupiter.api.Assertions.*;

public class MathTest {
    Path projectRoot = Paths.get(System.getProperty("user.dir"));
    Path testClassesDir = projectRoot.resolve("target/test-classes");

    /*
    FIXME: Right now all this test changes whenever we alter the source code
     of Math. There will be as many graphs as there are methods that throw.
     */
    @Test
    public void findGraphsForMethodsThatThrow() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertEquals(3, graphs.size());
    }

    @Test
    public void findDivThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: int div(int,int)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> conditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(1, conditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());

        JSONArray paths = WITUpGraph.findConditionPathsThatThrow(g, throwNodes);
        System.out.println("div: number of unique paths: " + paths.length());
        System.out.println(paths);
    }

    @Test
    public void findCircleAreaThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double circleArea()>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> conditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(1, conditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());

        JSONArray paths = WITUpGraph.findConditionPathsThatThrow(g, throwNodes);
        System.out.println("circle: number of unique paths: " + paths.length());
        System.out.println(paths);
    }

    @Test
    public void findProbabilityThrowConditions() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double probability(int)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> throwConditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(2, throwConditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(2, throwConditions.get(throwNodes.get(0)).size());

        JSONArray conditionPaths = WITUpGraph.findConditionPathsThatThrow(g, throwNodes);
        System.out.println("probability: number of unique paths: " + conditionPaths.length());
        System.out.println(conditionPaths);

        ProcessBuilder pb = new ProcessBuilder("python", "./src/scripts//symsolver.py");
        try {
            Process process = pb.start();
            // Send JSONArray to Python via stdin
            try (OutputStream os = process.getOutputStream()) {
                os.write(conditionPaths.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Read Python output (assume single-line JSON)
            String result;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                result = br.readLine();
            }

            process.waitFor();

            System.out.println("Python returned: " + result);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
