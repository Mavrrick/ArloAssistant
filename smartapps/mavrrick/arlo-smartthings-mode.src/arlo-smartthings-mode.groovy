/**
 *  Arlo Smartthings Mode
 *
 *  Copyright 2018 Mavrrick
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Arlo Smartthings Mode",
    namespace: "Mavrrick",
    author: "Mavrrick",
    description: "Trigger a recording based on device action. Repeat if enabled until condition has passed.",
    category: "My Apps",
    parent: "Mavrrick:Arlo Assistant",
	iconUrl: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX2Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX3Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg"
    )
 
import groovy.time.TimeCategory 

preferences {
	page (name: "mainPage", title: "Setup Smartthings Arlo Mode integration")
	page (name: "modeDefine", title: "What will define what the mode is")    
	page (name: "securityState", title: "What security modes will be used")
	page (name: "modeRulesetSetup", title: "Define rulesets")
//    page (name: "modeLightsOpt", title: "Optional Light Setup")
//	page (name: "modeCameraSetup", title: "Camera setup")
//    page (name: "modeAlarmSetup", title: "Siren Setup")
//    page (name: "notificationSetup", title: "Notification Setup")
}

def mainPage()
{
	dynamicPage(name: "mainPage", title: "Setup Smartthings Arlo Assistant Mode integration", uninstall: true, install: true)
	{
    	section(title: "Please name the Smartthings Arlo Assistant Mode") {
        	label title: "Please name this Smartthings Arlo Mode", required: true, defaultValue: "Smartthings Arlo Mode"
        }
		section("What will determine when this mode is active")
		{
         	href "modeDefine", title: "Define criteria for Arlo Assitant Mode", description: "Define Arlo Assistant Mode"
		}
        section("Camera State Management")
        {
        input "camerasOn", "capability.videoCapture", title: "What cameras do you want to activate in this mode", multiple: true, required: false
        input "camerasOff", "capability.videoCapture", title: "What cameras do you want to deactivate in this mode", multiple: true, required: false
        }
       section("Define Number of Rulesets")
        {
        input "numOfSets", "number", title: "How Many Rule Sets will the mode use?", required: false, range: "1..5", defaultValue: 1       
        } 
		section("Define ruleset values ")
		{
            href "modeRulesetSetup", title: "Define rulesets for mode.", description: "Defin the Rulesets that will be active in mode."
		}
/*		section("Mode Action When triggered")
		{
	       	input name: "numOfSets", type: "number", title: "Number of rule sets", description: "How many different rule sets will you hae", required: true, range: "1...5", defaultValue: 1           	
            href "modeCameraSetup", title: "Camera setup for mode.", description: "Camera setup for mode Action"
			href "modeLightsOpt", title: "Light setup actions for mode.", description: "Define light actions for this mode"
			href "notificationSetup", title: "Notification setup", description: "Notification values for smartapp"
		}*/
	}
}

def modeDefine()
{
	dynamicPage(name: "modeDefine", title: "What will determine if this Arlo Assitant Mode is active", nextPage: "mainPage", uninstall: false, install: false)
	{
		section("Defin Smartthings Mode")
		{
 			input "stmode", "mode", title: "Smartthings Mode that must be active", required: false 
//			input "presense", "capability.presenceSensor", title: "What presense check will be required", required: false
		}

		section("Alarm Mode State integration")
		{
			input "shmUseState", "bool", title: "Do you want to use the SHM/Location Alarm mode", required: false
			input "adtUseState", "bool", title: "Do you want to use the ADT/Smartthings Alarm Panel mode", required: false 
			href "securityState", title: "Security system modes to use", description: "Select security modes that will apply"
		}
		section("Defin Time criteria")
		{
            paragraph "If you use the time values please use it alone. It is not currently setup to work with the ST Mode or alarm state."
			input "timeSetup", "bool", title: "This will enable the mode between these times.", required: false
            input "fromTime", "time", title: "From", required: false
        	input "toTime", "time", title: "To", required: false
            paragraph "Specify all of the days you wish to use this mode for."
			input "days", "enum", title: "Select Days of the Week", required: false, multiple: true, options: ["Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday"]
		}
        section("Define virtual Switch")
        {
            paragraph "If you use a virtual switch please configure it by its self. It is not currently setup to work with the ST Mode or alarm state."
            input "virtualSwitch", "capability.switch", title: "Add virtual switch for mode.", required: false, multiple: false
        }
        section("Is this a general rule")
        {
			input "generalRule", "bool", title: "This is a general rule that does not require validation", required: false
        }
/*        section("Camera State Management")
        {
        input "camerasOn", "capability.videoCapture", title: "What cameras do you want to activate in this mode", multiple: true, required: false
        input "camerasOff", "capability.videoCapture", title: "What cameras do you want to deactivate in this mode", multiple: true, required: false
        }*/
	}
}



def securityState()
{
	dynamicPage(name: "securityState", title: "What security modes will be used", nextPage: "modeDefine", uninstall: false, install: false)
	{
		if (settings.shmUseState) {
		section("Smart Home Monitor mode selection")
			{
            paragraph "Please select the SHM Security mode that will be used to identify this mode" 
            paragraph "What Active alarm mode do you want to monitor for 1= Stay, 2=Away, 3=Disarmed. All other numberical valudes wil be ignored."
        	input "alarmtype1", "number", title: "What type of alarm do you want to trigger from?", required: false, defaultValue: 1        
            }
         }
        if (settings.adtUseState) {
		section("ADT Smartthings Alarm mode selection")
			{
            paragraph "Please select the ADT Smartthings Alarm mode that will be used to identify this mode"       	
			input "panel", "capability.securitySystem", title: "Select ADT Panel for Alarm Status.", required: true
			paragraph "What Active alarm mode do you want to monitor for 1= Arm/Stay, 2=Armed/Away, 3=Disarmed. All other numberical valudes wil be ignored."
        	input "alarmtype2", "number", title: "What type of alarm do you want to trigger from?", required: false, defaultValue: 1        
			}
		}
        section ("Return to Arlo Smartthings Mode setup"){
            href "modeDefine", title: "Return to the previous menu", description: "Return to the previous menu to complete setup."            
		}
	}
}

