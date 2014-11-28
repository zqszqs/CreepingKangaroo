package michaelwork.elephant.web;

import com.stubhub.qe.platform.elephant.protocol.*;

import java.util.Map;

/**
 * @author Hongfei Zhou
 * @version 1.0, Aug. 26 2014
 */
public class WebUnit {

    protected static ID id(String id) {
        return new ID(id);
    }

    protected static NAME name(String name) {
        return new NAME(name);
    }

    protected static CLASS_NAME className(String className) {
        return new CLASS_NAME(className);
    }

    protected static LINK_TEXT linkText(String linkText) {
        return new LINK_TEXT(linkText);
    }

    protected static PARTIAL_LINK_TEXT partialLinkText(String partial) {
        return new PARTIAL_LINK_TEXT(partial);
    }

    protected static CSS_SELECTOR cssSelector(String cssSelector) {
        return new CSS_SELECTOR(cssSelector);
    }

    protected static TAG_NAME tagName(String tagName) {
        return new TAG_NAME(tagName);
    }

    protected static XPATH xpath(String xpath) {
        return new XPATH(xpath);
    }


    protected static Label label(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new Label(name, identifier, config);
    }

    protected static Link link(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new Link(name, identifier, config);
    }

    protected static Button button(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new Button(name, identifier, config);
    }

    protected static CheckBox checkBox(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new CheckBox(name, identifier, config);
    }

    protected static Image image(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new Image(name, identifier, config);
    }

    protected static RadioButton radioButton(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new RadioButton(name, identifier, config);
    }

    protected static SelectList selectList(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new SelectList(name, identifier, config);
    }

    protected static TextField textField(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new TextField(name, identifier, config);
    }

    protected static Table table(String name, Identifier identifier, Map<String, Interceptor> config) {
        return new Table(name, identifier, config);
    }
}
