/*
 * 프로그램명 : Code
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Model
 */
package com.mosom.common.standalone.cache.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mosom.common.standalone.cache.Identifier;

import java.io.Serializable;

class Code implements ImmutableCode, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Identifier identifier;

    private Category category;

    private String key1;

    private String key2;

    private int orderSequence;

    private String comment1;

    private String comment2;

    private String comment3;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

    private String option5;

    private String option6;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    @Override
    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    @Override
    public int getOrderSequence() {
        return orderSequence;
    }

    public void setOrderSequence(int orderSequence) {
        this.orderSequence = orderSequence;
    }

    @Override
    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    @Override
    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    @Override
    public String getComment3() {
        return comment3;
    }

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    @Override
    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    @Override
    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    @Override
    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    @Override
    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    @Override
    public String getOption5() {
        return option5;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }

    @Override
    public String getOption6() {
        return option6;
    }

    public void setOption6(String option6) {
        this.option6 = option6;
    }

    @Override
    public String toString() {
        return "Code{" +
                "identifier=" + identifier +
                ", category=" + category +
                ", key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                ", orderSequence=" + orderSequence +
                ", comment1='" + comment1 + '\'' +
                ", comment2='" + comment2 + '\'' +
                ", comment3='" + comment3 + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                ", option3='" + option3 + '\'' +
                ", option4='" + option4 + '\'' +
                ", option5='" + option5 + '\'' +
                ", option6='" + option6 + '\'' +
                '}';
    }

}
