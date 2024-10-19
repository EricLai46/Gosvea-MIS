package org.gosvea.service.impl;

import org.gosvea.mapper.VenueMapper;
import org.gosvea.pojo.Instructor;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;
import org.gosvea.service.InstructorService;
import org.gosvea.service.VenueService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;



@Service
public class VenueServiceImpl implements VenueService {


    @Autowired
    private VenueMapper venueMapper;

    private RestTemplate restTemplate;

    @Autowired
    private InstructorService instructorService;
    @Override
    public void add(Venue venue) {


        venueMapper.add(venue);
    }

    @Override
    public PageResponse<Venue> list(Integer pageNum, Integer pageSize,String state, String city, String icpisManager, String timeZone,String venueId,String venueStatus) {
        PageResponse<Venue> ps=new PageResponse<>();
        // 打印分页参数
        //System.out.println("Page number: " + pageNum);
        //System.out.println("Page size: " + pageSize);

        PageHelper.startPage(pageNum,pageSize);
        List<Venue> lv =venueMapper.list(state,city,icpisManager,timeZone,venueId,venueStatus);
        //System.out.println(lv.get(0).getInstructors() );
        if(lv!=null)
        {
            for (Venue venue : lv) {

                Venue.VenueStatus status = venue.getVenueStatus();
                if (status != null) {
                    System.out.println("状态："+status);
                    String statusUppercase = status.getValue().toUpperCase();
                    Venue.VenueStatus updatedStatus = Venue.VenueStatus.fromValue(statusUppercase);
                    venue.setVenueStatus(updatedStatus);
                } else {
                    // 处理 venueStatus 为空的情况，例如设置默认值
                    venue.setVenueStatus(Venue.VenueStatus.NORMAL);  // 或其他默认状态
                }


            }
        }
        Page<Venue> pv=(Page<Venue>) lv;

        ps.setTotalElement(pv.getTotal());
        ps.setItems(pv.getResult());


        return ps;
    }

    @Override
    public void updateVenue(Venue venue) {
//        // 更新场地关联的讲师
//        if (venue.getInstructors() != null && !venue.getInstructors().isEmpty()) {
//            // 先删除旧的关联
//            venueMapper.deleteInstructorVenueRelationsByVenueId(venue.getId());
//
//            // 插入新的关联
//            for (Instructor instructor : venue.getInstructors()) {
//                // 确保讲师存在，或者根据业务逻辑决定如何处理不存在的讲师
//                Instructor existingInstructor = instructorService.getInstructorById(instructor.getId());
//                if (existingInstructor != null) {
//                    venueMapper.addInstructorVenueRelation(instructor.getId(), venue.getId());
//                } else {
//                    // 处理讲师不存在的情况
//                    System.out.println("Instructor with ID " + instructor.getId() + " does not exist.");
//                }
//            }
//        }
        venueMapper.updateVenue(venue);
    }

    @Override
    public void deleteVenue(String venueId) {
        venueMapper.deleteVenue(venueId);
    }