def modeRulesetSetup()
{
	dynamicPage(name: "modeRulesetSetup", title: "Define Rulesets", nextPage: "mainPage", uninstall: false, install: false)
	{
		section("Ruleset 1")
		{
        	paragraph "Please select triggers for Ruleset 1" 
			input "motion", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
            paragraph "Please select Cameras involved with Ruleset 1"
            input "recordCameras", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
			paragraph "Please define light actions with Ruleset 1"
 	        input "lightAction1", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
			input "switcheSet1", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
			input "switchLevel1", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
			input "switchLevelChg1", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
       		input "lightShutOff", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
     	    input "lightShutOffTime", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
			paragraph "Please define alarm action with Ruleset 1"
            input "alarmAction1", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       		paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        	input "alarmActionType1", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
			input "alarmSiren1", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        	input "alarmDuration1", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
			paragraph "Please define notification setup for Ruleset 1"
            input "notifyEnable", "bool", title: "Do you want to use this mode to send notifications.", required: false
        	input "message", "text", title: "Send this message if activity is detected.", required: false
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input("recipients", "contact", title: "Send notifications to?") {
			input "phone", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
            }
            input "notifyTimeout", "number", title: "Minimum time between messages", required: false
		}
		if (settings.numOfSets > 1){
       		section("Ruleset 2"){
        	paragraph "Please select triggers for Ruleset 2" 
			input "motion2", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound2", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact2", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton2", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration2", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch2", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture2", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
            paragraph "Please select Cameras involved with Ruleset 2"
            input "recordCameras2", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras2", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength2", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
			paragraph "Please define light actions with Ruleset 2"
 	        input "lightAction2", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
			input "switcheSet2", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
			input "switchLevel2", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
			input "switchLevelChg2", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
       		input "lightShutOff2", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
     	    input "lightShutOffTime2", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
			paragraph "Please define alarm action with Ruleset 2"
            input "alarmAction2", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       		paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        	input "alarmActionType2", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
			input "alarmSiren2", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        	input "alarmDuration2", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
			paragraph "Please define notification setup for Ruleset 2"
            input "notifyEnable2", "bool", title: "Do you want to use this mode to send notifications.", required: false
        	input "message2", "text", title: "Send this message if activity is detected.", required: false
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input ("recipients2", "contact", title: "Send notifications to?") {
			input "phone2", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush2", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
            }
            input "notifyTimeout2", "number", title: "Minimum time between messages", required: false        	
        }
        }
        		if (settings.numOfSets > 2){
       		section("Ruleset 3"){
        	paragraph "Please select triggers for Ruleset 3" 
			input "motion3", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound3", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact3", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton3", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration3", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch3", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture3", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
            paragraph "Please select Cameras involved with Ruleset 3"
            input "recordCameras3", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras3", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength3", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
			paragraph "Please define light actions with Ruleset 3"
 	        input "lightAction3", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
			input "switcheSet3", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
			input "switchLevel3", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
			input "switchLevelChg3", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
       		input "lightShutOff3", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
     	    input "lightShutOffTime3", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
			paragraph "Please define alarm action with Ruleset 3"
            input "alarmAction3", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       		paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        	input "alarmActionType3", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
			input "alarmSiren3", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        	input "alarmDuration3", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
			paragraph "Please define notification setup for Ruleset 3"
            input "notifyEnable3", "bool", title: "Do you want to use this mode to send notifications.", required: false
        	input "message3", "text", title: "Send this message if activity is detected.", required: false
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input ("recipients3", "contact", title: "Send notifications to?") {
			input "phone3", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush3", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
            }
            input "notifyTimeout3", "number", title: "Minimum time between messages", required: false        	
        }
        }
            if (settings.numOfSets > 3){
       		section("Ruleset 4"){
        	paragraph "Please select triggers for Ruleset 4" 
			input "motion4", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound4", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact4", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton4", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration4", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch4", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture4", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
            paragraph "Please select Cameras involved with Ruleset 4"
            input "recordCameras4", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras4", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength4", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
			paragraph "Please define light actions with Ruleset 4"
 	        input "lightAction4", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
			input "switcheSet4", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
			input "switchLevel4", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
			input "switchLevelChg4", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
       		input "lightShutOff4", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
     	    input "lightShutOffTime4", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
			paragraph "Please define alarm action with Ruleset 4"
            input "alarmAction4", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       		paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        	input "alarmActionType4", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
			input "alarmSiren4", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        	input "alarmDuration4", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
			paragraph "Please define notification setup for Ruleset 4"
            input "notifyEnable4", "bool", title: "Do you want to use this mode to send notifications.", required: false
        	input "message4", "text", title: "Send this message if activity is detected.", required: false
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input ("recipients4", "contact", title: "Send notifications to?") {
			input "phone4", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush4", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
            }
            input "notifyTimeout4", "number", title: "Minimum time between messages", required: false        	
        }
        }
            if (settings.numOfSets > 4){
       		section("Ruleset 5"){
        	paragraph "Please select triggers for Ruleset 5" 
			input "motion5", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound5", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact5", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton5", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration5", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch5", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture5", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
            paragraph "Please select Cameras involved with Ruleset 5"
            input "recordCameras5", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras5", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength5", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
			paragraph "Please define light actions with Ruleset 5"
 	        input "lightAction5", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
			input "switcheSet5", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
			input "switchLevel5", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
			input "switchLevelChg5", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
       		input "lightShutOff5", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
     	    input "lightShutOffTime5", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
			paragraph "Please define alarm action with Ruleset 5"
            input "alarmAction5", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       		paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        	input "alarmActionType5", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
			input "alarmSiren5", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        	input "alarmDuration5", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
			paragraph "Please define notification setup for Ruleset 5"
            input "notifyEnable5", "bool", title: "Do you want to use this mode to send notifications.", required: false
        	input "message5", "text", title: "Send this message if activity is detected.", required: false
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input ("recipients5", "contact", title: "Send notifications to?") {
			input "phone5", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush5", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
            }
            input "notifyTimeout5", "number", title: "Minimum time between messages", required: false        	
        }
        }
	}
}


