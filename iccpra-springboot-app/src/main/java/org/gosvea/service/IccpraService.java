package org.gosvea.service;

import org.gosvea.pojo.Icpie;



public interface IccpraService {

    public Icpie findByIcpieName(String icpiename);

    public void registerIcpie(String icpiename,String password);

    void update(Icpie icpie);

    void updatePassword( String newPassword);

    void deleteIcpie(Integer icpieId);
}
