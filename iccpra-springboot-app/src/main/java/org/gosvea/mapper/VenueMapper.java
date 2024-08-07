package org.gosvea.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.gosvea.pojo.PageResponse;
import org.gosvea.pojo.Venue;
import org.gosvea.pojo.VenueSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface VenueMapper {

    @Insert("insert into venue(state, city, instructor, address, time_zone, cancellation_policy, payment_mode, nonrefundable_fee, fob_key, deposit, membership_fee, usage_fee, refundable_status, book_method, registration_link) " +
            "values(#{state}, #{city}, #{instructor}, #{address}, #{timeZone}, #{cancellationPolicy}, #{paymentMode}, #{nonrefundableFee}, #{fobKey}, #{deposit}, #{membershipFee}, #{usageFee}, #{refundableStatus}, #{bookMethod}, #{registrationLink})")
    void add(Venue venue);

    List<Venue> list(String state, String city, Integer instructor, String paymentMethod, String timeZone);
    //@Update("update venue set state=#{state},city=#{city},instructor=#{instructor},address=#{address},time_zone=#{timeZone},cancellation_policy=#{cancellationPolicy},payment_mode=#{paymentMode},nonrefundable_fee=#{nonrefundableFee},fob_key=#{fobKey},deposit=#{deposit},membership_fee=#{membershipFee},usage_fee=#{usageFee},refundable_status=#{refundableStatus},book_method=#{bookMethod},registration_link=#{registrationLink} where id=#{id}")
    void updateVenue(Venue venue);
    @Delete("delete from venue where id=#{venueId}")
    void deleteVenue(Integer venueId);
    @Update("update venueschedule set date=#{date},start_time=#{startTime},end_time=#{endTime} where venue_id=#{venueId}")
    void updateVenueSchedule(LocalDate date, LocalTime startTime, LocalTime endTime, Integer venueId);
    @Delete("delete from venueschedule where venue_id=#{venueId}")
    void deleteVenueSchedule(Integer venueId);
    @Select("select * from venueschedule where venue_id=#{venueId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "venueId", column = "venue_id"),
            @Result(property = "date", column = "date", javaType = LocalDate.class, jdbcType = JdbcType.DATE),
            @Result(property = "startTime", column = "start_time", javaType = LocalTime.class, jdbcType = JdbcType.TIME),
            @Result(property = "endTime", column = "end_time", javaType = LocalTime.class, jdbcType = JdbcType.TIME),
            @Result(property = "isBooked", column = "is_booked")
    })
    List<VenueSchedule> getVenueSchedule(Integer venueId);

    @Insert("insert into venueschedule(date,start_time,end_time,venue_id,course_title) values(#{date},#{startTime},#{endTime},#{venueId},#{courseTitle}) ")
    void addVenueSchedule(VenueSchedule venueSchedule);
    @Delete("delete from venueschedule where id=#{id}")
    void deleteVenueScheduleSingle(Integer id);
    @Select("select * from venue")
    List<Venue> getAllVenues();

}
