package com.github.yafithekid.project_y.example;

public class Main {
    public static void main(String[] args){
        TResource resource = new TResource();
        resource.testMemory();
        resource.testTime();
        TReturn tReturn = new TReturn();
        int x = tReturn.returnInt();
        System.out.println(tReturn.returnString());
        tReturn.returnVoid();

        AbsService absService = new AbsServiceImpl();
        Service service = new IfaceServiceImpl();
        absService.overrideNoSuper();
        absService.overrideWithSuper();
        service.overload();
        service.overload(1);
    }

}