def modeCameraSetup()
{
	dynamicPage(name: "modeCameraSetup", title: "Camera Setup", nextPage: "modeLightsOpt")
	{
    	section("Camera ruleset1 setup (Optional)"){
        	input "recordCameras", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
			input "cameras", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
//        	input "alarms", "capability.alarm", title: "Which Alarm(s) to trigger when ADT alarm goes off?", multiple: true, required: false
		}
/*		if (settings.numOfSets > 1){
    	section("Camera ruleset2 setup (Optional)"){
        	input "recordCameras2", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
//			input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
			input "cameras2", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength2", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
        	}
        }
		if (settings.numOfSets > 2){
    	section("Camera ruleset3 setup (Optional)"){
        	input "recordCameras3", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
//			input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
			input "cameras3", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength3", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
        	}
        }
		if (settings.numOfSets > 3){
    	section("Camera ruleset4 setup (Optional)"){
        	input "recordCameras4", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
//			input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
			input "cameras4", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength4", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
        	}
        } 
		if (settings.numOfSets > 4){
    	section("Camera ruleset5 setup (Optional)"){
        	input "recordCameras5", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
//			input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
			input "cameras5", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength5", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
        	}
        }*/
        section ("Return to Arlo Assistant Main page"){
            href "mainPage", title: "Return to the previous menu", description: "Return to the previous menu to complete setup."            
		}
	} 
}

def modeLightsOpt()
{
	dynamicPage(name: "modeLightsOpt", title: "Optional Light Setup", nextPage: "modeAlarmSetup")
    {
    	section("Light Activation options"){
        input "lightAction1", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
		input "switcheSet1", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
		input "switchLevel1", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
		input "switchLevelChg1", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
        input "lightShutOff", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
        input "lightShutOffTime", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
		}		
        section ("Return to Arlo Assistant Main page"){
            href "mainPage", title: "Return to the previous menu", description: "Return to the previous menu to complete setup."            
		}
	}
}

def modeAlarmSetup()
{
	dynamicPage(name: "modeAlarmSetup", title: "Optional Alarm Setup", nextPage: "notificationSetup")
    {
    	section("Setup your alarm options"){
        input "alarmAction1", "bool", title: "Enable Alarm action for this mode", description: "This switch will tell the mode to run the Alarm action.", defaultValue: false, required: true, multiple: false
       	paragraph "Valid alarm types are 1= Siren, 2=Strobe, and 3=Both. All other numberical valudes wil be ignored."
        input "alarmActionType1", "number", title: "What type of alarm do you want to trigger from?", required: false, range:"1..3", defaultValue: 1        
		input "alarmSiren1", "capability.alarm", title: "Set these lights to the specified value below", multiple: true, required: false
        input "alarmDuration1", "number", title: "How many minutes should the alarm stay on for?", required: false, range:"1..15", defaultValue: 1        
		}		
        section ("Return to Arlo Assistant Main page"){
            href "mainPage", title: "Return to the previous menu", description: "Return to the previous menu to complete setup."            
		}
	}
}

def notificationSetup()
{
	dynamicPage(name: "notificationSetup", title: "Notification setup", nextPage: "mainPage")
	{
        section("Via a push notification and/or an SMS message"){
        input "notifyEnable", "bool", title: "Do you want to use this mode to send notifications.", required: false
        input "message", "text", title: "Send this message if activity is detected.", required: false
        }
        section("Via a push notification and/or an SMS message?"){
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input("recipients", "contact", title: "Send notifications to?") {
			input "phone", "phone", title: "Enter a phone number to get SMS.", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
		}
	}
		section("Message repeat options") {
			input "notifyTimeout", "number", title: "Minimum time between messages", required: false
//			input "notifyRepeat", "bool", title: "Enable this switch if you want to recieves messages until someone actively clears the alarm.", description: "Enable this switch if you want to recieves messages until someone actively clears the alarm.", defaultValue: false, required: true, multiple: false
//			input "msgrepeat", "decimal", title: "Minutes", required: false
	}
	}
}



def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	if (stmode) {
	    subscribe(location, "mode", modeTriggerEvt)
    	}
    if (adtUseState) {
    	subscribe(location, "securitySystemStatus", modeTriggerEvt)
        }
    if (shmUseState) {    
        subscribe(location, "alarmSystemStatus", modeTriggerEvt)
        }
    if (virtualSwitch) {
        subscribe(virtualSwitch, "switch.on", modeTriggerEvt) 
        }
    if (timeSetup) {
    	schedule (fromTime, modeTriggerEvt)
        schedule (toTime, modeTriggerEvt)
        }
   	state.noteTime = now()
    state.noteTime2 = now()
    state.noteTime3 = now()
    state.noteTime4 = now()
    state.noteTime5 = now()
    state.modeActive = 0
} 

