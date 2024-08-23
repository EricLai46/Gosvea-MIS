import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker,Tooltip,useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { Card, CardContent, Typography } from '@mui/material';
import axiosInstance from '../AxiosInstance';
import { useNotification } from '../NotificationContext';


const VenueMap = () => {
  const [venues, setVenues] = useState([]);
  const { showNotification } = useNotification();
  const [mapCenter, setMapCenter] = useState([37.3733939, -121.9182157]);
  useEffect(() => {
    fetchVenues();
  }, []);

  // 当 venues 数据更新时，设置地图的中心为第一个场馆的位置
  useEffect(() => {
    if (venues && venues.length > 0) { // 确保 venues 是一个已定义的数组并且长度大于 0
      const firstVenue = venues[0];
      setMapCenter([Number(firstVenue.latitude), Number(firstVenue.longitude)]);
    }
    console.log("Venues array after setVenues:", venues); // 打印 venues 数组，调试用
  }, [venues]);


  const fetchVenues = async () => {
    try {
      const response = await axiosInstance.get('/venue/venuemap');
      if (response.data.message === "success") {
        setVenues(response.data.data); // 直接设置 venues
        console.log("Venue data:", response);
        showNotification('Venue Information loaded successfully!', 'success');
      } else {
        showNotification('Get Venue Information failed!', 'error');
      }
    } catch (error) {
      showNotification('Get Venue Information failed!', 'error');
    }
  };
    // 输出 venues 数组的内容
    useEffect(() => {
      console.log("Venues array after setVenues:", venues);
    }, [venues]);  

    const getIconByStatus = (status) => {
      let color;
      switch (status) {
        case 'NORMAL':
          color = 'green';
          break;
        case 'CLOSED':
          color = 'gray';
          break;
        case 'VENUEISSUE':
          color = 'yellow';
          break;
        case 'INSTRUCTORISSUE':
          color='red';
          break;
        case 'INVESTIGATION':
          color='blue';
          break;
        default:
          color = 'blue'; 
      }
      return L.divIcon({
        className: 'custom-icon',
        html: `<div style="background-color:${color}; width:20px; height:20px; border-radius:50%;"></div>`,
      });
    };
    
    const countVenuesByStatus = (status) => {
      return venues.filter(venue => venue.venueStatus === status).length;
    };
  
    const CustomControl = () => {
      const map = useMap();
  
      useEffect(() => {
        const customControl = L.control({ position: 'bottomleft' });
  
        customControl.onAdd = function () {
          const div = L.DomUtil.create('div', 'custom-control');
          div.innerHTML = `
            <div style="background-color: white; padding: 5px; border-radius: 5px;">
              <strong>Venue Status Counts:</strong><br />
              Normal: ${countVenuesByStatus('NORMAL')}<br />
              Instructor Issue: ${countVenuesByStatus('INSTRUCTORISSUE')}<br />
              Venue Issue: ${countVenuesByStatus('VENUEISSUE')}<br />
              Closed: ${countVenuesByStatus('CLOSED')}<br />
              Investigation: ${countVenuesByStatus('INVESTIGATION')}
            </div>
          `;
          return div;
        };
  
        customControl.addTo(map);
  
        return () => {
          customControl.remove();
        };
      }, [map]);
  
      return null;
    };
    return (
      <Card style={{ height: '80vh', width: '100%', margin: '20px' }}>
        <CardContent>
          <Typography variant="h5" component="div">
            Venue Map
          </Typography>
          <MapContainer center={mapCenter} zoom={12} style={{ height: '70vh', width: '100%' }}>
            <TileLayer
              url="https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            {Array.isArray(venues) && venues.map(venue => (
              <Marker
                key={venue.id}
                position={[venue.latitude, venue.longitude]}
                icon={getIconByStatus(venue.venueStatus)}
              >
                <Tooltip>
                  <span>
                    <strong>{venue.name}</strong><br />
                    Status: {venue.venueStatus}<br />
                    Address: {venue.address}
                  </span>
                </Tooltip>
              </Marker>
            ))}
            
            <CustomControl/>
            </MapContainer>
         
        </CardContent>
      </Card>
    );
  };
  
  export default VenueMap;