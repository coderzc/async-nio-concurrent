package com.zc.async.nio.concurrent.script;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.groovy.GroovyScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import com.zc.async.nio.concurrent.reactor.HomeController;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * @author zhaocong <zhaocong@kuaishou.com>
 * Created on 2020-08-26
 */
public class ScriptUtils {
    public static final String TYPE_GROOVY = "groovy";
    public static final String TYPE_MVEL = "mvel";

    public static final String GROOVY_COMMON_IMPORTS = "groovyCommonImports";
    public static final Map<String, GroovyObject> GROOVY_CACHE = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

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

    public static Object compilerEvaluate(Script script, Map<String, Object> params, Object[] args) {
        GroovyObject groovyObject = GROOVY_CACHE.computeIfAbsent(script.getCode(), (k) -> getGroovyObject(script));
        if (groovyObject == null) {
            return null;
        }
        if (params != null) {
            params.forEach(groovyObject::setProperty);
        }
        return groovyObject.invokeMethod(StringUtils.isBlank(script.getName()) ? "run" : script.getName(), args);
    }

    public static GroovyObject getGroovyObject(Script script) {
        GroovyClassLoader loader = null;
        try {
            CompilerConfiguration config = new CompilerConfiguration();
            config.setSourceEncoding("UTF-8");

            loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
            Class<GroovyObject> groovyClass = (Class<GroovyObject>) loader.parseClass(script.getContent());
            return groovyClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("groovy compiler exception:", e);
            return null;
        } finally {
            if (loader != null) {
                loader.clearCache();
            }
        }
    }

    // 一分钟加载一次
    @Scheduled(fixedDelay = 1000)
    public static void loadScript() {
        Supplier<List<Script>> supplier = () -> {
            List<Script> scriptList_ = new ArrayList<>();
            try {
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                String content;
                content = Files.readString(Paths.get(path, "groovy/Function.groovy"));
                Script script = new Script();
                script.setType(TYPE_GROOVY);
                script.setContent(content);
                script.setCode("FUNCTIONS");
                scriptList_.add(script);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return scriptList_;
        };
        List<Script> scriptList = supplier.get();
        for (Script script0 : scriptList) {
            GROOVY_CACHE.put(script0.getCode(), getGroovyObject(script0));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 加载并编译脚本
        new Thread(() -> {
            try {
                loadScript();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            Script script = new Script("FUNCTIONS");
            //        script.setName("sayHello");
            //        Object result = compiler(script, Map.of("titile",""), new Object[] {"coderzc", 25});
            Object result = compilerEvaluate(script, Map.of("text", "hello groovy!"), null);
            System.out.println(result);

            Thread.sleep(5000);
        }
    }
}
