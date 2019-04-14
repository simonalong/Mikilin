package com.simon.mikilin.core.express;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * groovy脚本类享元工厂
 *
 * @author zhouzhenyong
 * @since 2019/1/17 下午7:33
 */
@SuppressWarnings("unchecked")
public class GroovyScriptFactory {

    private static Map<String, Class<Script>> scriptCache = new HashMap<>();
    private GroovyClassLoader classLoader = new GroovyClassLoader();
    private static GroovyScriptFactory factory = new GroovyScriptFactory();

    private static final String TEMPLAT = ""
        + "import groovy.lang.*\n"
        + "import java.lang.*\n"
        + "\n";

    /**
     * 设置为单例模式
     */
    private GroovyScriptFactory() {
    }

    public static GroovyScriptFactory getInstance() {
        return factory;
    }

    private Class getScript(String key) {
        if (scriptCache.containsKey(key)) {
            return scriptCache.get(key);
        } else {
            // 脚本不存在则创建新的脚本
            Class<Script> scriptClass = classLoader.parseClass(key);
            scriptCache.put(key, scriptClass);
            return scriptClass;
        }
    }

    private Object run(Class<Script> script, Binding binding) {
        Script scriptObj = InvokerHelper.createScript(script, binding);
        Object result = scriptObj.run();
        // 每次脚本执行完之后，一定要清理掉内存
        classLoader.clearCache();
        return result;
    }

    public Object scriptGetAndRun(String key, Binding binding) {
        return run(getScript(key), binding);
    }
}
