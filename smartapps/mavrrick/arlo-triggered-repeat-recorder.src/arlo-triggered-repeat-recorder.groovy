/**
 *  Arlo Triggered Recorder with Repeat
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
    name: "Arlo Triggered Repeat Recorder",
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
	section("When any of the following devices trigger..."){
		input "motion", "capability.motionSensor", title: "Motion Sensor?", required: false
		input "contact", "capability.contactSensor", title: "Contact Sensor?", required: false
        input "myButton", "capability.momentary", title: "What Button?", required: false, multiple: false
		input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false
		input "mySwitch", "capability.switch", title: "Switch?", required: false
		input "myPresence", "capability.presenceSensor", title: "Presence Sensor?", required: false
        input "myMoisture", "capability.waterSensor", title: "Moisture Sensor?", required: false
	}
	section("Setup") {
		input "cameras", "capability.videoCapture", description: "Please select the cameras will when the triggered", multiple: true
        input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording", required: true, range: "5..120"
        input "recordRepeat", "bool", title: "Enable Camare to trigger recording as long as arlarm is occuring?", description: "This switch will enable cameras generate new clips as long as there is a active alarm.", defaultValue: false, required: true, multiple: false
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
		subscribe(contact, "contact.open", arloCapture)
	}
	if (acceleration) {
		subscribe(acceleration, "acceleration.active", arloCapture)
	}
	if (motion) {
		subscribe(motion, "motion.active", arloCapture)
	}
	if (mySwitch) {
		subscribe(mySwitch, "switch.on", arloCapture)
	}
	if (myPresence) {
		subscribe(myPresence, "presence", arloCapture)
	}
    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", arloCapture)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", arloCapture)
		}
}

def arloCapture(evt) {	
//	log.debug "$evt.name: $evt.value"
	log.debug "Refreshing cameras with ${clipLength} second capture"
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

/* 		numFlashes.times {
			log.trace "Switch on after  $delay msec"
			switches.eachWithIndex {s, i ->
				if (initialActionOn[i]) {
					s.on(delay: delay)
				}
				else {
					s.off(delay:delay)
				}
			}
*/
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