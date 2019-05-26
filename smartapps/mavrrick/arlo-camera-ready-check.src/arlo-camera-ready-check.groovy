/**
 *  Arlo Cameras Ready Check
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
    name: "Arlo Camera Ready Check",
    namespace: "Mavrrick",
    author: "Mavrrick",
    description: "Check Status of camera to ensure status is in good state",
    category: "My Apps",
    parent: "Mavrrick:Arlo Assistant",
	iconUrl: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX2Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX3Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg"
    )

import groovy.time.TimeCategory 

preferences {
	section("Setup") {
		input "cameras", "capability.videoCapture", multiple: false
        input name: "clipLength", type: "number", title: "Check time", description: "Please Specify time before checking camera status in seconds", required: true, range: "5..600"
//        input "frequencyUnit", "enum", options: ["mins", "hours"], required: true, defaultValue: "hours", title: "Unit for frequency"
//        input name: "frequency", type: "number", title: "Frequency to refresh cameras", description: "Please specify how often you would like to refresh cameras", required: true
        }
        	section("Via a push notification and/or an SMS message"){
			input("recipients", "contact", title: "Send notifications to") {
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input "phone", "phone", title: "Enter a phone number to get SMS", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush", "bool", title: "Send Push notifications to everyone?", description: "This will tell ADT Tools to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
//          input "sendPush", "enum", title: "Send Push notifications to everyone?", required: false, options: ["Yes", "No"]
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
	subscribe(cameras, "clipStatus.Initiated", arloCheck)
/*	if (frequencyUnit?.equals("hours")) {
		schedule("0 0 0/${frequency} * * ?", arloRefresh)
		}
        else {
	schedule("0 0/${frequency} * * * ?", arloRefresh)
					} */
//	schedule("0 0 0/${frequency} * * ?", arloRefresh)
}

def arloCheck(evt) {	
	log.debug "$evt.name: $evt.value. Schedule check in ${clipLength}"
//	log.debug "Refreshing cameras with ${clipLength} second capture"
//	def clipStatusState = cameras.latestState("clipStatus")
//	log.debug "Latest Clip status value: ${clipStatusState.value}"
    runIn(clipLength, arloRefresh)
}

def arloRefresh() {	
//	log.debug "$evt.name: $evt.value"
//	log.debug "Refreshing cameras with ${clipLength} second capture"
//	def clipStatusState = cameras.latestState("clipStatus")
        	def camaraSatus = cameras.currentClipStatus
//    		log.debug "Camera is in ${camaraSatus} state"
            if (camaraSatus == "Completed") 
        	{
        	log.debug "Camera has returned to a completed state "
//            sendCameraHealthNotify()
    		}
            else {
            log.debug "Camera state has failed to return to completed. Submitting Notification for action"
            sendCameraHealthNotify()
            }
            }
            
def sendCameraHealthNotify() {
       	log.debug "Camera Health Notify"
       	def msg = "${cameras} camera has not returned to a completed states. Please check your device."
//        log.debug "$evt.name:$evt.value, sendPush:$sendPush, '$msg'"

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
	   }       