package hf.util.state;

import fj.Effect;
import org.testng.annotations.Test;

/**
 * @author Hongfei Zhou
 * @version 1.0, Feb. 27 2014
 */
public class StateWatcherTest {

    private Effect<String> effective = new Effect<String>() {
        @Override
        public void e(String s) {
            System.out.println("in effective");
        }
    };

    private Effect<String> inEffective = new Effect<String>() {
        @Override
        public void e(String s) {
            System.out.println("in in-effective");
        }
    };

    @Test
    public void testWhen() throws Exception {
        new TestWatcher("ATest").when("ATest", effective)
                                .when("ATest1", inEffective)
                                .start();
    }

    @Test
    public void testWhenOn() throws Exception {
        new TestWatcher("ATest").whenOn(effective).start();
    }

    @Test
    public void testWhenOff() throws Exception {
        new TestWatcher("ATest").whenOff(inEffective).start();
    }
}
