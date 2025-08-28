/*
 * 프로그램명 : SQLXml
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Model
 */
package com.mosom.common.standalone.cache.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mosom.common.standalone.cache.Identifier;

class SQLXml implements ImmutableSQLXml {

    @JsonIgnore
    private Identifier identifier;

    private String group;

    private String name;

    private String comments;

    private String programCode;

    private String statement;

    private long time;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    @Override
    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    @Override
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SQLXml{" +
                "identifier=" + identifier +
                ", group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", comments='" + comments + '\'' +
                ", programCode='" + programCode + '\'' +
                ", statement='" + statement + '\'' +
                ", time=" + time +
                '}';
    }

}
