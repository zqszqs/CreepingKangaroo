package hf.util.state;

import fj.Effect;
import fj.F;

import java.util.HashMap;

/**
 * @author Hongfei Zhou
 * @version 1.0, Feb. 27 2014
 */
public class StateWatcher {
    private String key;
    private F<String, String> keyToValue;
    private HashMap<String, Effect<String>> effects = new HashMap<String, Effect<String>>();

    private StateWatcher(String key, F<String, String> keyToValue, HashMap<String, Effect<String>> effects) {
        this.key = key;
        this.keyToValue = keyToValue;
        this.effects = effects;
    }

    public StateWatcher(String key, F<String, String> keyToValue) {
        this.key = key;
        this.keyToValue = keyToValue;
    }

    @SuppressWarnings("unchecked")
    public StateWatcher when(String state, Effect<String> function) {
        HashMap<String, Effect<String>> cloneEffects = (HashMap<String, Effect<String>>) effects.clone();
        cloneEffects.put(state, function);
        return new StateWatcher(key, keyToValue, cloneEffects);
    }

    public StateWatcher whenOn(Effect<String> function) {
        return when("TRUE", function);
    }

    public StateWatcher whenOff(Effect<String> function) {
        return when("FALSE", function);
    }

    public void start() {
        final String value = keyToValue.f(key);
        Effect<String> e = effects.get(value);

        if (e != null) e.e(value);
    }
}
