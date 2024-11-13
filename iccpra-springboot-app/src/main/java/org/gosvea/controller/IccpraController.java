package org.gosvea.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Delete;
import org.gosvea.pojo.Icpie;
import org.gosvea.pojo.Result;

import org.gosvea.service.EmailService;
import org.gosvea.service.IccpraService;
import org.gosvea.utils.JwtUtil;
import org.gosvea.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/icpie")
@Validated
public class IccpraController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private IccpraService iccpraService;
    //register
    @PostMapping("/register")
    public Result register(String icpiename,String password)
    {
        Icpie ie=iccpraService.findByIcpieName(icpiename);
        if(ie==null)
        {
            iccpraService.registerIcpie(icpiename,password);
            return Result.success("success");
        }
        else{
            return Result.error("error");
        }
    }


    //login
    @PostMapping("/login")
    public Result<String> login(@NotNull(message = "name can‘t null")  String icpiename, @NotBlank(message = "password can't be null") String password){

        Icpie loginicpie=iccpraService.findByIcpieName(icpiename);
        if(loginicpie==null)
        {
            return Result.error("No this Icpie");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hashedPassword = passwordEncoder.encode(password);
        if(passwordEncoder.matches(password, hashedPassword))
        {

            Map<String,Object> claims=new HashMap<>();
            claims.put("id",loginicpie.getId());
            claims.put("firstname",loginicpie.getFirstname());
            claims.put("icpiename",loginicpie.getIcpiename());
            claims.put("role",loginicpie.getRole());
            claims.put("email",loginicpie.getEmail());
            String token=JwtUtil.genToken(claims);


            if (JwtUtil.hasRole(token, "ROLE_ICPIE")) {
                //System.out.println("ss");
                //emailService.sendEmailNotification(JwtUtil.getUserEmailFromToken(token),"Login successfully","Hello World");
                return Result.success( token);
            } else {
                //System.out.println("ff");
                return Result.success( token);
            }
            //return Result.success(token);
        }

        return Result.error("Password incorrect");
    }


    //get icpie information
    @GetMapping("/icpieinfo")
    public Result<Icpie> icpieinfo(){
        Map<String,Object> claims=ThreadLocalUtil.get();
        String icpiename=(String)claims.get("icpiename");
        Icpie icpie=iccpraService.findByIcpieName(icpiename);
        return Result.success(icpie);
    }


    //updtae icpie information
    @PutMapping("/update")
    public Result updateIcpieInformation(@RequestBody @Validated Icpie icpie){
        iccpraService.update(icpie);
        return Result.success();
    }


    @PatchMapping("/updatePwd")
    public Result updateIcpiePassword(@RequestBody Map<String,String> passwordmaps){
        String oldPassword=passwordmaps.get("old_password");
        String newPassword=passwordmaps.get("new_password");
        String confirmPassword=passwordmaps.get("confirm_password");

        Map<String,Object> claims=ThreadLocalUtil.get();
        String icpieName=(String)claims.get("icpiename");
        Icpie loginicpie=iccpraService.findByIcpieName(icpieName);
        if(loginicpie.getPassword().equals(oldPassword))
        {
            if(newPassword.equals(confirmPassword))
            {
                iccpraService.updatePassword(newPassword);
            }
            else
            {
                return Result.error("Password is not same");
            }

        }
        else
        {
            return Result.error("Password incorrect");
        }
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result deleteIcpie(Integer icpieId){
        try{
            iccpraService.deleteIcpie(icpieId);
            return Result.success("Delete Icpie successfully");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage() + "\n" );
        }
    }
}
