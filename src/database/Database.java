/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.HashMap;

/**
 *
 * @author Vitor Tozzi
 */
public class Database {

    private static String User1Pass = "1234";
    private static String User2Pass = "9876";

    public HashMap<String, String> map;

    // TODO Utilizar hash nas senhas
    
    public Database() {
        map = new HashMap<>();
        map.put("vitortozzi", "1234");
        map.put("ze", "9876");
    }
    
    

}
