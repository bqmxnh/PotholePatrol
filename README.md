# Pothole Patrol - Pothole Detection and Reporting Application
## Introduction
**Pothole Patrol** is an Android application designed to assist users in navigating, detecting, and reporting potholes on roads efficiently. The app leverages accelerometer sensors, GPS, and mapping technologies to collect, process, and display pothole information, enabling authorities to address issues quickly and enhancing road safety.
## Objectives
- Search for locations and provide navigation.
- Automatically detect potholes using mobile device accelerometer sensors.
- Collect and process data in real-time during travel.
- Record precise pothole locations using GPS.
- Display information visually on a map.
- Allow users to manually report potholes and view detailed information.
## Interface Design Overview
![Cover](https://github.com/user-attachments/assets/60a1cb9e-829c-4802-bb1f-64bfe4b782e7)
![Intro video](https://github.com/user-attachments/assets/980da3af-e6a2-4ccc-81fd-794dd47f174f)



## Technologies Used
### Back-end
- **NodeJS:** Built REST API using the Express framework.
- **MongoDB Atlas:** Database for storing information.
- **AWS EC2:** Virtual private server for API deployment.
### Front-end
- **Figma:** User interface design.
- **Android Studio:** Development environment for Android applications.
- **Java:** Primary programming language.
- **Mapping:**
  - **osmdroid:** OpenStreetMap library for map display.
  - **Nominatim API:** OpenStreetMap geocoding service for location search.
  - **OSRM API:** OpenStreetMap Routing Machine for navigation.
- **Charts:** MPAndroidChart library for data visualization.
## Key Features
**1. User Authentication**
- Multi-language support: English, Vietnamese, Korean, Japanese.
- Authentication screens: Login, Sign-up, Password Recovery.

**2. Dashboard**
- Displays overview information:
  - Number of detected potholes.
  - Distance traveled.
  - Number of collisions.
  - Data analysis charts.
- Allows adding new pothole reports.
- View notifications (maintenance and system alerts).

**3. Mapping and Navigation**
- Displays pothole locations on the map.
- Location search and navigation.
- Filter displayed information by severity and time.
- Alerts users when approaching a pothole.
- Notifies when the destination is reached.
- Automatically detects potholes via vibration sensors.
- Supports zoom in/out and manual pothole reporting.

**4. Settings**
- Edit personal information.
- Customize pothole detection sensitivity.
- Manage notifications (sound, vibration).
- Configure privacy settings (data sharing).
- Report issues.
- View terms of use.
- Log out of the account.
# License

The project was developed as part of the **Mobile Application Development** course for educational and research purposes.
