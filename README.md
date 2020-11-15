# Project Overview
## Problem Statement
Home Team Medical Services Divison (HTMSD), a division within Home Team under the Ministry of Home Affairs (MHA), currently adopts a rather manual approach in conducting the medical board process for Home Team personnel. This approach is rather ineffective as it causes a multitude of issues that arise from the manual nature of these processes. From the usage of physical forms to the lack of an integrated system for dynamically supporting medical board processes, these issues reduce the efficiency and adaptability for the HTMSD medical boarding workflow, especially in cases where there is an influx of personnel who requires medical boarding.

## Project Description
This project aims to automate and digitalise the entire end-to-end medical process within HTMSD, enabling newfound capabilities and allowing HTMSD to fully unleash the potential of technology.

<img src="https://i.imgur.com/iKvNvbX.gif" alt="HoMED Project Description" width="375"/>

## Project Solution - Home Team Medical System (HoMED)
+ <b>Dynamic E-form Builder</b> - A utility tool that provides the flexibility for Home Team administrators to dynamically create e-forms out of the box.

+ <b>Fully Electronic Appointment Booking Process</b> - A full-fledged booking system for Home Team medical staff to manage the booking process, as well as providing an easy channel for Home Team personnel to make bookings.

+ <b>Digitalised End-to-End Medical Board Workflow</b> - A fully digitalised service for Home Team medical staff to perform medical boards in a streamlined fashion.

+ <b>Interactive data visualisation tool</b> - A user-friendly visualisation tool to display statistics in an easy to understand manner based on the customised data chosen.

+ <b>Integrated management functions</b> - An all-in-one management system for Home Team personnel to seamlessly manage accounts, consultations and medical boards.

## Deployment Instructions
+ <b>HoMED-ejb (HoMED Backend)</b>
  1. Create a MySQL database and name it as "HoMED". The version of MySQL used in this project is 8.0.
  2. Create 3 Library variables:
     + "Bean_Validation_API_2.0.1" and include the "validation-api-2.0.1.jar" file from the "lib" subfolder to the variable.
	   + "JSON_In_Java" and include the "json-20200518.jar" file from the "lib" subfolder to the variable.
	   + "Apache_Http" and include all the files in "lib/httpcomponents-client-4.5.13" subfolder to the variable.
  3. Attach the library to the EJB module.

+ <b>HoMED-war (HoMED Employee Web Application)</b>
  1. Create 3 Library variables:
     + "PrimeFaces 8.0" and include the "primefaces-8.0.jar" file from the lib subfolder to the variable.
     + "Roma 2.2.0" and include the "roma-theme-2.2.0.jar" file from the lib subfolder to the variable.
	   + "HTML Sanitizer" and include all the files in "lib/HTML Sanitizer" subfolder to the variable.
  2. Attach the library to the Web application.

+ <b>To activate the firing of Push Notification to mobile devices from the Glassfish Server:</b>
  1. Navigate to https://fcm.googleapis.com/fcm/send and retrieve the GlobalSign certificate in .cer format. 
  2. With the certificate, add it to GlassFish cacerts.jks using the following commands:
	    + <b>macOS</b>: "sudo keytool -import -alias [NAME_OF_CERT] -file [PATH_TO_CERT] -keystore [PATH_TO_GLASSFISH_DOMAINS_DOMAIN1_CONFIG_CACERTS.JKS]"
      + <b>Windows</b>: "[PATH_TO_JAVA_JDK_BIN_KEYTOOL.EXE] -import -alias [NAME_OF_CERT] -file [PATH_TO_CERT] -keystore [PATH_TO_GLASSFISH_DOMAINS_DOMAIN1_CONFIG_CACERTS.JKS]"

##### The required URL to access the Web Application is "http://localhost:8080/HoMED-war/".

### Attachment
<img src="https://imgur.com/ov6owwU.png" alt="HoMED Project Poster" width="500"/>
