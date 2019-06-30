package com.simonalong.mikilin.express;

import com.simonalong.mikilin.util.EncryptUtil;
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
class GroovyScriptFactory {

    private static Map<String, Class<Script>> scriptCache = new HashMap<>();
    private GroovyClassLoader classLoader = new GroovyClassLoader();
    private static GroovyScriptFactory factory = new GroovyScriptFactory();

    /**
     * 设置为单例模式
     */
    private GroovyScriptFactory() {
    }

    static GroovyScriptFactory getInstance() {
        return factory;
    }

    private Class getScript(String key) {
        // 压缩脚本节省空间
        String encodeStr = EncryptUtil.SHA256(key);
        if (scriptCache.containsKey(encodeStr)) {
            return scriptCache.get(encodeStr);
        } else {
            // 脚本不存在则创建新的脚本
            Class<Script> scriptClass = classLoader.parseClass(key);
            scriptCache.put(encodeStr, scriptClass);
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

    Object scriptGetAndRun(String key, Binding binding) {
        return run(getScript(key), binding);
    }
}
