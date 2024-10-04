package org.gosvea.service.impl;

import org.gosvea.mapper.IccpraMapper;
import org.gosvea.pojo.Icpie;
import org.gosvea.service.IccpraService;
import org.gosvea.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class IccpraServiceImpl implements IccpraService {

    @Autowired
    private IccpraMapper iccpraMapper;


    private static final Set<String> VALID_ROLES=Set.of("ICPIS","ICPIM","ICPIE");

    @Override
    public Icpie findByIcpieName(String icpiename) {
        Icpie ie=iccpraMapper.findByIcpieName(icpiename);
        return ie;
    }

    @Override
    public void registerIcpie(String icpiename, String password) {
        iccpraMapper.registerIcpie(icpiename,password);
    }

    @Override
    public void update(Icpie icpie) {
        iccpraMapper.update(icpie);
    }

    @Override
    public void updatePassword( String newPassword) {
        Map<String,Object> map= ThreadLocalUtil.get();
        Integer id=(Integer)map.get("id");
        iccpraMapper.updatePassword(newPassword,id);
    }

    @Override
    public void deleteIcpie(Integer icpieId) {
        iccpraMapper.deleteIcpie(icpieId);
    }

    @Override
    public void processIcpie(Icpie icpie) {
        if (!VALID_ROLES.contains(icpie.getRole())) {
            throw new IllegalArgumentException("Invalid role: " + icpie.getRole());
        }
    }
}
