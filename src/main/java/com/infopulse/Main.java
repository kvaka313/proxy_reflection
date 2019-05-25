package com.infopulse;

import java.lang.annotation.*;
import java.lang.reflect.*;

@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation{
}

interface InterfaceA{
    public Integer getA();

    public void setA(Integer a);
}

@MyAnnotation
class A implements InterfaceA{
    private Integer a;

    public A(){
        this.a = 0;
    }

    public A(int aa){
        this.a = aa;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        System.out.println("setA");
        this.a = a;
    }
}

class CustomInvocationHandler implements InvocationHandler{
    private Object original;

    CustomInvocationHandler(Object original){
        this.original = original;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable{
        System.out.println("yes!");

        return method.invoke(original, args);
    }
}

class FactoryA{
    private InterfaceA pa;
    public FactoryA(){
        Class classA = A.class;
        MyAnnotation annotation = (MyAnnotation)classA.getAnnotation(MyAnnotation.class);
        A original = new A();
        if(annotation != null){
           CustomInvocationHandler invocationHandler = new CustomInvocationHandler(original);
           pa = (InterfaceA)Proxy.newProxyInstance(classA.getClassLoader(), classA.getInterfaces(), invocationHandler);
        } else {
            pa = original;
        }

    }

    public InterfaceA getA(){
       return pa;
    }
}


public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Class c3 = Class.forName("com.infopulse.A");
        Field[] fields = c3.getDeclaredFields();
        for(Field f:fields){
            System.out.println(f.getName()+":"+f.getType().toString());
        }

        Method[] methods = c3.getDeclaredMethods();
        for(Method m:methods){
            System.out.println(Modifier.toString(m.getModifiers())+" " +m.getName());
        }

        A pa =(A)c3.newInstance();
        Field fieldA = c3.getDeclaredField("a");
        fieldA.setAccessible(true);
        fieldA.set(pa, 300);
        System.out.println(pa.getA());

        Method setAmethod = c3.getMethod("setA", Integer.class);
        setAmethod.invoke(pa, new Integer(600));
        System.out.println(pa.getA());
        Constructor constructor = c3.getDeclaredConstructor(int.class);
        A pa1= (A)constructor.newInstance(new Integer(900));
        System.out.println(pa1.getA());

        FactoryA factoryA = new FactoryA();
        InterfaceA paa = factoryA.getA();
        paa.setA(400);

    }
}
