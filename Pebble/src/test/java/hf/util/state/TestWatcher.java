package hf.util.state;

/**
 * @author Hongfei Zhou
 * @version 1.0, Feb. 27 2014
 */
public class TestWatcher extends StateWatcher {
    public TestWatcher(String key) {
        super(key);
    }

    @Override
    protected String keyToValue(String key) {
        return "FALSE";
    }
}
