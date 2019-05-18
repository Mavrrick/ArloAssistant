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
	page (name: "securityState", title: "What security modes will be used")
	page (name: "modeTriggers", title: "Smartthings triggered by event")
    page (name: "modeLightsOpt", title: "Optional Light Setup")
	page (name: "modeCameraSetup", title: "Camera setup")
    page (name: "notificationSetup", title: "Notification Setup")
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
         	paragraph "If you use a virtual switch please configure it by its self. It is not currently setup to work with the ST Mode or alarm state."
 			input "stmode", "mode", title: "Smartthings Mode that must be active", required: false
        	paragraph "The below switches will determine if this mode will validate against Alarm system mode."       	
			input "shmUseState", "bool", title: "Do you want to use the SHM/Location Alarm mode", required: false
			input "adtUseState", "bool", title: "Do you want to use the ADT/Smartthings Alarm Panel mode", required: false 
			href "securityState", title: "Security system modes to use", description: "Select security modes that will apply"
            input "virtualSwitch", "capability.switch", title: "Add virtual switch for mode.", required: false, multiple: false
			input "generalRule", "bool", title: "This is a general rule that does not require validation", required: false 
//			input "presense", "capability.presenceSensor", title: "What presense check will be required", required: false
		}

		section("Define triggers for integration mode action")
		{
			input "motion", "capability.motionSensor", title: "Motion Sensor?", required: false, multiple: true
			input "sound", "capability.soundSensor", title: "Audio Sensor?", required: false, multiple: true
			input "contact", "capability.contactSensor", title: "Contact Sensor?", required: false, multiple: true
        	input "myButton", "capability.momentary", title: "What Button?", required: false, multiple: true
			input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false, multiple: true
			input "mySwitch", "capability.switch", title: "Switch?", required: false, multiple: true
        	input "myMoisture", "capability.waterSensor", title: "Moisture Sensor?", required: false, multiple: true
		}
		section("Mode Action When triggered")
		{
//        	input name: "numOfSets", type: "number", title: "Number of rule sets", description: "How many different rule sets will you hae", required: true, range: "1...5", defaultValue: 1           	
            href "modeCameraSetup", title: "Camera setup for mode.", description: "Camera setup for mode Action"
			href "modeLightsOpt", title: "Light setup actions for mode.", description: "Define light actions for this mode"
			href "notificationSetup", title: "Notification setup", description: "Notification values for smartapp"
		}
	}
}

def securityState()
{
	dynamicPage(name: "securityState", title: "What security modes will be used", nextPage: "mainPage")
	{
		section("Smart Home Monitor mode selection")
		{
			if (settings.shmUseState) {
            paragraph "Please select the SHM Security mode that will be used to identify this mode" 
            paragraph "What Active alarm mode do you want to monitor for 1= Stay, 2=Away, 3=Disarmed. All other numberical valudes wil be ignored."
        	input "alarmtype1", "number", title: "What type of alarm do you want to trigger from?", required: false, defaultValue: 1        
            }
         }
        section("ADT Smartthings Alarm mode selection")
		{
            if (settings.adtUseState) {
            paragraph "Please select the ADT Smartthings Alarm mode that will be used to identify this mode"       	
			input "panel", "capability.securitySystem", title: "Select ADT Panel for Alarm Status.", required: true
			paragraph "What Active alarm mode do you want to monitor for 1= Arm/Stay, 2=Armed/Away, 3=Disarmed. All other numberical valudes wil be ignored."
        	input "alarmtype2", "number", title: "What type of alarm do you want to trigger from?", required: false, defaultValue: 1        
			}
		}
        section ("Return to Arlo Smartthings Mode setup"){
            href "mainPage", title: "Return to the previous menu", description: "Return to the previous menu to complete setup."            
		}
	}
}

