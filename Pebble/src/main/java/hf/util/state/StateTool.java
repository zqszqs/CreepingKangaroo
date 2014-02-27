package hf.util.state;

import fj.F;

/**
 * @author Hongfei Zhou
 * @version 1.0, Feb. 28 2014
 */
public class StateTool {
    public static StateWatcher watch(String key, F<String, String> keyToValue) {
        return new StateWatcher(key, keyToValue);
    }
}
