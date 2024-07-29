package org.gosvea.mapper;

import org.apache.ibatis.annotations.*;
import org.gosvea.pojo.Icpie;

@Mapper
public interface IccpraMapper {

    @Select("select * from icpies where icpiename=#{icpiename}")
     Icpie findByIcpieName(String icpiename);
    @Insert("insert into icpies (icpiename,password,firstname,lastname) values(#{icpiename},#{password},#{icpiename},#{icpiename})")
     void registerIcpie(String icpiename,String password);

    @Update("update icpies set firstname=#{firstname},lastname=#{lastname},state=#{state},city=#{city},icpiename=#{icpiename},email=#{email} where id=#{id}")
    void update(Icpie icpie);
    @Update("update icpies set password=#{newPassword} where id=#{id}")
    void updatePassword( String newPassword,Integer id);
    @Delete("delete from icpies where id=#{icpieId}")
    void deleteIcpie(Integer icpieId);
}
