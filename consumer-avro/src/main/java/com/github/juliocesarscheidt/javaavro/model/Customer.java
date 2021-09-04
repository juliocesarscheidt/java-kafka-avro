package com.github.juliocesarscheidt.javaavro.model;

public class Customer {

  private String first_name;

  private String last_name;

  private int age;

  private float height;

  private float weight;

  private Boolean automated_email;

  public Customer(String first_name, String last_name, int age, float height, float weight, Boolean automated_email) {
    this.first_name = first_name;
    this.last_name = last_name;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.automated_email = automated_email;
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public String getLast_name() {
    return last_name;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getWeight() {
    return weight;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public Boolean getAutomated_email() {
    return automated_email;
  }

  public void setAutomated_email(Boolean automated_email) {
    this.automated_email = automated_email;
  }

  @Override
  public String toString() {
    return "Customer [first_name=" + first_name + ", last_name=" + last_name + ", age=" + age + ", height=" + height
      + ", weight=" + weight + ", automated_email=" + automated_email + "]";
  }
}