    @Override
    public void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, String venueId) {
        venueMapper.updateVenueSchedule(date,startTime,endTime,venueId);
    }

    @Override
    public void addVenueSchedule(VenueSchedule venueSchedule) {
        venueMapper.addVenueSchedule(venueSchedule);
    }

    @Override
    public void deleteVenueSchedule(String venueId) {
        venueMapper.deleteVenueSchedule(venueId);
    }

    @Override
    public List<VenueSchedule> getVenueSchedule(String venueId) {
        List<VenueSchedule> venueScheduleslist=new ArrayList<>();
        venueScheduleslist=venueMapper.getVenueSchedule(venueId);

        return venueScheduleslist;
    }

    @Override
    public void deleteVenueScheduleSingle(String id) {
        venueMapper.deleteVenueScheduleSingle(id);
    }

    @Override
    public List<Venue> getAllVenues() {
        List<Venue> venues=venueMapper.getAllVenues();
        for (Venue venue : venues) {
            List<VenueSchedule> scheduleList = getVenueSchedule(venue.getId());
            venue.setScheduleList(scheduleList);
        }
        return venues;
    }

    @Override
    public double[] getLatLon(String address) {
        this.restTemplate=new RestTemplate();
        String newAddress=cleanAddress(address);
        String url = "https://nominatim.openstreetmap.org/search?q=" + newAddress + "&format=json&addressdetails=1&limit=1";
        String response=restTemplate.getForObject(url,String.class);


        JSONArray jsonArray=new JSONArray(response);
        if(jsonArray.length()>0)
        {
            JSONObject location=jsonArray.getJSONObject(0);
            double lat=location.getDouble("lat");
            double lon=location.getDouble("lon");
            return new double[]{lat,lon};
        }
        else{
            //System.out.println("No geocoding result found for address: " + address);
            return null;
        }
    }

    @Override
    public void saveLatLon(double[] latlon, String id) {
        venueMapper.saveLatLon(latlon[0],latlon[1],id);
    }

    @Override
    public String cleanAddress(String address) {
        // 去除括号中的内容
        address = address.replaceAll("\\s*\\(.*?\\)", "");

        // 去除以逗号或空格分隔的“Suite”、“Ste”、“Apt”、“Unit”等单元号，同时保留街道号
        address = address.replaceAll(",?\\s*(Suite|Ste|Apt|Apartment|Room|Rm|Unit)\\s*\\d+", "");

        // 去除多余的逗号和空格
        address = address.replaceAll(",\\s*,", ", ")
                .replaceAll("\\s{2,}", " ").trim();
        address = address.replaceAll(",\\s*$", "").trim();
        return address;
    }

    @Override
    public void updateVenueStatus(Integer id,Venue.VenueStatus venueStatus) {
        venueMapper.updateVenueStatus(id, venueStatus);
    }

    @Override
    public Venue getVenueById(String venueId) {
        return venueMapper.getVenueById(venueId);
    }

    @Override
    public void insertListVenues(List<Venue> venueList) {
        venueMapper.insertListVenues(venueList);
    }

    @Override
    public void updateListVenues(List<Venue> venueList) {
        venueMapper.updateListVenues(venueList);
    }

    @Override
    public String getVenueIdByAddress(String address) {
        return venueMapper.getVenueIdByAddress(address);
    }

    @Override
    public  PageResponse<Venue> getNormalStatusVenues(Integer pageNum, Integer pageSize, String state, String timeZone) {
        PageResponse<Venue> ps=new PageResponse<>();
        // 打印分页参数
        //System.out.println("Page number: " + pageNum);
        //System.out.println("Page size: " + pageSize);

        PageHelper.startPage(pageNum,pageSize);
        List<Venue> lv =venueMapper.getNormalStatusVenues(state,timeZone);
        if(lv!=null)
        {
            for (Venue venue : lv) {
                Venue.VenueStatus status = venue.getVenueStatus();
                if (status != null) {
                    String statusUppercase = status.getValue().toUpperCase();
                    Venue.VenueStatus updatedStatus = Venue.VenueStatus.fromValue(statusUppercase);
                    venue.setVenueStatus(updatedStatus);
                } else {
                    // 处理 venueStatus 为空的情况，例如设置默认值
                    venue.setVenueStatus(Venue.VenueStatus.NORMAL);  // 或其他默认状态
                }
            }
        }
        Page<Venue> pv=(Page<Venue>) lv;

        ps.setTotalElement(pv.getTotal());
        ps.setItems(pv.getResult());


        return ps;
    }

    @Override
    public List<Venue> getVenueByInstructorId(String instructorId) {
        return venueMapper.getVenueByInstructorId(instructorId);
    }

    @Override
    public void addLatLonInformationForListVenues(List<Venue> venueList) {
        for(Venue venue:venueList)
        {
            double[] latlon=getLatLon(venue.getAddress());
            if(latlon!=null){
                venue.setLatitude(latlon[0]);
                venue.setLongitude(latlon[1]);
                saveLatLon(latlon,venue.getId());
            }
        }

    }

    @Override
    public void updtaeLatLonInformationForOneVenue(Venue venue) {
        double[] latlon=getLatLon(venue.getAddress());
        if(latlon!=null){
            venue.setLatitude(latlon[0]);
            venue.setLongitude(latlon[1]);
            saveLatLon(latlon,venue.getId());
        }
    }

    @Override
    public List<Venue> getAllSpecStatusVenues(Venue.VenueStatus venueStatus) {
        return venueMapper.getAllSpecStatusVenues(venueStatus);
    }

    @Override
    public void addInstructorVenueRelation(List<String> instructorIds, String venueId) {
        venueMapper.addInstructorVenueRelation(instructorIds,venueId);
    }

    @Override
    public void deleteInstructorVenueRelationsByInstructorId(String id) {
        venueMapper.deleteInstructorVenueRealtionsByInstructorId(id);
    }

    @Override
    public void deleteInstructorVenueRelationsByVenueId(String venueId) {
        venueMapper.deleteInstructorVenueRelationsByVenueId(venueId);
    }

    @Override
    public boolean isInstructrorListChanged(List<String> currentInstructorIds,List<String> previousInstructorIds) {
        if(currentInstructorIds==null&&previousInstructorIds==null)
        {
            return !(currentInstructorIds==null&&previousInstructorIds==null);
        }
        if(currentInstructorIds.size()!=previousInstructorIds.size())
        {
            return true;
        }
        Collections.sort(currentInstructorIds);
        Collections.sort(previousInstructorIds);



        return !currentInstructorIds.equals(previousInstructorIds);
    }

    @Override
    public boolean verifyVenueId(String id) {
        String venueId= venueMapper.verifyVenueId(id);
        if(venueId==null)
            return true;
        else
            return false;
    }

    @Override
    public PageResponse<Venue> icpislist(Integer pageNum, Integer pageSize, String state, String city, String icpisname, String timeZone, String venueId,String venueStatus) {
        PageResponse<Venue> ps=new PageResponse<>();
        // 打印分页参数
        //System.out.println("Page number: " + pageNum);
        //System.out.println("Page size: " + pageSize);

        PageHelper.startPage(pageNum,pageSize);
        List<Venue> lv =venueMapper.icpislist(state,city,icpisname,timeZone,venueId, venueStatus);
        //System.out.println(lv.get(0).getInstructors() );
        if(lv!=null)
        {
            for (Venue venue : lv) {
                Venue.VenueStatus status = venue.getVenueStatus();
                if (status != null) {
                    String statusUppercase = status.getValue().toUpperCase();
                    Venue.VenueStatus updatedStatus = Venue.VenueStatus.fromValue(statusUppercase);
                    venue.setVenueStatus(updatedStatus);
                } else {
                    // 处理 venueStatus 为空的情况，例如设置默认值
                    venue.setVenueStatus(Venue.VenueStatus.NORMAL);  // 或其他默认状态
                }


            }
        }
        Page<Venue> pv=(Page<Venue>) lv;

        ps.setTotalElement(pv.getTotal());
        ps.setItems(pv.getResult());


        return ps;
    }

    @Override
    public VenueSchedule getSingleVenueSchedule(String id) {
        return venueMapper.getSingleVenueSchedule(id);
    }

    @Override
    public List<Map<String, String>> getAllVenueAddress() {
        return venueMapper.getAllVenueAddress();
    }

    @Override
    public  List<Map<String,String>> getAllVenueStatus() {
        return venueMapper.getAllVenueStatus();
    }

    @Override
    public Map<String, Venue> getVenueListMap() {
        return venueMapper.getVenueListMap();
    }


}
