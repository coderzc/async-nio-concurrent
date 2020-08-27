package com.zc.async.nio.concurrent.script;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import com.zc.async.nio.concurrent.AsyncNioConcurrentApplication;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * @author coderzc
 * Created on 2020-08-26
 */
@Service
public class ScriptUtils {
    public static final String TYPE_GROOVY = "groovy";
    public static final String TYPE_MVEL = "mvel";

    public static final Map<String, Script> SCRIPT_CACHE = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ScriptUtils.class);
    private static final GroovyClassLoader loader;
    private static final ScriptEngine engine;

    static {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);

        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("groovy");
    }

    // 解释执行 (性能低)
    public static Object evaluate(Script script, Map<String, Object> params) {
        if (Objects.isNull(script)) {
            return null;
        }
        if (TYPE_GROOVY.equals(script.getType())) {
            StaticScriptSource scriptSource = new StaticScriptSource(script.getContent());
            GroovyScriptEvaluator evaluator = new GroovyScriptEvaluator();
            return evaluator.evaluate(scriptSource, params);
        }
        return null;
    }

    // 执行编译后的脚本
    public static Object compilerEvaluate(String code, Map<String, Object> params, String funcName, Object[] args)
            throws Exception {
        Script script = SCRIPT_CACHE.get(code);
        if (script == null) {
            return null;
        }
        synchronized (ScriptUtils.class) {
            if (TYPE_GROOVY.equals(script.getType())) {
                Bindings bindings = script.getCompiledScript().getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
                if (params != null) {
                    params.forEach(bindings::put);
                }
                return script.getCompiledScript().eval(bindings);
            } else if (TYPE_MVEL.equals(script.getType())) {
                CompiledExpression compiledExpression = script.getCompiledExpression();
                return MVEL.executeExpression(compiledExpression, params);
            } else {
                throw new Exception("不支持脚本类型," + script.getType());
            }
        }
    }

    // 编译groovy脚本
    private static GroovyObject compilerClassGroovy(Script script) {
        try {
            Class<GroovyObject> groovyClass = (Class<GroovyObject>) loader.parseClass(script.getContent());
            return groovyClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("groovy compilerGroovy exception:", e);
            return null;
        } finally {
            if (loader != null) {
                loader.clearCache();
            }
        }
    }

    // 编译groovy脚本 (JSR 223)
    private static Script compilerGroovy(Script script) {
        try {
            CompiledScript compile = ((Compilable) engine).compile(script.getContent());
            script.setCompiledScript(compile);
            return compile == null ? null : script;
        } catch (Exception e) {
            logger.error("groovy compilerGroovy exception:", e);
            return null;
        }
    }

    // 编译mvel脚本
    private static Script compilerMvel(Script script) {
        ExpressionCompiler compiler = new ExpressionCompiler(script.getContent());
        CompiledExpression exp = compiler.compile();
        script.setCompiledExpression(exp);
        return exp == null ? null : script;
    }

    //加载脚本源码
    public static Supplier<Map<String, Script>> getScriptSourceMap() {
        return () -> {
            Map<String, Script> scriptMap = new HashMap<>();
            try {
                String content = Files.readString(Paths.get(
                        "/Users/zhaocong/IdeaProjects/async-nio-concurrent/src/main/resources/groovy/Function.groovy"));
                Script script = new Script("FUNCTIONS");
                script.setType(TYPE_GROOVY);
                script.setContent(content);
                scriptMap.put(script.getCode(), script);

                Script script2 = new Script("FUNCTIONS2");
                script2.setType(TYPE_GROOVY);
                script2.setContent("return 'hi~,'+text+' cnt:'+cnt");
                scriptMap.put(script2.getCode(), script2);
            } catch (Exception e) {
                logger.error("groovy getScriptMap exception:", e);
            }
            //            logger.info("scriptMap:{}", toJSON(scriptMap));
            return scriptMap;
        };
    }

    // 3s加载一次
    @Scheduled(fixedDelay = 3000)
    public static void loadAndCompilerScript() {
        Map<String, Script> scriptMap = getScriptSourceMap().get();
        scriptMap.forEach((k, v) -> SCRIPT_CACHE.compute(k, (k0, v0) -> {
            Script script;
            if (TYPE_GROOVY.equals(v.getType())) {
                script = v.getContent().equals(v0 == null ? null : v0.getContent()) ? v0
                                                                                    : compilerGroovy(v);
            } else if (TYPE_MVEL.equals(v.getType())) {
                script = v.getContent().equals(v0 == null ? null : v0.getContent()) ? v0
                                                                                    : compilerMvel(v);
            } else {
                return v0;
            }
            if (script == null) {
                return v0;
            }
            return script;
        }));
    }

    public static void main(String[] args) {
        SpringApplication.run(AsyncNioConcurrentApplication.class, args);

        // 加载并编译脚本
        loadAndCompilerScript();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                Object result = compilerEvaluate("FUNCTIONS", Map.of("text", "hello groovy!", "cnt", 1), null, null);
                logger.info("result:{}", result);

                Object result2 = compilerEvaluate("FUNCTIONS2", Map.of("text", "hello mvel!", "cnt", 999), null, null);
                logger.info("result:{}", result2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
