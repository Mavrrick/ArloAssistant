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
        }
        section ("Virtual Tile Setup"){
	   		input "createTileDev", "bool", title: "Would you like to create a virtual tile to create your virtual buttons for Mode change functianlity", description: "ADT Tools will attempt to create virtual devices for the mode change functinality", defaultValue: false, required: true, multiple: false
		}
    section("IFTTT Fix Integration") {
		input "iftttSwitch", "capability.switch", multiple: false, required: false
        input name: "iftttLength", type: "number", title: "Check time", description: "Time in seconds to validate IFTTT call corrected problem.", required: false, range: "5..600"
//        input name: "clipLength", type: "number", title: "Check time", description: "Please Specify time before checking camera status in seconds", required: true, range: "5..600"
        }

        	section("Via a push notification and/or an SMS message"){
   			input "sendNotify", "bool", title: "Do you want to notifiy on this event", description: "This will tell Arlo Assistant to send notification for Camera Health Check", defaultValue: false, required: true, multiple: false
			input("recipients", "contact", title: "Send notifications to") {
        	paragraph "Multiple numbers can be entered as long as sperated by a (;)"
			input "phone", "phone", title: "Enter a phone number to get SMS", required: false
			paragraph "If outside the US please make sure to enter the proper country code."
   			input "sendPush", "bool", title: "Send Push notifications to everyone?", description: "This will tell Arlo Assistant to send out push notifications to all users of the location", defaultValue: false, required: true, multiple: false
//          input "sendPush", "enum", title: "Send Push notifications to everyone?", required: false, options: ["Yes", "No"]
		}
	}
    section("Camera to refresh clips"){  
    		input "camerasIMG", "capability.imageCapture", multiple: false, required: false
            }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    state.createTileDev = false
    state.getLastImageURL= null
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(cameras, "clipStatus.Initiated", arloCheck)
	subscribe(cameras, "clip", arloDetails)
/*	if (frequencyUnit?.equals("hours")) {
		schedule("0 0 0/${frequency} * * ?", arloRefresh)
		}
        else {
	schedule("0 0/${frequency} * * * ?", arloRefresh)
					} */
//	schedule("0 0 0/${frequency} * * ?", arloRefresh)
    if (settings.createTileDev) {
    	if (state.createTileDev) {
        log.debug "Virtual buttons already created, Will not create buttons"
        }
        else {
    				log.debug "initialize: Creating virtual button devices ADT Mode Change"
				addChildDevice("shackrat", "arloPilotCameraTile", "${cameras} - ArloTile", location.hubs[0].id, [
					"name": "${cameras} - ArloTile",
					"label": "${cameras} - ArloTile",
					"completedSetup": true, 					
				])
                state?.createTileDev = true
                log.debug "Arlo Assistant Virtual Tile Device Created"
                }
		}
}

def arloCheck(evt) {	
	log.debug "$evt.name: $evt.value. Schedule check in ${clipLength}"
//	log.debug "Refreshing cameras with ${clipLength} second capture"
//	def clipStatusState = cameras.latestState("clipStatus")
//	log.debug "Latest Clip status value: ${clipStatusState.value}"
//    runIn(clipLength, arloRefresh)
}

def arloIFTTTCheck() {	
	log.debug "${cameras} IFTTT Validation of fix action in ${iftttLength}"
//    runIn(iftttLength, arloRefresh)
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
            else if (camaraSatus == "Initiated"){
            log.debug "Camera state has failed to return to completed. Submitting Notification for action"
            if (sendNotify) {
				sendCameraHealthNotify()
                }
            iftttSwitch?.on()
            arloIFTTTCheck()
            }
            else {
            log.debug "Camera status is not avaliable to check. Please remove Health check app"
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
       
def arloDetails(evt) {	
	log.debug "${cameras} clip status data check"
    def data = parseJson(evt.data)
    log.debug "event data: ${data}"
    log.debug "event key1: ${data.clipPath}"
    log.debug "event key2: ${data.thumbnailPath}"
    def thumbnail = data.thumbnailPath
    log.debug "${thumbnail} passing to virtual device now"
    state?.getLastImageURL = thumbnail
    camerasIMG?.doRefresh(data.thumbnailPath)    
}

/*
	getLastImageURL

	Returns the a pre-signed URL to the Arlo cloud for the latest captured still image.

	Note: Called by a the child device.

	Added: v1.4
*/
public def getLastImageURL()
{
	if (getLastImageURL == null)
    	{ 
        log.debug "Image value is null and will not continue"
        }
	else {
		// Invalidate the cache
		state.arloDeviceCacheTS = 0

//		def arloDevice = getArloDevice(deviceId)
//		if (arloDevice)
//		{
			log.debug "getLastImageURL: Returning pre-signed image URL ${state.getLastImageUrl}"
			return state.getLastImageURL
//		}
	}

	log.debug "getLastImageURL: Unable to fetch the latest image URL."
	return false
}