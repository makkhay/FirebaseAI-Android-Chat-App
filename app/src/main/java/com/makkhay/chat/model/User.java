package com.makkhay.chat.model;



public class User {
    public String name;
    public String email;
    public String avata;
    public Message message;


    public User(){
        message = new Message();
        message.setChatBotName(" ");
    }
}
