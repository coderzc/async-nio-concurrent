package com.zc.async.nio.concurrent.script;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
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

    public static final String GROOVY_COMMON_IMPORTS = "groovyCommonImports";
    public static final Map<String, GroovyObject> GROOVY_CACHE = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ScriptUtils.class);
    private static final GroovyClassLoader loader;

    static {
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
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
        } else if (TYPE_MVEL.equals(script.getType())) {
            //            return MvelUtil.execute(script.getCompiledContent(), context);
        }
        return null;
    }

    // 执行编译后的脚本
    public static Object compilerEvaluate(String code, Map<String, Object> params, String funcName, Object[] args) {
        GroovyObject groovyObject = GROOVY_CACHE.computeIfAbsent(code, (k) -> {
            Map<String, Script> scriptMap = getScriptMap().get();
            Script script0 = scriptMap.get(code);
            if (script0 == null || script0.getContent() == null) {
                return null;
            }
            return compilerGroovy(script0);
        });
        if (groovyObject == null) {
            return null;
        }

        synchronized (ScriptUtils.class) {
            if (params != null) {
                params.forEach(groovyObject::setProperty);
                groovyObject.setProperty("propertyKeySet", params.keySet());
            }
            return groovyObject.invokeMethod(StringUtils.isBlank(funcName) ? "run" : funcName, args);
        }
    }

    // 编译groovy脚本
    private static GroovyObject compilerGroovy(Script script) {
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

    //加载脚本源码
    private static Supplier<Map<String, Script>> getScriptMap() {
        return () -> {
            Map<String, Script> scriptMap = new HashMap<>();
            try {
                String content = Files.readString(Paths.get(
                        "/Users/zc/IdeaProjects/async-nio-concurrent/src/main/resources/groovy/Function.groovy"));
                Script script = new Script("FUNCTIONS");
                script.setType(TYPE_GROOVY);
                script.setContent(content);
                scriptMap.put(script.getCode(), script);
            } catch (Exception e) {
                logger.error("groovy getScriptMap exception:", e);
            }
            //            logger.info("scriptMap:{}", toJSON(scriptMap));
            return scriptMap;
        };
    }

    // 一分钟加载一次
    @Scheduled(fixedDelay = 1000)
    public static void loadAndCompilerScript() {
        Map<String, Script> scriptMap = getScriptMap().get();
        scriptMap.forEach((k, v) -> GROOVY_CACHE.compute(k, (k0, v0) -> {
            GroovyObject groovyObject = compilerGroovy(v);
            if (groovyObject == null) {
                return v0;
            }
//            synchronized (ScriptUtils.class) {
//                if (v0 != null) {
//                    Set<String> propertyKeySet = (Set<String>) v0.getProperty("propertyKeySet");
//                    if (propertyKeySet != null) {
//                        propertyKeySet.forEach(property -> {
//                            groovyObject.setProperty(property, v0.getProperty(property));
//                        });
//                    }
//                }
//            }
            return groovyObject;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
