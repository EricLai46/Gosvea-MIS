package org.gosvea.service.impl;

import org.gosvea.mapper.VenueMapper;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;
import org.gosvea.service.VenueService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;



@Service
public class VenueServiceImpl implements VenueService {


    @Autowired
    private VenueMapper venueMapper;

    private RestTemplate restTemplate;
    @Override
    public void add(Venue venue) {


        venueMapper.add(venue);
    }

    @Override
    public PageResponse<Venue> list(Integer pageNum, Integer pageSize,String state, String city, Integer instructor, String paymentMethod, String timeZone) {
        PageResponse<Venue> ps=new PageResponse<>();
        // 打印分页参数
        //System.out.println("Page number: " + pageNum);
        //System.out.println("Page size: " + pageSize);

        PageHelper.startPage(pageNum,pageSize);
        List<Venue> lv =venueMapper.list(state,city,instructor,paymentMethod,timeZone);
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
    public void updateVenue(Venue venue) {
        venueMapper.updateVenue(venue);
    }

    @Override
    public void deleteVenue(Integer venueId) {
        venueMapper.deleteVenue(venueId);
    }

    @Override
    public void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, Integer venueId) {
        venueMapper.updateVenueSchedule(date,startTime,endTime,venueId);
    }

    @Override
    public void addVenueSchedule(VenueSchedule venueSchedule) {
        venueMapper.addVenueSchedule(venueSchedule);
    }

    @Override
    public void deleteVenueSchedule(Integer venueId) {
        venueMapper.deleteVenueSchedule(venueId);
    }

    @Override
    public List<VenueSchedule> getVenueSchedule(Integer venueId) {
        List<VenueSchedule> venueScheduleslist=new ArrayList<>();
        venueScheduleslist=venueMapper.getVenueSchedule(venueId);

        return venueScheduleslist;
    }

    @Override
    public void deleteVenueScheduleSingle(Integer id) {
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
            System.out.println("No geocoding result found for address: " + address);
            return null;
        }
    }

    @Override
    public void saveLatLon(double[] latlon, Integer id) {
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
    public Venue getVenueById(Integer venueId) {
        return venueMapper.getVenueById(venueId);
    }




}
