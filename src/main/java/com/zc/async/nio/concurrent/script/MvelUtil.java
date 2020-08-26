//package com.zc.async.nio.concurrent.script;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author cpderzc
// * Created on 2020-08-26
// */
//public class MvelUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MvelUtil.class);
//    private static final Map<String, Object> IMPORTS = new HashMap();
//
//    static {
//        // jit先禁用, 整体功能验证后再去掉
//        System.setProperty("mvel2.disable.jit", "true");
//        SpringExprFunctions.getAllMethods().entrySet().forEach(entry -> {
//            IMPORTS.put(FUNCTION_PREFIX + entry.getKey(), entry.getValue());
//        });
//        AccessorOptimizer defaultAccessorCompiler = OptimizerFactory.getDefaultAccessorCompiler();
//        AccessorOptimizer threadAccessorOptimizer = OptimizerFactory.getThreadAccessorOptimizer();
//        LOGGER.info("defaultAccessorCompiler = {}",
//                defaultAccessorCompiler == null ? "" : defaultAccessorCompiler.getClass()
//                        .getSimpleName());
//        LOGGER.info("threadAccessorOptimizer = {}",
//                threadAccessorOptimizer == null ? "" : threadAccessorOptimizer.getClass()
//                        .getSimpleName());
//    }
//
//    public static Serializable compile(String expression) {
//        // mvel函数调用不加#号（spel使用#）
//        String formattedExpression = StringUtils.replace(expression, "#" + FUNCTION_PREFIX, FUNCTION_PREFIX);
//        return MVEL.compileExpression(formattedExpression, IMPORTS);
//    }
//
//    public static Object execute(Serializable compiled, Map<String, Object> vars) {
//        return MVEL.executeExpression(compiled, vars);
//    }
//
//    public static Serializable compile(String expression, String uk, String type) {
//        try {
//            if (StringUtils.isNotBlank(expression)) {
//                return MvelUtil.compile(expression);
//            }
//        } catch (Throwable t) {
//            LOGGER.warn("mvel compile exception! {} = {} expr = {}", type, uk, expression);
//        }
//        return null;
//    }
//}
