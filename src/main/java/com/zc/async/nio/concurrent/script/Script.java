package com.zc.async.nio.concurrent.script;

import javax.script.CompiledScript;

/**
 * @author coderzc
 * Created on 2020-08-26
 */
public class Script {

    private String code;

    private String type;

    private String content;

    private CompiledScript compiledScript;

    public Script(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public CompiledScript getCompiledScript() {
        return compiledScript;
    }

    public void setCompiledScript(CompiledScript compiledScript) {
        this.compiledScript = compiledScript;
    }
}
