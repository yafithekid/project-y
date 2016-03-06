package com.github.yafithekid.project_y.example;

public class Main {
    public static void main(String[] args){
//        TResource resource = new TResource();
//        resource.testMemory();
//        resource.testTime();
//        TReturn tReturn = new TReturn();
//        int x = tReturn.returnInt();
//        System.out.println(tReturn.returnString());
//        tReturn.returnVoid();

        AbsService absService = new AbsServiceImpl();
        AbsServiceImpl absServiceImpl = new AbsServiceImpl();
        Service service = new IfaceServiceImpl();
        System.out.println("scenario 1");
        absService.overrideNoSuper();
        System.out.println("scenario 2");
        absService.overrideWithSuper();
        System.out.println("scenario 3");
        absService.foo();
        System.out.println("scenario 4");
        absService.bar();
        System.out.println("scenario 5");
        absServiceImpl.overrideNoSuper();
        System.out.println("scenario 6");
        absServiceImpl.overrideWithSuper();
        System.out.println("scenario 7");
        absService.foo();
        System.out.println("scenario 8");
        absService.bar();
        System.out.println("scenario 9");
        int x = AbsService.staticInt();
        AbsService.staticVoid();
        System.out.println("scenario 10");
        x = AbsServiceImpl.staticInt();
        AbsService.staticVoid();
        service.overload();
        service.overload(1);
    }
}
