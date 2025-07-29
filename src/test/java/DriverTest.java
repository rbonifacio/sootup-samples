import org.junit.Test;

public class DriverTest {

    @Test
    public void buildJimple() {
        Driver driver = new Driver();
        driver.execute("/Users/rbonifacio/Documents/workspace-java/sootup/target/test-classes", "br.unb.cic.samples.Math", "foo");
    }
}
