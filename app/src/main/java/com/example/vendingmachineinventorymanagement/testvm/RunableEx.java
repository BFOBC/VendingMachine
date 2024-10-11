package com.example.vendingmachineinventorymanagement.testvm;

abstract class RunableEx implements Runnable{
    String data;
    public RunableEx(String data)
    {
        this.data = data;
    }
}
