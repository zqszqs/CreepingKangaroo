package hf.util.state;

import fj.Effect;
import fj.F;
import fj.Unit;
import fj.data.HashMap;

/**
 * @author Hongfei Zhou
 * @version 1.0, Feb. 27 2014
 */
public class StateWatcher {
    private String key;
    private F<String, String> keyToValue;
    private HashMap<String, Effect<String>> effects = HashMap.hashMap();

    public StateWatcher(String key, F<String, String> keyToValue) {
        this.key = key;
        this.keyToValue = keyToValue;
    }

    public StateWatcher when(String state, Effect<String> function) {
        effects.set(state, function);
        return this;
    }

    public StateWatcher whenOn(Effect<String> function) {
        return when("TRUE", function);
    }

    public StateWatcher whenOff(Effect<String> function) {
        return when("FALSE", function);
    }

    public void start() {
        final String value = keyToValue.f(key);
        effects.get(value).map(new F<Effect<String>, Unit>() {
            @Override
            public Unit f(Effect<String> objectEffect) {
                objectEffect.e(value);
                return Unit.unit();
            }
        });
    }
}