def modeCameraSetup()
{
	dynamicPage(name: "modeCameraSetup", title: "Camera Setup", nextPage: "modeLightsOpt")
	{
    	section("Camera ruleset1 setup (Optional)"){
        	input "recordCameras", "bool", title: "Enable Camera recording?", description: "This switch will enable cameras to record on alarm events.", defaultValue: false, required: true, multiple: false
//			input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
			input "cameras", "capability.videoCapture", multiple: true, required: false
        	input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording.", required: true, range: "5..120", defaultValue: 120
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
	dynamicPage(name: "modeLightsOpt", title: "Optional Light Setup", nextPage: "notificationSetup")
    {
    	section("Light Activation options"){
        input "lightAction1", "bool", title: "Enable Light action for this mode", description: "This switch will tell the mode to run the light action.", defaultValue: false, required: true, multiple: false
		input "switcheSet1", "capability.switch", title: "These lights will be activated with the action", multiple: true, required: false
		input "switchLevel1", "capability.switchLevel", title: "Set these lights to the specified value below", multiple: true, required: false
		input "switchLevelChg1", "number", title: "What level do you want your lights set at.", required: true, range: "1..100", defaultValue: 100
        input "lightShutOff", "bool", title: "Enable this option to turn off the light automatically", description: "This switch will schedule a check to turn the lights off", defaultValue: false, required: true, multiple: false        
        input "lightShutOffTime", "number", title: "How many minutes to wait before turning off the lights", required: true, range: "1..90", defaultValue: 5
//		input "lightRepeat", "bool", title: "Enable lights to continue flashing as long as arlarm is occuring.", description: "This switch will enable lights to continue to flash long as there is a active alarm.", defaultValue: false, required: true, multiple: false
		}		
/*        section("Flashing Options"){
		input "onFor", "number", title: "On for (default 5000)", required: false
		input "offFor", "number", title: "Off for (default 5000)", required: false
        input "numFlashes", "number", title: "This number of times (default 3)", required: false
		} */
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
			input "notifyRepeat", "bool", title: "Enable this switch if you want to recieves messages until someone actively clears the alarm.", description: "Enable this switch if you want to recieves messages until someone actively clears the alarm.", defaultValue: false, required: true, multiple: false
			input "msgrepeat", "decimal", title: "Minutes", required: false
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
	if (contact) {
		subscribe(contact, "contact.open", modeTriggerEvt)
	}
	if (acceleration) {
		subscribe(acceleration, "acceleration.active", modeTriggerEvt)
	}
	if (motion) {
		subscribe(motion, "motion.active", modeTriggerEvt)
	}
	if (mySwitch) {
		subscribe(mySwitch, "switch.on", modeTriggerEvt)
	}
    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", modeTriggerEvt)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", modeTriggerEvt)
		}
    if (sound) {    
        subscribe(myButton, "sound.detected", modeTriggerEvt)
		}
}

def modeTriggerEvt(evt){
	log.debug "${evt.value} Event has occured. Checking to see if in mode for this Smartapp"
    log.debug "${evt.device} has generated an Event. Checking to see if in mode for this Smartapp"
    log.debug "${evt.displayName} created event. Checking to see if in mode for this Smartapp"
    log.debug "${evt.name} name event. Checking to see if in mode for this Smartapp"    
	if (stmode && shmUseState) {
    	def curMode = location.currentMode
            if (curMode == stmode) {
    		def alarmState = location.currentState("alarmSystemStatus")?.value
        	if (alarmState == "stay" && alarmtype1 == 1) {
        	log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
                    }
        	else if (alarmState == "away" && alarmtype1 == 2) {
        	log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
            else if (alarmState == "off" && alarmtype1 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
            }
            else 
            log.debug "Smartthings mode did not validate. This action does not apply to this mode"
            }
        else if (stmode && adtUseState) {
           	def curMode = location.currentMode
            if (curMode == stmode) {
    			def alarmState = panel.currentSecuritySystemStatus
        	if (alarmState == "armedStay" && alarmtype2 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
            }
        	else if (alarmState == "armedAway" && alarmtype2 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
        	else if (alarmState == "disarmed" && alarmtype2 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
            }
            else 
            	log.debug "Smartthings mode did not validate. This action does not apply to this mode"
        }
		else if (shmUseState) {
    	def alarmState = location.currentState("alarmSystemStatus")?.value
        	log.debug "Identified to use ADT Alarm Mode. Checking what alarm mode is active"
        	if (alarmState == "stay" && alarmtype1 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
                    }
        	else if (alarmState == "away" && alarmtype1 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
            else if (alarmState == "off" && alarmtype1 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current SHM Smartthings alarm mode has been validated. Executing Action."
            	modeAction()
        	}
            else {
            log.debug "Current alarm mode: ${alarmState}. Current alarm setup value: ${alarmtype1}. This is not a valid match. Mode will not execute"
            }
    	}
	else if (adtUseState) {
    	def alarmState = panel.currentSecuritySystemStatus
        	log.debug "Identified to use ADT Alarm Mode. Checking what alarm mode is active"
            log.debug "Current alarm mode: ${alarmState}. Current alarm setup value: ${alarmtype2}."
        	if (alarmState == "armedStay" && alarmtype2 == 1) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeAction()
            }
        	else if (alarmState == "armedAway" && alarmtype2 == 2) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeAction()
        	}
        	else if (alarmState == "disarmed" && alarmtype2 == 3) {
        		log.debug "Current alarm mode: ${alarmState}.Current ADT Smartthings alarm mode has been validated. Executing Action."
				modeAction()
        	}
            else {
            log.debug "Current alarm mode: ${alarmState}. Current alarm setup value: ${alarmtype2}. This is not a valid match. Mode will not execute"
            }
        }
    else if (stmode) {
    	def curMode = location.currentMode
    		if (curMode == stmode) {
    			log.debug "Smartthings mode has been validated. Executing Action"
			modeAction()
    		}
    }
    else if (virtualSwitch) {
    	def check = virtualSwitch.currentSwitch
			if (check != "off") {        	
    			log.debug "Virtual Switch mode validation has been validated. Executing Action"
		modeAction()
    	}
    else
    log.debug "Virtual swtich is off and not in proper state for mode"
    }
    else if (generalRule){
    log.debug "No Mode critera defined. Running actions"
		modeAction()
    }
    else {
    log.debug "Smartthings in not in applicable conditions for mode to apply."
    }
}

def modeAction(){
    	if (recordCameras) {
        	cameras.each {
        	def camaraSatus = it.currentClipStatus
    		log.debug "Camera is in ${camaraSatus} state"
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
    	if (notifyEnable){
    		sendnotification() }
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
    	if (settings.recordRepeat)
	{
		runIn(clipLength, cameraRepeatChk)
	}
}

def cameraRepeatChk() {
		if (mySwitch) {
		def check = mySwitch.currentSwitch
    	log.debug "Switch is in ${check} state"
		if (check != "off") 
        	{
        	log.debug "Switch is still on. Submitting another clip to record."
        arloCapture()   
        	}
		else {
        log.debug "Switch is off and is no longer active recordings are stoping."
		}
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
    sendNotificationEvent(msg)	
        if (settings.notifyRepeat)
	{
		runIn((msgrepeat * 60) , notifyRepeatChk)
	}
}
}

/* def notifyRepeatChk() {
		def alarmActive = panel.currentSecuritySystemStatus
    	log.debug "Current alarms is in ${alarmActive} state"
		if (alarmActive != "disarmed") 
        	{
        	log.debug "Alarm Event is still occuring. Submitting another notification"
        sendnotification()   
        	}
		else {
        log.debug "Alarm has cleared and is no more notifications are needed."
		}
        } */
        
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
	