def modeNowActive (){
	if (contact) {
		subscribe(contact, "contact.open", modeAction)
	}
	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeAction)
	}
	if (motion) {
		subscribe(motion, "motion.active", modeAction)
	}
	if (mySwitch) {
		subscribe(mySwitch, "switch.on", modeAction)
	}
    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeAction)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeAction)
		}
    if (sound) {    
        subscribe(sound, "sound.detected", modeAction)
		}
    if (settings.numOfSets > 1){
    	if (contact2) {
			subscribe(contact2, "contact.open", modeAction2)
				}
		if (acceleration2) {
			subscribe(acceleration2, "acceleration.active", modeAction2)
				}
		if (motion2) {
			subscribe(motion2, "motion.active", modeAction2)
				}
		if (mySwitch2) {
			subscribe(mySwitch2, "switch.on", modeAction2)
					}
    	if (myMoisture2) {
    		subscribe(myMoisture2, "water.wet", modeAction2)
        }
    	if (myButton2) {    
        	subscribe(myButton2, "momentary.pushed", modeAction2)
		}
   		if (sound2) {    
        	subscribe(sound2, "sound.detected", modeAction2)
		}
        }
    if (settings.numOfSets > 2){
    	if (contact3) {
			subscribe(contact3, "contact.open", modeAction3)
				}
		if (acceleration3) {
			subscribe(acceleration3, "acceleration.active", modeAction3)
				}
		if (motion3) {
			subscribe(motion3, "motion.active", modeAction3)
				}
		if (mySwitch3) {
			subscribe(mySwitch3, "switch.on", modeAction3)
					}
    	if (myMoisture3) {
    		subscribe(myMoisture3, "water.wet", modeAction3)
        }
    	if (myButton3) {    
        	subscribe(myButton3, "momentary.pushed", modeAction3)
		}
   		if (sound3) {    
        	subscribe(sound3, "sound.detected", modeAction3)
		}
        }
    if (settings.numOfSets > 3){
    	if (contact4) {
			subscribe(contact4, "contact.open", modeAction4)
				}
		if (acceleration4) {
			subscribe(acceleration4, "acceleration.active", modeAction4)
				}
		if (motion4) {
			subscribe(motion4, "motion.active", modeAction4)
				}
		if (mySwitch4) {
			subscribe(mySwitch4, "switch.on", modeAction4)
					}
    	if (myMoisture4) {
    		subscribe(myMoisture4, "water.wet", modeAction4)
        }
    	if (myButton4) {    
        	subscribe(myButton4, "momentary.pushed", modeAction4)
		}
   		if (sound4) {    
        	subscribe(sound4, "sound.detected", modeAction4)
		}
        }
    if (settings.numOfSets > 4){
    	if (contact5) {
			subscribe(contact5, "contact.open", modeAction5)
				}
		if (acceleration5) {
			subscribe(acceleration5, "acceleration.active", modeAction5)
				}
		if (motion5) {
			subscribe(motion5, "motion.active", modeAction5)
				}
		if (mySwitch5) {
			subscribe(mySwitch5, "switch.on", modeAction5)
					}
    	if (myMoisture5) {
    		subscribe(myMoisture5, "water.wet", modeAction5)
        }
    	if (myButton5) {    
        	subscribe(myButton5, "momentary.pushed", modeAction5)
		}
   		if (sound5) {    
        	subscribe(sound5, "sound.detected", modeAction5)
		}
        }
    if (camerasOn) {
    	camerasOn.on()
    	}
    if (camerasOff) {
    	camerasOff.off()
    	}
   	state.noteTime = now()
    state.noteTime2 = now()
    state.noteTime3 = now()
    state.noteTime4 = now()
    state.noteTime5 = now()
    state.modeActive = 1
} 

def modeActivateEvent(evt){
	if (stmode) {
    		if (evt.value == stmode) {
    			log.debug "Smartthings mode has been validated. Executing Action"
			modeNowActive()
    		}
            else 
            log.debug "No longer in proper ST Mode for integration reseting app"
            unsubscribe()
			initialize()
            }
            }

