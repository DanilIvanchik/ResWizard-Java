package com.reswizard.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RestController
public class StaticOutputController {
    @GetMapping("/css/{code}.css")
    @ResponseBody
    public ResponseEntity<String> styles(@PathVariable("code") String code) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("static/css/"+code+".css");
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = bf.readLine()) != null){
            sb.append(line+"\n");
        }

        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        return new ResponseEntity<String>( sb.toString(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/js/{code}.js")
    @ResponseBody
    public ResponseEntity<String> js(@PathVariable("code") String code) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("static/js/"+code+".js");
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = bf.readLine()) != null){
            sb.append(line+"\n");
        }

        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        return new ResponseEntity<String>( sb.toString(), httpHeaders, HttpStatus.OK);
    }
}
