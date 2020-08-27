package com.zc.async.nio.concurrent.script;

import java.io.Serializable;
import java.util.Date;

import javax.script.CompiledScript;

import org.mvel2.compiler.CompiledExpression;

/**
 * @author coderzc
 * Created on 2020-08-26
 */
public class Script {
    private Long id;

    private String code;

    private String name;

    private String type;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    private String content;

    private CompiledExpression compiledExpression;

    private CompiledScript compiledScript;

    public Script(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public CompiledExpression getCompiledExpression() {
        return compiledExpression;
    }

    public void setCompiledExpression(CompiledExpression compiledExpression) {
        this.compiledExpression = compiledExpression;
    }

    public CompiledScript getCompiledScript() {
        return compiledScript;
    }

    public void setCompiledScript(CompiledScript compiledScript) {
        this.compiledScript = compiledScript;
    }
}