// Method to activate when a mode defining state changes in Smartthings
def modeTriggerEvt(evt){
    log.debug "${evt.device} has generated a ${evt.name} event with status of ${evt.value}. Checking to see if in mode for this Smartapp"
	if (stmode && shmUseState) {
    	def curMode = location.currentMode
            if (curMode == stmode) {
    		def alarmState = location.currentState("alarmSystemStatus")?.value
        	if (alarmState == "stay" && alarmtype1 == 1) {
        	log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
                    }
        	else if (alarmState == "away" && alarmtype1 == 2) {
        	log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
            else if (alarmState == "off" && alarmtype1 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
            }
            else {
            log.debug "Smartthings mode did not validate. This action does not apply to this mode"
            unsubscribe()
			initialize()
            }
            }
        else if (stmode && adtUseState) {
           	def curMode = location.currentMode
            if (curMode == stmode) {
    			def alarmState = panel.currentSecuritySystemStatus
        	if (alarmState == "armedStay" && alarmtype2 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
            }
        	else if (alarmState == "armedAway" && alarmtype2 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
        	else if (alarmState == "disarmed" && alarmtype2 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
            }
            else {
            	log.debug "Smartthings mode did not validate. This action does not apply to this mode"
                unsubscribe()
				initialize()
                }
        }
		else if (shmUseState) {
    	def alarmState = location.currentState("alarmSystemStatus")?.value
        	log.debug "Identified to use ADT Alarm Mode. Checking what alarm mode is active"
        	if (alarmState == "stay" && alarmtype1 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
                    }
        	else if (alarmState == "away" && alarmtype1 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
            else if (alarmState == "off" && alarmtype1 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeNowActive()
        	}
            else {
            log.debug "Current alarm mode: ${alarmState}. Current alarm setup value: ${alarmtype1}. This is not a valid match. Mode will not execute"
            unsubscribe()
			initialize()
            }
    	}
	else if (adtUseState) {
    	def alarmState = panel.currentSecuritySystemStatus
        	log.debug "Identified to use ADT Alarm Mode. Checking what alarm mode is active"
        	if (alarmState == "armedStay" && alarmtype2 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeNowActive()
            }
        	else if (alarmState == "armedAway" && alarmtype2 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeNowActive()
        	}
        	else if (alarmState == "disarmed" && alarmtype2 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeNowActive()
        	}
            else {
            log.debug "Current alarm mode: ${alarmState}. Current alarm setup value: ${alarmtype2}. This is not a valid match. Mode will not execute"
            unsubscribe()
			initialize()
            }
        }
    else if (stmode) {
    	def curMode = location.currentMode
    		if (curMode == stmode) {
    			log.debug "Smartthings mode has been validated. Executing Action"
			modeNowActive()
    		}
            else {
            log.debug "No longer in proper ST Mode for integration reseting app"
            unsubscribe()
			initialize()
            }
    }
    else if (timeSetup) {
    	def df = new java.text.SimpleDateFormat("EEEE")
		    df.setTimeZone(location.timeZone)
		    def day = df.format(new Date())
		    def dayCheck = days.contains(day)
		    if (dayCheck) {
	   	 	def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
			if (between) {        	
    			log.debug "Time Validation successfull"
			modeNowActive()
    	}
        }
        	else {
            log.debug "Time did not validate. No caction"
            unsubscribe()
			initialize()
            }
            }
	else if (virtualSwitch) {
    	def check = virtualSwitch.currentSwitch
			if (check != "off") {        	
    			log.debug "Virtual Switch mode validation has been validated. Executing Action"
		modeNowActive()
    	}
    else {
    log.debug "Virtual swtich is off and not in proper state for mode"
            unsubscribe()
			initialize()
            }
    }
    else if (generalRule){
    log.debug "No Mode critera defined. Running actions"
		modeNowActive()
    }
    else {
    log.debug "Smartthings in not in applicable conditions for mode to apply."
            unsubscribe()
			initialize()
    }
}

def modeAction(evt){
		log.debug "Ruleset 1 event"
    	if (recordCameras) {
        	cameras.each {
        	def camaraSatus = it.currentClipStatus
            log.debug "Camera Status is ${camaraSatus}"
            if (camaraSatus == "Completed") 
        	{
        	log.debug "Camera is not recording. Submitting clip to record."
    		arloCapture()
    		}
            else {
            log.debug "Camera is active and recording can not be submitted."
            }
            }
            }
        if (lightAction1) {
        	lightAction()
        	}
        if (alarmAction1) {
        	alarmActionOn()
        	}            
            alarmAction1
    	if (notifyEnable){
			def timePassed = now() - state.noteTime
        	log.debug "${new Date(state.noteTime)}"
        	log.debug "${timePassed}"
            if (timePassed > notifyTimeout*60000){
    		sendnotification()
            state.noteTime = now()
            	}
            else {
            log.debug "Not enogh time has passed. No notification is sent"
            	}
            }
            }

def alarmActionOn()    
	{
        switch (alarmActionType1.value)
        	{
            	case 1 :
                	log.debug "Alarm type ${alarmActionType1.value} detected. Turning on siren."
                    alarmSiren1?.siren()
                    break
                case 2 :
                	log.debug "Alarm type ${alarmActionType1.value} detected. Turning on strobe."
                    alarmSiren1?.strobe()
                    break
                case 3 :
                	log.debug "Alarm type ${alarmActionType1.value} detected. Turning on Siren and Strobe."
                    alarmSiren1?.both()
                    break
                default:
					log.debug "Ignoring unexpected alarmtype mode."
        			log.debug "Alarm type ${alarmActionType1.value} detected."
                    break
              }
		runIn((alarmDuration1*60), alarmActionOff)
	}


def alarmActionOff()
	{
    log.debug "Turning off siren"
    alarmSiren1?.off()
    }

def arloCapture() {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Sending cameras message to record with ${clipLength} second capture"
    Date start = new Date()
    Date end = new Date() 
    use( TimeCategory ) {
    	end = start + clipLength.seconds
 	}
    log.debug "Capturing at ${start} to ${end}..."
    cameras.capture(start, start, end)
        use( TimeCategory ) {
    	start = start + clipLength.seconds
 	}
}

def sendnotification() {
def msg = message
        if ( msg == null ) {
        	log.debug "Message not configured. Skipping notification"
            }
        else {
        log.debug "Alarm Notification., '$msg'"
/*        log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'" */
   if (phone) { // check that the user did select a phone number
        if ( phone.indexOf(";") > 0){
            def phones = phone.split(";")
            for ( def i = 0; i < phones.size(); i++) {
                log.debug("Sending SMS ${i+1} to ${phones[i]}")
                sendSmsMessage(phones[i], msg)
            }
        } else {
            log.debug("Sending SMS to ${phone}")
            sendSmsMessage(phone, msg)
        }
    } 
    if (settings.sendPush) {
        log.debug("Sending Push to everyone")
        sendPush(msg)
    }
//def noteTime = now()    
    sendNotificationEvent(msg)	
        if (settings.notifyRepeat)
	{
		runIn((msgrepeat * 60) , notifyRepeatChk)
	}
}
}
        
def lightAction(){
		switcheSet1?.on()
        if (switchLevel1) {
        switchLevel1?.setLevel(${switchLevelChg1})
        }
        if (settings.lightShutOff)
	{
		runIn((lightShutOffTime*60), lightActionChk)
	}
	}
    
def lightActionChk() {
	def curActive = 0
	if (contact) {
		contact.each {
        	def satus = it.currentContact
    		log.debug "Contact is in ${satus} state"
            if (satus == "open") {
        		log.debug("Contact is still open. Leaving Light on")
                curActive = (curActive+1)
                }
            else {
            	log.debug("Contact is closed. Turning the light off")
                }
		}
    log.debug("curActive = ${curActive}")
    }
/*	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	} */
	if (motion) {
		motion.each {
        	def satus = it.currentContact
    		log.debug "motion is in ${satus} state"
            if (satus == "on") {
        		log.debug("Motion is still active. Leaving Light on")
                curActive = (curActive+1)
                }
            else {
            	log.debug("Motion is inactive, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive}")
	}
	if (mySwitch) {
		mySwitch.each {
        	def satus = it.currentSwitch
    		log.debug "mySwitch is in ${satus} state"
            if (satus == "on") {
        		log.debug("Switch is still on. Leaving Light on")
                curActive = (curActive+1)
                }
            else {
            	log.debug("Switch is off, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive}")
	}
/*    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		} */
    if (curActive >0) {
    	log.debug("Current activity value is greater then 1")
        runIn((lightShutOffTime*60), lightActionChk)
        }
    else {
    	log.debug ("no activty found turning off lights")
        		switcheSet1?.off()
		}
	}
    
// Create Routines for ruleset 2

def modeAction2(evt){
		log.debug "Ruleset2 event"
    	if (recordCameras2) {
        	cameras2.each {
        	def camaraSatus2 = it.currentClipStatus
            log.debug "Camera Status is ${camaraSatus2}"
            if (camaraSatus2 == "Completed") 
        	{
        	log.debug "Camera is not recording. Submitting clip to record."
    		arloCapture2()
    		}
            else {
            log.debug "Camera is active and recording can not be submitted."
            }
            }
            }
        if (lightAction2) {
        	lightAction2()
        	}
        if (alarmAction2) {
        	alarmActionOn2()
        	}            
            alarmAction2
    	if (notifyEnable2){
			def timePassed2 = now() - state.noteTime2
        	log.debug "${new Date(state.noteTime2)}"
        	log.debug "${timePassed2}"
            if (timePassed2 > notifyTimeout2*60000){
    		sendnotification2()
            state.noteTime2 = now()
            	}
            else {
            log.debug "Not enogh time has passed. No notification is sent"
            	}
            }
            }

def alarmActionOn2()    
	{
        switch (alarmActionType2.value)
        	{
            	case 1 :
                	log.debug "Alarm type ${alarmActionType2.value} detected. Turning on siren."
                    alarmSiren2?.siren()
                    break
                case 2 :
                	log.debug "Alarm type ${alarmActionType2.value} detected. Turning on strobe."
                    alarmSiren2?.strobe()
                    break
                case 3 :
                	log.debug "Alarm type ${alarmActionType2.value} detected. Turning on Siren and Strobe."
                    alarmSiren2?.both()
                    break
                default:
					log.debug "Ignoring unexpected alarmtype mode."
        			log.debug "Alarm type ${alarmActionType2.value} detected."
                    break
              }
		runIn((alarmDuration2*60), alarmActionOff2)
	}


def alarmActionOff2()
	{
    log.debug "Turning off siren"
    alarmSiren2?.off()
    }

def arloCapture2() {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Sending cameras message to record with ${clipLength2} second capture"
    Date start2 = new Date()
    Date end2 = new Date() 
    use( TimeCategory ) {
    	end2 = start2 + clipLength2.seconds
 	}
    log.debug "Capturing at ${start2} to ${end2}..."
    cameras2.capture(start2, start2, end2)
}

def sendnotification2() {
def msg2 = message2
        if ( msg2 == null ) {
        	log.debug "Message not configured. Skipping notification"
            }
        else {
        log.debug "Alarm Notification., '$msg2'"
/*        log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'" */
   if (phone2) { // check that the user did select a phone number
        if ( phone2.indexOf(";") > 0){
            def phones2 = phone2.split(";")
            for ( def i = 0; i < phones2.size(); i++) {
                log.debug("Sending SMS ${i+1} to ${phones2[i]}")
                sendSmsMessage(phones2[i], msg2)
            }
        } else {
            log.debug("Sending SMS to ${phone2}")
            sendSmsMessage(phone2, msg2)
        }
    } 
    if (settings.sendPush2) {
        log.debug("Sending Push to everyone")
        sendPush(msg2)
    }
//def noteTime = now()    
    sendNotificationEvent(msg2)	
        if (settings.notifyRepeat2)
	{
		runIn((msgrepeat2 * 60) , notifyRepeatChk2)
	}
}
}
        
def lightAction2(){
		switcheSet2?.on()
        if (switchLevel2) {
        switchLevel2?.setLevel(${switchLevelChg2})
        }
        if (settings.lightShutOff2)
	{
		runIn((lightShutOffTime2*60), lightActionChk2)
	}
	}
    
def lightActionChk2() {
	def curActive2 = 0
	if (contact2) {
		contact2.each {
        	def satus2 = it.currentContact2
    		log.debug "Contact is in ${satus2} state"
            if (satus2 == "open") {
        		log.debug("Contact is still open. Leaving Light on")
                curActive2 = (curActive2+1)
                }
            else {
            	log.debug("Contact is closed. Turning the light off")
                }
		}
    log.debug("curActive = ${curActive2}")
    }
/*	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	} */
	if (motion2) {
		motion2.each {
        	def satus2 = it.currentMotion2
    		log.debug "Motion is in ${satus2} state"
            if (satus2 == "on") {
        		log.debug("Motion is still active. Leaving Light on")
                curActive2 = (curActive2+1)
                }
            else {
            	log.debug("Motion is inactive, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive2}")
	}
	if (mySwitch2) {
		mySwitch2.each {
        	def satus2 = it.currentSwitch2
    		log.debug "mySwitch is in ${satus2} state"
            if (satus2 == "on") {
        		log.debug("Switch is still on. Leaving Light on")
                curActive2 = (curActive2+1)
                }
            else {
            	log.debug("Switch is off, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive2}")
	}
/*    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		} */
    if (curActive2 >0) {
    	log.debug("Current activity value is greater then 1")
        runIn((lightShutOffTime2*60), lightActionChk2)
        }
    else {
    	log.debug ("no activty found turning off lights")
        		switcheSet2?.off()
		}
	}

//Rule set 3 routines

def modeAction3(evt){
		log.debug "Ruleset 3 event"
    	if (recordCameras3) {
        	cameras3.each {
        	def camaraSatus3 = it.currentClipStatus
            log.debug "Camera Status is ${camaraSatus3}"
            if (camaraSatus3 == "Completed") 
        	{
        	log.debug "Camera is not recording. Submitting clip to record."
    		arloCapture3()
    		}
            else {
            log.debug "Camera is active and recording can not be submitted."
            }
            }
            }
        if (lightAction3) {
        	lightAction3()
        	}
        if (alarmAction3) {
        	alarmActionOn3()
        	}            
            alarmAction3
    	if (notifyEnable3){
			def timePassed3 = now() - state.noteTime3
        	log.debug "${new Date(state.noteTime3)}"
        	log.debug "${timePassed3}"
            if (timePassed3 > notifyTimeout3*60000){
    		sendnotification3()
            state.noteTime3 = now()
            	}
            else {
            log.debug "Not enogh time has passed. No notification is sent"
            	}
            }
            }

def alarmActionOn3()    
	{
        switch (alarmActionType3.value)
        	{
            	case 1 :
                	log.debug "Alarm type ${alarmActionType3.value} detected. Turning on siren."
                    alarmSiren3?.siren()
                    break
                case 2 :
                	log.debug "Alarm type ${alarmActionType3.value} detected. Turning on strobe."
                    alarmSiren3?.strobe()
                    break
                case 3 :
                	log.debug "Alarm type ${alarmActionType3.value} detected. Turning on Siren and Strobe."
                    alarmSiren3?.both()
                    break
                default:
					log.debug "Ignoring unexpected alarmtype mode."
        			log.debug "Alarm type ${alarmActionType3.value} detected."
                    break
              }
		runIn((alarmDuration3*60), alarmActionOff3)
	}


def alarmActionOff3()
	{
    log.debug "Turning off siren"
    alarmSiren3?.off()
    }

def arloCapture3() {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Sending cameras message to record with ${clipLength3} second capture"
    Date start3 = new Date()
    Date end3 = new Date() 
    use( TimeCategory ) {
    	end3 = start3 + clipLength3.seconds
 	}
    log.debug "Capturing at ${start3} to ${end3}..."
    cameras3.capture(start3, start3, end3)
}

def sendnotification3() {
def msg3 = message3
        if ( msg3 == null ) {
        	log.debug "Message not configured. Skipping notification"
            }
        else {
        log.debug "Alarm Notification., '$msg3'"
/*        log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'" */
   if (phone3) { // check that the user did select a phone number
        if ( phone3.indexOf(";") > 0){
            def phones3 = phone3.split(";")
            for ( def i = 0; i < phones3.size(); i++) {
                log.debug("Sending SMS ${i+1} to ${phones3[i]}")
                sendSmsMessage(phones3[i], msg3)
            }
        } else {
            log.debug("Sending SMS to ${phone3}")
            sendSmsMessage(phone3, msg3)
        }
    } 
    if (settings.sendPush3) {
        log.debug("Sending Push to everyone")
        sendPush(msg3)
    }
//def noteTime = now()    
    sendNotificationEvent(msg3)	
        if (settings.notifyRepeat3)
	{
		runIn((msgrepeat3 * 60) , notifyRepeatChk3)
	}
}
}
        
def lightAction3(){
		switcheSet3?.on()
        if (switchLevel3) {
        switchLevel3?.setLevel(${switchLevelChg3})
        }
        if (settings.lightShutOff3)
	{
		runIn((lightShutOffTime3*60), lightActionChk3)
	}
	}
    
def lightActionChk3() {
	def curActive3 = 0
	if (contact3) {
		contact3.each {
        	def satus3 = it.currentContact3
    		log.debug "Contact is in ${satus3} state"
            if (satus3 == "open") {
        		log.debug("Contact is still open. Leaving Light on")
                curActive3 = (curActive3+1)
                }
            else {
            	log.debug("Contact is closed. Turning the light off")
                }
		}
    log.debug("curActive = ${curActive3}")
    }
/*	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	} */
	if (motion3) {
		motion3.each {
        	def satus3 = it.currentMotion3
    		log.debug "Motion is in ${satus3} state"
            if (satus3 == "on") {
        		log.debug("Motion is still active. Leaving Light on")
                curActive3 = (curActive3+1)
                }
            else {
            	log.debug("Motion is inactive, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive3}")
	}
	if (mySwitch3) {
		mySwitch3.each {
        	def satus3 = it.currentSwitch3
    		log.debug "mySwitch is in ${satus3} state"
            if (satus3 == "on") {
        		log.debug("Switch is still on. Leaving Light on")
                curActive3 = (curActive3+1)
                }
            else {
            	log.debug("Switch is off, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive3}")
	}
/*    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		} */
    if (curActive3 >0) {
    	log.debug("Current activity value is greater then 1")
        runIn((lightShutOffTime3*60), lightActionChk3)
        }
    else {
    	log.debug ("no activty found turning off lights")
        		switcheSet3?.off()
		}
	}
    
// Ruleset 4 Methods

def modeAction4(evt){
		log.debug "Ruleset 4 event"
    	if (recordCameras4) {
        	cameras4.each {
        	def camaraSatus4 = it.currentClipStatus
            log.debug "Camera Status is ${camaraSatus4}"
            if (camaraSatus4 == "Completed") 
        	{
        	log.debug "Camera is not recording. Submitting clip to record."
    		arloCapture4()
    		}
            else {
            log.debug "Camera is active and recording can not be submitted."
            }
            }
            }
        if (lightAction4) {
        	lightAction4()
        	}
        if (alarmAction4) {
        	alarmActionOn4()
        	}            
            alarmAction4
    	if (notifyEnable4){
			def timePassed4 = now() - state.noteTime4
        	log.debug "${new Date(state.noteTime4)}"
        	log.debug "${timePassed4}"
            if (timePassed4 > notifyTimeout4*60000){
    		sendnotification4()
            state.noteTime4 = now()
            	}
            else {
            log.debug "Not enogh time has passed. No notification is sent"
            	}
            }
            }

def alarmActionOn4()    
	{
        switch (alarmActionType4.value)
        	{
            	case 1 :
                	log.debug "Alarm type ${alarmActionType4.value} detected. Turning on siren."
                    alarmSiren4?.siren()
                    break
                case 2 :
                	log.debug "Alarm type ${alarmActionType4.value} detected. Turning on strobe."
                    alarmSiren4?.strobe()
                    break
                case 3 :
                	log.debug "Alarm type ${alarmActionType4.value} detected. Turning on Siren and Strobe."
                    alarmSiren4?.both()
                    break
                default:
					log.debug "Ignoring unexpected alarmtype mode."
        			log.debug "Alarm type ${alarmActionType4.value} detected."
                    break
              }
		runIn((alarmDuration4*60), alarmActionOff4)
	}


def alarmActionOff4()
	{
    log.debug "Turning off siren"
    alarmSiren4?.off()
    }

def arloCapture4() {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Sending cameras message to record with ${clipLength4} second capture"
    Date start4 = new Date()
    Date end4 = new Date() 
    use( TimeCategory ) {
    	end4 = start4 + clipLength4.seconds
 	}
    log.debug "Capturing at ${start4} to ${end4}..."
    cameras4.capture(start4, start4, end4)
}

def sendnotification4() {
def msg4 = message4
        if ( msg4 == null ) {
        	log.debug "Message not configured. Skipping notification"
            }
        else {
        log.debug "Alarm Notification., '$msg4'"
/*        log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'" */
   if (phone4) { // check that the user did select a phone number
        if ( phone4.indexOf(";") > 0){
            def phones4 = phone4.split(";")
            for ( def i = 0; i < phones4.size(); i++) {
                log.debug("Sending SMS ${i+1} to ${phones4[i]}")
                sendSmsMessage(phones4[i], msg4)
            }
        } else {
            log.debug("Sending SMS to ${phone4}")
            sendSmsMessage(phone4, msg4)
        }
    } 
    if (settings.sendPush4) {
        log.debug("Sending Push to everyone")
        sendPush(msg4)
    }
//def noteTime = now()    
    sendNotificationEvent(msg4)	
        if (settings.notifyRepeat4)
	{
		runIn((msgrepeat4 * 60) , notifyRepeatChk4)
	}
}
}
        
def lightAction4(){
		switcheSet4?.on()
        if (switchLevel4) {
        switchLevel4?.setLevel(${switchLevelChg4})
        }
        if (settings.lightShutOff4)
	{
		runIn((lightShutOffTime4*60), lightActionChk4)
	}
	}
    
def lightActionChk4() {
	def curActive4 = 0
	if (contact4) {
		contact4.each {
        	def satus4 = it.currentContact4
    		log.debug "Contact is in ${satus4} state"
            if (satus4 == "open") {
        		log.debug("Contact is still open. Leaving Light on")
                curActive4 = (curActive4+1)
                }
            else {
            	log.debug("Contact is closed. Turning the light off")
                }
		}
    log.debug("curActive = ${curActive4}")
    }
/*	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	} */
	if (motion4) {
		motion4.each {
        	def satus4 = it.currentMotion4
    		log.debug "Motion is in ${satus4} state"
            if (satus4 == "on") {
        		log.debug("Motion is still active. Leaving Light on")
                curActive4 = (curActive4+1)
                }
            else {
            	log.debug("Motion is inactive, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive4}")
	}
	if (mySwitch4) {
		mySwitch4.each {
        	def satus4 = it.currentSwitch4
    		log.debug "mySwitch is in ${satus4} state"
            if (satus4 == "on") {
        		log.debug("Switch is still on. Leaving Light on")
                curActive4 = (curActive4+1)
                }
            else {
            	log.debug("Switch is off, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive4}")
	}
/*    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		} */
    if (curActive4 >0) {
    	log.debug("Current activity value is greater then 1")
        runIn((lightShutOffTime4*60), lightActionChk4)
        }
    else {
    	log.debug ("no activty found turning off lights")
        		switcheSet4?.off()
		}
	}
    
// Methods for Ruleset 5    


def modeAction5(evt){
		log.debug "Ruleset 5 event"
    	if (recordCameras5) {
        	cameras5.each {
        	def camaraSatus5 = it.currentClipStatus
            log.debug "Camera Status is ${camaraSatus5}"
            if (camaraSatus5 == "Completed") 
        	{
        	log.debug "Camera is not recording. Submitting clip to record."
    		arloCapture5()
    		}
            else {
            log.debug "Camera is active and recording can not be submitted."
            }
            }
            }
        if (lightAction5) {
        	lightAction5()
        	}
        if (alarmAction5) {
        	alarmActionOn5()
        	}            
            alarmAction5
    	if (notifyEnable5){
			def timePassed5 = now() - state.noteTime5
        	log.debug "${new Date(state.noteTime5)}"
        	log.debug "${timePassed5}"
            if (timePassed5 > notifyTimeout5*60000){
    		sendnotification5()
            state.noteTime5 = now()
            	}
            else {
            log.debug "Not enogh time has passed. No notification is sent"
            	}
            }
            }

def alarmActionOn5()    
	{
        switch (alarmActionType5.value)
        	{
            	case 1 :
                	log.debug "Alarm type ${alarmActionType5.value} detected. Turning on siren."
                    alarmSiren5?.siren()
                    break
                case 2 :
                	log.debug "Alarm type ${alarmActionType5.value} detected. Turning on strobe."
                    alarmSiren5?.strobe()
                    break
                case 3 :
                	log.debug "Alarm type ${alarmActionType5.value} detected. Turning on Siren and Strobe."
                    alarmSiren5?.both()
                    break
                default:
					log.debug "Ignoring unexpected alarmtype mode."
        			log.debug "Alarm type ${alarmActionType5.value} detected."
                    break
              }
		runIn((alarmDuration5*60), alarmActionOff5)
	}


def alarmActionOff5()
	{
    log.debug "Turning off siren"
    alarmSiren5?.off()
    }

def arloCapture5() {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Sending cameras message to record with ${clipLength5} second capture"
    Date start5 = new Date()
    Date end5 = new Date() 
    use( TimeCategory ) {
    	end5 = start5 + clipLength5.seconds
 	}
    log.debug "Capturing at ${start5} to ${end5}..."
    cameras5.capture(start5, start5, end5)
}

def sendnotification5() {
def msg5 = message5
        if ( msg5 == null ) {
        	log.debug "Message not configured. Skipping notification"
            }
        else {
        log.debug "Alarm Notification., '$msg5'"
/*        log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'" */
   if (phone5) { // check that the user did select a phone number
        if ( phone5.indexOf(";") > 0){
            def phones5 = phone5.split(";")
            for ( def i = 0; i < phones5.size(); i++) {
                log.debug("Sending SMS ${i+1} to ${phones5[i]}")
                sendSmsMessage(phones5[i], msg5)
            }
        } else {
            log.debug("Sending SMS to ${phone5}")
            sendSmsMessage(phone5, msg5)
        }
    } 
    if (settings.sendPush5) {
        log.debug("Sending Push to everyone")
        sendPush(msg5)
    }
//def noteTime = now()    
    sendNotificationEvent(msg5)	
        if (settings.notifyRepeat5)
	{
		runIn((msgrepeat5 * 60) , notifyRepeatChk5)
	}
}
}
        
def lightAction5(){
		switcheSet5?.on()
        if (switchLevel5) {
        switchLevel5?.setLevel(${switchLevelChg5})
        }
        if (settings.lightShutOff5)
	{
		runIn((lightShutOffTime5*60), lightActionChk5)
	}
	}
    
def lightActionChk5() {
	def curActive5 = 0
	if (contact5) {
		contact5.each {
        	def satus5 = it.currentContact5
    		log.debug "Contact is in ${satus5} state"
            if (satus5 == "open") {
        		log.debug("Contact is still open. Leaving Light on")
                curActive5 = (curActive5+1)
                }
            else {
            	log.debug("Contact is closed. Turning the light off")
                }
		}
    log.debug("curActive = ${curActive5}")
    }
/*	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	} */
	if (motion5) {
		motion5.each {
        	def satus5 = it.currentMotion5
    		log.debug "Motion is in ${satus5} state"
            if (satus5 == "on") {
        		log.debug("Motion is still active. Leaving Light on")
                curActive5 = (curActive5+1)
                }
            else {
            	log.debug("Motion is inactive, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive5}")
	}
	if (mySwitch5) {
		mySwitch5.each {
        	def satus5 = it.currentSwitch5
    		log.debug "mySwitch is in ${satus5} state"
            if (satus5 == "on") {
        		log.debug("Switch is still on. Leaving Light on")
                curActive5 = (curActive5+1)
                }
            else {
            	log.debug("Switch is off, Turning the light off")
                }
		}
    log.debug("curActive = ${curActive5}")
	}
/*    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		} */
    if (curActive5 >0) {
    	log.debug("Current activity value is greater then 1")
        runIn((lightShutOffTime5*60), lightActionChk5)
        }
    else {
    	log.debug ("no activty found turning off lights")
        		switcheSet5?.off()
		}
	}