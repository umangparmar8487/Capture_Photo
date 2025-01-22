
# Capture Photo App

## Overview
This app provides functionality for capturing and cropping images using an in-app camera, previewing them, and tracking live location updates using a homescreen widget.

## Features
1. **Home Activity**:
   - A button to navigate to the Camera Activity.

2. **Camera Activity**:
   - Mimics the Google Pay QR scanner UI with a rectangular viewport.
   - Captures and crops images using the rear camera.
   - Saves the cropped images to the local storage.

3. **Image Preview Activity**:
   - Displays the cropped image from the Camera Activity.

4. **Homescreen Widget**:
   - Button to directly open the Camera Activity.
   - Slider to start/stop live location tracking:
     - Updates a persistent notification with live location details.
     - Saves location data (latitude, longitude) to a CSV file.

5. **Location Service**:
   - Fetches live location data every 2 seconds while active.
   - Saves the data in `Downloads/location_data_<timestamp>.csv`.

## Prerequisites
- Android device running API Level 21 (Lollipop) or higher.
- Permissions for Camera, Location, and Storage.

## How to Use
1. Open the app and navigate to the Camera Activity.
2. Align the object in the viewport and capture the image.
3. View the cropped image in the Image Preview Activity.
4. Use the widget:
   - Click "Click Image" to open the Camera Activity.
   - Slide the toggle to start/stop live location tracking.

## Storage Paths
- **Images**: `Pictures/MyCapturedImages`.
- **CSV Files**: `Downloads/location_data_<timestamp>.csv`.

## Tools and Technologies
- AndroidX CameraX for in-app camera functionality.
- FusedLocationProviderClient for live location updates.
- FileWriter for saving location data in CSV format.

## Permissions
The app requires the following permissions:
- Camera: To capture images.
- Location: To track live location.
- Storage: To save images and CSV files.

## Future Enhancements
- Add error handling for storage issues and permission revocation.
- Optimize the viewport cropping for devices with varied aspect ratios.
