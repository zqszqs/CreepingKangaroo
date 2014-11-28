package michaelwork.elephant.web;

import com.stubhub.qe.platform.elephant.protocol.Locator;

/**
 * @author Hongfei Zhou
 * @version 1.0, Aug. 26 2014
 */
public abstract class WebPage extends WebUnit {
    public abstract Locator landmark();
    public abstract String url();

    public abstract Locator identifier();
}
