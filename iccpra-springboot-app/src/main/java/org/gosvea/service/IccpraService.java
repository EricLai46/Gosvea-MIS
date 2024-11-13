package org.gosvea.service;

import org.gosvea.pojo.Icpie;



public interface IccpraService {

     Icpie findByIcpieName(String icpiename);

     void registerIcpie(String icpiename,String password);

    void update(Icpie icpie);

    void updatePassword( String newPassword);

    void deleteIcpie(Integer icpieId);
    void processIcpie(Icpie icpie);



}
