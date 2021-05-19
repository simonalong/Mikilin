package com.simonalong.mikilin.express;

import com.simonalong.mikilin.util.Maps;
import groovy.lang.Binding;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static com.simonalong.mikilin.MkConstant.MK_LOG_PRE;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午10:23
 */
@Slf4j
@Accessors(chain = true)
public class ExpressParser {

    private GroovyScriptFactory groovy = GroovyScriptFactory.getInstance();

    private Binding binding;

    public ExpressParser(){
        binding = new Binding();
    }

    public ExpressParser(Maps<?, ?> maps){
        binding = new Binding(maps.build());
    }

    public void setBinding(Maps<?, ?> maps){
        binding = new Binding(maps.build());
    }

    @SuppressWarnings("unchecked")
    public void addBinding(Maps<?, ?> maps){
        binding.getVariables().putAll(maps.build());
    }

    /**
     * 表达式解析
     *
     * 我们这里只返回Boolean结果
     * @param importPath 脚本依赖的其他的jar包路径
     * @param script 待解析的表达式脚本
     * @return true=解析为true, false=解析结果为false
     */
    public Boolean parse(String importPath, String script){
        script = importPath + "\n" + script;
        try {
            Object result = groovy.scriptGetAndRun(script, binding);
            if (null == result) {
                return null;
            }
            return (Boolean) result;
        }catch (Exception e){
            log.error(MK_LOG_PRE + "表达式执行失败", e);
        }
        return null;
    }

    /**
     * 表达式解析
     *
     * 我们这里只返回Boolean结果
     * @param script 待解析的表达式脚本
     * @return true=解析为true, false=解析结果为false
     */
    public Boolean parse(String script){
        return parse("", script);
    }
}
